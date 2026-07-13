package com.mealio.category.adapter.in.web.dto;

import com.mealio.category.domain.exception.InvalidCategoryException;
import com.mealio.category.domain.model.CategoryType;
import com.mealio.category.domain.model.FieldPatch;
import com.mealio.category.domain.model.PatchCategoryCommand;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record CategoryPatchRequest(
        FieldPatch<String> name,
        FieldPatch<UUID> parentId,
        FieldPatch<CategoryType> type,
        FieldPatch<Integer> sortOrder
) {

    private static final Set<String> SUPPORTED_FIELDS = Set.of("name", "parentId", "type", "sortOrder");

    public static CategoryPatchRequest from(Map<String, Object> fields) {
        if (fields == null) {
            throw new InvalidCategoryException("Category patch payload must be a JSON object");
        }
        if (fields.isEmpty()) {
            throw new InvalidCategoryException("Category patch payload must contain at least one supported field");
        }
        var unsupportedFields = fields.keySet().stream()
                .filter(field -> !SUPPORTED_FIELDS.contains(field))
                .toList();
        if (!unsupportedFields.isEmpty()) {
            throw new InvalidCategoryException("Unsupported category patch fields: " + unsupportedFields);
        }
        return new CategoryPatchRequest(
                stringPatch(fields, "name"),
                uuidPatch(fields, "parentId"),
                categoryTypePatch(fields, "type"),
                integerPatch(fields, "sortOrder")
        );
    }

    public PatchCategoryCommand toCommand(UUID id) {
        return new PatchCategoryCommand(id, name, parentId, type, sortOrder);
    }

    private static FieldPatch<String> stringPatch(Map<String, Object> fields, String field) {
        if (!fields.containsKey(field)) {
            return FieldPatch.missing();
        }
        var value = fields.get(field);
        if (value == null) {
            return FieldPatch.of(null);
        }
        if (!(value instanceof String text)) {
            throw new InvalidCategoryException(field + " must be a string");
        }
        return FieldPatch.of(text);
    }

    private static FieldPatch<UUID> uuidPatch(Map<String, Object> fields, String field) {
        if (!fields.containsKey(field)) {
            return FieldPatch.missing();
        }
        var value = fields.get(field);
        if (value == null) {
            return FieldPatch.of(null);
        }
        if (!(value instanceof String text)) {
            throw new InvalidCategoryException(field + " must be a UUID string or null");
        }
        try {
            return FieldPatch.of(UUID.fromString(text));
        } catch (IllegalArgumentException exception) {
            throw new InvalidCategoryException(field + " must be a valid UUID");
        }
    }

    private static FieldPatch<CategoryType> categoryTypePatch(Map<String, Object> fields, String field) {
        if (!fields.containsKey(field)) {
            return FieldPatch.missing();
        }
        var value = fields.get(field);
        if (value == null) {
            return FieldPatch.of(null);
        }
        if (!(value instanceof String text)) {
            throw new InvalidCategoryException(field + " must be a string");
        }
        try {
            return FieldPatch.of(CategoryType.valueOf(text));
        } catch (IllegalArgumentException exception) {
            throw new InvalidCategoryException(field + " must be one of MEAL, TASTE, CUSTOM");
        }
    }

    private static FieldPatch<Integer> integerPatch(Map<String, Object> fields, String field) {
        if (!fields.containsKey(field)) {
            return FieldPatch.missing();
        }
        var value = fields.get(field);
        if (value == null) {
            return FieldPatch.of(null);
        }
        if (!(value instanceof Number number)) {
            throw new InvalidCategoryException(field + " must be an integer");
        }
        return FieldPatch.of(toExactInteger(field, number));
    }

    private static int toExactInteger(String field, Number number) {
        try {
            return switch (number) {
                case Integer value -> value;
                case Short value -> value.intValue();
                case Byte value -> value.intValue();
                case Long value -> Math.toIntExact(value);
                case BigInteger value -> value.intValueExact();
                case BigDecimal value -> value.intValueExact();
                default -> throw new ArithmeticException();
            };
        } catch (ArithmeticException exception) {
            throw new InvalidCategoryException(field + " must be an integer within range");
        }
    }
}
