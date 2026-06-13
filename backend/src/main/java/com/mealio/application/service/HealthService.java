package com.mealio.application.service;

import com.mealio.adapter.in.web.dto.HealthResponse;
import com.mealio.infrastructure.config.MealioApplicationProperties;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthService {

    private final Clock clock;
    private final MealioApplicationProperties properties;

    public HealthResponse health() {
        return new HealthResponse(
                "UP",
                properties.getApplicationName(),
                Instant.now(clock)
        );
    }
}
