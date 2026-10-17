package com.mealio.recipe.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealio.category.infrastructure.persistence.repository.MealCategoryJpaRepository;
import com.mealio.adapter.out.persistence.repository.RecipeIngredientJpaRepository;
import com.mealio.adapter.out.persistence.repository.RecipeJpaRepository;
import com.mealio.category.adapter.in.web.dto.CategoryCreateRequest;
import com.mealio.category.adapter.in.web.dto.ApiErrorResponse;
import com.mealio.category.adapter.in.web.dto.CategoryResponse;
import com.mealio.category.domain.model.CategoryType;
import com.mealio.recipe.adapter.in.web.dto.IngredientCreateRequest;
import com.mealio.recipe.adapter.in.web.dto.IngredientResponse;
import com.mealio.recipe.adapter.in.web.dto.RecipeCreateRequest;
import com.mealio.recipe.adapter.in.web.dto.RecipeDetailsResponse;
import com.mealio.recipe.adapter.in.web.dto.RecipeResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
        "spring.datasource.url=jdbc:h2:mem:mealio_recipe_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;INIT=CREATE SCHEMA IF NOT EXISTS backend",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.liquibase.enabled=true", "spring.liquibase.change-log=classpath:/db/changelog/db.changelog-master.yaml",
        "spring.liquibase.default-schema=backend", "spring.liquibase.parameters.mealioSchema=backend",
        "spring.jpa.hibernate.ddl-auto=none", "spring.jpa.properties.hibernate.default_schema=backend"
})
class RecipeControllerIntegrationTest {
    @Autowired WebApplicationContext context;
    @Autowired ObjectMapper mapper;
    @Autowired RecipeIngredientJpaRepository ingredients;
    @Autowired RecipeJpaRepository recipes;
    @Autowired MealCategoryJpaRepository categories;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        ingredients.deleteAllInBatch();
        recipes.deleteAllInBatch();
        categories.deleteAllInBatch();
    }

    @Test
    void createsRecipeWhenOptionalCategoriesAreOmitted() throws Exception {
        var json = mvc.perform(post("/api/v1/recipes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Rice\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        assertThat(mapper.readValue(json, RecipeResponse.class).categoryIds()).isEmpty();
    }

    @Test
    void createsListsGetsRecipeAndAddsIngredient() throws Exception {
        var category = createCategory();
        var request = new RecipeCreateRequest("Owsianka", "Z bananem", LocalDate.of(2026, 7, 15),
                "https://mealio.test/o.jpg", Set.of(category.id()));
        var createdJson = mvc.perform(post("/api/v1/recipes").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        var created = mapper.readValue(createdJson, RecipeResponse.class);
        assertThat(created.categoryIds()).containsExactly(category.id());

        var ingredientRequest = new IngredientCreateRequest("Banan", new BigDecimal("120.500"), "g",
                new BigDecimal("107.25"));
        var ingredientJson = mvc.perform(post("/api/v1/recipes/{id}/ingredients", created.id())
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(ingredientRequest)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        var ingredient = mapper.readValue(ingredientJson, IngredientResponse.class);
        assertThat(ingredient.unit()).isEqualTo("g");

        var detailsJson = mvc.perform(get("/api/v1/recipes/{id}", created.id()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var details = mapper.readValue(detailsJson, RecipeDetailsResponse.class);
        assertThat(details.ingredients()).containsExactly(ingredient);

        var listJson = mvc.perform(get("/api/v1/recipes")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(mapper.readValue(listJson, new TypeReference<List<RecipeResponse>>() {})).containsExactly(created);
    }

    @Test
    void rejectsUnknownCategoryAndInvalidIngredient() throws Exception {
        var request = new RecipeCreateRequest("Test", null, null, null, Set.of(UUID.randomUUID()));
        mvc.perform(post("/api/v1/recipes").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))).andExpect(status().isBadRequest());

        var recipe = createRecipeWithoutCategories();
        mvc.perform(post("/api/v1/recipes/{id}/ingredients", recipe.id()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sugar\",\"quantity\":0,\"unit\":\"g\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsNotFoundEnvelopeForMissingRecipe() throws Exception {
        var id = UUID.randomUUID();
        var json = mvc.perform(get("/api/v1/recipes/{id}", id)).andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        assertThat(mapper.readValue(json, ApiErrorResponse.class).code()).isEqualTo("RECIPE_NOT_FOUND");

        mvc.perform(post("/api/v1/recipes/{id}/ingredients", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sugar\",\"quantity\":1,\"unit\":\"g\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsDecimalsThatDoNotFitDatabaseColumns() throws Exception {
        var recipe = createRecipeWithoutCategories();
        assertInvalidIngredient(recipe.id(), "{\"name\":\"Sugar\",\"quantity\":1.2345,\"unit\":\"g\"}");
        assertInvalidIngredient(recipe.id(), "{\"name\":\"Sugar\",\"quantity\":1000000000,\"unit\":\"g\"}");
        assertInvalidIngredient(recipe.id(), "{\"name\":\"Sugar\",\"quantity\":1,\"unit\":\"g\",\"calories\":1.234}");
        assertInvalidIngredient(recipe.id(), "{\"name\":\"Sugar\",\"quantity\":1,\"unit\":\"g\",\"calories\":100000000}");
    }

    @Test
    void rejectsMalformedPayloadUnknownUnitAndNegativeCalories() throws Exception {
        var recipe = createRecipeWithoutCategories();
        assertInvalidIngredient(recipe.id(), "{");
        assertInvalidIngredient(recipe.id(), "{\"name\":\"Sugar\",\"quantity\":1,\"unit\":\"bucket\"}");
        assertInvalidIngredient(recipe.id(), "{\"name\":\"Sugar\",\"quantity\":1,\"unit\":\"g\",\"calories\":-1}");
    }

    private CategoryResponse createCategory() throws Exception {
        var json = mvc.perform(post("/api/v1/categories").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CategoryCreateRequest(null, "Breakfast", CategoryType.MEAL, 0))))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readValue(json, CategoryResponse.class);
    }

    private RecipeResponse createRecipeWithoutCategories() throws Exception {
        var json = mvc.perform(post("/api/v1/recipes").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RecipeCreateRequest("Test", null, null, null, Set.of()))))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readValue(json, RecipeResponse.class);
    }

    private void assertInvalidIngredient(UUID recipeId, String json) throws Exception {
        var response = mvc.perform(post("/api/v1/recipes/{id}/ingredients", recipeId)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
        assertThat(mapper.readValue(response, ApiErrorResponse.class).code()).isEqualTo("INVALID_RECIPE_REQUEST");
    }

    @Test
    void deletingCategoryRemovesOnlyItsRecipeAssociation() throws Exception {
        var category = createCategory();
        var createdJson = mvc.perform(post("/api/v1/recipes").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RecipeCreateRequest("Rice", null, null, null,
                                Set.of(category.id())))))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        var recipe = mapper.readValue(createdJson, RecipeResponse.class);

        mvc.perform(delete("/api/v1/categories/{id}", category.id())).andExpect(status().isNoContent());

        var fetched = mvc.perform(get("/api/v1/recipes/{id}", recipe.id()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var details = mapper.readValue(fetched, RecipeDetailsResponse.class);
        assertThat(details.id()).isEqualTo(recipe.id());
        assertThat(details.categoryIds()).isEmpty();
    }
}
