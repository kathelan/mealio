# Etapy jakości CI: JaCoCo i SonarQube

Data: 2026-10-17. Zakres: usprawnienie CI bez zmian funkcjonalnych.

## Kryteria

- GitHub Actions używa utrzymywanych wersji akcji i cache'uje zależności Maven.
- Osobne joby unit i integration zapisują raporty JaCoCo HTML/XML oraz raporty testów.
- Maven ma profil `integration-tests`; standardowe `mvn verify` nadal wykonuje unit i IT.
- Job integration używa profilu i nie wykonuje drugi raz unit tests.
- Opcjonalna analiza SonarQube jest gotowa do włączenia przez repo variables i secret.
- Workflow i pełna weryfikacja Maven przechodzą lokalnie.

## Fazy

1. [x] Zaktualizować workflow, cache i raportowanie jednostek.
2. [x] Dodać profil Maven integracji zachowując pełne domyślne `verify`.
3. [x] Dodać SonarQube analysis zgodny z istniejącą integracją Cloud.
4. [x] Zweryfikować workflow, komendy Maven oraz pełny zestaw testów; zapisać wynik.

## Implementacja

Etap integracyjny używa `-Pintegration-tests -DskipUnitTests`, a job unit
pozostaje jedynym właścicielem progu 100% linii. Dla SonarQube Server Maven scan
działa po unit tests i korzysta z wygenerowanego raportu JaCoCo. SonarQube Cloud
Automatic Analysis koliduje z CI analysis; domyślnie używana jest integracja
GitHub Cloud. Użytkownik może wyłączyć Automatic Analysis i ustawić
`SONAR_CI_ANALYSIS=true`, aby włączyć Maven scanner oraz przesłać JaCoCo.
URL i klucz projektu mogą pochodzić z Actions variables lub secrets; token jest
pobierany wyłącznie z Actions secrets.

Wynik: `mvn clean verify -DskipITs` zaliczył unit tests i próg 100% linii;
`mvn clean verify -Pintegration-tests -DskipUnitTests` uruchomił 29 testów
integracyjnych (8 ASR, 21 backend) na Testcontainers PostgreSQL bez powtórzenia
unit tests; pełne `mvn clean verify` także przeszło. YAML workflow parsuje się,
`git diff --check` przechodzi. `actionlint` nie jest dostępny lokalnie.
SonarCloud Code Analysis w PR przeszło. Pierwszy CI Maven scan PR zakończył się
błędem z powodu włączonego SonarCloud Automatic Analysis; workflow zmieniono tak,
by nie uruchamiać obu metod naraz.
