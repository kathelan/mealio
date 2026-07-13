package com.mealio.category.domain.model;

import java.time.Instant;
import java.util.UUID;

public record MealCategory(
        UUID id,
        UUID parentId,
        String name,
        String slug,
        String path,
        CategoryType type,
        int sortOrder,
        Instant createdAt,
        Instant updatedAt
) {
}
