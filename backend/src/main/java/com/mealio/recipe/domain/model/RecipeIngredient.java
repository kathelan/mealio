package com.mealio.recipe.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record RecipeIngredient(
        UUID id,
        UUID recipeId,
        String name,
        BigDecimal quantity,
        MeasurementUnit unit,
        BigDecimal calories
) {
}
