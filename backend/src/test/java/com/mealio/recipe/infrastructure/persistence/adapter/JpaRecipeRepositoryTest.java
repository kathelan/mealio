package com.mealio.recipe.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mealio.recipe.domain.exception.RecipePersistenceException;
import com.mealio.recipe.domain.model.MeasurementUnit;
import com.mealio.recipe.domain.model.Recipe;
import com.mealio.recipe.domain.model.RecipeIngredient;
import com.mealio.recipe.infrastructure.persistence.entity.RecipeEntity;
import com.mealio.recipe.infrastructure.persistence.entity.RecipeIngredientEntity;
import com.mealio.recipe.infrastructure.persistence.repository.RecipeIngredientJpaRepository;
import com.mealio.recipe.infrastructure.persistence.repository.RecipeJpaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;

class JpaRecipeRepositoryTest {
    private final RecipeJpaRepository recipes = mock(RecipeJpaRepository.class);
    private final RecipeIngredientJpaRepository ingredients = mock(RecipeIngredientJpaRepository.class);
    private final JpaRecipeRepository repository = new JpaRecipeRepository(recipes, ingredients);
    private final UUID id = UUID.randomUUID();

    private RecipeEntity entity() {
        var entity = new RecipeEntity();
        entity.setId(id);
        entity.setName("Rice");
        entity.setDescription("description");
        entity.setCreatedOn(LocalDate.of(2026, 10, 17));
        entity.setImageUrl("image");
        entity.setCategoryIds(Set.of(id));
        return entity;
    }

    @Test
    void mapsRecipeAndIngredientPersistenceWithoutLosingFields() {
        var entity = entity();
        var recipe = new Recipe(id, "Rice", "description", entity.getCreatedOn(), "image", Set.of(id));
        when(recipes.findAll()).thenReturn(List.of(entity));
        when(recipes.findById(id)).thenReturn(Optional.of(entity));
        when(recipes.saveAndFlush(any())).thenAnswer(call -> {
            RecipeEntity saved = call.getArgument(0);
            saved.setId(id);
            return saved;
        });
        when(recipes.getReferenceById(id)).thenReturn(entity);
        when(ingredients.saveAndFlush(any())).thenAnswer(call -> {
            RecipeIngredientEntity saved = call.getArgument(0);
            saved.setId(id);
            return saved;
        });
        assertThat(repository.findAll()).containsExactly(recipe);
        assertThat(repository.findById(id)).contains(recipe);
        assertThat(repository.save(recipe)).isEqualTo(recipe);
        var ingredient = new RecipeIngredient(id, id, "Rice", BigDecimal.ONE, MeasurementUnit.GRAM, BigDecimal.TEN);
        assertThat(repository.saveIngredient(ingredient)).isEqualTo(ingredient);
        var stored = new RecipeIngredientEntity();
        stored.setId(id);
        stored.setRecipe(entity);
        stored.setName("Rice");
        stored.setQuantity(BigDecimal.ONE);
        stored.setUnit("g");
        stored.setCalories(BigDecimal.TEN);
        when(ingredients.findByRecipe_IdOrderByCreatedAtAscIdAsc(id)).thenReturn(List.of(stored));
        assertThat(repository.findIngredients(id)).containsExactly(ingredient);
        stored.setUnit("unknown");
        assertThatThrownBy(() -> repository.findIngredients(id)).isInstanceOf(RecipePersistenceException.class)
                .hasMessageContaining("Unknown persisted ingredient unit");
    }

    @Test
    void wrapsEveryDatabaseFailure() {
        var failure = new DataAccessResourceFailureException("offline");
        when(recipes.findAll()).thenThrow(failure);
        when(recipes.findById(any())).thenThrow(failure);
        when(recipes.saveAndFlush(any())).thenThrow(failure);
        when(ingredients.saveAndFlush(any())).thenThrow(failure);
        when(ingredients.findByRecipe_IdOrderByCreatedAtAscIdAsc(any())).thenThrow(failure);
        var recipe = new Recipe(id, "Rice", null, null, null, Set.of());
        var ingredient = new RecipeIngredient(id, id, "Rice", BigDecimal.ONE, MeasurementUnit.GRAM, null);
        for (Runnable operation : List.<Runnable>of(() -> repository.findAll(), () -> repository.findById(id),
                () -> repository.save(recipe), () -> repository.saveIngredient(ingredient), () -> repository.findIngredients(id))) {
            assertThatThrownBy(operation::run).isInstanceOf(RecipePersistenceException.class).hasCause(failure);
        }
    }
}
