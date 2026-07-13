package com.mealio.category.domain.exception;

import java.util.UUID;

public class CategoryHasChildrenException extends CategoryException {

    public CategoryHasChildrenException(UUID id) {
        super("Category has child categories and cannot be deleted: " + id);
    }
}
