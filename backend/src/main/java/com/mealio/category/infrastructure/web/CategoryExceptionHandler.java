package com.mealio.category.infrastructure.web;

import com.mealio.category.infrastructure.web.dto.ApiErrorResponse;
import com.mealio.category.domain.exception.CategoryHasChildrenException;
import com.mealio.category.domain.exception.CategoryNotFoundException;
import com.mealio.category.domain.exception.CategoryPersistenceException;
import com.mealio.category.domain.exception.DuplicateCategoryException;
import com.mealio.category.domain.exception.InvalidCategoryException;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(assignableTypes = CategoryController.class)
@RequiredArgsConstructor
public class CategoryExceptionHandler {

    private final Clock clock;

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiErrorResponse handleNotFound(CategoryNotFoundException exception) {
        return error("CATEGORY_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(DuplicateCategoryException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ApiErrorResponse handleDuplicate(DuplicateCategoryException exception) {
        return error("CATEGORY_ALREADY_EXISTS", exception.getMessage());
    }

    @ExceptionHandler(CategoryHasChildrenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ApiErrorResponse handleHasChildren(CategoryHasChildrenException exception) {
        return error("CATEGORY_HAS_CHILDREN", exception.getMessage());
    }

    @ExceptionHandler({
            InvalidCategoryException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiErrorResponse handleBadRequest(Exception exception) {
        return error("INVALID_CATEGORY_REQUEST", exception.getMessage());
    }

    @ExceptionHandler(CategoryPersistenceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ApiErrorResponse handlePersistence(CategoryPersistenceException exception) {
        return error("CATEGORY_PERSISTENCE_ERROR", exception.getMessage());
    }

    private ApiErrorResponse error(String code, String message) {
        return new ApiErrorResponse(code, message, Instant.now(clock));
    }
}
