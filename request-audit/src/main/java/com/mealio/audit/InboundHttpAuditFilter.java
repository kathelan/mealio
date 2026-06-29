package com.mealio.audit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

public class InboundHttpAuditFilter extends OncePerRequestFilter {

    private final HttpAuditProperties properties;
    private final HttpAuditEventLogger eventLogger;

    public InboundHttpAuditFilter(
            HttpAuditProperties properties,
            HttpAuditEventLogger eventLogger
    ) {
        this.properties = properties;
        this.eventLogger = eventLogger;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!properties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        var wrappedRequest = properties.isIncludeBodies()
                ? new ContentCachingRequestWrapper(request, properties.getMaxBodyBytes())
                : null;
        var wrappedResponse = properties.isIncludeBodies() ? new ContentCachingResponseWrapper(response) : null;
        var requestForChain = wrappedRequest == null ? request : wrappedRequest;
        var responseForChain = wrappedResponse == null ? response : wrappedResponse;
        var context = context(request);
        var startedAt = System.nanoTime();
        var previousMdc = mdcSnapshot();
        HttpAuditContext.set(context);
        putMdc(context);
        response.setHeader(properties.getRequestIdHeaderName(), context.requestId());
        response.setHeader(properties.getCorrelationIdHeaderName(), context.correlationId());

        try {
            filterChain.doFilter(requestForChain, responseForChain);
        } finally {
            try {
                var elapsedMs = (System.nanoTime() - startedAt) / 1_000_000;
                eventLogger.inboundExchange(new HttpAuditEventLogger.InboundHttpAuditEvent(
                        context,
                        request.getMethod(),
                        request.getRequestURI(),
                        request.getQueryString(),
                        responseStatus(responseForChain),
                        elapsedMs,
                        requestHeaders(requestForChain),
                        requestBody(wrappedRequest),
                        mediaType(request.getContentType()),
                        responseHeaders(responseForChain),
                        responseBody(wrappedResponse),
                        mediaType(response.getContentType())
                ));
                if (wrappedResponse != null) {
                    wrappedResponse.copyBodyToResponse();
                }
            } finally {
                restoreMdc(previousMdc);
                HttpAuditContext.clear();
            }
        }
    }

    private int responseStatus(HttpServletResponse response) {
        return response.getStatus();
    }

    private byte[] requestBody(ContentCachingRequestWrapper request) {
        return request == null ? null : request.getContentAsByteArray();
    }

    private byte[] responseBody(ContentCachingResponseWrapper response) {
        return response == null ? null : response.getContentAsByteArray();
    }

    private HttpAuditContextSnapshot context(HttpServletRequest request) {
        var traceParent = HttpAuditIds.traceParent(request.getHeader(HttpAuditIds.TRACEPARENT_HEADER));
        var traceId = HttpAuditIds.traceId(traceParent.traceId());
        var correlationId = HttpAuditIds.correlationId(
                request.getHeader(properties.getCorrelationIdHeaderName()),
                traceId
        );
        return new HttpAuditContextSnapshot(
                correlationId,
                traceId,
                HttpAuditIds.randomSpanId(),
                traceParent.parentSpanId(),
                HttpAuditIds.requestId(request.getHeader(properties.getRequestIdHeaderName())),
                traceParent.flags()
        );
    }

    private Map<String, String> mdcSnapshot() {
        var snapshot = new HashMap<String, String>();
        for (var key : mdcKeys()) {
            snapshot.put(key, MDC.get(key));
        }
        return snapshot;
    }

    private void putMdc(HttpAuditContextSnapshot context) {
        MDC.put("correlationId", context.correlationId());
        MDC.put("traceId", context.traceId());
        MDC.put("spanId", context.spanId());
        if (StringUtils.hasText(context.parentSpanId())) {
            MDC.put("parentSpanId", context.parentSpanId());
        }
        MDC.put("requestId", context.requestId());
    }

    private void restoreMdc(Map<String, String> previousMdc) {
        for (var key : mdcKeys()) {
            var previousValue = previousMdc.get(key);
            if (previousValue == null) {
                MDC.remove(key);
            } else {
                MDC.put(key, previousValue);
            }
        }
    }

    private java.util.List<String> mdcKeys() {
        return java.util.List.of("correlationId", "traceId", "spanId", "parentSpanId", "requestId");
    }

    private HttpHeaders requestHeaders(HttpServletRequest request) {
        var headers = new HttpHeaders();
        var names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            var name = names.nextElement();
            headers.addAll(name, java.util.Collections.list(request.getHeaders(name)));
        }
        return headers;
    }

    private HttpHeaders responseHeaders(HttpServletResponse response) {
        var headers = new HttpHeaders();
        response.getHeaderNames().forEach(name -> headers.addAll(name, new ArrayList<>(response.getHeaders(name))));
        return headers;
    }

    private MediaType mediaType(String value) {
        try {
            return value == null ? null : MediaType.parseMediaType(value);
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
