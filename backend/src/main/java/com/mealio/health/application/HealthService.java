package com.mealio.health.application;

import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HealthService {

    private final Clock clock;
    private final String applicationName;

    public HealthStatus health() {
        return new HealthStatus(
                "UP",
                applicationName,
                Instant.now(clock)
        );
    }
}
