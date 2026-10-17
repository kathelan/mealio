package com.mealio.category.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealio.category.infrastructure.persistence.repository.MealCategoryJpaRepository;
import com.mealio.category.adapter.in.web.dto.ApiErrorResponse;
import com.mealio.category.adapter.in.web.dto.CategoryCreateRequest;
import com.mealio.category.adapter.in.web.dto.CategoryResponse;
import com.mealio.category.domain.model.CategoryType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:mealio_category_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;INIT=CREATE SCHEMA IF NOT EXISTS backend",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.liquibase.enabled=true",
        "spring.liquibase.change-log=classpath:/db/changelog/db.changelog-master.yaml",
        "spring.liquibase.default-schema=backend",
        "spring.liquibase.parameters.mealioSchema=backend",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.properties.hibernate.default_schema=backend"
})
class CategoryControllerIntegrationTest {

    @Autowired
    private MealCategoryJpaRepository mealCategoryJpaRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mealCategoryJpaRepository.deleteAllInBatch();
    }

    @Test
    void createsListsGetsPatchesAndDeletesCategory() throws Exception {
        var created = postCategory(new CategoryCreateRequest(null, "Śniadanie", CategoryType.MEAL, 1));

        assertThat(created.id()).isNotNull();
        assertThat(created.name()).isEqualTo("Śniadanie");
        assertThat(created.slug()).isEqualTo("sniadanie");
        assertThat(created.path()).isEqualTo("sniadanie");
        assertThat(created.type()).isEqualTo(CategoryType.MEAL);

        var listedJson = mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var listed = objectMapper.readValue(listedJson, new TypeReference<List<CategoryResponse>>() {
        });
        assertThat(listed).hasSize(1);

        var fetchedJson = mockMvc.perform(get("/api/v1/categories/{id}", created.id()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var fetched = objectMapper.readValue(fetchedJson, CategoryResponse.class);
        assertThat(fetched).isEqualTo(created);

        var patchedJson = mockMvc.perform(patch("/api/v1/categories/{id}", created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "Lunch", "sortOrder", 9))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var patched = objectMapper.readValue(patchedJson, CategoryResponse.class);
        assertThat(patched.name()).isEqualTo("Lunch");
        assertThat(patched.slug()).isEqualTo("lunch");
        assertThat(patched.path()).isEqualTo("lunch");
        assertThat(patched.sortOrder()).isEqualTo(9);

        mockMvc.perform(delete("/api/v1/categories/{id}", created.id()))
                .andExpect(status().isNoContent());

        var missingJson = mockMvc.perform(get("/api/v1/categories/{id}", created.id()))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var missing = objectMapper.readValue(missingJson, ApiErrorResponse.class);
        assertThat(missing.code()).isEqualTo("CATEGORY_NOT_FOUND");
    }

    @Test
    void patchCanMoveChildToRootUsingExplicitNullParentId() throws Exception {
        var parent = postCategory(new CategoryCreateRequest(null, "Dinner", CategoryType.MEAL, 1));
        var child = postCategory(new CategoryCreateRequest(parent.id(), "Soup", CategoryType.CUSTOM, 2));
        var patch = new HashMap<String, Object>();
        patch.put("parentId", null);

        var patchedJson = mockMvc.perform(patch("/api/v1/categories/{id}", child.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var patched = objectMapper.readValue(patchedJson, CategoryResponse.class);

        assertThat(patched.parentId()).isNull();
        assertThat(patched.path()).isEqualTo("soup");
    }

    @Test
    void returnsConflictForDuplicateCategory() throws Exception {
        postCategory(new CategoryCreateRequest(null, "Breakfast", CategoryType.MEAL, 0));

        var responseJson = mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CategoryCreateRequest(null, "Breakfast", CategoryType.MEAL, 1))))
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var response = objectMapper.readValue(responseJson, ApiErrorResponse.class);

        assertThat(response.code()).isEqualTo("CATEGORY_ALREADY_EXISTS");
    }

    @Test
    void returnsBadRequestForInvalidPatch() throws Exception {
        var created = postCategory(new CategoryCreateRequest(null, "Breakfast", CategoryType.MEAL, 0));

        var responseJson = mockMvc.perform(patch("/api/v1/categories/{id}", created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("type", "UNKNOWN"))))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var response = objectMapper.readValue(responseJson, ApiErrorResponse.class);

        assertThat(response.code()).isEqualTo("INVALID_CATEGORY_REQUEST");
    }

    @Test
    void returnsBadRequestForUnsupportedPatchField() throws Exception {
        var created = postCategory(new CategoryCreateRequest(null, "Breakfast", CategoryType.MEAL, 0));

        var responseJson = mockMvc.perform(patch("/api/v1/categories/{id}", created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("unknown", "value"))))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var response = objectMapper.readValue(responseJson, ApiErrorResponse.class);

        assertThat(response.code()).isEqualTo("INVALID_CATEGORY_REQUEST");
    }

    @Test
    void returnsBadRequestForDecimalSortOrderPatch() throws Exception {
        var created = postCategory(new CategoryCreateRequest(null, "Breakfast", CategoryType.MEAL, 0));

        var responseJson = mockMvc.perform(patch("/api/v1/categories/{id}", created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sortOrder\":1.5}"))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var response = objectMapper.readValue(responseJson, ApiErrorResponse.class);

        assertThat(response.code()).isEqualTo("INVALID_CATEGORY_REQUEST");
    }

    @Test
    void returnsBadRequestForEmptyPatch() throws Exception {
        var created = postCategory(new CategoryCreateRequest(null, "Breakfast", CategoryType.MEAL, 0));

        var responseJson = mockMvc.perform(patch("/api/v1/categories/{id}", created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var response = objectMapper.readValue(responseJson, ApiErrorResponse.class);

        assertThat(response.code()).isEqualTo("INVALID_CATEGORY_REQUEST");
    }

    @Test
    void returnsConflictWhenDeletingCategoryWithChildren() throws Exception {
        var parent = postCategory(new CategoryCreateRequest(null, "Dinner", CategoryType.MEAL, 1));
        postCategory(new CategoryCreateRequest(parent.id(), "Soup", CategoryType.CUSTOM, 2));

        var responseJson = mockMvc.perform(delete("/api/v1/categories/{id}", parent.id()))
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var response = objectMapper.readValue(responseJson, ApiErrorResponse.class);

        assertThat(response.code()).isEqualTo("CATEGORY_HAS_CHILDREN");
    }

    private CategoryResponse postCategory(CategoryCreateRequest request) throws Exception {
        var responseJson = mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(responseJson, CategoryResponse.class);
    }

    @Test
    void renamingParentPersistsDescendantPathsAndAllCategoryTypes() throws Exception {
        var parent = postCategory(new CategoryCreateRequest(null, "Dinner", CategoryType.MEAL, 1));
        var child = postCategory(new CategoryCreateRequest(parent.id(), "Sweet", CategoryType.TASTE, 2));
        var grandchild = postCategory(new CategoryCreateRequest(child.id(), "Cake", CategoryType.CUSTOM, 3));

        mockMvc.perform(patch("/api/v1/categories/{id}", parent.id())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Lunch\"}"))
                .andExpect(status().isOk());

        var listedJson = mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var listed = objectMapper.readValue(listedJson, new TypeReference<List<CategoryResponse>>() {});
        assertThat(listed).extracting(CategoryResponse::path)
                .containsExactly("lunch", "lunch/sweet", "lunch/sweet/cake");
        assertThat(listed).extracting(CategoryResponse::type)
                .containsExactly(CategoryType.MEAL, CategoryType.TASTE, CategoryType.CUSTOM);
        assertThat(listed.get(1).parentId()).isEqualTo(parent.id());
        assertThat(listed.get(2).id()).isEqualTo(grandchild.id());
        assertThat(listed.get(2).parentId()).isEqualTo(child.id());
    }

    @Test
    void rejectedCycleAndDuplicateMovePreserveStoredTree() throws Exception {
        var parent = postCategory(new CategoryCreateRequest(null, "Dinner", CategoryType.MEAL, 1));
        var child = postCategory(new CategoryCreateRequest(parent.id(), "Soup", CategoryType.CUSTOM, 2));
        postCategory(new CategoryCreateRequest(null, "Soup", CategoryType.CUSTOM, 3));

        mockMvc.perform(patch("/api/v1/categories/{id}", parent.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("parentId", child.id()))))
                .andExpect(status().isBadRequest());
        var conflict = mockMvc.perform(patch("/api/v1/categories/{id}", child.id())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"parentId\":null}"))
                .andExpect(status().isConflict()).andReturn().getResponse().getContentAsString();
        assertThat(objectMapper.readValue(conflict, ApiErrorResponse.class).code())
                .isEqualTo("CATEGORY_ALREADY_EXISTS");

        var fetched = mockMvc.perform(get("/api/v1/categories/{id}", child.id()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(objectMapper.readValue(fetched, CategoryResponse.class)).isEqualTo(child);
    }
}
