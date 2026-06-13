package com.mealio.adapter.in.web.dto;

import java.time.Instant;

public record HealthResponse(
        String status,
        String application,
        Instant timestamp
) {
}
