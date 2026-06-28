package com.mealio.audit;

public record HttpAuditContextSnapshot(
        String correlationId,
        String traceId,
        String spanId,
        String parentSpanId,
        String requestId
) {
}
