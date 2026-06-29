package com.mealio.audit;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class OutboundHttpAuditInterceptor implements ClientHttpRequestInterceptor {

    private final HttpAuditProperties properties;
    private final HttpAuditEventLogger eventLogger;

    public OutboundHttpAuditInterceptor(HttpAuditProperties properties, HttpAuditEventLogger eventLogger) {
        this.properties = properties;
        this.eventLogger = eventLogger;
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

        var context = outboundContext();
        var auditedRequest = requestWithContext(request, context);
        var startedAt = System.nanoTime();
        eventLogger.outboundRequest(new HttpAuditEventLogger.OutboundHttpAuditRequestEvent(
                context,
                auditedRequest.getMethod(),
                auditedRequest.getURI(),
                auditedRequest.getHeaders(),
                body,
                auditedRequest.getHeaders().getContentType()
        ));

        try {
            var response = execution.execute(auditedRequest, body);
            var elapsedMs = (System.nanoTime() - startedAt) / 1_000_000;
            eventLogger.outboundResponse(new HttpAuditEventLogger.OutboundHttpAuditResponseEvent(
                    context,
                    auditedRequest.getMethod(),
                    auditedRequest.getURI(),
                    response.getStatusCode(),
                    elapsedMs,
                    response.getHeaders(),
                    null,
                    response.getHeaders().getContentType()
            ));
            return response;
        } catch (IOException exception) {
            var elapsedMs = (System.nanoTime() - startedAt) / 1_000_000;
            eventLogger.outboundError(new HttpAuditEventLogger.OutboundHttpAuditErrorEvent(
                    context,
                    auditedRequest.getMethod(),
                    auditedRequest.getURI(),
                    elapsedMs,
                    exception
            ));
            throw exception;
        }
    }

    private HttpAuditContextSnapshot outboundContext() {
        var current = HttpAuditContext.current();
        return new HttpAuditContextSnapshot(
                current.map(HttpAuditContextSnapshot::correlationId).orElseGet(() -> UUID.randomUUID().toString()),
                current.map(HttpAuditContextSnapshot::traceId).orElseGet(() -> HttpAuditIds.traceId(null)),
                HttpAuditIds.randomSpanId(),
                current.map(HttpAuditContextSnapshot::spanId).orElse(null),
                UUID.randomUUID().toString(),
                current.map(HttpAuditContextSnapshot::traceFlags).orElse(HttpAuditIds.DEFAULT_TRACE_FLAGS)
        );
    }

    private HttpRequest requestWithContext(HttpRequest request, HttpAuditContextSnapshot context) {
        var headers = new HttpHeaders();
        headers.putAll(request.getHeaders());
        if (properties.isPropagateRequestId()) {
            HttpAuditIds.putIfAbsent(headers, properties.getRequestIdHeaderName(), context.requestId());
        }
        if (properties.isPropagateCorrelationId()) {
            HttpAuditIds.putIfAbsent(headers, properties.getCorrelationIdHeaderName(), context.correlationId());
        }
        if (properties.isPropagateTraceContext()) {
            headers.set(HttpAuditIds.TRACEPARENT_HEADER, HttpAuditIds.traceParentHeader(context));
        }

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
}
