package com.mealio.recipe.infrastructure.persistence.repository;

import com.mealio.recipe.infrastructure.persistence.entity.RecipeEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeJpaRepository extends JpaRepository<RecipeEntity, UUID> {
}
