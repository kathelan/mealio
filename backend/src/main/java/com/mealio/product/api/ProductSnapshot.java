package com.mealio.product.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@lombok.Builder
public record ProductSnapshot(UUID id, java.util.Optional<String> gtin, Instant createdAt, UUID versionId, int versionNumber,
                              String name, java.util.Optional<String> brand, String basis, java.util.Optional<BigDecimal> energyKcal,
                              java.util.Optional<BigDecimal> protein, java.util.Optional<BigDecimal> fat, java.util.Optional<BigDecimal> carbohydrates,
                              String verificationStatus, String source, Instant recordedAt) {}
