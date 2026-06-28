package com.mealio.audit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

public class InboundHttpAuditFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(InboundHttpAuditFilter.class);

    private final HttpAuditProperties properties;
    private final HttpAuditSanitizer sanitizer;

    public InboundHttpAuditFilter(HttpAuditProperties properties, HttpAuditSanitizer sanitizer) {
        this.properties = properties;
        this.sanitizer = sanitizer;
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

        var wrappedRequest = new ContentCachingRequestWrapper(request, properties.getMaxBodyBytes());
        var wrappedResponse = new ContentCachingResponseWrapper(response);
        var requestId = requestId(request);
        var startedAt = System.nanoTime();
        HttpAuditContext.setRequestId(requestId);
        MDC.put("requestId", requestId);
        response.setHeader(properties.getRequestIdHeaderName(), requestId);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            try {
                var elapsedMs = (System.nanoTime() - startedAt) / 1_000_000;
                log.info(
                        "http.inbound.exchange requestId={} method={} uri={} status={} durationMs={} requestHeaders={} requestBody={} responseHeaders={} responseBody={}",
                        requestId,
                        request.getMethod(),
                        requestUri(request),
                        wrappedResponse.getStatus(),
                        elapsedMs,
                        sanitizer.headers(requestHeaders(wrappedRequest)),
                        sanitizer.body(wrappedRequest.getContentAsByteArray(), mediaType(request.getContentType())),
                        sanitizer.headers(responseHeaders(wrappedResponse)),
                        sanitizer.body(wrappedResponse.getContentAsByteArray(), mediaType(response.getContentType()))
                );
                wrappedResponse.copyBodyToResponse();
            } finally {
                MDC.remove("requestId");
                HttpAuditContext.clear();
            }
        }
    }

    private String requestId(HttpServletRequest request) {
        var requestId = request.getHeader(properties.getRequestIdHeaderName());
        return StringUtils.hasText(requestId) ? requestId : UUID.randomUUID().toString();
    }

    private String requestUri(HttpServletRequest request) {
        var queryString = request.getQueryString();
        return queryString == null ? request.getRequestURI() : request.getRequestURI() + "?" + queryString;
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
