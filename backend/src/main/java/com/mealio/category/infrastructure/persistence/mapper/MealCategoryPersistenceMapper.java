package com.mealio.category.infrastructure.persistence.mapper;

import com.mealio.category.infrastructure.persistence.entity.MealCategoryEntity;
import com.mealio.category.domain.model.MealCategory;
import org.springframework.stereotype.Component;

@Component
public class MealCategoryPersistenceMapper {

    public void copyToEntity(MealCategory category, MealCategoryEntity entity, MealCategoryEntity parent) {
        entity.setId(category.id());
        entity.setParent(parent);
        entity.setName(category.name());
        entity.setSlug(category.slug());
        entity.setPath(category.path());
        entity.setType(category.type());
        entity.setSortOrder(category.sortOrder());
        entity.setCreatedAt(category.createdAt());
        entity.setUpdatedAt(category.updatedAt());
    }

    public MealCategory toDomain(MealCategoryEntity entity) {
        return new MealCategory(
                entity.getId(),
                entity.getParent() == null ? null : entity.getParent().getId(),
                entity.getName(),
                entity.getSlug(),
                entity.getPath(),
                entity.getType(),
                entity.getSortOrder(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
