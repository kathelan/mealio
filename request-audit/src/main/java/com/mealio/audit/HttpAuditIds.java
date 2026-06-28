package com.mealio.audit;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

final class HttpAuditIds {

    static final String TRACEPARENT_HEADER = "traceparent";

    private static final Pattern SAFE_EXTERNAL_ID = Pattern.compile("[A-Za-z0-9._:-]{1,128}");
    private static final Pattern TRACE_ID = Pattern.compile("[0-9a-f]{32}");
    private static final Pattern SPAN_ID = Pattern.compile("[0-9a-f]{16}");
    private static final SecureRandom RANDOM = new SecureRandom();

    private HttpAuditIds() {
    }

    static String requestId(String candidate) {
        return safeExternalId(candidate) ? candidate : UUID.randomUUID().toString();
    }

    static String correlationId(String candidate, String fallbackTraceId) {
        if (safeExternalId(candidate)) {
            return candidate;
        }
        return StringUtils.hasText(fallbackTraceId) ? fallbackTraceId : UUID.randomUUID().toString();
    }

    static String traceId(String candidate) {
        return TRACE_ID.matcher(nullToEmpty(candidate)).matches() && !candidate.equals("00000000000000000000000000000000")
                ? candidate
                : randomHex(16);
    }

    static String spanId(String candidate) {
        return SPAN_ID.matcher(nullToEmpty(candidate)).matches() && !candidate.equals("0000000000000000")
                ? candidate
                : randomHex(8);
    }

    static String randomSpanId() {
        return randomHex(8);
    }

    static TraceParent traceParent(String value) {
        if (!StringUtils.hasText(value)) {
            return TraceParent.empty();
        }
        var parts = value.split("-");
        if (parts.length != 4 || !"00".equals(parts[0])) {
            return TraceParent.empty();
        }
        return new TraceParent(
                TRACE_ID.matcher(parts[1]).matches() ? parts[1] : null,
                SPAN_ID.matcher(parts[2]).matches() ? parts[2] : null,
                parts[3]
        );
    }

    static String traceParentHeader(HttpAuditContextSnapshot context) {
        return "00-%s-%s-01".formatted(context.traceId(), context.spanId());
    }

    static void putIfAbsent(HttpHeaders headers, String name, String value) {
        if (StringUtils.hasText(value) && !headers.containsHeader(name)) {
            headers.set(name, value);
        }
    }

    private static boolean safeExternalId(String value) {
        return SAFE_EXTERNAL_ID.matcher(nullToEmpty(value)).matches();
    }

    private static String randomHex(int bytes) {
        var value = new byte[bytes];
        RANDOM.nextBytes(value);
        return HexFormat.of().formatHex(value);
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    record TraceParent(String traceId, String parentSpanId, String flags) {

        static TraceParent empty() {
            return new TraceParent(null, null, null);
        }
    }
}
