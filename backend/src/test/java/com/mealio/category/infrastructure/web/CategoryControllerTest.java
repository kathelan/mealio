package com.mealio.category.infrastructure.web;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mealio.category.domain.exception.CategoryHasChildrenException;
import com.mealio.category.domain.exception.CategoryNotFoundException;
import com.mealio.category.domain.exception.CategoryPersistenceException;
import com.mealio.category.domain.exception.DuplicateCategoryException;
import com.mealio.category.domain.exception.InvalidCategoryException;
import com.mealio.category.domain.model.CategoryType;
import com.mealio.category.domain.model.MealCategory;
import com.mealio.category.domain.port.in.ManageCategoriesUseCase;
import com.mealio.category.infrastructure.web.dto.CategoryCreateRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CategoryControllerTest {
    @Test
    void mapsRequestsResponsesAndCreatedLocation() {
        var useCase = mock(ManageCategoriesUseCase.class);
        var category = new MealCategory(UUID.randomUUID(), null, "Lunch", "lunch", "lunch",
                CategoryType.MEAL, 1, Instant.EPOCH, Instant.EPOCH);
        when(useCase.list()).thenReturn(List.of(category));
        when(useCase.get(category.id())).thenReturn(category);
        when(useCase.create(any())).thenReturn(category);
        when(useCase.patch(any())).thenReturn(category);
        var controller = new CategoryController(useCase);
        assertThat(controller.list()).extracting(value -> value.name()).containsExactly("Lunch");
        assertThat(controller.get(category.id()).id()).isEqualTo(category.id());
        var created = controller.create(new CategoryCreateRequest(null, "Lunch", CategoryType.MEAL, 1));
        assertThat(created.getStatusCode().value()).isEqualTo(201);
        assertThat(created.getHeaders().getLocation()).hasPath("/api/v1/categories/" + category.id());
        assertThat(created.getBody().name()).isEqualTo("Lunch");
        assertThat(controller.patch(category.id(), Map.of("name", "Lunch")).name()).isEqualTo("Lunch");
        assertThat(controller.delete(category.id()).getStatusCode().value()).isEqualTo(204);
        verify(useCase).delete(category.id());
        var command = org.mockito.ArgumentCaptor.forClass(com.mealio.category.domain.model.CreateCategoryCommand.class);
        verify(useCase).create(command.capture());
        assertThat(command.getValue().name()).isEqualTo("Lunch");
    }

    @Test
    void handlersExposeStableCodesMessagesAndTimestamp() {
        var handler = new CategoryExceptionHandler(Clock.fixed(Instant.EPOCH, ZoneOffset.UTC));
        var id = UUID.randomUUID();
        assertThat(handler.handleNotFound(new CategoryNotFoundException(id)).code()).isEqualTo("CATEGORY_NOT_FOUND");
        assertThat(handler.handleDuplicate(new DuplicateCategoryException("path")).code())
                .isEqualTo("CATEGORY_ALREADY_EXISTS");
        assertThat(handler.handleHasChildren(new CategoryHasChildrenException(id)).code()).isEqualTo("CATEGORY_HAS_CHILDREN");
        assertThat(handler.handleBadRequest(new InvalidCategoryException("bad")).message()).isEqualTo("bad");
        assertThat(handler.handlePersistence(new CategoryPersistenceException("db", null)).code())
                .isEqualTo("CATEGORY_PERSISTENCE_ERROR");
        assertThat(handler.handlePersistence(new CategoryPersistenceException("db", null)).timestamp()).isEqualTo(Instant.EPOCH);
    }
}
