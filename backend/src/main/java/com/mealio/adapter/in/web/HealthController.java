package com.mealio.adapter.in.web;

import com.mealio.adapter.in.web.dto.HealthResponse;
import com.mealio.application.service.HealthService;
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
        return healthService.health();
    }
}
