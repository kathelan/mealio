package com.mealio.audit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class HttpAuditInfrastructureTest {
    private final HttpAuditProperties properties = new HttpAuditProperties();
    private final HttpAuditEventLogger logger = mock(HttpAuditEventLogger.class);
    private final HttpAuditContextSnapshot context = new HttpAuditContextSnapshot(
            "correlation", "1234567890abcdef1234567890abcdef", "1234567890abcdef", null, "request", "01");

    @AfterEach
    void clearContext() {
        HttpAuditContext.clear();
        MDC.clear();
    }

    @Test
    void propertiesRoundTripAndNormalizeRedactionNames() {
        assertThat(properties.isEnabled()).isTrue();
        properties.setEnabled(false);
        properties.setIncludeHeaders(true);
        properties.setIncludeBodies(true);
        properties.setPropagateRequestId(false);
        properties.setPropagateCorrelationId(false);
        properties.setPropagateTraceContext(false);
        properties.setIncludeQueryString(true);
        properties.setMaxBodyBytes(10);
        properties.setServiceName("backend");
        properties.setEnvironment("test");
        properties.setRequestIdHeaderName("Request");
        properties.setCorrelationIdHeaderName("Correlation");
        properties.setRedactedHeaders(Set.of("SECRET"));
        properties.setRedactedQueryParameters(Set.of("TOKEN"));
        assertThat(properties.isEnabled()).isFalse();
        assertThat(properties.isIncludeHeaders()).isTrue();
        assertThat(properties.isIncludeBodies()).isTrue();
        assertThat(properties.isPropagateRequestId()).isFalse();
        assertThat(properties.isPropagateCorrelationId()).isFalse();
        assertThat(properties.isPropagateTraceContext()).isFalse();
        assertThat(properties.isIncludeQueryString()).isTrue();
        assertThat(properties.getMaxBodyBytes()).isEqualTo(10);
        assertThat(properties.getServiceName()).isEqualTo("backend");
        assertThat(properties.getEnvironment()).isEqualTo("test");
        assertThat(properties.getRequestIdHeaderName()).isEqualTo("Request");
        assertThat(properties.getCorrelationIdHeaderName()).isEqualTo("Correlation");
        assertThat(properties.getRedactedHeaders()).containsExactly("secret");
        assertThat(properties.getRedactedQueryParameters()).containsExactly("token");
    }

    @Test
    void autoConfigurationCreatesUsableComponents() throws Exception {
        var config = new HttpAuditAutoConfiguration();
        var sanitizer = config.httpAuditSanitizer(properties);
        var mapper = config.httpAuditObjectMapper();
        var eventLogger = config.httpAuditEventLogger(mapper, properties, sanitizer);
        assertThat(mapper.readTree("{}").isObject()).isTrue();
        assertThat(config.outboundHttpAuditInterceptor(properties, eventLogger))
                .isInstanceOf(OutboundHttpAuditInterceptor.class);
        assertThat(config.inboundHttpAuditFilter(properties, eventLogger)).isNotNull();
    }

    @Test
    void contextIsScopedAndIdentifiersRejectUnsafeValues() {
        assertThat(HttpAuditContext.current()).isEmpty();
        assertThat(HttpAuditContext.correlationId()).isEmpty();
        HttpAuditContext.set(context);
        assertThat(HttpAuditContext.current()).contains(context);
        assertThat(HttpAuditContext.correlationId()).contains("correlation");
        assertThat(HttpAuditContext.requestId()).contains("request");
        assertThat(HttpAuditIds.requestId("safe-1")).isEqualTo("safe-1");
        assertThat(HttpAuditIds.requestId("unsafe value")).matches("[0-9a-f-]{36}");
        assertThat(HttpAuditIds.correlationId(null, "trace")).isEqualTo("trace");
        assertThat(HttpAuditIds.correlationId(null, null)).matches("[0-9a-f-]{36}");
        assertThat(HttpAuditIds.traceId(context.traceId())).isEqualTo(context.traceId());
        assertThat(HttpAuditIds.spanId(context.spanId())).isEqualTo(context.spanId());
        assertThat(HttpAuditIds.spanId(null)).matches("[0-9a-f]{16}");
        assertThat(HttpAuditIds.spanId("0".repeat(16))).isNotEqualTo("0".repeat(16));
        assertThat(HttpAuditIds.traceId("0".repeat(32))).isNotEqualTo("0".repeat(32));
        for (var invalid : new String[]{"", "wrong", "01-a-b-01", "00-invalid-invalid-01",
                "00-" + context.traceId() + "-invalid-01"}) {
            assertThat(HttpAuditIds.traceParent(invalid).traceId()).isNull();
        }
        var parsed = HttpAuditIds.traceParent("00-" + context.traceId() + "-" + context.spanId() + "-zz");
        assertThat(parsed.flags()).isEqualTo("01");
        assertThat(HttpAuditIds.traceParentHeader(new HttpAuditContextSnapshot(
                "c", context.traceId(), context.spanId(), null, "r", null))).endsWith("-01");
        var headers = new HttpHeaders();
        HttpAuditIds.putIfAbsent(headers, "X", null);
        assertThat(headers.isEmpty()).isTrue();
        HttpAuditIds.putIfAbsent(headers, "X", "first");
        HttpAuditIds.putIfAbsent(headers, "X", "second");
        assertThat(headers.getFirst("X")).isEqualTo("first");
    }

    @Test
    void inboundCapturesBodiesRestoresMdcAndCleansContextOnFailure() throws Exception {
        properties.setIncludeBodies(true);
        MDC.put("requestId", "previous");
        var request = new MockHttpServletRequest("POST", "/test");
        request.setContentType("text/plain");
        request.setContent("request".getBytes(StandardCharsets.UTF_8));
        request.addHeader("traceparent", HttpAuditIds.traceParentHeader(context));
        request.addHeader("X-Test", "value");
        var response = new MockHttpServletResponse();
        new InboundHttpAuditFilter(properties, logger).doFilter(request, response, (req, res) -> {
            assertThat(req.getInputStream().readAllBytes()).asString().isEqualTo("request");
            res.setContentType("text/plain");
            res.getOutputStream().write("response".getBytes(StandardCharsets.UTF_8));
        });
        assertThat(response.getContentAsString()).isEqualTo("response");
        var event = org.mockito.ArgumentCaptor.forClass(HttpAuditEventLogger.InboundHttpAuditEvent.class);
        verify(logger).inboundExchange(event.capture());
        assertThat(event.getValue().requestBody()).asString().isEqualTo("request");
        assertThat(event.getValue().responseBody()).asString().isEqualTo("response");
        assertThat(MDC.get("requestId")).isEqualTo("previous");
        assertThat(HttpAuditContext.current()).isEmpty();
        properties.setIncludeBodies(false);
        request = new MockHttpServletRequest("GET", "/failure");
        request.setContentType("invalid/type/value");
        var finalRequest = request;
        assertThatThrownBy(() -> new InboundHttpAuditFilter(properties, logger).doFilter(
                finalRequest, response, (req, res) -> { throw new IOException("broken"); }))
                .isInstanceOf(IOException.class);
        assertThat(HttpAuditContext.current()).isEmpty();
    }

    @Test
    void disabledFilterAndInterceptorPassThrough() throws Exception {
        properties.setEnabled(false);
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var chain = mock(jakarta.servlet.FilterChain.class);
        new InboundHttpAuditFilter(properties, logger).doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        var execution = mock(ClientHttpRequestExecution.class);
        var outgoing = new MockClientHttpRequest();
        var body = new byte[0];
        new OutboundHttpAuditInterceptor(properties, logger).intercept(outgoing, body, execution);
        verify(execution).execute(outgoing, body);
        verifyNoInteractions(logger);
    }

    @Test
    void outboundPropagatesContextAndLogsFailures() throws Exception {
        var request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://asr/test"));
        var interceptor = new OutboundHttpAuditInterceptor(properties, logger);
        HttpAuditContext.set(context);
        interceptor.intercept(request, new byte[0], (audited, body) -> {
            assertThat(audited.getHeaders().getFirst("X-Correlation-Id")).isEqualTo("correlation");
            assertThat(audited.getAttributes()).isEqualTo(request.getAttributes());
            return new MockClientHttpResponse(new byte[0], HttpStatus.OK);
        });
        HttpAuditContext.clear();
        properties.setPropagateRequestId(false);
        properties.setPropagateCorrelationId(false);
        properties.setPropagateTraceContext(false);
        var failure = new IOException("network");
        assertThatThrownBy(() -> interceptor.intercept(request, new byte[0], (audited, body) -> {
            assertThat(audited.getHeaders().isEmpty()).isTrue();
            throw failure;
        })).isSameAs(failure);
        verify(logger).outboundError(any());
    }

    @Test
    void eventLoggerEmitsStructuredRedactedPayloadsAndSurvivesSerializationFailure() throws Exception {
        properties.setIncludeBodies(true);
        properties.setIncludeHeaders(true);
        var mapper = spy(new ObjectMapper());
        var audit = new HttpAuditEventLogger(mapper, properties, new HttpAuditSanitizer(properties));
        var headers = new HttpHeaders();
        headers.setContentLength(2);
        headers.set("Authorization", "secret");
        headers.set("x-request-id", "provider-id");
        var bytes = "{}".getBytes(StandardCharsets.UTF_8);
        for (var uri : new String[]{"https://api.openai.com/test", "http://asr/test", "http://backend/test",
                "http://app/test", "http://example.com/test", "/relative"}) {
            audit.outboundRequest(new HttpAuditEventLogger.OutboundHttpAuditRequestEvent(
                    context, HttpMethod.POST, URI.create(uri), headers, bytes, MediaType.APPLICATION_JSON));
        }
        for (var status : new HttpStatus[]{HttpStatus.OK, HttpStatus.BAD_REQUEST, HttpStatus.BAD_GATEWAY}) {
            audit.outboundResponse(new HttpAuditEventLogger.OutboundHttpAuditResponseEvent(
                    context, HttpMethod.GET, URI.create("http://asr/test"), status, 5, headers, bytes,
                    MediaType.APPLICATION_JSON));
        }
        audit.outboundResponse(new HttpAuditEventLogger.OutboundHttpAuditResponseEvent(
                context, HttpMethod.GET, URI.create("http://asr/test"), HttpStatus.OK, 1,
                new HttpHeaders(), null, null));
        for (var error : new IOException[]{new IOException(), new IOException("network")}) {
            audit.outboundError(new HttpAuditEventLogger.OutboundHttpAuditErrorEvent(
                    context, HttpMethod.GET, URI.create("http://asr/test"), 4, error));
        }
        var payloads = org.mockito.ArgumentCaptor.forClass(Object.class);
        verify(mapper, atLeastOnce()).writeValueAsString(payloads.capture());
        assertThat(payloads.getAllValues().toString()).doesNotContain("secret").contains("<redacted>",
                "server_error", "client_error", "success", "provider-id", "network_error");
        doThrow(new JsonProcessingException("serialization") { }).when(mapper).writeValueAsString(any());
        assertThatCode(() -> audit.outboundError(new HttpAuditEventLogger.OutboundHttpAuditErrorEvent(
                context, HttpMethod.GET, URI.create("/"), 0, new IOException()))).doesNotThrowAnyException();
    }
}
