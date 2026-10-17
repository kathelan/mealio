package com.mealio;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

class MealioApplicationTest {
    @Test
    void entrypointStartsExpectedApplication() {
        assertThat(new MealioApplication()).isNotNull();
        try (var spring = mockStatic(SpringApplication.class)) {
            var arguments = new String[]{"--server.port=0"};
            MealioApplication.main(arguments);
            spring.verify(() -> SpringApplication.run(MealioApplication.class, arguments));
        }
    }
}
