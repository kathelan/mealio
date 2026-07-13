package com.mealio.adapter.out.persistence.repository;

import com.mealio.adapter.out.persistence.entity.MealCategoryEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealCategoryJpaRepository extends JpaRepository<MealCategoryEntity, UUID> {

    Optional<MealCategoryEntity> findByPath(String path);

    List<MealCategoryEntity> findByPathStartingWith(String pathPrefix);

    boolean existsByParent_Id(UUID parentId);
}
