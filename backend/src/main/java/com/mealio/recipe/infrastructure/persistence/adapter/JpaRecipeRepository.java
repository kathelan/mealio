package com.mealio.recipe.infrastructure.persistence.adapter;

import com.mealio.category.infrastructure.persistence.entity.MealCategoryEntity;
import com.mealio.adapter.out.persistence.entity.RecipeEntity;
import com.mealio.adapter.out.persistence.entity.RecipeIngredientEntity;
import com.mealio.category.infrastructure.persistence.repository.MealCategoryJpaRepository;
import com.mealio.adapter.out.persistence.repository.RecipeIngredientJpaRepository;
import com.mealio.adapter.out.persistence.repository.RecipeJpaRepository;
import com.mealio.domain.model.MeasurementUnit;
import com.mealio.domain.model.Recipe;
import com.mealio.domain.model.RecipeIngredient;
import com.mealio.recipe.domain.exception.RecipePersistenceException;
import com.mealio.recipe.domain.port.out.RecipeRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaRecipeRepository implements RecipeRepository {
    private final RecipeJpaRepository recipes;
    private final RecipeIngredientJpaRepository ingredients;
    private final MealCategoryJpaRepository categories;

    @Override
    public List<Recipe> findAll() {
        try { return recipes.findAll().stream().map(this::toDomain).toList(); }
        catch (DataAccessException e) { throw failure("Cannot load recipes", e); }
    }

    @Override
    public Optional<Recipe> findById(UUID id) {
        try { return recipes.findById(id).map(this::toDomain); }
        catch (DataAccessException e) { throw failure("Cannot load recipe", e); }
    }

    @Override
    public Recipe save(Recipe recipe) {
        try {
            var entity = new RecipeEntity();
            entity.setName(recipe.name());
            entity.setDescription(recipe.description());
            entity.setCreatedOn(recipe.createdOn());
            entity.setImageUrl(recipe.imageUrl());
            entity.setCategories(new LinkedHashSet<>(categories.findAllById(recipe.categoryIds())));
            return toDomain(recipes.saveAndFlush(entity));
        } catch (DataAccessException e) { throw failure("Cannot save recipe", e); }
    }

    @Override
    public RecipeIngredient saveIngredient(RecipeIngredient ingredient) {
        try {
            var entity = new RecipeIngredientEntity();
            entity.setRecipe(recipes.getReferenceById(ingredient.recipeId()));
            entity.setName(ingredient.name());
            entity.setQuantity(ingredient.quantity());
            entity.setUnit(ingredient.unit().code());
            entity.setCalories(ingredient.calories());
            return toDomain(ingredients.saveAndFlush(entity));
        } catch (DataAccessException e) { throw failure("Cannot save recipe ingredient", e); }
    }

    @Override
    public List<RecipeIngredient> findIngredients(UUID recipeId) {
        try { return ingredients.findByRecipe_IdOrderByCreatedAtAscIdAsc(recipeId).stream().map(this::toDomain).toList(); }
        catch (DataAccessException e) { throw failure("Cannot load recipe ingredients", e); }
    }

    @Override
    public Set<UUID> findExistingCategoryIds(Set<UUID> categoryIds) {
        try {
            return categories.findAllById(categoryIds).stream().map(MealCategoryEntity::getId)
                    .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
        } catch (DataAccessException e) { throw failure("Cannot validate recipe categories", e); }
    }

    private Recipe toDomain(RecipeEntity entity) {
        var categoryIds = entity.getCategories().stream().map(MealCategoryEntity::getId)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
        return new Recipe(entity.getId(), entity.getName(), entity.getDescription(), entity.getCreatedOn(),
                entity.getImageUrl(), categoryIds);
    }

    private RecipeIngredient toDomain(RecipeIngredientEntity entity) {
        return new RecipeIngredient(entity.getId(), entity.getRecipe().getId(), entity.getName(), entity.getQuantity(),
                unit(entity.getUnit()), entity.getCalories());
    }

    private MeasurementUnit unit(String code) {
        return java.util.Arrays.stream(MeasurementUnit.values()).filter(unit -> unit.code().equals(code)).findFirst()
                .orElseThrow(() -> new RecipePersistenceException("Unknown persisted ingredient unit: " + code, null));
    }

    private RecipePersistenceException failure(String message, Exception cause) {
        return new RecipePersistenceException(message, cause);
    }
}
