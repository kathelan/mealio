package com.mealio.recipe.infrastructure.persistence.adapter;

import com.mealio.recipe.infrastructure.persistence.entity.RecipeEntity;
import com.mealio.recipe.infrastructure.persistence.entity.RecipeIngredientEntity;
import com.mealio.recipe.infrastructure.persistence.repository.RecipeIngredientJpaRepository;
import com.mealio.recipe.infrastructure.persistence.repository.RecipeJpaRepository;
import com.mealio.recipe.domain.model.MeasurementUnit;
import com.mealio.recipe.domain.model.Recipe;
import com.mealio.recipe.domain.model.RecipeIngredient;
import com.mealio.recipe.domain.exception.RecipePersistenceException;
import com.mealio.recipe.domain.port.out.RecipeRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaRecipeRepository implements RecipeRepository {
    private final RecipeJpaRepository recipes;
    private final RecipeIngredientJpaRepository ingredients;

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
            entity.setCategoryIds(new LinkedHashSet<>(recipe.categoryIds()));
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

    private Recipe toDomain(RecipeEntity entity) {
        var categoryIds = new LinkedHashSet<>(entity.getCategoryIds());
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
