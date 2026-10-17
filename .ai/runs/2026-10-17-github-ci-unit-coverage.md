# GitHub CI i pełne jednostkowe pokrycie linii Java

Data: 2026-10-17.

## Cel

Joby PR: build, lint, testy jednostkowe i integracyjne z PostgreSQL/Testcontainers.
100% istniejących wykonywalnych linii Java pokrytych jednostkowo oraz obowiązkowa
zasada dla każdej nowej funkcji. Bez zmiany produktu, API i schematu danych.

## Działania i zmienione pliki

- `.github/workflows/verify.yml`: cztery niezależne joby, Java 25, cache Maven,
  wymagany Docker, raporty jako artefakty, anulowanie nieaktualnych przebiegów,
  minimalne uprawnienia read. Zdarzenia push i pull_request.
- `pom.xml`, `backend/pom.xml`, `asr/pom.xml`, `request-audit/pom.xml`:
  wspólny parent z zachowaniem Spring Boot 4.1.0, Surefire/Failsafe,
  oddzielne dane i raporty JaCoCo unit/integration; bramka LINE MISSEDCOUNT=0.
- `config/checkstyle.xml`: lint kodu produkcyjnego i testów. Poprawione importy
  w `TranscriptionEntity`, `RecipeController` i `RecipeExceptionHandler`.
- `Makefile`, `backend/Dockerfile`, `asr/Dockerfile`: wspólna konfiguracja lint
  dostępna w obrazach, lokalne odpowiedniki jobów; instalacja biblioteki także
  instaluje parent POM.
- `AGENTS.md`, `.ai/docs/engineering-standards.md`: obowiązek 100% linii z unit tests.
- `docs/development.md`, `docs/refactoring-plan.md` i README trzech modułów:
  komendy, frameworki, rozdzielenie testów, raporty, zakres Java i wymagane checki PR.
- Nowe testy jednostkowe: entrypointy aplikacji, konfiguracja, kontrolery/handlery,
  parsowanie PATCH i jednostek, mapowanie persystencji i błędów JPA, OpenAI przez
  MockRestServiceServer, kontekst/identyfikatory audytu, filtry/interceptory i logger.
  Rozszerzone testy CategoryService, RecipeService, TranscriptionService,
  MockAsrProviderAdapter i HttpAuditSanitizer.
- `OpenAiAsrProviderAdapterIntegrationTest`: zachowane dotychczasowe testy lokalnego
  serwera HTTP; `BackendPersistenceSchemaIntegrationTest`: istniejący test Spring/H2
  przeniesiony do Failsafe przez zmianę nazwy. Nie usuwano istniejących testów.
- Oba `PostgreSqlIntegrationTest`: RANDOM_PORT i rzeczywisty HTTP health check
  aplikacji korzystającej z kontenera PostgreSQL.
- Po testach charakteryzacyjnych usunięto nieosiągalny catch `saveAll` w
  `JpaCategoryRepository` oraz nieosiągalny końcowy warunek w `HttpAuditSanitizer`.
  Błędy zapisu nadal mapuje `save`; reguły logowania treści pozostają takie same.
- Plan przeniesiono do `.ai/specs/implemented/2026-10-17-github-ci-unit-coverage.md`;
  trwały wniosek o rozdzieleniu danych pokrycia zapisano w `.ai/lessons.md`.

## Weryfikacja

Baseline `mvn --batch-mode --no-transfer-progress clean verify`: PASS, 72 testy.

Dokładne polecenia jobów uruchomiono lokalnie, kolejno, z katalogu głównego:

| Polecenie | Wynik |
| --- | --- |
| `mvn --batch-mode --no-transfer-progress clean package -DskipTests` | PASS |
| `mvn --batch-mode --no-transfer-progress validate` | PASS, 0 naruszeń |
| `mvn --batch-mode --no-transfer-progress clean verify -DskipITs` | PASS, 83 unit tests |
| `docker info` | PASS |
| `make install-local-libs` | PASS, parent i biblioteka zainstalowane |
| `mvn --batch-mode --no-transfer-progress clean verify` | PASS, 112 testów |
| `actionlint .github/workflows/verify.yml` (1.7.12) | PASS |
| `git diff --check` | PASS |

W tym środowisku Maven uruchomiono z
`DEVELOPER_DIR=/Library/Developer/CommandLineTools`, bez zmiany globalnych ustawień Xcode.
JUnit: 83 jednostkowe i 29 integracyjnych, 0 failures/errors/skipped; ArchUnit: 7.
Integracje użyły PostgreSQL 17.11 i odpowiedzi HTTP 200 obu usług na losowych portach.
Testcontainers posprzątał kontenery po zakończeniu testów.

| Moduł | Linie jednostkowo | Gałęzie jednostkowo |
| --- | --- | --- |
| request-audit | 416/416 (100%) | 169/174 |
| ASR | 173/173 (100%) | 40/40 |
| backend | 430/430 (100%) | 159/165 |

Wynik pochodzi wyłącznie z `jacoco-unit.exec`; integracje nie uzupełniają licznika.
Nie dodano wykluczeń pokrycia. Raporty HTML/XML są w `MODUŁ/target/site/jacoco-unit/`
i `jacoco-integration/`, wyniki JUnit w surefire-reports/failsafe-reports.

Próby negatywne: tymczasowy plik z tabulatorem i nieużywanym importem zatrzymał
`mvn validate`; poprawny składniowo plik bez testu zatrzymał
`mvn -pl request-audit clean verify -DskipITs` na JaCoCo (2 niepokryte linie,
maximum 0). Plik próbny usunięty, następnie cały zestaw jobów uruchomiony ponownie.
Logi lokalne zapisano w `/tmp/mealio-ci-*.log`.

## Granice weryfikacji i otwarte kwestie

Workflow zweryfikowano lokalnie, bez push i bez przebiegu na runnerze GitHub Actions.
Włączenie wymaganych checków w ochronie gałęzi jest ustawieniem GitHub opisanym
w docs/development.md. Nie zmieniano go w tym zadaniu.
Prototyp SwiftUI nie ma projektu Xcode ani targetu builda; CI obejmuje kod Java.
Zmiany użytkownika zastane w README.md oraz górnej części AGENTS.md zachowano.
