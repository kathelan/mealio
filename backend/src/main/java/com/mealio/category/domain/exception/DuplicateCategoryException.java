package com.mealio.category.domain.exception;

public class DuplicateCategoryException extends CategoryException {

    public DuplicateCategoryException(String path) {
        super("Category already exists at path: " + path);
    }
}
