# Code review guide

Review findings should identify the affected file and behavior, explain the consequence, and use these severities:

- **Blocker** — data loss, security exposure, broken build, or a contract break that prevents existing clients or persisted data from working.
- **Major** — incorrect user-visible behavior, violated domain or module boundary, or missing required validation/coverage.
- **Minor** — localized maintainability or clarity issue with no immediate behavior or contract impact.

## Review priorities

1. **Correctness and domain rules.** Check that domain invariants are enforced when objects are created or changed. Nutrition values and calculations must come from the catalog or a verified provider; AI may interpret meal text but must not invent nutrition values. Missing nutrition data is not zero.
2. **Module boundaries.** The backend is a modular monolith organized by business domain. A module may use another module only through its public `api`; it must not reach into another module's entities, repositories, or infrastructure. Domain code must not depend on Spring, JPA, HTTP, external clients, or transport DTOs.
3. **HTTP and compatibility.** Controllers translate transport and invoke use cases. Validate input at the boundary, keep stable error responses, preserve `/api/v1` behavior, and update `docs/openapi/` and API documentation when a contract changes. Do not expose JPA entities.
4. **Persistence and migrations.** Liquibase owns PostgreSQL schema changes. Never edit an applied change set; add a new migration with a clear domain owner. Check constraints, indexes, nullability, and compatibility with existing rows. Keep JPA entities and Spring Data repositories inside their owning module's infrastructure.
5. **Security and privacy.** Never add secrets, audio recordings, or sensitive data to source, logs, fixtures, or reports. Do not introduce authentication, a technical user, or `userId` without an accepted update to ADR-0003.
6. **Tests and maintainability.** New behavior and bug fixes need deterministic unit tests. Every new feature and changed Java code must meet 100% executable-line unit coverage from Surefire; integration tests cannot make up the difference. Keep abstractions tied to a real boundary, replaceable implementation, or test need.

## Repository checks

- Read `docs/architecture.md`, `.ai/docs/engineering-standards.md`, relevant module README files, and applicable ADRs for changes in those areas.
- Preserve builder validation for multi-field objects and use `Optional` for absent values in Java domain and public contracts; map nullable JSON/JPA values at adapter boundaries.
- Keep OpenAPI and documentation aligned with externally observable changes.
- Run the configured gate from `.ai/agentic.config.json`: `mvn clean verify`. CI also separates `mvn validate`, unit verification (`mvn clean verify -DskipITs`), and PostgreSQL/Testcontainers integration verification (`mvn clean verify -Pintegration-tests -DskipUnitTests`).
- For documentation-only changes, check links, commands, and consistency with the repository configuration.
