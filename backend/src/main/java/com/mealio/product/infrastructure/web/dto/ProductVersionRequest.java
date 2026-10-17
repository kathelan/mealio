package com.mealio.product.infrastructure.web.dto;

import com.mealio.product.domain.*;
import java.math.BigDecimal;

@lombok.Builder
public record ProductVersionRequest(Integer expectedVersion, String name, String brand, String basis,
                                    BigDecimal energyKcal, BigDecimal protein, BigDecimal fat, BigDecimal carbohydrates) {
    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void rejectUnknownField(String name, Object value) {
        throw new InvalidProductException("Unsupported product field: " + name);
    }

    public ProductDraft toDraft() {
        java.util.Optional.ofNullable(expectedVersion).filter(value -> value > 0)
                .orElseThrow(() -> new InvalidProductException("Expected version must be positive"));
        return ProductCreateRequest.builder().name(name).brand(brand).basis(basis).energyKcal(energyKcal)
                .protein(protein).fat(fat).carbohydrates(carbohydrates).build().toDraft();
    }
}
