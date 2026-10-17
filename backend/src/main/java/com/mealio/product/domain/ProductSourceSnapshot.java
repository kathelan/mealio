package com.mealio.product.domain;

import java.time.Instant;
import java.util.Objects;

@lombok.Builder
public record ProductSourceSnapshot(Source source, Instant recordedAt) {
    public enum Source { USER_LABEL, USER_INPUT }
    public ProductSourceSnapshot {
        Objects.requireNonNull(source);
        Objects.requireNonNull(recordedAt);
    }
}
