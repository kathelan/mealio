package com.mealio.domain.model;

import java.util.UUID;

public record MealCategory(
        UUID id,
        UUID parentId,
        String name,
        String slug,
        String path,
        MealCategoryType type,
        Integer sortOrder
) {
}
