package com.mealio.audit;

import java.util.Optional;

public final class HttpAuditContext {

    private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();

    private HttpAuditContext() {
    }

    public static Optional<String> requestId() {
        return Optional.ofNullable(REQUEST_ID.get());
    }

    static void setRequestId(String requestId) {
        REQUEST_ID.set(requestId);
    }

    static void clear() {
        REQUEST_ID.remove();
    }
}
