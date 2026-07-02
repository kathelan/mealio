# Mealio

Mealio is a mobile-first nutrition tracking platform. The first backend milestone is a
small, clean API for recipes, generic meal categories, and health checks, built with a
hexagonal architecture and prepared for deployment on OpenShift.

## Current focus

- backend-first initialization
- Spring Boot API on Java 25
- PostgreSQL persistence
- Dockerized development
- OpenShift-friendly health probes

## Repository layout

```text
asr/
backend/
request-audit/
docs/
infra/
docker-compose.yml
README.md
```

## Local development

```bash
docker compose up --build
```

The app exposes:

- `GET /api/v1/health`
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`

The standalone ASR module is in `asr/`:

```bash
mvn -pl asr -am spring-boot:run
```

Because service modules depend on local shared jars, prefer running Maven from the
repository root with `-pl <module> -am`.

Convenience targets:

```bash
make test-asr
make run-asr
make install-local-libs
```

Running Maven directly from a service directory, for example `cd asr && mvn test`,
uses artifacts from `~/.m2` and will not rebuild sibling modules automatically.
Install local shared jars first if you want to work that way:

```bash
make install-local-libs
cd asr && mvn test
```

## Documentation

- [Architecture](docs/architecture.md)
- [MVP](docs/mvp.md)
- [Domain model](docs/domain.md)
- [API](docs/api.md)
- [Database](docs/database.md)
- [ASR module](docs/asr.md)
- [Development notes](docs/development.md)

## Next step

Initialize the backend application, wire the health endpoints, and then add the first
vertical slice for recipes and categories.
