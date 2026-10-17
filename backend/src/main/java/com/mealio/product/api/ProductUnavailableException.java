package com.mealio.product.api;

public class ProductUnavailableException extends RuntimeException {
    public ProductUnavailableException(Throwable cause) { super("Product catalog is unavailable", cause); }
}
