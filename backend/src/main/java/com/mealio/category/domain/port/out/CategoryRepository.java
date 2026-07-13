package com.mealio.category.domain.port.out;

import com.mealio.category.domain.model.MealCategory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

    List<MealCategory> findAll();

    Optional<MealCategory> findById(UUID id);

    Optional<MealCategory> findByPath(String path);

    List<MealCategory> findByPathPrefix(String pathPrefix);

    boolean existsByParentId(UUID parentId);

    MealCategory save(MealCategory category);

    List<MealCategory> saveAll(List<MealCategory> categories);

    void deleteById(UUID id);
}
