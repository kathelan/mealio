# Monorepo — faza 1: własność kategorii i kontrola granic

Data: 2026-10-17.

## Cel

Rozpocząć migrację rzeczywistym kodem według architektury i kolejności z
`docs/refactoring-plan.md`. W `.ai/specs` nie było specyfikacji; utworzono
`2026-10-17-monorepo-migration.md` z pięcioma fazami i kryteriami ukończenia.

## Wykonane działania i pliki

- `backend/src/test/java/com/mealio/category/`: cztery testy charakteryzacyjne
  przed przenosinami (kolejność i unikalność ścieżek, timestamps przy no-op,
  propagacja zmiany ścieżki w drzewie, odrzucone przenosiny bez zmiany zapisanych danych).
- `MealCategoryEntity` i `MealCategoryJpaRepository` przeniesione z globalnego
  `adapter/out/persistence` do `category/infrastructure/persistence/entity` i `repository`.
- Mapper kategorii oraz encja używają domenowego `CategoryType`; usunięto konwersję
  przez globalny enum. Wartości zapisywane jako STRING pozostają identyczne.
- Poprawione importy: adaptery JPA kategorii i przepisów, `RecipeEntity`, testy HTTP
  kategorii/przepisów i `BackendPersistenceSchemaTest`.
- `backend/pom.xml` oraz `architecture/ModuleArchitectureTest.java`: ArchUnit 1.4.1
  i pięć testów chroniących domenę, aplikację, dostęp do modułów i cykle biznesowe.
  Jawne wyjątki odpowiadają istniejącemu długowi; ich lista nie obejmuje całych pakietów.
- `backend/README.md`, `docs/refactoring-plan.md`: aktualna struktura i dalsze prace.
- Specyfikacja oraz `.ai/lessons.md`: postęp i trwały wniosek dotyczący przenosin JPA.

Zachowano zastane zmiany użytkownika w `AGENTS.md` i głównym `README.md`.
Nie zmieniono kontraktu HTTP, change setów, schematu ani reguł biznesowych.

## Weryfikacja

- Baseline `mvn verify` w sandboxie: błąd ASR przy otwieraniu lokalnych portów,
  `java.net.SocketException: Operation not permitted`. Ponowienie poza sandboxem:
  BUILD SUCCESS, 48 testów.
- `mvn -pl backend -am test` przed przenosinami, po dodaniu charakterystyki:
  BUILD SUCCESS; 31 testów backendu + 11 request-audit. Ponowienie poza sandboxem
  również przeszło.
- Pierwsze `mvn clean verify`: testy funkcjonalne przechodzą, dwie porażki ArchUnit
  ujawniły niepełny opis istniejących zależności. Skorygowano plan i dokładne wyjątki
  (DTO błędu recipe/category, health oraz właściciel odziedziczonego getId).
- Końcowe `mvn clean verify` poza sandboxem: BUILD SUCCESS; 57 testów,
  0 failures, 0 errors, 0 skipped. Backend: 36 testów, w tym 5 ArchUnit.
  Bramka JaCoCo LINE >= 80% przechodzi.
- Test schematu uruchamia Liquibase od pustej bazy H2 i Hibernate validate.
- `git diff --check`: OK; lokalne odnośniki zmienionej dokumentacji: OK.
- `git diff --exit-code -- docs/openapi backend/src/main/resources/db/changelog`: OK.

## Otwarte kwestie i następny etap

Faza 1 ukończona; cała migracja trwa. Następna jest faza 2: własność modelu
i persystencji recipe, publiczne API kategorii, oddzielenie DTO błędów, odwrócenie
zależności health, porządkowanie adapterów i usunięcie wyjątków ArchUnit.
PostgreSQL/Testcontainers, projekt iOS i przepływy nowego MVP pozostają kolejnymi fazami.
H2 nie potwierdza zgodności PostgreSQL. Pozostają dotychczasowe ostrzeżenia Mockito
o dynamicznym dołączaniu agenta i Lombok o Unsafe; nie blokują builda.
