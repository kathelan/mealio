package com.mealio.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.mealio.adapter.in.web.dto.HealthResponse;
import com.mealio.infrastructure.config.MealioApplicationProperties;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class HealthServiceTest {

    @Test
    void returnsApplicationHealthPayload() {
        var properties = new MealioApplicationProperties();
        properties.setApplicationName("Mealio");
        var service = new HealthService(Clock.fixed(Instant.parse("2026-06-13T10:15:30Z"), ZoneOffset.UTC), properties);

        HealthResponse response = service.health();

        assertThat(response.status()).isEqualTo("UP");
        assertThat(response.application()).isEqualTo("Mealio");
        assertThat(response.timestamp()).isEqualTo(Instant.parse("2026-06-13T10:15:30Z"));
    }
}
