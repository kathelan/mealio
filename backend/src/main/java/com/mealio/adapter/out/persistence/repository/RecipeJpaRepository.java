package com.mealio.adapter.out.persistence.repository;

import com.mealio.adapter.out.persistence.entity.RecipeEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeJpaRepository extends JpaRepository<RecipeEntity, UUID> {
}
