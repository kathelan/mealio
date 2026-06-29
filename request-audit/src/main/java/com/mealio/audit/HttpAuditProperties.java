package com.mealio.audit;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mealio.audit.http")
public class HttpAuditProperties {

    private boolean enabled = true;
    private boolean includeHeaders = false;
    private boolean includeBodies = false;
    private boolean propagateRequestId = true;
    private boolean propagateCorrelationId = true;
    private boolean propagateTraceContext = true;
    private boolean includeQueryString = false;
    private int maxBodyBytes = 2048;
    private String serviceName = "unknown";
    private String environment = "local";
    private String requestIdHeaderName = "X-Request-Id";
    private String correlationIdHeaderName = "X-Correlation-Id";
    private Set<String> redactedHeaders = Set.of(
            "authorization",
            "proxy-authorization",
            "cookie",
            "set-cookie",
            "api-key",
            "x-api-key",
            "openai-api-key",
            "openai-organization",
            "openai-project",
            "x-openai-api-key",
            "x-auth-token",
            "x-access-token",
            "x-csrf-token",
            "x-xsrf-token",
            "client-secret",
            "session",
            "x-session-id"
    );
    private Set<String> redactedQueryParameters = Set.of(
            "token",
            "access_token",
            "refresh_token",
            "code",
            "key",
            "api_key",
            "client_secret",
            "password",
            "email",
            "phone",
            "session",
            "jwt",
            "signature"
    );

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
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

    public boolean isPropagateCorrelationId() {
        return propagateCorrelationId;
    }

    public void setPropagateCorrelationId(boolean propagateCorrelationId) {
        this.propagateCorrelationId = propagateCorrelationId;
    }

    public boolean isPropagateTraceContext() {
        return propagateTraceContext;
    }

    public void setPropagateTraceContext(boolean propagateTraceContext) {
        this.propagateTraceContext = propagateTraceContext;
    }

    public boolean isIncludeQueryString() {
        return includeQueryString;
    }

    public void setIncludeQueryString(boolean includeQueryString) {
        this.includeQueryString = includeQueryString;
    }

    public int getMaxBodyBytes() {
        return maxBodyBytes;
    }

    public void setMaxBodyBytes(int maxBodyBytes) {
        this.maxBodyBytes = maxBodyBytes;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getRequestIdHeaderName() {
        return requestIdHeaderName;
    }

    public void setRequestIdHeaderName(String requestIdHeaderName) {
        this.requestIdHeaderName = requestIdHeaderName;
    }

    public String getCorrelationIdHeaderName() {
        return correlationIdHeaderName;
    }

    public void setCorrelationIdHeaderName(String correlationIdHeaderName) {
        this.correlationIdHeaderName = correlationIdHeaderName;
    }

    public Set<String> getRedactedHeaders() {
        return redactedHeaders;
    }

    public void setRedactedHeaders(Set<String> redactedHeaders) {
        this.redactedHeaders = redactedHeaders.stream()
                .map(header -> header.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> getRedactedQueryParameters() {
        return redactedQueryParameters;
    }

    public void setRedactedQueryParameters(Set<String> redactedQueryParameters) {
        this.redactedQueryParameters = redactedQueryParameters.stream()
                .map(parameter -> parameter.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }
}
