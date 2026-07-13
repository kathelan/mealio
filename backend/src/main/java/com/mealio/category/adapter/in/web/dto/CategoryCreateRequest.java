package com.mealio.category.adapter.in.web.dto;

import com.mealio.category.domain.model.CategoryType;
import com.mealio.category.domain.model.CreateCategoryCommand;
import java.util.UUID;

public record CategoryCreateRequest(
        UUID parentId,
        String name,
        CategoryType type,
        Integer sortOrder
) {

    public CreateCategoryCommand toCommand() {
        return new CreateCategoryCommand(parentId, name, type, sortOrder);
    }
}
