package com.mealio.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

public class HttpAuditEventLogger {

    private static final Logger log = LoggerFactory.getLogger(HttpAuditEventLogger.class);

    private final ObjectMapper objectMapper;
    private final HttpAuditProperties properties;
    private final HttpAuditSanitizer sanitizer;

    public HttpAuditEventLogger(
            ObjectMapper objectMapper,
            HttpAuditProperties properties,
            HttpAuditSanitizer sanitizer
    ) {
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.sanitizer = sanitizer;
    }

    public void inboundExchange(InboundHttpAuditEvent event) {
        var payload = base("http.exchange", event.context());
        payload.put("http", Map.of(
                "method", event.method(),
                "path", event.path(),
                "query", sanitizer.query(event.queryString()),
                "statusCode", event.statusCode(),
                "durationMs", event.durationMs()
        ));
        payload.put("outcome", outcome(event.statusCode()));
        payload.put("request", httpPayload(event.requestHeaders(), event.requestBody(), event.requestContentType(), false));
        payload.put("response", httpPayload(event.responseHeaders(), event.responseBody(), event.responseContentType(), false));
        write(payload);
    }

    public void outboundRequest(OutboundHttpAuditRequestEvent event) {
        var payload = base("http.call.request", event.context());
        payload.put("http", http(event.method(), event.uri(), null, null));
        payload.put("peer", peer(event.uri()));
        payload.put("request", httpPayload(event.headers(), event.body(), event.contentType(), false));
        write(payload);
    }

    public void outboundResponse(OutboundHttpAuditResponseEvent event) {
        var payload = base("http.call.response", event.context());
        payload.put("http", http(event.method(), event.uri(), event.statusCode().value(), event.durationMs()));
        payload.put("peer", peer(event.uri()));
        payload.put("outcome", outcome(event.statusCode()));
        payload.put("response", httpPayload(event.headers(), event.body(), event.contentType(), true));
        write(payload);
    }

    public void outboundError(OutboundHttpAuditErrorEvent event) {
        var payload = base("http.call.error", event.context());
        payload.put("http", http(event.method(), event.uri(), null, event.durationMs()));
        payload.put("peer", peer(event.uri()));
        payload.put("outcome", "network_error");
        payload.put("error", Map.of(
                "type", event.exception().getClass().getName(),
                "message", event.exception().getMessage() == null ? "" : event.exception().getMessage()
        ));
        write(payload);
    }

    private Map<String, Object> base(String eventType, HttpAuditContextSnapshot context) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("timestamp", Instant.now().toString());
        payload.put("eventType", eventType);
        payload.put("serviceName", properties.getServiceName());
        payload.put("environment", properties.getEnvironment());
        payload.put("correlationId", context.correlationId());
        payload.put("traceId", context.traceId());
        payload.put("spanId", context.spanId());
        payload.put("parentSpanId", context.parentSpanId());
        payload.put("requestId", context.requestId());
        return payload;
    }

    private Map<String, Object> http(HttpMethod method, URI uri, Integer statusCode, Long durationMs) {
        var http = new LinkedHashMap<String, Object>();
        http.put("method", method.name());
        http.put("scheme", uri.getScheme());
        http.put("host", uri.getHost());
        http.put("path", uri.getPath());
        http.put("query", sanitizer.query(uri.getRawQuery()));
        if (statusCode != null) {
            http.put("statusCode", statusCode);
        }
        if (durationMs != null) {
            http.put("durationMs", durationMs);
        }
        return http;
    }

    private Map<String, Object> peer(URI uri) {
        var peer = new LinkedHashMap<String, Object>();
        peer.put("service", peerService(uri.getHost()));
        peer.put("host", uri.getHost());
        peer.put("port", uri.getPort());
        return peer;
    }

    private String peerService(String host) {
        if (host == null) {
            return "unknown";
        }
        if (host.contains("openai.com")) {
            return "openai";
        }
        if (host.contains("asr")) {
            return "mealio-asr";
        }
        if (host.contains("backend") || host.contains("app")) {
            return "mealio-backend";
        }
        return host;
    }

    private Map<String, Object> httpPayload(
            HttpHeaders headers,
            byte[] body,
            MediaType contentType,
            boolean includeProviderRequestId
    ) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("contentType", contentType == null ? null : contentType.toString());
        payload.put("contentLength", contentLength(headers));
        payload.put("bodyBytes", body == null ? null : body.length);
        payload.put("bodyLogged", sanitizer.bodyLogged(body, contentType));
        var bodyOmittedReason = sanitizer.bodyOmittedReason(body, contentType);
        if (bodyOmittedReason != null) {
            payload.put("bodyOmittedReason", bodyOmittedReason);
        }
        var providerRequestId = includeProviderRequestId ? providerRequestId(headers) : null;
        if (providerRequestId != null) {
            payload.put("providerRequestId", providerRequestId);
        }
        if (properties.isIncludeHeaders()) {
            payload.put("headers", sanitizer.headers(headers));
        }
        if (sanitizer.bodyLogged(body, contentType)) {
            payload.put("body", sanitizer.body(body, contentType));
        }
        return payload;
    }

    private Long contentLength(HttpHeaders headers) {
        var contentLength = headers.getContentLength();
        return contentLength >= 0 ? contentLength : null;
    }

    private String providerRequestId(HttpHeaders headers) {
        var openAiRequestId = headers.getFirst("x-request-id");
        if (openAiRequestId != null) {
            return openAiRequestId;
        }
        return headers.getFirst("X-Request-ID");
    }

    private String outcome(HttpStatusCode statusCode) {
        return outcome(statusCode.value());
    }

    private String outcome(int statusCode) {
        if (statusCode >= 500) {
            return "server_error";
        }
        if (statusCode >= 400) {
            return "client_error";
        }
        return "success";
    }

    private void write(Map<String, Object> payload) {
        try {
            log.info(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException exception) {
            log.warn("http.audit.event.serialization_failed eventType={}", payload.get("eventType"), exception);
        }
    }

    public record InboundHttpAuditEvent(
            HttpAuditContextSnapshot context,
            String method,
            String path,
            String queryString,
            int statusCode,
            long durationMs,
            HttpHeaders requestHeaders,
            byte[] requestBody,
            MediaType requestContentType,
            HttpHeaders responseHeaders,
            byte[] responseBody,
            MediaType responseContentType
    ) {
    }

    public record OutboundHttpAuditRequestEvent(
            HttpAuditContextSnapshot context,
            HttpMethod method,
            URI uri,
            HttpHeaders headers,
            byte[] body,
            MediaType contentType
    ) {
    }

    public record OutboundHttpAuditResponseEvent(
            HttpAuditContextSnapshot context,
            HttpMethod method,
            URI uri,
            HttpStatusCode statusCode,
            long durationMs,
            HttpHeaders headers,
            byte[] body,
            MediaType contentType
    ) {
    }

    public record OutboundHttpAuditErrorEvent(
            HttpAuditContextSnapshot context,
            HttpMethod method,
            URI uri,
            long durationMs,
            IOException exception
    ) {
    }
}
