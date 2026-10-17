package com.mealio.product.infrastructure.web;

import com.mealio.audit.HttpAuditContext;
import com.mealio.product.api.ProductNotFoundException;
import com.mealio.product.api.ProductUnavailableException;
import com.mealio.product.domain.InvalidProductException;
import com.mealio.product.domain.ProductConflictException;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(assignableTypes = ProductController.class)
@RequiredArgsConstructor
public class ProductExceptionHandler {
    private final Clock clock;

    @lombok.Builder
    public record ProductError(String code, String message, Instant timestamp, String requestId) {}

    @ExceptionHandler({InvalidProductException.class, IllegalArgumentException.class,
            HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ProductError invalid(Exception exception) { return error("INVALID_PRODUCT_REQUEST", "Invalid product request"); }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ProductError notFound() { return error("PRODUCT_NOT_FOUND", "Product or requested version was not found"); }

    @ExceptionHandler(ProductConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ProductError conflict() { return error("PRODUCT_CONFLICT", "GTIN already exists or product version has changed"); }

    @ExceptionHandler(ProductUnavailableException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ProductError unavailable() { return error("PRODUCT_PERSISTENCE_ERROR", "Product catalog is unavailable"); }

    private ProductError error(String code, String message) {
        return ProductError.builder().code(code).message(message).timestamp(Instant.now(clock))
                .requestId(HttpAuditContext.requestId().orElseGet(() -> UUID.randomUUID().toString())).build();
    }
}
