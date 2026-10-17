package com.mealio.product.domain;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Builder;

@Builder
public record NutritionFacts(NutritionBasis basis, Optional<BigDecimal> energyKcal, Optional<BigDecimal> protein,
                             Optional<BigDecimal> fat, Optional<BigDecimal> carbohydrates) {
    public NutritionFacts {
        basis = Optional.ofNullable(basis).orElseThrow(() -> new InvalidProductException("Nutrition basis is required"));
        energyKcal = optional(energyKcal);
        protein = optional(protein);
        fat = optional(fat);
        carbohydrates = optional(carbohydrates);
        Stream.of(energyKcal, protein, fat, carbohydrates).flatMap(Optional::stream).forEach(value -> {
            var normalized = value.stripTrailingZeros();
            if (value.signum() < 0 || Math.max(normalized.scale(), 0) > 4
                    || Math.max(normalized.precision() - normalized.scale(), 0) > 8) {
                throw new InvalidProductException("Nutrition values must be non-negative and fit numeric(12,4)");
            }
        });
    }

    public VerificationStatus status() {
        return Stream.of(energyKcal, protein, fat, carbohydrates).allMatch(Optional::isPresent)
                ? VerificationStatus.USER_CONFIRMED : VerificationStatus.INCOMPLETE;
    }

    private static Optional<BigDecimal> optional(Optional<BigDecimal> value) {
        return Optional.ofNullable(value).orElseGet(Optional::empty);
    }
}
