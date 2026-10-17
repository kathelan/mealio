# Raport: usprawnienie etapów GitHub CI

Data: 2026-10-17

## Cel

Rozdzielić raporty JaCoCo i testy jednostkowe/integracyjne, dodać nazwany
profil Maven dla integracji oraz przygotować opcjonalny skan SonarQube.

## Wykonane działania

- Zaktualizowano akcje GitHub do checkout v7, setup-java v6 i upload-artifact v7;
  cache Maven jest używany we wszystkich jobach Java.
- Unit i integration publikują osobne artefakty z raportami HTML/XML JaCoCo
  oraz wynikami Surefire/Failsafe.
- Konfiguracja Failsafe i raportu JaCoCo integracji jest w profilu Maven
  `integration-tests`. Profil jest aktywny domyślnie, więc `mvn verify` nadal
  wykonuje pełny zestaw.
- Job integracyjny używa `-Pintegration-tests -DskipUnitTests`, by nie powielać
  unit tests. Próg 100% linii pozostaje sprawdzany w jobie unit.
- Skan SonarQube uruchamia się po unit tests na wygenerowanych klasach i raporcie
  JaCoCo. URL i klucz akceptują Actions variables lub secrets; token jest
  pobierany z sekretu. SonarQube Cloud wymaga także `SONAR_ORGANIZATION`.
- Zaktualizowano `Makefile` i `docs/development.md`.

## Zmienione pliki

- `.github/workflows/verify.yml`
- `pom.xml`
- `Makefile`
- `docs/development.md`
- `.ai/specs/implemented/2026-10-17-ci-quality-stages.md`

## Weryfikacja

- `mvn --batch-mode --no-transfer-progress clean verify -DskipITs` — PASS;
  jednostkowy próg pokrycia 100% spełniony.
- `mvn --batch-mode --no-transfer-progress clean verify -Pintegration-tests -DskipUnitTests` — PASS;
  29 testów integracyjnych (8 ASR i 21 backend) z PostgreSQL przez Testcontainers,
  bez uruchomienia Surefire.
- `mvn --batch-mode --no-transfer-progress clean verify` — PASS; pełny zestaw
  testów i raporty obu typów wygenerowane.
- Ruby YAML parser — PASS; `git diff --check` — PASS.
- `actionlint` nie jest zainstalowany lokalnie.
- Skan SonarQube nie był uruchomiony w tym przebiegu lokalnym. Użytkownik dodał
  `SONAR_HOST_URL`, `SONAR_PROJECT_KEY` i `SONAR_TOKEN` jako GitHub secrets;
  workflow obsługuje host i klucz zarówno jako secrets, jak i variables. Jeśli
  używa SonarQube Cloud, należy dodać `SONAR_ORGANIZATION`.

## Otwarte kwestie

- SonarCloud Code Analysis check PR przeszedł. Pierwszy Maven scanner w tym PR
  wykrył włączoną Automatic Analysis i odrzucił równoległą analizę CI. Workflow
  dostosowano: domyślnie używa integracji Cloud; Maven scanner z JaCoCo można
  włączyć przez `SONAR_CI_ANALYSIS=true` po wyłączeniu Automatic Analysis.
- Sprawdzenia nowego commita na PR należy potwierdzić.
