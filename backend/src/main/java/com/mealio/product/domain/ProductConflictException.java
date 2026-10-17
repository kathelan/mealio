package com.mealio.product.domain;

public class ProductConflictException extends RuntimeException {
    public ProductConflictException(String message) { super(message); }
}
