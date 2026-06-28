package com.mealio.asr.domain.model;

public record AudioContent(
        String filename,
        String contentType,
        byte[] bytes
) {
}
