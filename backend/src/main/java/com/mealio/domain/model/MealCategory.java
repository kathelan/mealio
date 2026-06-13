package com.mealio.domain.model;

import java.util.UUID;

public record MealCategory(
        UUID id,
        String name,
        String slug,
        MealCategoryType type,
        Integer sortOrder
) {
}
