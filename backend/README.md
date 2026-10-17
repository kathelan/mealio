# Backend

Backend Mealio oparty na Javie 25 i Spring Boot 4.

## Budowanie i testy

```bash
mvn -pl backend -am test
```

Pełna bramka z katalogu głównego: `mvn clean verify`. Po przenoszeniu klas JPA
używaj `clean`, aby stare pliki `.class` nie zostały wykryte jako dodatkowe encje.
Testy architektury (`ModuleArchitectureTest`) są częścią standardowego zestawu testów.

Testy wymagają działającego Dockera: `PostgreSqlIntegrationTest` uruchamia
PostgreSQL 17 przez Testcontainers, stosuje Liquibase na pustej bazie i sprawdza
mapowanie Hibernate, precyzję składników oraz ograniczenia i usuwanie powiązań.
Brak Dockera przerywa build; test nie jest pomijany. H2 pozostaje szybkim
środowiskiem istniejących testów HTTP.

## Uruchomienie w Dockerze

```bash
docker compose up --build
```

## Struktura

```text
src/main/java/com/mealio/
  category/
    api/           # publiczny odczyt identyfikatorów kategorii
    domain/
    application/
    infrastructure/
      web/
      persistence/
  recipe/
    domain/
    application/
    infrastructure/
      web/
      persistence/
  health/
    application/
    infrastructure/
  infrastructure/  # konfiguracja aplikacji
```

Tabela `meal_categories`, jej encja, mapper i repository należą do `category`.
Tabele `recipes`, `recipe_categories` i `recipe_ingredients` należą do `recipe`.
Przepis zapisuje identyfikatory kategorii jako UUID i sprawdza ich istnienie przez
`category.api.CategoryLookup`. Moduły mają własne bazowe encje i DTO HTTP.
Health zwraca wynik aplikacyjny mapowany przez swój adapter HTTP.

ArchUnit sprawdza granice bez wyjątków migracyjnych. Dalsze etapy opisuje
[plan migracji](../.ai/specs/2026-10-17-monorepo-migration.md).
