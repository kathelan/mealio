package com.mealio.recipe.domain.port.out;

import com.mealio.domain.model.Recipe;
import com.mealio.domain.model.RecipeIngredient;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RecipeRepository {
    List<Recipe> findAll();
    Optional<Recipe> findById(UUID id);
    Recipe save(Recipe recipe);
    RecipeIngredient saveIngredient(RecipeIngredient ingredient);
    List<RecipeIngredient> findIngredients(UUID recipeId);
    Set<UUID> findExistingCategoryIds(Set<UUID> categoryIds);
}
