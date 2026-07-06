package com.mealio.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DomainModelTest {

    @Test
    void exposesMeasurementUnitCodes() {
        assertThat(MeasurementUnit.GRAM.code()).isEqualTo("g");
        assertThat(MeasurementUnit.SPOON.code()).isEqualTo("t");
        assertThat(MeasurementUnit.SLICE.code()).isEqualTo("w");
        assertThat(MeasurementUnit.MILLILITER.code()).isEqualTo("ml");
        assertThat(MeasurementUnit.PIECE.code()).isEqualTo("pcs");
    }

    @Test
    void representsNestedMealCategory() {
        var id = UUID.randomUUID();
        var parentId = UUID.randomUUID();

        var category = new MealCategory(id, parentId, "Na slodko", "na-slodko", "sniadanie/na-slodko",
                MealCategoryType.TASTE, 10);

        assertThat(category.id()).isEqualTo(id);
        assertThat(category.parentId()).isEqualTo(parentId);
        assertThat(category.name()).isEqualTo("Na slodko");
        assertThat(category.slug()).isEqualTo("na-slodko");
        assertThat(category.path()).isEqualTo("sniadanie/na-slodko");
        assertThat(category.type()).isEqualTo(MealCategoryType.TASTE);
        assertThat(category.sortOrder()).isEqualTo(10);
    }

    @Test
    void representsRecipeWithManyCategories() {
        var id = UUID.randomUUID();
        var breakfastId = UUID.randomUUID();
        var sweetId = UUID.randomUUID();
        var categoryIds = Set.of(breakfastId, sweetId);
        var createdOn = LocalDate.of(2026, 7, 6);

        var recipe = new Recipe(id, "Owsianka", "Owsianka z bananem", createdOn,
                "https://mealio.test/owsianka.jpg", categoryIds);

        assertThat(recipe.id()).isEqualTo(id);
        assertThat(recipe.name()).isEqualTo("Owsianka");
        assertThat(recipe.description()).isEqualTo("Owsianka z bananem");
        assertThat(recipe.createdOn()).isEqualTo(createdOn);
        assertThat(recipe.imageUrl()).isEqualTo("https://mealio.test/owsianka.jpg");
        assertThat(recipe.categoryIds()).containsExactlyInAnyOrder(breakfastId, sweetId);
    }

    @Test
    void representsRecipeIngredientWithDecimalCalories() {
        var id = UUID.randomUUID();
        var recipeId = UUID.randomUUID();

        var ingredient = new RecipeIngredient(id, recipeId, "Banan", new BigDecimal("120.500"),
                MeasurementUnit.GRAM, new BigDecimal("107.25"));

        assertThat(ingredient.id()).isEqualTo(id);
        assertThat(ingredient.recipeId()).isEqualTo(recipeId);
        assertThat(ingredient.name()).isEqualTo("Banan");
        assertThat(ingredient.quantity()).isEqualByComparingTo("120.500");
        assertThat(ingredient.unit()).isEqualTo(MeasurementUnit.GRAM);
        assertThat(ingredient.calories()).isEqualByComparingTo("107.25");
    }
}
