package com.mealio.health.infrastructure.config;

import com.mealio.health.application.HealthService;
import com.mealio.infrastructure.config.MealioApplicationProperties;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class HealthConfiguration {
    @Bean
    HealthService healthService(Clock clock, MealioApplicationProperties properties) {
        return new HealthService(clock, properties.getApplicationName());
    }
}
