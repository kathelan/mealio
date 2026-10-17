package com.mealio.health.application;

import java.time.Instant;

public record HealthStatus(String status, String application, Instant timestamp) {}
