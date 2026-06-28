package com.mealio.asr.domain.model;

import java.time.Instant;

public record TranscriptionResult(
        String transcript,
        String language,
        String provider,
        Double confidence,
        Instant transcribedAt
) {
}
