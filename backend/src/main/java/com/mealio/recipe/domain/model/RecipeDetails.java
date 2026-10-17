package com.mealio.recipe.domain.model;

import com.mealio.recipe.domain.model.Recipe;
import com.mealio.recipe.domain.model.RecipeIngredient;
import java.util.List;

public record RecipeDetails(Recipe recipe, List<RecipeIngredient> ingredients) {
}
