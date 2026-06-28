package com.mealio.audit;

import java.util.Optional;

public final class HttpAuditContext {

    private static final ThreadLocal<HttpAuditContextSnapshot> CONTEXT = new ThreadLocal<>();

    private HttpAuditContext() {
    }

    public static Optional<HttpAuditContextSnapshot> current() {
        return Optional.ofNullable(CONTEXT.get());
    }

    public static Optional<String> correlationId() {
        return current().map(HttpAuditContextSnapshot::correlationId);
    }

    public static Optional<String> requestId() {
        return current().map(HttpAuditContextSnapshot::requestId);
    }

    static void set(HttpAuditContextSnapshot context) {
        CONTEXT.set(context);
    }

    static void clear() {
        CONTEXT.remove();
    }
}
