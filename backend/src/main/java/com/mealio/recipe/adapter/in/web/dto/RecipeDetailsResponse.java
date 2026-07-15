package com.mealio.recipe.adapter.in.web.dto;

import com.mealio.recipe.domain.model.RecipeDetails;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record RecipeDetailsResponse(UUID id, String name, String description, LocalDate createdOn, String imageUrl,
                                    Set<UUID> categoryIds, List<IngredientResponse> ingredients) {
    public static RecipeDetailsResponse from(RecipeDetails details) {
        var recipe = details.recipe();
        return new RecipeDetailsResponse(recipe.id(), recipe.name(), recipe.description(), recipe.createdOn(),
                recipe.imageUrl(), recipe.categoryIds(), details.ingredients().stream().map(IngredientResponse::from).toList());
    }
}
