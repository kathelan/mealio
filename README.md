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
backend/
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

## Documentation

- [Architecture](docs/architecture.md)
- [MVP](docs/mvp.md)
- [Domain model](docs/domain.md)
- [API](docs/api.md)
- [Development notes](docs/development.md)

## Next step

Initialize the backend application, wire the health endpoints, and then add the first
vertical slice for recipes and categories.
