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

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {
        "spring.liquibase.default-schema=backend",
        "spring.liquibase.parameters.mealioSchema=backend",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.properties.hibernate.default_schema=backend"
})
class PostgreSqlIntegrationTest {
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
    @Autowired com.mealio.product.application.ProductService products;
    @Autowired ManageCategoriesUseCase categories;
    @Autowired ManageRecipesUseCase recipes;

    @BeforeEach
    void clearData() {
        jdbc.update("DELETE FROM backend.product_versions");
        jdbc.update("DELETE FROM backend.products");
        jdbc.update("DELETE FROM backend.recipe_ingredients");
        jdbc.update("DELETE FROM backend.recipes");
        jdbc.update("DELETE FROM backend.meal_categories");
    }

    @Test
    void migratesEmptyDatabaseAndPreservesRecipeDataAndCategoryCascade() {
        assertThat(jdbc.queryForList("SELECT id FROM backend.databasechangelog", String.class))
                .containsExactlyInAnyOrder("001-create-recipes-and-categories", "002-create-product-catalog");
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

    @Test
    void productVersionsPreserveSnapshotsAndRejectStaleWrites() {
        var created = products.create(java.util.Optional.of("5901234123457"), productDraft("Rice", false));
        var oldSnapshot = products.snapshotVersion(created.id(), 1);
        var changed = products.revise(created.id(), 1, productDraft("Updated rice", true));
        assertThat(changed.version().number()).isEqualTo(2);
        assertThat(products.snapshotVersion(created.id(), 1)).isEqualTo(oldSnapshot);
        assertThat(products.snapshot(created.id()).verificationStatus()).isEqualTo("USER_CONFIRMED");
        assertThat(products.byGtin("05901234123457").id()).isEqualTo(created.id());
        assertThatThrownBy(() -> products.revise(created.id(), 1, productDraft("Stale", true)))
                .isInstanceOf(com.mealio.product.domain.ProductConflictException.class);
        assertThatThrownBy(() -> products.create(java.util.Optional.of("05901234123457"), productDraft("Duplicate", true)))
                .isInstanceOf(com.mealio.product.domain.ProductConflictException.class);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM backend.products", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM backend.product_versions", Integer.class)).isEqualTo(2);
    }

    @Test
    void concurrentCorrectionsHaveOneWinner() throws Exception {
        var product = products.create(java.util.Optional.empty(), productDraft("Rice", false));
        var start = new java.util.concurrent.CountDownLatch(1);
        try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            java.util.concurrent.Callable<String> correction = () -> {
                start.await();
                try {
                    products.revise(product.id(), 1, productDraft("Corrected", true));
                    return "created";
                } catch (com.mealio.product.domain.ProductConflictException exception) {
                    return "conflict";
                }
            };
            var first = executor.submit(correction);
            var second = executor.submit(correction);
            start.countDown();
            assertThat(java.util.List.of(first.get(15, java.util.concurrent.TimeUnit.SECONDS),
                    second.get(15, java.util.concurrent.TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder("created", "conflict");
        }
        assertThat(products.get(product.id()).version().number()).isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM backend.product_versions", Integer.class)).isEqualTo(2);
    }

    @Test
    void failedVersionInsertRollsBackProductIdentity() {
        jdbc.execute("ALTER TABLE backend.product_versions ADD CONSTRAINT test_reject_version CHECK (name <> 'Rejected')");
        try {
            assertThatThrownBy(() -> products.create(java.util.Optional.empty(), productDraft("Rejected", true)))
                    .isInstanceOf(com.mealio.product.api.ProductUnavailableException.class);
            assertThat(jdbc.queryForObject("SELECT count(*) FROM backend.products", Integer.class)).isZero();
            assertThat(jdbc.queryForObject("SELECT count(*) FROM backend.product_versions", Integer.class)).isZero();
        } finally {
            jdbc.execute("ALTER TABLE backend.product_versions DROP CONSTRAINT test_reject_version");
        }
    }

    @Test
    void upgradesLegacyDatabaseWithoutLosingExistingData() throws Exception {
        jdbc.execute("CREATE SCHEMA upgrade_test");
        try (var connection = java.sql.DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {
            var database = liquibase.database.DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                    new liquibase.database.jvm.JdbcConnection(connection));
            database.setDefaultSchemaName("upgrade_test");
            try (var migration = new liquibase.Liquibase("db/changelog/db.changelog-master.yaml",
                    new liquibase.resource.ClassLoaderResourceAccessor(), database)) {
                migration.setChangeLogParameter("mealioSchema", "upgrade_test");
                migration.update(1, new liquibase.Contexts(), new liquibase.LabelExpression());
                try (var statement = connection.createStatement()) {
                    statement.executeUpdate("INSERT INTO upgrade_test.meal_categories (id,name,slug,path,type,sort_order) "
                            + "VALUES ('00000000-0000-0000-0000-000000000001','Legacy','legacy','legacy','CUSTOM',0)");
                }
                migration.update(new liquibase.Contexts(), new liquibase.LabelExpression());
            }
            assertThat(jdbc.queryForObject("SELECT name FROM upgrade_test.meal_categories", String.class)).isEqualTo("Legacy");
            assertThat(jdbc.queryForObject("SELECT count(*) FROM upgrade_test.products", Integer.class)).isZero();
            assertThat(jdbc.queryForObject("SELECT count(*) FROM upgrade_test.product_versions", Integer.class)).isZero();
        } finally {
            jdbc.execute("DROP SCHEMA upgrade_test CASCADE");
        }
    }

    private com.mealio.product.domain.ProductDraft productDraft(String name, boolean complete) {
        var nutrition = com.mealio.product.domain.NutritionFacts.builder()
                .basis(com.mealio.product.domain.NutritionBasis.PER_100_G)
                .energyKcal(java.util.Optional.of(new BigDecimal("123.4567")))
                .protein(java.util.Optional.of(BigDecimal.ZERO)).fat(java.util.Optional.of(BigDecimal.ZERO));
        if (complete) nutrition.carbohydrates(java.util.Optional.of(BigDecimal.ZERO));
        return com.mealio.product.domain.ProductDraft.builder().name(name).nutrition(nutrition.build()).build();
    }
}
