package com.mealio.audit;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mealio.audit.http")
public class HttpAuditProperties {

    private boolean enabled = true;
    private boolean includeBodies = true;
    private boolean propagateRequestId = true;
    private int maxBodyBytes = 2048;
    private String requestIdHeaderName = "X-Request-Id";
    private Set<String> redactedHeaders = Set.of(
            "authorization",
            "cookie",
            "set-cookie",
            "x-api-key",
            "openai-api-key"
    );

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isIncludeBodies() {
        return includeBodies;
    }

    public void setIncludeBodies(boolean includeBodies) {
        this.includeBodies = includeBodies;
    }

    public boolean isPropagateRequestId() {
        return propagateRequestId;
    }

    public void setPropagateRequestId(boolean propagateRequestId) {
        this.propagateRequestId = propagateRequestId;
    }

    public int getMaxBodyBytes() {
        return maxBodyBytes;
    }

    public void setMaxBodyBytes(int maxBodyBytes) {
        this.maxBodyBytes = maxBodyBytes;
    }

    public String getRequestIdHeaderName() {
        return requestIdHeaderName;
    }

    public void setRequestIdHeaderName(String requestIdHeaderName) {
        this.requestIdHeaderName = requestIdHeaderName;
    }

    public Set<String> getRedactedHeaders() {
        return redactedHeaders;
    }

    public void setRedactedHeaders(Set<String> redactedHeaders) {
        this.redactedHeaders = redactedHeaders.stream()
                .map(header -> header.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }
}
