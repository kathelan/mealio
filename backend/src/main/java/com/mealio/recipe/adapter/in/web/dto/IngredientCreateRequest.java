package com.mealio.recipe.adapter.in.web.dto;

import com.mealio.domain.model.MeasurementUnit;
import com.mealio.recipe.domain.model.AddIngredientCommand;
import java.math.BigDecimal;
import java.util.UUID;

public record IngredientCreateRequest(String name, BigDecimal quantity, String unit, BigDecimal calories) {
    public AddIngredientCommand toCommand(UUID recipeId) {
        return new AddIngredientCommand(recipeId, name, quantity, parseUnit(unit), calories);
    }

    private MeasurementUnit parseUnit(String value) {
        if (value == null) return null;
        return java.util.Arrays.stream(MeasurementUnit.values())
                .filter(unit -> unit.code().equalsIgnoreCase(value) || unit.name().equalsIgnoreCase(value))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown ingredient unit: " + value));
    }
}
