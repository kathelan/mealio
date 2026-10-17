package com.mealio.product.api;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException() { super("Product or requested version was not found"); }
}
