# Monorepo — faza 3: PostgreSQL i wspólna weryfikacja

Data: 2026-10-17.

## Cel i zmiany

Wdrożyć fazę 3 aktywnej specyfikacji migracji. Baseline po fazie 2: pełny
`mvn clean verify`, 67 testów bez błędów.

- `backend/pom.xml`, `asr/pom.xml`: Testcontainers PostgreSQL i JUnit Jupiter,
  wersja 2.0.5 zarządzana przez istniejący BOM Spring Boot.
- `backend/src/test/java/com/mealio/infrastructure/persistence/PostgreSqlIntegrationTest.java`:
  pusta baza PostgreSQL 17, Liquibase, Hibernate validate, zapis i odczyt przepisu
  oraz składników z dokładnymi liczbami, domyślny created_at powiązania,
  usuwanie kategorii przez kaskadę, unikalność ścieżki i klucz obcy kategorii.
- `asr/src/test/java/com/mealio/asr/infrastructure/persistence/PostgreSqlIntegrationTest.java`:
  migracja pustej bazy ASR, Hibernate validate, zapis tekstu i metadanych transkrypcji.
- `*/src/test/resources/db/postgres-test-init.sql`: schemat właściwego modułu.
- Testy kontenerowe należą do standardowego zestawu testów; brak Dockera powoduje
  porażkę builda. Nie dodano profilu ani automatycznego pomijania testów.
- `Makefile`: cel verify wykonujący mvn clean verify.
- `.github/workflows/verify.yml`: pełny reaktor na push i PR, Java 25 oraz Docker.
- README modułów, docs/database.md, docs/development.md, docs/refactoring-plan.md
  i aktywna specyfikacja: wymagania, polecenia i postęp.

## Weryfikacja

- Docker 29.2.1 dostępny poza sandboxem. Testcontainers uruchomił PostgreSQL 17.11
  w odizolowanych bazach bez dostępu do danych lokalnego Compose.
- `mvn clean verify` z katalogu głównego poza sandboxem: BUILD SUCCESS (54 s),
  70 testów: backend 48, ASR 11, request-audit 11; 0 failures/errors/skipped.
- Testy ArchUnit i bramka JaCoCo przechodzą; linie backendu: 374/432 = 86,57%.
- `make -n verify`: poprawne rozwinięcie do mvn clean verify.
- Ruby/Psych odczytał YAML workflow i potwierdził obecność zadania verify.
  Zdalny workflow GitHub nie był uruchamiany; lokalnie przeszło jego polecenie Maven.
- Kontrola lokalnych odnośników, git diff --check i brak zmian OpenAPI/change setów: OK.
- Usunięto własny skrypt przenosin i tymczasowe logi faz 2–3 z /tmp.
  Zastane zmiany AGENTS.md i głównego README.md pozostają poza commitami.

## Następny etap i ograniczenia

Faza 3 ukończona, następna jest faza 4 (projekt iOS i integracja API).
Wykonano kontrolę środowiska: xcodebuild -version zgłasza aktywne Command Line Tools,
xcrun --sdk iphoneos --show-sdk-path zwraca SDK "iphoneos" cannot be located.
W /Applications i ~/Applications nie znaleziono aplikacji Xcode. Swift 6.4 jest
dostępny, ale pełny build iOS wymaga Xcode z SDK; nie deklarujemy ukończenia fazy 4.
