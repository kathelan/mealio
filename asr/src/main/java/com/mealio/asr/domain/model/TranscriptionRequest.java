package com.mealio.asr.domain.model;

public record TranscriptionRequest(
        AudioContent audio,
        String language,
        String prompt
) {
}
