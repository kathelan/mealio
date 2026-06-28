package com.mealio.audit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class HttpAuditSanitizerTest {

    private final HttpAuditProperties properties = new HttpAuditProperties();
    private final HttpAuditSanitizer sanitizer = new HttpAuditSanitizer(properties);

    @Test
    void redactsSensitiveHeaders() {
        var headers = new HttpHeaders();
        headers.setBearerAuth("secret-token");
        headers.add("X-Request-Id", "request-1");

        assertThat(sanitizer.headers(headers))
                .containsEntry("Authorization", java.util.List.of("<redacted>"))
                .containsEntry("X-Request-Id", java.util.List.of("request-1"));
    }

    @Test
    void redactsHeadersConfiguredWithDifferentCase() {
        properties.setRedactedHeaders(Set.of("X-CUSTOM-SECRET"));
        var headers = new HttpHeaders();
        headers.add("X-Custom-Secret", "secret");

        assertThat(sanitizer.headers(headers))
                .containsEntry("X-Custom-Secret", java.util.List.of("<redacted>"));
    }

    @Test
    void includesTextBodyPreview() {
        assertThat(sanitizer.body("hello".getBytes(), MediaType.TEXT_PLAIN))
                .isEqualTo("hello");
    }

    @Test
    void truncatesLongTextBody() {
        properties.setMaxBodyBytes(5);

        assertThat(sanitizer.body("hello world".getBytes(), MediaType.TEXT_PLAIN))
                .isEqualTo("hello...<truncated>");
    }

    @Test
    void omitsBinaryBody() {
        assertThat(sanitizer.body(new byte[] {1, 2, 3}, MediaType.APPLICATION_OCTET_STREAM))
                .isEqualTo("<binary body omitted>");
    }
}
