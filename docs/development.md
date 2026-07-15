# Development Notes

## Working style

- keep the codebase small until the first use case works end to end
- document decisions once, briefly, and keep them updated
- prefer one compact source of truth in `docs/`

## Suggested workflow

1. initialize the backend project
2. add Docker and PostgreSQL locally
3. expose health probes
4. add backend persistence for recipes and configurable category trees
5. add the first category use cases, ports, adapters, and REST endpoints - done
6. add recipe use cases, persistence adapters, REST endpoints, and tests - done
7. publish the category, recipe, and ingredient API as `docs/openapi/backend.yaml` - done
8. define the meal-parser and nutrition-resolver OpenAPI contracts - next
9. implement transcript-to-meal proposals with calorie calculation and provenance
10. generate REST clients and add service-to-service contract tests
11. add user confirmation before proposed data is persisted by the backend

## Commands

```bash
cd backend
mvn test
```

```bash
docker compose up --build
```

## Notes on token usage

Keep this repository documentation concise. Update the same files rather than creating many
parallel notes, so future Codex sessions can read less and do more.
