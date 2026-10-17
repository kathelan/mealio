package com.mealio.recipe.infrastructure.web.dto;

import com.mealio.recipe.domain.model.CreateRecipeCommand;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record RecipeCreateRequest(String name, String description, LocalDate createdOn, String imageUrl,
                                  Set<UUID> categoryIds) {
    public CreateRecipeCommand toCommand() {
        return new CreateRecipeCommand(name, description, createdOn, imageUrl, categoryIds);
    }
}
