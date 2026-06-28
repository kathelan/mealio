package com.mealio.asr.infrastructure.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {

    @Bean
    RestClient.Builder restClientBuilder(List<ClientHttpRequestInterceptor> interceptors) {
        return RestClient.builder()
                .requestInterceptors(items -> items.addAll(interceptors));
    }
}
