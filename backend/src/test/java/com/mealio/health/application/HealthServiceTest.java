package com.mealio.health.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.mealio.health.application.HealthStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class HealthServiceTest {

    @Test
    void returnsApplicationHealthPayload() {
        var service = new HealthService(Clock.fixed(Instant.parse("2026-06-13T10:15:30Z"), ZoneOffset.UTC), "Mealio");

        HealthStatus response = service.health();

        assertThat(response.status()).isEqualTo("UP");
        assertThat(response.application()).isEqualTo("Mealio");
        assertThat(response.timestamp()).isEqualTo(Instant.parse("2026-06-13T10:15:30Z"));
    }
}
