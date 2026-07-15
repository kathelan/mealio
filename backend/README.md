# Backend

Backend Mealio oparty na Javie 25 i Spring Boot 4.

## Budowanie i testy

```bash
mvn -pl backend -am test
```

## Uruchomienie w Dockerze

```bash
docker compose up --build
```

## Struktura

```text
src/main/java/com/mealio/
  domain/
  application/
  adapter/
  infrastructure/
  shared/
```
