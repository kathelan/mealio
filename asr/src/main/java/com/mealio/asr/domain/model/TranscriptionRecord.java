package com.mealio.asr.domain.model;

import java.time.Instant;
import java.util.UUID;

public record TranscriptionRecord(
        UUID id,
        String transcript,
        String language,
        String provider,
        String model,
        Double confidence,
        AudioMetadata audio,
        TranscriptionStatus status,
        String errorCode,
        String errorMessage,
        Instant createdAt,
        Instant transcribedAt
) {
}
