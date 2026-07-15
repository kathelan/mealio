# Architecture

Mealio starts as a backend-first mobile API with a hexagonal architecture.

## Goals

- keep the domain isolated from frameworks
- keep the first MVP small and testable
- make deployment on OpenShift straightforward
- keep documentation compact so it stays usable

## Suggested backend shape

```text
backend/src/main/java/com/mealio/
  domain/
    model/
    port/
      in/
      out/
  application/
    service/
  adapter/
    in/
      web/
    out/
      persistence/
  infrastructure/
    config/
    security/
    database/
    observability/
  shared/
    exception/
    validation/
```

## Design rules

- domain code does not depend on Spring
- application services orchestrate use cases
- adapters translate between HTTP, persistence, and domain objects
- OpenAPI and health probes live with the infrastructure layer

## Voice meal flow

Voice input is split into small services with separate responsibilities:

```text
audio
  -> ASR: transcript
  -> meal parser: proposed foods, quantities, units, and categories
  -> nutrition resolver: calories, nutrition data source, and confidence
  -> user confirmation
  -> backend: persist accepted categories, recipes, ingredients, or meals
```

ASR only converts speech to text. It must not create backend data or calculate
nutrition. The meal parser and nutrition resolver return proposals; only an explicit
confirmation request may persist their result.

## Service integration

REST integrations are contract-first. Each service owns an OpenAPI document for its
public API, and consumers should use generated clients and DTOs where practical.

- keep service-owned OpenAPI contracts in `docs/openapi/`
- use stable operation ids and shared error-envelope semantics
- propagate correlation and trace identifiers across every service call
- define explicit connect and response timeouts
- retry only operations that are safe or protected by idempotency keys
- verify provider and consumer compatibility with contract tests
- do not share persistence entities or service database tables across modules
