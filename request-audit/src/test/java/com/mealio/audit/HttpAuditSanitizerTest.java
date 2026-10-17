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
        headers.add("openai-project", "proj_secret");

        assertThat(sanitizer.headers(headers))
                .containsEntry("Authorization", java.util.List.of("<redacted>"))
                .containsEntry("openai-project", java.util.List.of("<redacted>"))
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
    void omitsBodyByDefault() {
        assertThat(sanitizer.body("secret transcript".getBytes(), MediaType.TEXT_PLAIN))
                .isEmpty();
        assertThat(sanitizer.bodyLogged("secret transcript".getBytes(), MediaType.TEXT_PLAIN))
                .isFalse();
        assertThat(sanitizer.bodyOmittedReason("secret transcript".getBytes(), MediaType.TEXT_PLAIN))
                .isEqualTo("disabled");
    }

    @Test
    void includesTextBodyPreview() {
        properties.setIncludeBodies(true);

        assertThat(sanitizer.body("hello".getBytes(), MediaType.TEXT_PLAIN))
                .isEqualTo("hello");
    }

    @Test
    void truncatesLongTextBody() {
        properties.setIncludeBodies(true);
        properties.setMaxBodyBytes(5);

        assertThat(sanitizer.body("hello world".getBytes(), MediaType.TEXT_PLAIN))
                .isEqualTo("hello...<truncated>");
    }

    @Test
    void omitsBinaryBody() {
        properties.setIncludeBodies(true);

        assertThat(sanitizer.body(new byte[] {1, 2, 3}, MediaType.APPLICATION_OCTET_STREAM))
                .isEqualTo("<binary body omitted>");
    }

    @Test
    void redactsQueryParametersByDefault() {
        assertThat(sanitizer.query("token=secret&lang=pl"))
                .containsEntry("token", java.util.List.of("<redacted>"))
                .containsEntry("lang", java.util.List.of("<redacted>"));
    }

    @Test
    void redactsSensitiveQueryParametersWhenQueryLoggingIsEnabled() {
        properties.setIncludeQueryString(true);

        assertThat(sanitizer.query("token=secret&lang=pl&email=user@example.com"))
                .containsEntry("token", java.util.List.of("<redacted>"))
                .containsEntry("lang", java.util.List.of("pl"))
                .containsEntry("email", java.util.List.of("<redacted>"));
    }
    @Test
    void handlesNullEmptyBinaryAndSupportedTextBodies() {
        assertThat(sanitizer.bodyOmittedReason(null, null)).isEqualTo("disabled");
        properties.setIncludeBodies(true);
        assertThat(sanitizer.bodyOmittedReason(null, null)).isEqualTo("not_captured");
        assertThat(sanitizer.bodyOmittedReason(new byte[0], null)).isNull();
        assertThat(sanitizer.body(null, null)).isEmpty();
        assertThat(sanitizer.body(new byte[0], null)).isEmpty();
        assertThat(sanitizer.body(new byte[]{1}, null)).isEqualTo("<binary body omitted>");
        assertThat(sanitizer.bodyOmittedReason(new byte[]{1}, MediaType.APPLICATION_OCTET_STREAM))
                .isEqualTo("binary_or_unsupported_content_type");
        for (var type : java.util.List.of(MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML,
                MediaType.APPLICATION_JSON, MediaType.parseMediaType("application/problem+json"))) {
            assertThat(sanitizer.bodyLogged(new byte[]{65}, type)).isTrue();
            assertThat(sanitizer.bodyOmittedReason(new byte[]{65}, type)).isNull();
        }
    }

    @Test
    void queryParserFailureCannotLeakRawValues() {
        try (var parser = org.mockito.Mockito.mockStatic(org.springframework.web.util.UriComponentsBuilder.class)) {
            parser.when(() -> org.springframework.web.util.UriComponentsBuilder.fromUriString("/?invalid"))
                    .thenThrow(new IllegalArgumentException("malformed URI"));
            assertThat(sanitizer.query("invalid"))
                    .containsEntry("<invalid-query>", java.util.List.of("<redacted>"));
        }
    }
}
