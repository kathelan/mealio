package com.mealio.asr.domain.model;

public record AudioMetadata(
        String filename,
        String contentType,
        Long sizeBytes
) {
}
