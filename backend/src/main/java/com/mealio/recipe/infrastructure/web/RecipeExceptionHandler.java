package com.mealio.recipe.infrastructure.web;

import com.mealio.recipe.infrastructure.web.dto.ApiErrorResponse;
import com.mealio.recipe.domain.exception.InvalidRecipeException;
import com.mealio.recipe.domain.exception.RecipeNotFoundException;
import com.mealio.recipe.domain.exception.RecipePersistenceException;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(assignableTypes = RecipeController.class)
@RequiredArgsConstructor
public class RecipeExceptionHandler {
    private final Clock clock;

    @ExceptionHandler(RecipeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiErrorResponse notFound(Exception e) { return error("RECIPE_NOT_FOUND", e); }

    @ExceptionHandler({InvalidRecipeException.class, IllegalArgumentException.class,
            HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiErrorResponse invalid(Exception e) { return error("INVALID_RECIPE_REQUEST", e); }

    @ExceptionHandler(RecipePersistenceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ApiErrorResponse persistence(Exception e) { return error("RECIPE_PERSISTENCE_ERROR", e); }

    private ApiErrorResponse error(String code, Exception e) {
        return new ApiErrorResponse(code, e.getMessage(), Instant.now(clock));
    }
}
