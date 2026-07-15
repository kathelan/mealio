package com.mealio.recipe.domain.port.in;

import com.mealio.domain.model.Recipe;
import com.mealio.domain.model.RecipeIngredient;
import com.mealio.recipe.domain.model.AddIngredientCommand;
import com.mealio.recipe.domain.model.CreateRecipeCommand;
import com.mealio.recipe.domain.model.RecipeDetails;
import java.util.List;
import java.util.UUID;

public interface ManageRecipesUseCase {
    List<Recipe> list();
    RecipeDetails get(UUID id);
    Recipe create(CreateRecipeCommand command);
    RecipeIngredient addIngredient(AddIngredientCommand command);
}
