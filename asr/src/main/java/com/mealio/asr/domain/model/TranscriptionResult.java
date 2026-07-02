package com.mealio.asr.domain.model;

import java.time.Instant;
import java.util.UUID;

public record TranscriptionResult(
        UUID id,
        String transcript,
        String language,
        String provider,
        Double confidence,
        Instant transcribedAt
) {
}
