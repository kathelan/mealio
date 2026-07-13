package com.mealio.category.domain.exception;

import java.util.UUID;

public class CategoryNotFoundException extends CategoryException {

    public CategoryNotFoundException(UUID id) {
        super("Category not found: " + id);
    }
}
