package com.mealio.product.domain;

public record Gtin(String value) {
    public static Gtin of(String value) { return new Gtin(value); }

    public Gtin {
        value = java.util.Optional.ofNullable(value).orElseThrow(() -> new InvalidProductException("GTIN is required"));
        if (!value.trim().matches("(?:[0-9]{8}|[0-9]{12}|[0-9]{13}|[0-9]{14})")) {
            throw new InvalidProductException("GTIN must contain 8, 12, 13 or 14 digits");
        }
        value = value.trim();
        int sum = 0;
        for (int index = value.length() - 2, weight = 3; index >= 0; index--, weight = 4 - weight) {
            sum += (value.charAt(index) - '0') * weight;
        }
        if ((10 - sum % 10) % 10 != value.charAt(value.length() - 1) - '0') {
            throw new InvalidProductException("Invalid GTIN check digit");
        }
        value = "0".repeat(14 - value.length()) + value;
    }
}
