package com.mealio.product.infrastructure.web.dto;

import com.mealio.product.domain.Product;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@lombok.Builder
public record ProductResponse(UUID id, String gtin, Instant createdAt, UUID versionId, int versionNumber,
                              String name, String brand, String basis, BigDecimal energyKcal,
                              BigDecimal protein, BigDecimal fat, BigDecimal carbohydrates,
                              String verificationStatus, String source, Instant recordedAt) {
    public static ProductResponse from(Product product) {
        var version = product.version();
        var details = version.details();
        var nutrition = details.nutrition();
        return ProductResponse.builder().id(product.id()).gtin(product.gtin().map(com.mealio.product.domain.Gtin::value).orElse(null))
                .createdAt(product.createdAt()).versionId(version.id()).versionNumber(version.number())
                .name(details.name()).brand(details.brand().orElse(null)).basis(nutrition.basis().name())
                .energyKcal(nutrition.energyKcal().orElse(null)).protein(nutrition.protein().orElse(null))
                .fat(nutrition.fat().orElse(null)).carbohydrates(nutrition.carbohydrates().orElse(null))
                .verificationStatus(nutrition.status().name()).source(version.source().source().name())
                .recordedAt(version.source().recordedAt()).build();
    }
}
