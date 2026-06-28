package com.mealio.audit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class OutboundHttpAuditInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(OutboundHttpAuditInterceptor.class);

    private final HttpAuditProperties properties;
    private final HttpAuditSanitizer sanitizer;

    public OutboundHttpAuditInterceptor(HttpAuditProperties properties, HttpAuditSanitizer sanitizer) {
        this.properties = properties;
        this.sanitizer = sanitizer;
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {
        if (!properties.isEnabled()) {
            return execution.execute(request, body);
        }

        var requestId = HttpAuditContext.requestId().orElseGet(() -> UUID.randomUUID().toString());
        var auditedRequest = requestWithRequestId(request, requestId);
        var startedAt = System.nanoTime();
        log.info(
                "http.outbound.request requestId={} method={} uri={} headers={} body={}",
                requestId,
                auditedRequest.getMethod(),
                auditedRequest.getURI(),
                sanitizer.headers(auditedRequest.getHeaders()),
                sanitizer.body(body, auditedRequest.getHeaders().getContentType())
        );

        var response = new BufferedClientHttpResponse(execution.execute(auditedRequest, body));
        var elapsedMs = (System.nanoTime() - startedAt) / 1_000_000;
        log.info(
                "http.outbound.response requestId={} method={} uri={} status={} durationMs={} headers={} body={}",
                requestId,
                auditedRequest.getMethod(),
                auditedRequest.getURI(),
                response.getStatusCode(),
                elapsedMs,
                sanitizer.headers(response.getHeaders()),
                sanitizer.body(response.body(), response.getHeaders().getContentType())
        );
        return response;
    }

    private HttpRequest requestWithRequestId(HttpRequest request, String requestId) {
        if (!properties.isPropagateRequestId()
                || request.getHeaders().containsHeader(properties.getRequestIdHeaderName())) {
            return request;
        }
        var headers = new HttpHeaders();
        headers.putAll(request.getHeaders());
        headers.set(properties.getRequestIdHeaderName(), requestId);

        return new HttpRequest() {
            @Override
            public HttpMethod getMethod() {
                return request.getMethod();
            }

            @Override
            public URI getURI() {
                return request.getURI();
            }

            @Override
            public HttpHeaders getHeaders() {
                return headers;
            }

            @Override
            public Map<String, Object> getAttributes() {
                return request.getAttributes();
            }
        };
    }

    private static class BufferedClientHttpResponse implements ClientHttpResponse {

        private final ClientHttpResponse delegate;
        private final byte[] body;

        BufferedClientHttpResponse(ClientHttpResponse delegate) throws IOException {
            this.delegate = delegate;
            this.body = delegate.getBody().readAllBytes();
        }

        byte[] body() {
            return body;
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return delegate.getStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return delegate.getStatusText();
        }

        @Override
        public void close() {
            delegate.close();
        }

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        @Override
        public HttpHeaders getHeaders() {
            return delegate.getHeaders();
        }
    }
}
