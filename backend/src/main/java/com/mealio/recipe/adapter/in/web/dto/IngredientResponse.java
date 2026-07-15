package com.mealio.recipe.adapter.in.web.dto;

import com.mealio.domain.model.RecipeIngredient;
import java.math.BigDecimal;
import java.util.UUID;

public record IngredientResponse(UUID id, String name, BigDecimal quantity, String unit, BigDecimal calories) {
    public static IngredientResponse from(RecipeIngredient value) {
        return new IngredientResponse(value.id(), value.name(), value.quantity(), value.unit().code(), value.calories());
    }
}
