package com.mealio.recipe.domain.exception;

import java.util.UUID;

public class RecipeNotFoundException extends RecipeException {
    public RecipeNotFoundException(UUID id) { super("Recipe not found: " + id); }
}
