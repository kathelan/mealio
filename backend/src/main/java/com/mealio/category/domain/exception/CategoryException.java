package com.mealio.category.domain.exception;

public abstract class CategoryException extends RuntimeException {

    protected CategoryException(String message) {
        super(message);
    }

    protected CategoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
