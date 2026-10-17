package com.mealio.product.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealio.product.infrastructure.web.dto.ProductResponse;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:mealio_product_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;INIT=CREATE SCHEMA IF NOT EXISTS backend",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.liquibase.default-schema=backend", "spring.liquibase.parameters.mealioSchema=backend",
        "spring.jpa.hibernate.ddl-auto=validate", "spring.jpa.properties.hibernate.default_schema=backend"
})
class ProductControllerIntegrationTest {
    @Autowired WebApplicationContext context;
    @Autowired ObjectMapper mapper;
    @Autowired JdbcTemplate jdbc;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        jdbc.update("DELETE FROM backend.product_versions");
        jdbc.update("DELETE FROM backend.products");
    }

    @Test
    void createsFindsAndRevisesProductWithoutChangingHistoricalVersion() throws Exception {
        var original = create("{\"name\":\"Milk\",\"gtin\":\"5901234123457\",\"basis\":\"PER_100_ML\",\"protein\":0}");
        assertThat(original.verificationStatus()).isEqualTo("INCOMPLETE");
        assertThat(original.energyKcal()).isNull();
        assertThat(original.source()).isEqualTo("USER_LABEL");
        assertThat(original.gtin()).isEqualTo("05901234123457");
        mvc.perform(get("/api/v1/products/by-gtin/5901234123457"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(original.id().toString()));

        var correction = "{\"expectedVersion\":1,\"name\":\"Corrected milk\",\"basis\":\"PER_100_ML\",\"energyKcal\":62.25,\"protein\":3,\"fat\":3,\"carbohydrates\":4}";
        mvc.perform(post("/api/v1/products/{id}/versions", original.id()).contentType(MediaType.APPLICATION_JSON).content(correction))
                .andExpect(status().isCreated()).andExpect(header().string("Location", "/api/v1/products/" + original.id() + "/versions/2"))
                .andExpect(jsonPath("$.versionNumber").value(2)).andExpect(jsonPath("$.verificationStatus").value("USER_CONFIRMED"));
        mvc.perform(get("/api/v1/products/{id}", original.id())).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Corrected milk"));
        mvc.perform(get("/api/v1/products/{id}/versions/1", original.id())).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Milk")).andExpect(jsonPath("$.energyKcal").isEmpty());
        mvc.perform(post("/api/v1/products/{id}/versions", original.id()).contentType(MediaType.APPLICATION_JSON).content(correction))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("PRODUCT_CONFLICT"))
                .andExpect(jsonPath("$.requestId").isNotEmpty());
    }

    @Test
    void rejectsEquivalentGtinAndKeepsCreateAtomic() throws Exception {
        create("{\"name\":\"Rice\",\"gtin\":\"5901234123457\",\"basis\":\"PER_100_G\"}");
        mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Other rice\",\"gtin\":\"05901234123457\",\"basis\":\"PER_100_G\"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("PRODUCT_CONFLICT"));
        assertThat(jdbc.queryForObject("SELECT count(*) FROM backend.products", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM backend.product_versions", Integer.class)).isEqualTo(1);
    }

    @Test
    void treatsSearchWildcardsLiterallyAndBoundsPages() throws Exception {
        var special = create("{\"name\":\"Rice 100%_!\",\"basis\":\"PER_100_G\"}");
        create("{\"name\":\"Rice brown\",\"basis\":\"PER_100_G\"}");
        mvc.perform(get("/api/v1/products").param("query", "%_!"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(special.id().toString()));
        mvc.perform(get("/api/v1/products").param("query", "RICE").param("size", "1").param("page", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/v1/products").param("size", "101")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/products").param("page", "-1")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/products").param("size", "abc")).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{", "{\"name\":\"Rice\",\"basis\":\"PER_100_G\",\"energyKcal\":-1}",
            "{\"name\":\"Rice\",\"basis\":\"PER_100_G\",\"protein\":0.00001}",
            "{\"name\":\"Rice\",\"basis\":\"SERVING\"}",
            "{\"name\":\"Rice\",\"basis\":\"PER_100_G\",\"gtin\":\"invalid\"}"})
    void rejectsInvalidRequestsWithSafeEnvelope(String body) throws Exception {
        mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_PRODUCT_REQUEST"))
                .andExpect(jsonPath("$.requestId").isNotEmpty()).andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void rejectsMissingExpectedVersionAndReturnsNotFoundForUnknownVersions() throws Exception {
        var product = create("{\"name\":\"Rice\",\"basis\":\"PER_100_G\"}");
        mvc.perform(post("/api/v1/products/{id}/versions", product.id()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Rice\",\"basis\":\"PER_100_G\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/products/{id}/versions/2", product.id())).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/products/{id}/versions/0", product.id())).andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/products/{id}", UUID.randomUUID())).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/products/not-a-uuid")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/products/by-gtin/5901234123458")).andExpect(status().isBadRequest());
    }

    @Test
    void rejectsUnknownFieldsAndKeepsPersistenceErrorsPrivate() throws Exception {
        mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Rice\",\"basis\":\"PER_100_G\",\"unknown\":1}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/products").param("page", "2147483647").param("size", "100"))
                .andExpect(status().isBadRequest());
        jdbc.execute("ALTER TABLE backend.product_versions ADD CONSTRAINT test_reject CHECK (name <> 'Rejected')");
        try {
            mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Rejected\",\"basis\":\"PER_100_G\"}"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value("PRODUCT_PERSISTENCE_ERROR"))
                    .andExpect(jsonPath("$.message").value("Product catalog is unavailable"));
            assertThat(jdbc.queryForObject("SELECT count(*) FROM backend.products", Integer.class)).isZero();
        } finally {
            jdbc.execute("ALTER TABLE backend.product_versions DROP CONSTRAINT test_reject");
        }
    }

    private ProductResponse create(String body) throws Exception {
        var response = mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse();
        var product = mapper.readValue(response.getContentAsString(), ProductResponse.class);
        assertThat(response.getHeader("Location")).isEqualTo("/api/v1/products/" + product.id());
        return product;
    }
}
