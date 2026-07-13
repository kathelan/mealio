package com.mealio.category.domain.port.in;

import com.mealio.category.domain.model.CreateCategoryCommand;
import com.mealio.category.domain.model.MealCategory;
import com.mealio.category.domain.model.PatchCategoryCommand;
import java.util.List;
import java.util.UUID;

public interface ManageCategoriesUseCase {

    List<MealCategory> list();

    MealCategory get(UUID id);

    MealCategory create(CreateCategoryCommand command);

    MealCategory patch(PatchCategoryCommand command);

    void delete(UUID id);
}
