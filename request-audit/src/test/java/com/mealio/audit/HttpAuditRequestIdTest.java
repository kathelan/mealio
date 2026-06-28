package com.mealio.audit;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class HttpAuditRequestIdTest {

    private final HttpAuditProperties properties = new HttpAuditProperties();
    private final HttpAuditSanitizer sanitizer = new HttpAuditSanitizer(properties);
    private final HttpAuditEventLogger eventLogger = new HttpAuditEventLogger(new ObjectMapper(), properties, sanitizer);

    @AfterEach
    void clearContext() {
        HttpAuditContext.clear();
    }

    @Test
    void inboundRequestUsesExistingCorrelationAndRequestIds() throws Exception {
        var filter = new InboundHttpAuditFilter(properties, eventLogger);
        var request = new MockHttpServletRequest("GET", "/api/test");
        var response = new MockHttpServletResponse();
        var contextInFlow = new AtomicReference<HttpAuditContextSnapshot>();
        request.addHeader(properties.getCorrelationIdHeaderName(), "flow-1");
        request.addHeader(properties.getRequestIdHeaderName(), "request-1");

        filter.doFilter(request, response, (servletRequest, servletResponse) -> {
            contextInFlow.set(HttpAuditContext.current().orElse(null));
            ((HttpServletResponse) servletResponse).getWriter().write("{\"ok\":true}");
        });

        assertThat(contextInFlow.get().correlationId()).isEqualTo("flow-1");
        assertThat(contextInFlow.get().requestId()).isEqualTo("request-1");
        assertThat(response.getHeader(properties.getCorrelationIdHeaderName())).isEqualTo("flow-1");
        assertThat(response.getHeader(properties.getRequestIdHeaderName())).isEqualTo("request-1");
        assertThat(HttpAuditContext.requestId()).isEmpty();
    }

    @Test
    void inboundRequestRegeneratesUnsafeExternalRequestId() throws Exception {
        var filter = new InboundHttpAuditFilter(properties, eventLogger);
        var request = new MockHttpServletRequest("GET", "/api/test");
        var response = new MockHttpServletResponse();
        var contextInFlow = new AtomicReference<HttpAuditContextSnapshot>();
        request.addHeader(properties.getRequestIdHeaderName(), "bad id with spaces");

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                contextInFlow.set(HttpAuditContext.current().orElse(null)));

        assertThat(contextInFlow.get().requestId()).isNotEqualTo("bad id with spaces");
        assertThat(response.getHeader(properties.getRequestIdHeaderName())).isEqualTo(contextInFlow.get().requestId());
    }

    @Test
    void outboundRequestPropagatesCorrelationAndTraceContext() throws Exception {
        var interceptor = new OutboundHttpAuditInterceptor(properties, eventLogger);
        var propagatedHeaders = new AtomicReference<HttpHeaders>();
        HttpAuditContext.set(new HttpAuditContextSnapshot(
                "flow-2",
                "1234567890abcdef1234567890abcdef",
                "1234567890abcdef",
                null,
                "inbound-request"
        ));

        interceptor.intercept(new TestHttpRequest(), new byte[0], (request, body) -> {
            propagatedHeaders.set(request.getHeaders());
            return new MockClientHttpResponse("{}".getBytes(StandardCharsets.UTF_8), HttpStatus.OK);
        });

        assertThat(propagatedHeaders.get().getFirst(properties.getCorrelationIdHeaderName())).isEqualTo("flow-2");
        assertThat(propagatedHeaders.get().getFirst(properties.getRequestIdHeaderName()))
                .isNotBlank()
                .isNotEqualTo("inbound-request");
        assertThat(propagatedHeaders.get().getFirst("traceparent"))
                .startsWith("00-1234567890abcdef1234567890abcdef-");
    }

    private static class TestHttpRequest implements HttpRequest {

        private final HttpHeaders headers = new HttpHeaders();

        @Override
        public HttpMethod getMethod() {
            return HttpMethod.GET;
        }

        @Override
        public URI getURI() {
            return URI.create("http://localhost/test");
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }

        @Override
        public Map<String, Object> getAttributes() {
            return Map.of();
        }
    }
}
