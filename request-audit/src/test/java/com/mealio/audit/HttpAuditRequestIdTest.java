package com.mealio.audit;

import static org.assertj.core.api.Assertions.assertThat;

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

    @AfterEach
    void clearContext() {
        HttpAuditContext.clear();
    }

    @Test
    void inboundRequestUsesExistingRequestIdAndReturnsItInResponse() throws Exception {
        var filter = new InboundHttpAuditFilter(properties, sanitizer);
        var request = new MockHttpServletRequest("GET", "/api/test");
        var response = new MockHttpServletResponse();
        var requestIdInFlow = new AtomicReference<String>();
        request.addHeader(properties.getRequestIdHeaderName(), "flow-1");

        filter.doFilter(request, response, (servletRequest, servletResponse) -> {
            requestIdInFlow.set(HttpAuditContext.requestId().orElse(null));
            ((HttpServletResponse) servletResponse).getWriter().write("{\"ok\":true}");
        });

        assertThat(requestIdInFlow).hasValue("flow-1");
        assertThat(response.getHeader(properties.getRequestIdHeaderName())).isEqualTo("flow-1");
        assertThat(HttpAuditContext.requestId()).isEmpty();
    }

    @Test
    void outboundRequestPropagatesRequestIdFromCurrentFlow() throws Exception {
        var interceptor = new OutboundHttpAuditInterceptor(properties, sanitizer);
        var propagatedHeaders = new AtomicReference<HttpHeaders>();
        HttpAuditContext.setRequestId("flow-2");

        interceptor.intercept(new TestHttpRequest(), new byte[0], (request, body) -> {
            propagatedHeaders.set(request.getHeaders());
            return new MockClientHttpResponse("{}".getBytes(StandardCharsets.UTF_8), HttpStatus.OK);
        });

        assertThat(propagatedHeaders.get().getFirst(properties.getRequestIdHeaderName())).isEqualTo("flow-2");
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
