package com.mealio.adapter.out.persistence.repository;

import com.mealio.adapter.out.persistence.entity.MealCategoryEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealCategoryJpaRepository extends JpaRepository<MealCategoryEntity, UUID> {
}
