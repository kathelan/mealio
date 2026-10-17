package com.mealio.recipe.infrastructure.web;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mealio.recipe.domain.model.MeasurementUnit;
import com.mealio.recipe.domain.model.Recipe;
import com.mealio.recipe.domain.model.RecipeDetails;
import com.mealio.recipe.domain.model.RecipeIngredient;
import com.mealio.recipe.domain.port.in.ManageRecipesUseCase;
import com.mealio.recipe.infrastructure.web.dto.IngredientCreateRequest;
import com.mealio.recipe.infrastructure.web.dto.RecipeCreateRequest;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RecipeControllerTest {
    @Test
    void mapsRecipeAndIngredientRequestsResponsesAndLocations() {
        var useCase = mock(ManageRecipesUseCase.class);
        var recipe = new Recipe(UUID.randomUUID(), "Rice", "description", LocalDate.of(2026, 10, 17), "image", Set.of());
        var ingredient = new RecipeIngredient(UUID.randomUUID(), recipe.id(), "Rice", BigDecimal.ONE,
                MeasurementUnit.GRAM, BigDecimal.TEN);
        when(useCase.list()).thenReturn(List.of(recipe));
        when(useCase.get(recipe.id())).thenReturn(new RecipeDetails(recipe, List.of(ingredient)));
        when(useCase.create(any())).thenReturn(recipe);
        when(useCase.addIngredient(any())).thenReturn(ingredient);
        var controller = new RecipeController(useCase);
        assertThat(controller.list()).extracting(value -> value.id()).containsExactly(recipe.id());
        var details = controller.get(recipe.id());
        assertThat(details.name()).isEqualTo("Rice");
        assertThat(details.ingredients()).extracting(value -> value.id()).containsExactly(ingredient.id());
        var created = controller.create(new RecipeCreateRequest("Rice", "description", recipe.createdOn(), "image", Set.of()));
        assertThat(created.getStatusCode().value()).isEqualTo(201);
        assertThat(created.getHeaders().getLocation()).hasPath("/api/v1/recipes/" + recipe.id());
        assertThat(created.getBody().name()).isEqualTo("Rice");
        var added = controller.addIngredient(recipe.id(), new IngredientCreateRequest("Rice", BigDecimal.ONE, "g", BigDecimal.TEN));
        assertThat(added.getStatusCode().value()).isEqualTo(201);
        assertThat(added.getHeaders().getLocation()).hasPath("/api/v1/recipes/" + recipe.id() + "/ingredients/" + ingredient.id());
        assertThat(added.getBody().unit()).isEqualTo("g");
        var deleted = controller.delete(recipe.id());
        assertThat(deleted.getStatusCode().value()).isEqualTo(204);
        assertThat(deleted.getBody()).isNull();
        verify(useCase).delete(recipe.id());
        var command = org.mockito.ArgumentCaptor.forClass(com.mealio.recipe.domain.model.AddIngredientCommand.class);
        verify(useCase).addIngredient(command.capture());
        assertThat(command.getValue().recipeId()).isEqualTo(recipe.id());
    }

    @Test
    void ingredientUnitsAcceptCodesAndNamesAndRejectUnknownValues() {
        for (var unit : MeasurementUnit.values()) {
            assertThat(new IngredientCreateRequest("a", BigDecimal.ONE, unit.code(), null).toCommand(null).unit()).isEqualTo(unit);
            assertThat(new IngredientCreateRequest("a", BigDecimal.ONE, unit.name(), null).toCommand(null).unit()).isEqualTo(unit);
        }
        assertThat(new IngredientCreateRequest("a", BigDecimal.ONE, null, null).toCommand(null).unit()).isNull();
        assertThatThrownBy(() -> new IngredientCreateRequest("a", BigDecimal.ONE, "bad", null).toCommand(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void mapsErrorsToStableCodesAndTime() {
        var handler = new RecipeExceptionHandler(Clock.fixed(Instant.EPOCH, ZoneOffset.UTC));
        var error = new IllegalArgumentException("failure");
        assertThat(handler.notFound(error).code()).isEqualTo("RECIPE_NOT_FOUND");
        assertThat(handler.invalid(error).code()).isEqualTo("INVALID_RECIPE_REQUEST");
        assertThat(handler.persistence(error).code()).isEqualTo("RECIPE_PERSISTENCE_ERROR");
        assertThat(handler.invalid(error).message()).isEqualTo("failure");
        assertThat(handler.invalid(error).timestamp()).isEqualTo(Instant.EPOCH);
    }
}
