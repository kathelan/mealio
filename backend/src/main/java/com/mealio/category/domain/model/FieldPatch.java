package com.mealio.category.domain.model;

public record FieldPatch<T>(boolean present, T value) {

    public static <T> FieldPatch<T> missing() {
        return new FieldPatch<>(false, null);
    }

    public static <T> FieldPatch<T> of(T value) {
        return new FieldPatch<>(true, value);
    }
}
