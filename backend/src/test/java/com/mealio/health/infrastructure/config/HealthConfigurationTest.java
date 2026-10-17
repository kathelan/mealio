package com.mealio.health.infrastructure.config;

import static org.assertj.core.api.Assertions.*;

import com.mealio.infrastructure.config.MealioApplicationProperties;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class HealthConfigurationTest {
    @Test
    void healthServiceUsesConfiguredApplicationNameAndClock() {
        var properties = new MealioApplicationProperties();
        properties.setApplicationName("test");
        var service = new HealthConfiguration().healthService(Clock.fixed(Instant.EPOCH, ZoneOffset.UTC), properties);
        assertThat(service).isNotNull();
        assertThat(service.health().application()).isEqualTo("test");
    }
}
