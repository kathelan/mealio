package com.mealio.recipe.domain.exception;

public abstract class RecipeException extends RuntimeException {
    protected RecipeException(String message) { super(message); }
    protected RecipeException(String message, Throwable cause) { super(message, cause); }
}
