package com.mealio.recipe.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mealio.domain.model.MeasurementUnit;
import com.mealio.domain.model.Recipe;
import com.mealio.recipe.domain.exception.InvalidRecipeException;
import com.mealio.recipe.domain.exception.RecipeNotFoundException;
import com.mealio.recipe.domain.model.AddIngredientCommand;
import com.mealio.recipe.domain.model.CreateRecipeCommand;
import com.mealio.recipe.domain.port.out.RecipeRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RecipeServiceTest {
    private final RecipeRepository repository = mock(RecipeRepository.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-17T23:30:00Z"), ZoneOffset.UTC);
    private final RecipeService service = new RecipeService(repository, clock);

    @Test
    void normalizesRecipeAndUsesDateFromClockWhenCategoriesAreAbsent() {
        when(repository.findExistingCategoryIds(Set.of())).thenReturn(Set.of());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.create(new CreateRecipeCommand("  Rice  bowl ", null, null, null, null));

        assertThat(result.name()).isEqualTo("Rice bowl");
        assertThat(result.createdOn()).isEqualTo(LocalDate.of(2026, 10, 17));
        assertThat(result.categoryIds()).isEmpty();
        assertThat(result.description()).isNull();
    }

    @Test
    void rejectsMissingCategoryBeforeSavingRecipe() {
        var missing = UUID.randomUUID();
        when(repository.findExistingCategoryIds(Set.of(missing))).thenReturn(Set.of());

        assertThatThrownBy(() -> service.create(new CreateRecipeCommand("Rice", null, null, null, Set.of(missing))))
                .isInstanceOf(InvalidRecipeException.class).hasMessageContaining(missing.toString());
        verify(repository, never()).save(any());
    }

    @Test
    void listsNewestRecipesFirstThenNameAndNullDatesLast() {
        var day = LocalDate.of(2026, 10, 17);
        var older = recipe("Older", day.minusDays(1));
        var alpha = recipe("Alpha", day);
        var beta = recipe("Beta", day);
        var undated = recipe("Undated", null);
        when(repository.findAll()).thenReturn(List.of(undated, beta, older, alpha));

        assertThat(service.list()).containsExactly(alpha, beta, older, undated);
    }

    @Test
    void preservesIngredientPrecisionAndMissingCalories() {
        var recipe = recipe("Rice", LocalDate.now(clock));
        when(repository.findById(recipe.id())).thenReturn(Optional.of(recipe));
        when(repository.saveIngredient(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.addIngredient(new AddIngredientCommand(recipe.id(), "  Rice ",
                new BigDecimal("120.500"), MeasurementUnit.GRAM, null));

        assertThat(result.name()).isEqualTo("Rice");
        assertThat(result.quantity()).isEqualByComparingTo("120.500");
        assertThat(result.calories()).isNull();
        assertThat(result.recipeId()).isEqualTo(recipe.id());
    }

    @Test
    void missingRecipePreventsIngredientSave() {
        var id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.addIngredient(new AddIngredientCommand(id, "Rice",
                BigDecimal.ONE, MeasurementUnit.GRAM, null))).isInstanceOf(RecipeNotFoundException.class);
        verify(repository, never()).saveIngredient(any());
    }

    private Recipe recipe(String name, LocalDate date) {
        return new Recipe(UUID.randomUUID(), name, null, date, null, Set.of());
    }
}
