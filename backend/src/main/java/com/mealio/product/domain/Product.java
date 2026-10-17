package com.mealio.product.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@lombok.Builder
public record Product(UUID id, java.util.Optional<Gtin> gtin, Instant createdAt, ProductVersion version) {
    public Product {
        gtin = java.util.Optional.ofNullable(gtin).orElseGet(java.util.Optional::empty);
        Objects.requireNonNull(id);
        Objects.requireNonNull(createdAt);
        Objects.requireNonNull(version);
    }

    public Product revise(int expectedVersion, UUID versionId, ProductDraft draft, Instant now) {
        if (expectedVersion != version.number()) throw new ProductConflictException("Product version has changed");
        return Product.builder().id(id).gtin(gtin).createdAt(createdAt)
                .version(ProductVersion.builder().id(versionId).number(Math.addExact(version.number(), 1))
                        .details(draft).source(ProductSourceSnapshot.builder()
                                .source(version.source().source()).recordedAt(now).build()).build()).build();
    }
}
