package com.mealio.audit;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.filter.OncePerRequestFilter;

@AutoConfiguration
@EnableConfigurationProperties(HttpAuditProperties.class)
public class HttpAuditAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    HttpAuditSanitizer httpAuditSanitizer(HttpAuditProperties properties) {
        return new HttpAuditSanitizer(properties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "outboundHttpAuditInterceptor")
    ClientHttpRequestInterceptor outboundHttpAuditInterceptor(
            HttpAuditProperties properties,
            HttpAuditSanitizer sanitizer
    ) {
        return new OutboundHttpAuditInterceptor(properties, sanitizer);
    }

    @Bean
    @ConditionalOnClass(OncePerRequestFilter.class)
    @ConditionalOnMissingBean
    InboundHttpAuditFilter inboundHttpAuditFilter(HttpAuditProperties properties, HttpAuditSanitizer sanitizer) {
        return new InboundHttpAuditFilter(properties, sanitizer);
    }
}
