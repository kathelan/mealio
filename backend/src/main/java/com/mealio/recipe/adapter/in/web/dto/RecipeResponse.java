package com.mealio.recipe.adapter.in.web.dto;

import com.mealio.domain.model.Recipe;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record RecipeResponse(UUID id, String name, String description, LocalDate createdOn, String imageUrl,
                             Set<UUID> categoryIds) {
    public static RecipeResponse from(Recipe value) {
        return new RecipeResponse(value.id(), value.name(), value.description(), value.createdOn(), value.imageUrl(),
                value.categoryIds());
    }
}
