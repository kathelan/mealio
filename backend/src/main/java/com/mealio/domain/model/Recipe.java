package com.mealio.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public record Recipe(
        UUID id,
        String name,
        String description,
        LocalDate createdOn,
        String imageUrl,
        UUID mealCategoryId
) {
}
