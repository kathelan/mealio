package com.mealio.recipe.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mealio.category.api.CategoryLookup;
import com.mealio.category.api.CategoryLookupUnavailableException;
import com.mealio.recipe.domain.exception.RecipePersistenceException;
import com.mealio.recipe.domain.model.MeasurementUnit;
import com.mealio.recipe.domain.model.Recipe;
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
    private final CategoryLookup categoryLookup = mock(CategoryLookup.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-17T23:30:00Z"), ZoneOffset.UTC);
    private final RecipeService service = new RecipeService(repository, clock, categoryLookup);

    @Test
    void normalizesRecipeAndUsesDateFromClockWhenCategoriesAreAbsent() {
        when(categoryLookup.findExistingIds(Set.of())).thenReturn(Set.of());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.create(new CreateRecipeCommand("  Rice  bowl ", null, null, null, null));

        assertThat(result.name()).isEqualTo("Rice bowl");
        assertThat(result.createdOn()).isEqualTo(LocalDate.of(2026, 10, 17));
        assertThat(result.categoryIds()).isEmpty();
        assertThat(result.description()).isNull();
    }

    @Test
    void mapsCategoryLookupFailureToExistingRecipePersistenceError() {
        var ids = Set.of(UUID.randomUUID());
        var failure = new CategoryLookupUnavailableException(new IllegalStateException());
        when(categoryLookup.findExistingIds(ids)).thenThrow(failure);

        assertThatThrownBy(() -> service.create(new CreateRecipeCommand("Rice", null, null, null, ids)))
                .isInstanceOf(RecipePersistenceException.class)
                .hasMessage("Cannot validate recipe categories").hasCause(failure);
        verify(repository, never()).save(any());
    }

    @Test
    void rejectsMissingCategoryBeforeSavingRecipe() {
        var missing = UUID.randomUUID();
        when(categoryLookup.findExistingIds(Set.of(missing))).thenReturn(Set.of());

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

    @Test
    void deletesExistingRecipe() {
        var recipe = recipe("Rice", LocalDate.now(clock));
        when(repository.findById(recipe.id())).thenReturn(Optional.of(recipe));

        service.delete(recipe.id());

        verify(repository).deleteById(recipe.id());
    }

    @Test
    void missingRecipePreventsDeletion() {
        var id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(id)).isInstanceOf(RecipeNotFoundException.class);

        verify(repository, never()).deleteById(any());
    }

    @Test
    void validatesAllRecipeAndIngredientBoundaries() {
        assertThatThrownBy(() -> service.create(null)).isInstanceOf(InvalidRecipeException.class);
        for (var command : List.of(new CreateRecipeCommand(" ", null, null, null, null),
                new CreateRecipeCommand("x".repeat(221), null, null, null, null),
                new CreateRecipeCommand("Rice", "x".repeat(2049), null, null, null),
                new CreateRecipeCommand("Rice", null, null, "x".repeat(1025), null),
                new CreateRecipeCommand("Rice", null, null, null,
                        new java.util.HashSet<>(java.util.Arrays.asList((UUID) null))))) {
            assertThatThrownBy(() -> service.create(command)).isInstanceOf(InvalidRecipeException.class);
        }
        assertThatThrownBy(() -> service.addIngredient(null)).isInstanceOf(InvalidRecipeException.class);
        assertThatThrownBy(() -> service.addIngredient(new AddIngredientCommand(null, "Rice", BigDecimal.ONE,
                MeasurementUnit.GRAM, null))).isInstanceOf(InvalidRecipeException.class);
        var recipe = recipe("Rice", LocalDate.now(clock));
        when(repository.findById(recipe.id())).thenReturn(Optional.of(recipe));
        for (var command : List.of(new AddIngredientCommand(recipe.id(), "Rice", null, MeasurementUnit.GRAM, null),
                new AddIngredientCommand(recipe.id(), "Rice", BigDecimal.ZERO, MeasurementUnit.GRAM, null),
                new AddIngredientCommand(recipe.id(), "Rice", new BigDecimal("0.0001"), MeasurementUnit.GRAM, null),
                new AddIngredientCommand(recipe.id(), "Rice", new BigDecimal("1000000000"), MeasurementUnit.GRAM, null),
                new AddIngredientCommand(recipe.id(), "Rice", BigDecimal.ONE, null, null),
                new AddIngredientCommand(recipe.id(), "Rice", BigDecimal.ONE, MeasurementUnit.GRAM, BigDecimal.ONE.negate()),
                new AddIngredientCommand(recipe.id(), "Rice", BigDecimal.ONE, MeasurementUnit.GRAM, new BigDecimal("0.001")),
                new AddIngredientCommand(recipe.id(), "Rice", BigDecimal.ONE, MeasurementUnit.GRAM, new BigDecimal("100000000")))) {
            assertThatThrownBy(() -> service.addIngredient(command)).isInstanceOf(InvalidRecipeException.class);
        }
        verify(repository, never()).saveIngredient(any());
    }

    @Test
    void mapsDetailsAndPreservesExplicitDateOptionalTextAndCalories() {
        var date = LocalDate.of(2026, 10, 17);
        when(categoryLookup.findExistingIds(Set.of())).thenReturn(Set.of());
        when(repository.save(any())).thenAnswer(call -> call.getArgument(0));
        var result = service.create(new CreateRecipeCommand("Rice", " description ", date, " image ", Set.of()));
        assertThat(result.description()).isEqualTo("description");
        assertThat(result.imageUrl()).isEqualTo("image");
        assertThat(result.createdOn()).isEqualTo(date);
        var recipe = recipe("Rice", date);
        when(repository.findById(recipe.id())).thenReturn(Optional.of(recipe));
        when(repository.findIngredients(recipe.id())).thenReturn(List.of());
        assertThat(service.get(recipe.id()).recipe()).isEqualTo(recipe);
        assertThat(service.get(recipe.id()).ingredients()).isEmpty();
        assertThatThrownBy(() -> service.get(UUID.randomUUID())).isInstanceOf(RecipeNotFoundException.class);
        when(repository.saveIngredient(any())).thenAnswer(call -> call.getArgument(0));
        assertThat(service.addIngredient(new AddIngredientCommand(recipe.id(), "Rice", BigDecimal.ONE,
                MeasurementUnit.GRAM, BigDecimal.TEN)).calories()).isEqualByComparingTo("10");
    }

    private Recipe recipe(String name, LocalDate date) {
        return new Recipe(UUID.randomUUID(), name, null, date, null, Set.of());
    }
}
