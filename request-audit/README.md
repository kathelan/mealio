# Request Audit

Reusable HTTP request/response audit library for Mealio services.

It provides:

- inbound servlet request/response audit logging,
- outbound `RestClient` request/response audit logging,
- `X-Request-Id` correlation across inbound logs, outbound logs, and propagated calls,
- sensitive header redaction,
- body previews only for text-like content,
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
      enabled: true
      include-bodies: true
      max-body-bytes: 2048
      request-id-header-name: X-Request-Id
      propagate-request-id: true
```
