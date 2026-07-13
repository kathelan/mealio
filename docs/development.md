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
6. add recipe use cases once category management works end to end - next

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
