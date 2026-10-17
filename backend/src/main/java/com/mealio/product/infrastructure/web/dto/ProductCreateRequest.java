package com.mealio.product.infrastructure.web.dto;

import com.mealio.product.domain.*;
import java.math.BigDecimal;

@lombok.Builder
public record ProductCreateRequest(String name, String brand, String gtin, String basis,
                                   BigDecimal energyKcal, BigDecimal protein, BigDecimal fat, BigDecimal carbohydrates) {
    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void rejectUnknownField(String name, Object value) {
        throw new InvalidProductException("Unsupported product field: " + name);
    }

    public ProductDraft toDraft() {
        var requiredBasis = java.util.Optional.ofNullable(basis)
                .orElseThrow(() -> new InvalidProductException("Nutrition basis is required"));
        return ProductDraft.builder().name(name).brand(java.util.Optional.ofNullable(brand))
                .nutrition(NutritionFacts.builder().basis(NutritionBasis.valueOf(requiredBasis))
                        .energyKcal(java.util.Optional.ofNullable(energyKcal)).protein(java.util.Optional.ofNullable(protein))
                        .fat(java.util.Optional.ofNullable(fat)).carbohydrates(java.util.Optional.ofNullable(carbohydrates)).build()).build();
    }
}
