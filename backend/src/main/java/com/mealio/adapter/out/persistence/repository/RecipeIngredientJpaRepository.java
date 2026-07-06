package com.mealio.adapter.out.persistence.repository;

import com.mealio.adapter.out.persistence.entity.RecipeIngredientEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeIngredientJpaRepository extends JpaRepository<RecipeIngredientEntity, UUID> {
}
