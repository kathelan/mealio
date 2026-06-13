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
