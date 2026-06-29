# Request Audit

Reusable HTTP request/response audit library for Mealio services.

It provides:

- inbound servlet request/response audit logging,
- outbound `RestClient` request/response audit logging,
- `X-Correlation-Id`, `X-Request-Id`, and W3C `traceparent` propagation,
- structured JSON audit events,
- sensitive header redaction,
- body previews only when explicitly enabled and only for text-like content,
- binary body omission for uploads such as audio.

The library is packaged as a jar and consumed by service modules such as `asr` and
`backend`.

## Build

From the repository root:

```bash
mvn -pl request-audit test
```

Build a service together with the library:

```bash
mvn -pl asr -am test
```

## Configuration

```yaml
mealio:
  audit:
    http:
      service-name: mealio-asr
      environment: local
      enabled: true
      include-headers: false
      include-bodies: false
      max-body-bytes: 2048
      request-id-header-name: X-Request-Id
      correlation-id-header-name: X-Correlation-Id
      propagate-request-id: true
      propagate-correlation-id: true
      propagate-trace-context: true
      include-query-string: false
```

## Event Model

Each audit event is logged as JSON with stable correlation fields:

- `correlationId` identifies the whole flow across services.
- `traceId`, `spanId`, and `parentSpanId` describe the call tree.
- `requestId` identifies a single HTTP exchange.
- `serviceName`, `environment`, and `eventType` describe the source.
- `http.statusCode`, `http.durationMs`, and `outcome` describe the result.
- `peer.service` and `peer.host` describe external service calls.

Inbound requests use an existing safe `X-Correlation-Id`/`X-Request-Id` when present
or generate missing IDs. Outbound `RestClient` calls create a new local `requestId`
and child `spanId`, while propagating the flow-wide correlation and trace context.

Header and body logging are disabled by default. Normal audit events keep compact
metadata such as content type, content length, body size, status, duration, outcome,
and provider request IDs such as OpenAI `x-request-id`. Enable full headers or body
previews only for short-lived local diagnostics. Query values are redacted by default;
when query logging is enabled, sensitive parameters such as tokens, keys, email, and
password are still redacted.

Current context propagation is designed for synchronous Spring MVC flows using
`ThreadLocal` and MDC. Add an executor `TaskDecorator` before using this context
through `@Async`, custom executors, schedulers, or reactive/WebFlux flows.
