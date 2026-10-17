# Monorepo — faza 2: domknięcie granic recipe/category

Data: 2026-10-17.

Cel: wdrożyć fazę 2 aktywnej specyfikacji monorepo i usunąć wyjątki ArchUnit.

## Działania i pliki

- Przed przenosinami dodano testy serwisu recipe i usuwania kategorii przez HTTP.
  Wykryty błąd opcjonalnych kategorii naprawiono w osobnym commicie `b81c3a7`
  i opisano osobną wdrożoną specyfikacją oraz raportem.
- `category/api`, `CategoryLookupService`, port CategoryRepository i adapter JPA:
  publiczny odczyt istniejących UUID; publiczny błąd API oddziela domenę kategorii.
- `recipe/application` i port repozytorium: sprawdzanie kategorii przez CategoryLookup,
  mapowanie błędu odczytu na dotychczasowy RecipePersistenceException.
- `recipe/domain/model` i `recipe/infrastructure/persistence`: modele, encje i repository
  należą do recipe. RecipeEntity mapuje istniejącą tabelę powiązań przez kolekcję UUID;
  adapter nie zna JPA kategorii. Category i recipe mają własne BaseEntity.
- `category/infrastructure/web`, `recipe/infrastructure/web`: przeniesione kontrolery,
  obsługa błędów, DTO i testy HTTP; recipe ma własne DTO błędu o tym samym JSON.
- `health/application`, `health/infrastructure`: wynik aplikacyjny HealthStatus,
  konfiguracja serwisu i mapowanie HTTP. Usunięto zależności aplikacji od infrastruktury.
- Usunięto nieużywane globalne modele kategorii oraz puste katalogi po przenosinach.
- `ModuleArchitectureTest`: usunięto wszystkie wyjątki; reguła zakazuje powrotu
  globalnych warstw i starych adapterów. Testy lookup i błędu recipe zabezpieczają nowe API.
- Zaktualizowano README backendu, dokumentację bazy, plan refaktoryzacji i aktywną specyfikację.

## Weryfikacja

- Test usuwania kategorii oraz testy charakterystyki recipe przeszły przed przenosinami;
  niezależna poprawka NPE ma regresję serwisu i HTTP.
- Końcowe `mvn clean verify` poza sandboxem: BUILD SUCCESS, 67 testów
  (11 request-audit, 10 ASR, 46 backend), 0 failures/errors/skipped.
- Pięć testów ArchUnit przechodzi bez wyjątków; migracja Liquibase od pustej bazy H2
  i Hibernate validate przechodzą. Pokrycie linii backendu 86,57%, próg 80% zachowany.
- `git diff --check` i brak zmian OpenAPI oraz change setów: OK.
- Zachowano zastane zmiany użytkownika w AGENTS.md i głównym README.md poza commitami.

## Dalej

Faza 2 ukończona. Kolejna faza dodaje Testcontainers/PostgreSQL i automatyzację.
Mapowanie UUID jest sprawdzone na H2; zgodność z PostgreSQL wymaga jeszcze fazy 3.
