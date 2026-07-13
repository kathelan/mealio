# Database

Mealio uses PostgreSQL as the durable application store. Local development starts
PostgreSQL from Docker Compose, and later deployments should keep the same database
contract so moving from local Docker to OpenShift Sandbox and then to a VPS only
changes connection settings.

## Local Shape

The local database runs as the `postgres` service in `docker-compose.yml`:

```text
database: mealio
user: mealio
schemas:
  backend
  asr
```

The schemas isolate service-owned tables while keeping one PostgreSQL instance for the
MVP. The backend service uses the `backend` schema. The ASR service uses the `asr`
schema.

## Ownership

```text
backend schema:
  meal categories
  recipes and recipe ingredients
  future users
  future products and meals
  future meal diary

asr schema:
  transcription records
  audio metadata
  future provider audit metadata
```

Audio files are not stored durably in the MVP. The mobile app uploads audio, ASR sends
it to the transcription provider, the service stores the transcript and metadata, and
the audio bytes are discarded.

The backend schema currently stores nested meal categories, recipes, recipe-category
links, and recipe ingredients. Categories use a generated `slug` and slash-separated
`path`; deleting a category that still has children is rejected by the application so
the tree stays explicit and predictable.

## Migrations

Each service owns its Liquibase changelog:

```text
backend/src/main/resources/db/changelog/db.changelog-master.yaml
asr/src/main/resources/db/changelog/db.changelog-master.yaml
```

Hibernate must not create or update tables directly. Keep:

```yaml
spring.jpa.hibernate.ddl-auto: none
```

Schema changes go through Liquibase changesets.

## Running Locally

Start the stack:

```bash
docker compose up --build
```

Start only PostgreSQL:

```bash
docker compose up -d postgres
```

Inspect schemas:

```bash
docker compose exec postgres psql -U mealio -d mealio -c "\dn"
```

Reset local data:

```bash
docker compose down -v
docker compose up -d postgres
```

## Deployment Path

The planned path is:

```text
local Docker Compose -> OpenShift Sandbox -> VPS Docker Compose
```

OpenShift Sandbox can host a temporary PostgreSQL instance for demos, but it should be
treated as disposable. Before moving between environments, take a `pg_dump` and verify
restore.

On the VPS, PostgreSQL should live on a persistent Docker volume, with scheduled
encrypted backups copied outside the server.
