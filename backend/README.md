# Backend

Java 25 + Spring Boot 4 backend for Mealio.

## Build

```bash
mvn -pl backend -am test
```

## Run with Docker

```bash
docker compose up --build
```

## Structure

```text
src/main/java/com/mealio/
  domain/
  application/
  adapter/
  infrastructure/
  shared/
```
