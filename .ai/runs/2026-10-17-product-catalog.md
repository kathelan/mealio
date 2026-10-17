# Faza 5.1 — lokalny katalog produktów

Data: 2026-10-17.

## Cel

Rozpocząć fazę 5 przed integracją iOS: wdrożyć lokalny katalog, na którym będą
opierały się obliczenia i snapshoty dziennika. Utworzono osobną specyfikację,
po wdrożeniu przeniesioną do specs/implemented/2026-10-17-product-catalog.md.

## Działania i pliki

- `backend/src/main/java/com/mealio/product/`: domena GTIN i danych odżywczych,
  niezmienne wersje, snapshoty ręcznego źródła, aplikacja z transakcjami, port
  repozytorium, adapter JPA, HTTP i osobne DTO. Publiczny ProductCatalog zwraca
  bieżący albo historyczny snapshot bez ujawniania domeny lub infrastruktury.
- GTIN-8/12/13/14 ma cyfrę kontrolną i jest normalizowany do 14 cyfr. Katalog
  wyszukuje dosłowny fragment nazwy; rozmiar i offset stron są walidowane.
- Pełne wartości dają USER_CONFIRMED, braki INCOMPLETE. Optional w domenie i API
  odróżnia brak od zera; adaptery mapują JSON/JPA. Złożone obiekty używają builderów,
  GTIN fabryki of. Korekty użytkownika utrwalono w osobnym commicie standardów.
- Nowa wersja wymaga expectedVersion, zachowuje poprzednie wartości i źródło;
  optymistyczna rewizja chroni równoczesny zapis. Nieznane pola HTTP są odrzucane.
- `002-create-product-catalog.yaml` (autor product): tabele products i product_versions,
  klucze, unikalność, kontrola nieujemności i kompletności. Master zawiera dodatkowy
  include; zastosowany change set 001 pozostaje bez zmian.
- Testy domeny/aplikacji/HTTP produktu i rozszerzony PostgreSqlIntegrationTest.
- OpenAPI backendu 0.2.0, docs/api.md, docs/database.md, docs/domain.md,
  README backendu i główny plan migracji opisują rzeczywisty stan.

## Weryfikacja

- Baseline `mvn --batch-mode --no-transfer-progress clean verify`: BUILD SUCCESS,
  70 testów. Po pierwszej implementacji i zmianie stylu testy backendu przechodziły.
- Końcowo `DEVELOPER_DIR=/Library/Developer/CommandLineTools mvn --batch-mode
  --no-transfer-progress clean verify`: BUILD SUCCESS, 111 testów:
  backend 89, ASR 11, request-audit 11; 0 failures/errors/skipped.
- JaCoCo: 553/623 linii backendu = 88,76%; próg 80% zachowany.
- PostgreSQL: pusta baza, aktualizacja od migracji 001 z zachowaniem danych legacy,
  zachowanie wersji, unikalność GTIN, rollback przy nieudanym zapisie wersji,
  dokładnie jeden zwycięzca równoczesnej korekty. Istniejące testy recipe/ASR przechodzą.
- HTTP: tworzenie, GTIN, wersje, konflikty, literalne wildcardy, paginacja,
  walidacja wejścia i nieznanych pól, brak ujawniania SQL i identyfikatory błędów.
- ArchUnit: granice bez wyjątków, brak field injection i Optional w encjach JPA.
- Ruby/Psych sprawdził OpenAPI YAML, referencje i 16 unikalnych operationId.
- Lokalne odnośniki i git diff --check: OK. Porównanie z HEAD potwierdza,
  że oryginalna zawartość mastera została zachowana i jedynie rozszerzona.

## Korekty i równoległa praca

Na polecenie użytkownika przeniesiono standardy do .ai/docs i wdrożono preferencje
Optional/builderów; odrębny raport opisuje ten commit. Jawnie uruchomiony drugi
agent przygotował research, zapisany w osobnym commicie bez zmian stacku.
Zachowano zastane zmiany użytkownika w opisie stosu AGENTS.md i w README.md.
Własne pliki tymczasowe po przebiegu usunięto; standardowe target pozostają ignorowane.

## Następny etap

Etap 5.1 ukończony; faza 5 trwa. Dalej nutrition, diary, kandydaci zewnętrzni
i mealparser. Odczyt GTIN sprawdza tylko katalog lokalny; brak odpowiedzi nie
oznacza odpytania dostawców. Nie dodano kont, auth ani kalkulacji po stronie iOS.
