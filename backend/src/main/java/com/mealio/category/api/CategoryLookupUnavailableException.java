package com.mealio.category.api;

public class CategoryLookupUnavailableException extends RuntimeException {
    public CategoryLookupUnavailableException(Throwable cause) {
        super("Cannot look up categories", cause);
    }
}
