package com.mealio.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.mealio.category.infrastructure.persistence.repository.MealCategoryJpaRepository;
import com.mealio.recipe.infrastructure.persistence.repository.RecipeIngredientJpaRepository;
import com.mealio.recipe.infrastructure.persistence.repository.RecipeJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:mealio_backend_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;INIT=CREATE SCHEMA IF NOT EXISTS backend",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.liquibase.enabled=true",
        "spring.liquibase.change-log=classpath:/db/changelog/db.changelog-master.yaml",
        "spring.liquibase.default-schema=backend",
        "spring.liquibase.parameters.mealioSchema=backend",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.properties.hibernate.default_schema=backend"
})
class BackendPersistenceSchemaIntegrationTest {

    @Autowired
    private MealCategoryJpaRepository mealCategoryJpaRepository;

    @Autowired
    private RecipeJpaRepository recipeJpaRepository;

    @Autowired
    private RecipeIngredientJpaRepository recipeIngredientJpaRepository;

    @Test
    void loadsPersistenceSchema() {
        assertThat(mealCategoryJpaRepository.count()).isZero();
        assertThat(recipeJpaRepository.count()).isZero();
        assertThat(recipeIngredientJpaRepository.count()).isZero();
    }
}
