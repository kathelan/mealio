package com.mealio.infrastructure.config;

import static org.assertj.core.api.Assertions.*;

import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class ConfigurationTest {
    @Test
    void providesUtcClockAndApplicationName() {
        assertThat(new ClockConfig().clock().getZone()).isEqualTo(ZoneOffset.UTC);
        var properties = new MealioApplicationProperties();
        assertThat(properties.getApplicationName()).isEqualTo("Mealio");
        properties.setApplicationName("test");
        assertThat(properties.getApplicationName()).isEqualTo("test");
    }
}
