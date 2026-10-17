package com.mealio.health.infrastructure.web;

import com.mealio.health.infrastructure.web.dto.HealthResponse;
import com.mealio.health.application.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    @GetMapping("/health")
    public HealthResponse health() {
        var health = healthService.health();
        return new HealthResponse(health.status(), health.application(), health.timestamp());
    }
}
