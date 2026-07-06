package com.mealio.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.mealio.application.service.HealthService;
import com.mealio.infrastructure.config.MealioApplicationProperties;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class HealthControllerTest {

    @Test
    void delegatesHealthResponseToService() {
        var properties = new MealioApplicationProperties();
        properties.setApplicationName("Mealio");
        var service = new HealthService(Clock.fixed(Instant.parse("2026-07-06T12:00:00Z"), ZoneOffset.UTC),
                properties);
        var controller = new HealthController(service);

        assertThat(controller.health().status()).isEqualTo("UP");
        assertThat(controller.health().application()).isEqualTo("Mealio");
        assertThat(controller.health().timestamp()).isEqualTo(Instant.parse("2026-07-06T12:00:00Z"));
    }
}
