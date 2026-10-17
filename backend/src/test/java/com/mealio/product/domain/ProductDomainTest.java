package com.mealio.product.domain;

import static org.assertj.core.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductDomainTest {
    @ParameterizedTest
    @ValueSource(strings = {"96385074", "036000291452", "5901234123457", "10012345000017"})
    void validatesSupportedGtinLengths(String code) {
        assertThat(Gtin.of(code).value()).hasSize(14).endsWith(code);
    }

    @Test
    void normalizesEquivalentCodesAndPreservesLeadingZeros() {
        assertThat(Gtin.of(" 5901234123457 ")).isEqualTo(Gtin.of("05901234123457"));
        assertThat(Gtin.of("036000291452").value()).isEqualTo("00036000291452");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"5901234123458", "123456789", "5901234 23457", "abcdef12", "５９０１２３４１２３４５７"})
    void rejectsMalformedGtin(String code) {
        assertThatThrownBy(() -> Gtin.of(code)).isInstanceOf(InvalidProductException.class);
    }

    @Test
    void preservesUnknownValuesAndDistinguishesThemFromZero() {
        var partial = NutritionFacts.builder().basis(NutritionBasis.PER_100_G).protein(java.util.Optional.of(BigDecimal.ZERO))
                .carbohydrates(java.util.Optional.of(BigDecimal.ZERO)).build();
        assertThat(partial.energyKcal()).isEmpty();
        assertThat(partial.status()).isEqualTo(VerificationStatus.INCOMPLETE);
        assertThat(complete().status()).isEqualTo(VerificationStatus.USER_CONFIRMED);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.0001", "0.00001", "100000000", "1E+12"})
    void rejectsNegativeOrUnrepresentableNutrition(String value) {
        assertThatThrownBy(() -> NutritionFacts.builder().basis(NutritionBasis.PER_100_G).energyKcal(java.util.Optional.of(new BigDecimal(value))).build())
                .isInstanceOf(InvalidProductException.class);
    }

    @Test
    void acceptsExactBoundaryWithoutRounding() {
        var value = new BigDecimal("99999999.9999");
        var nutrition = NutritionFacts.builder().basis(NutritionBasis.PER_100_ML).energyKcal(java.util.Optional.of(value)).build();
        assertThat(nutrition.energyKcal()).hasValueSatisfying(actual -> assertThat(actual).isEqualByComparingTo(value));
        assertThatThrownBy(() -> NutritionFacts.builder().build())
                .isInstanceOf(InvalidProductException.class);
    }

    @Test
    void validatesNamesBrandsAndRequiredFacts() {
        var draft = ProductDraft.builder().name("  Rice   bowl ").brand(java.util.Optional.of(" ")).nutrition(complete()).build();
        assertThat(draft.name()).isEqualTo("Rice bowl");
        assertThat(draft.brand()).isEmpty();
        assertThatThrownBy(() -> ProductDraft.builder().name(" ").nutrition(complete()).build()).isInstanceOf(InvalidProductException.class);
        assertThatThrownBy(() -> ProductDraft.builder().name("x".repeat(221)).nutrition(complete()).build()).isInstanceOf(InvalidProductException.class);
        assertThatThrownBy(() -> ProductDraft.builder().name("Rice").brand(java.util.Optional.of("x".repeat(161))).nutrition(complete()).build()).isInstanceOf(InvalidProductException.class);
        assertThatThrownBy(() -> ProductDraft.builder().name("Rice").build()).isInstanceOf(InvalidProductException.class);
    }

    @Test
    void revisionsPreserveHistoricalValuesAndRejectStaleVersion() {
        var now = Instant.parse("2026-10-17T10:00:00Z");
        var original = Product.builder().id(UUID.randomUUID()).gtin(java.util.Optional.of(Gtin.of("5901234123457")))
                .createdAt(now).version(ProductVersion.builder().id(UUID.randomUUID()).number(1)
                        .details(ProductDraft.builder().name("Rice").nutrition(complete()).build())
                        .source(ProductSourceSnapshot.builder().source(ProductSourceSnapshot.Source.USER_LABEL).recordedAt(now).build())
                        .build()).build();
        var changed = original.revise(1, UUID.randomUUID(), ProductDraft.builder().name("Updated rice").nutrition(complete()).build(), now.plusSeconds(1));
        assertThat(original.version().number()).isEqualTo(1);
        assertThat(original.version().details().name()).isEqualTo("Rice");
        assertThat(changed.version().number()).isEqualTo(2);
        assertThat(changed.createdAt()).isEqualTo(now);
        assertThat(changed.gtin()).isEqualTo(original.gtin());
        assertThat(changed.version().source().source()).isEqualTo(ProductSourceSnapshot.Source.USER_LABEL);
        assertThatThrownBy(() -> changed.revise(1, UUID.randomUUID(), original.version().details(), now))
                .isInstanceOf(ProductConflictException.class);
    }

    private NutritionFacts complete() {
        return NutritionFacts.builder().basis(NutritionBasis.PER_100_G).energyKcal(java.util.Optional.of(BigDecimal.ZERO))
                .protein(java.util.Optional.of(BigDecimal.ZERO)).fat(java.util.Optional.of(BigDecimal.ZERO))
                .carbohydrates(java.util.Optional.of(BigDecimal.ZERO)).build();
    }
}
