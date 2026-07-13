package com.mealio.category.domain.model;

import java.util.UUID;

public record PatchCategoryCommand(
        UUID id,
        FieldPatch<String> name,
        FieldPatch<UUID> parentId,
        FieldPatch<CategoryType> type,
        FieldPatch<Integer> sortOrder
) {
}
