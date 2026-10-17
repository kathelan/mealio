package com.mealio.product.domain;

import java.util.Objects;
import java.util.UUID;

@lombok.Builder
public record ProductVersion(UUID id, int number, ProductDraft details, ProductSourceSnapshot source) {
    public ProductVersion {
        Objects.requireNonNull(id);
        if (number < 1) throw new InvalidProductException("Version must be positive");
        Objects.requireNonNull(details);
        Objects.requireNonNull(source);
    }
}
