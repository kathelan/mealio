package com.mealio.asr.adapter.in.web.dto;

import java.time.Instant;

public record ApiErrorResponse(
        String code,
        String message,
        Instant timestamp
) {
}
