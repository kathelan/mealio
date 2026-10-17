# Backward compatibility policy

This file lists the repository's protected contract surfaces and the change path for each. If a change intentionally breaks a surface, call it out in the specification and PR, update its authoritative contract, and provide the migration or client transition needed before relying on the new behavior.

## Protected surfaces

| Surface | Breaking change examples | Required handling |
|---|---|---|
| Versioned HTTP APIs (`/api/v1` in backend and ASR) | Removing or renaming a route or field; changing field meaning, type, requiredness, status code, or error shape; changing multipart upload expectations | Update the relevant OpenAPI document under `docs/openapi/` and API docs. Prefer additive compatible changes within v1. For an intentional incompatible change, publish a new API version and document a client migration. |
| OpenAPI documents and generated-client inputs | Removing operations or schemas, changing required fields or enums incompatibly, or silently allowing the document to drift from runtime behavior | Review the contract diff alongside the implementation; keep examples and documentation current. Treat generated-client compatibility as part of the API change. |
| Public module APIs | Removing or changing signatures/semantics of contracts in module `api` packages, including `category.api.CategoryLookup` | Update every in-repository consumer in the same change. Prefer additive evolution; keep consumers independent of another module's internals. |
| PostgreSQL schema and stored data | Dropping/renaming columns or tables, narrowing accepted values, changing nullability, or changing persisted meaning so existing rows cannot be read | Add a new Liquibase change set; never edit an applied one. Use an expand/migrate/contract sequence when a rollout needs old and new code to coexist. Preserve the owning module and test migrations from an empty database and against existing data. |
| Runtime configuration and environment variables | Removing or renaming documented Spring properties or environment variables, changing defaults, or changing the meaning of a setting | Update deployment and developer documentation. For a transition, accept the old and new setting for a documented period and define precedence before removing the old name. |
| ASR audio upload and response format | Changing the transcription endpoint, accepted multipart field/media type, response fields, or error format | Update ASR OpenAPI and `docs/asr.md`; preserve the existing endpoint contract within its API version or introduce a new version for incompatible behavior. |

## Review and release expectations

- Describe the affected surface and compatibility impact in the specification and PR.
- Update OpenAPI, architecture/API documentation, and migration notes in the same change when relevant.
- Keep applied Liquibase migrations immutable. A schema migration must have an explicit owner and a safe path for existing data.
- Add contract or migration tests for externally observable changes; retain the unit-coverage gate for changed Java code.
- Do not introduce user identity or authentication as an implementation detail. ADR-0003 must be explicitly superseded first.
