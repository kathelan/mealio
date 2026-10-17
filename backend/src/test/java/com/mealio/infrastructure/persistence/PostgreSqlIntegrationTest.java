package com.mealio.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mealio.category.domain.model.CreateCategoryCommand;
import com.mealio.category.domain.port.in.ManageCategoriesUseCase;
import com.mealio.recipe.domain.model.AddIngredientCommand;
import com.mealio.recipe.domain.model.CreateRecipeCommand;
import com.mealio.recipe.domain.model.MeasurementUnit;
import com.mealio.recipe.domain.port.in.ManageRecipesUseCase;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = {
        "spring.liquibase.default-schema=backend",
        "spring.liquibase.parameters.mealioSchema=backend",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.properties.hibernate.default_schema=backend"
})
class PostgreSqlIntegrationTest {
    @org.springframework.boot.test.web.server.LocalServerPort
    int port;

    @Test
    void servesHttpFromTemporaryApplicationWithPostgres() throws Exception {
        try (var client = java.net.http.HttpClient.newHttpClient()) {
            var response = client.send(java.net.http.HttpRequest.newBuilder(
                    java.net.URI.create("http://localhost:" + port + "/actuator/health")).GET().build(),
                    java.net.http.HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).contains("UP");
        }
    }

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine")
            .withInitScript("db/postgres-test-init.sql");

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired ManageCategoriesUseCase categories;
    @Autowired ManageRecipesUseCase recipes;

    @BeforeEach
    void clearData() {
        jdbc.update("DELETE FROM backend.recipe_ingredients");
        jdbc.update("DELETE FROM backend.recipes");
        jdbc.update("DELETE FROM backend.meal_categories");
    }

    @Test
    void migratesEmptyDatabaseAndPreservesRecipeDataAndCategoryCascade() {
        assertThat(jdbc.queryForObject("SELECT count(*) FROM backend.databasechangelog", Integer.class)).isEqualTo(1);
        var category = categories.create(new CreateCategoryCommand(null, "Dinner", null, 1));
        var recipe = recipes.create(new CreateRecipeCommand("Rice", null, LocalDate.of(2026, 10, 17),
                null, Set.of(category.id())));
        var ingredient = recipes.addIngredient(new AddIngredientCommand(recipe.id(), "Rice",
                new BigDecimal("120.500"), MeasurementUnit.GRAM, new BigDecimal("107.25")));

        var stored = recipes.get(recipe.id());
        assertThat(stored.recipe().categoryIds()).containsExactly(category.id());
        assertThat(stored.ingredients()).containsExactly(ingredient);
        assertThat(stored.ingredients().getFirst().quantity()).isEqualByComparingTo("120.500");
        assertThat(stored.ingredients().getFirst().calories()).isEqualByComparingTo("107.25");
        assertThat(jdbc.queryForObject("SELECT created_at FROM backend.recipe_categories WHERE recipe_id = ?",
                java.sql.Timestamp.class, recipe.id())).isNotNull();

        categories.delete(category.id());
        assertThat(recipes.get(recipe.id()).recipe().categoryIds()).isEmpty();
        assertThat(recipes.get(recipe.id()).ingredients()).containsExactly(ingredient);
    }

    @Test
    void databaseRejectsDuplicateRootPathsAndUnknownCategoryLinks() {
        categories.create(new CreateCategoryCommand(null, "Dinner", null, 0));
        assertThatThrownBy(() -> jdbc.update("""
                INSERT INTO backend.meal_categories (id, name, slug, path, type, sort_order)
                VALUES (?, 'Dinner', 'dinner', 'dinner', 'CUSTOM', 0)
                """, UUID.randomUUID())).isInstanceOf(DataIntegrityViolationException.class);

        var recipe = recipes.create(new CreateRecipeCommand("Rice", null, null, null, Set.of()));
        assertThatThrownBy(() -> jdbc.update("""
                INSERT INTO backend.recipe_categories (recipe_id, category_id) VALUES (?, ?)
                """, recipe.id(), UUID.randomUUID())).isInstanceOf(DataIntegrityViolationException.class);
    }
}
