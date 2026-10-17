package com.mealio.asr.infrastructure.config;

import static org.assertj.core.api.Assertions.*;

import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.RestClient;

class ConfigurationTest {
    @Test
    void providesUtcClockAndRegistersHttpInterceptors() {
        assertThat(new ClockConfig().clock().getZone()).isEqualTo(ZoneOffset.UTC);
        var builder = new HttpClientConfig().restClientBuilder(List.of((request, body, execution) ->
                new MockClientHttpResponse("intercepted".getBytes(java.nio.charset.StandardCharsets.UTF_8), HttpStatus.OK)));
        RestClient client = builder.build();
        assertThat(client.get().uri("https://unused.test").retrieve().body(String.class)).isEqualTo("intercepted");
    }
}
