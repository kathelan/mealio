package com.mealio.product.domain;

import java.util.Optional;
import lombok.Builder;

@Builder
public record ProductDraft(String name, Optional<String> brand, NutritionFacts nutrition) {
    public ProductDraft {
        name = Optional.ofNullable(name).filter(value -> !value.isBlank())
                .orElseThrow(() -> new InvalidProductException("Product name is required")).trim().replaceAll("\\s+", " ");
        if (name.length() > 220) throw new InvalidProductException("Product name must be at most 220 characters");
        brand = Optional.ofNullable(brand).orElseGet(Optional::empty).map(String::trim).filter(value -> !value.isBlank());
        brand.ifPresent(value -> {
            if (value.length() > 160) throw new InvalidProductException("Brand must be at most 160 characters");
        });
        nutrition = Optional.ofNullable(nutrition).orElseThrow(() -> new InvalidProductException("Nutrition facts are required"));
    }
}
