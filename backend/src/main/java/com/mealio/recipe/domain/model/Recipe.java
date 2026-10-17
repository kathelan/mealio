package com.mealio.recipe.domain.model;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record Recipe(
        UUID id,
        String name,
        String description,
        LocalDate createdOn,
        String imageUrl,
        Set<UUID> categoryIds
) {
}
