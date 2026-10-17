package com.mealio.recipe.domain.port.out;

import com.mealio.recipe.domain.model.Recipe;
import com.mealio.recipe.domain.model.RecipeIngredient;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecipeRepository {
    List<Recipe> findAll();
    Optional<Recipe> findById(UUID id);
    Recipe save(Recipe recipe);
    void deleteById(UUID id);
    RecipeIngredient saveIngredient(RecipeIngredient ingredient);
    List<RecipeIngredient> findIngredients(UUID recipeId);
}
