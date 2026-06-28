package com.mealio.audit;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

public class HttpAuditSanitizer {

    private static final String REDACTED = "<redacted>";
    private static final String BINARY_BODY = "<binary body omitted>";
    private static final String EMPTY_BODY = "";

    private final HttpAuditProperties properties;

    public HttpAuditSanitizer(HttpAuditProperties properties) {
        this.properties = properties;
    }

    public Map<String, List<String>> headers(HttpHeaders headers) {
        var sanitized = new LinkedHashMap<String, List<String>>();
        headers.forEach((name, values) -> {
            if (properties.getRedactedHeaders().contains(name.toLowerCase())) {
                sanitized.put(name, List.of(REDACTED));
            } else {
                sanitized.put(name, values);
            }
        });
        return sanitized;
    }

    public String body(byte[] body, MediaType contentType) {
        if (!properties.isIncludeBodies() || body == null || body.length == 0) {
            return EMPTY_BODY;
        }
        if (!isTextLike(contentType)) {
            return BINARY_BODY;
        }

        var length = Math.min(body.length, properties.getMaxBodyBytes());
        var preview = new String(body, 0, length, StandardCharsets.UTF_8);
        return body.length > length ? preview + "...<truncated>" : preview;
    }

    private boolean isTextLike(MediaType contentType) {
        if (contentType == null) {
            return false;
        }
        var subtype = contentType.getSubtype();
        return MediaType.APPLICATION_JSON.includes(contentType)
                || MediaType.APPLICATION_XML.includes(contentType)
                || MediaType.TEXT_PLAIN.includes(contentType)
                || (StringUtils.hasText(subtype) && subtype.endsWith("+json"));
    }
}
