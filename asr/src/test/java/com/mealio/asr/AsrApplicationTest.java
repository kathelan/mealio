package com.mealio.asr;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

class AsrApplicationTest {
    @Test
    void entrypointStartsExpectedApplication() {
        org.assertj.core.api.Assertions.assertThat(new AsrApplication()).isNotNull();
        try (var spring = mockStatic(SpringApplication.class)) {
            var arguments = new String[]{"--server.port=0"};
            AsrApplication.main(arguments);
            spring.verify(() -> SpringApplication.run(AsrApplication.class, arguments));
        }
    }
}
