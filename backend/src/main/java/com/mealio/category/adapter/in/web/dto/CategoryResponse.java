package com.mealio.category.adapter.in.web.dto;

import com.mealio.category.domain.model.CategoryType;
import com.mealio.category.domain.model.MealCategory;
import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
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

    public static CategoryResponse from(MealCategory category) {
        return new CategoryResponse(
                category.id(),
                category.parentId(),
                category.name(),
                category.slug(),
                category.path(),
                category.type(),
                category.sortOrder(),
                category.createdAt(),
                category.updatedAt()
        );
    }
}
