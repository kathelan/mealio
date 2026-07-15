package com.mealio.recipe.domain.model;

import com.mealio.domain.model.MeasurementUnit;
import java.math.BigDecimal;
import java.util.UUID;

public record AddIngredientCommand(
        UUID recipeId,
        String name,
        BigDecimal quantity,
        MeasurementUnit unit,
        BigDecimal calories
) {
}
