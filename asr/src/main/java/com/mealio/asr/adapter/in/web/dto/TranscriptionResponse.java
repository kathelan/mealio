package com.mealio.asr.adapter.in.web.dto;

import com.mealio.asr.domain.model.TranscriptionResult;
import java.time.Instant;
import java.util.UUID;

public record TranscriptionResponse(
        UUID id,
        String transcript,
        String language,
        String provider,
        Double confidence,
        Instant transcribedAt
) {

    public static TranscriptionResponse from(TranscriptionResult result) {
        return new TranscriptionResponse(
                result.id(),
                result.transcript(),
                result.language(),
                result.provider(),
                result.confidence(),
                result.transcribedAt()
        );
    }
}
