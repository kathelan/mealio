package com.mealio.category.domain.model;

import java.util.UUID;

public record CreateCategoryCommand(
        UUID parentId,
        String name,
        CategoryType type,
        Integer sortOrder
) {
}
