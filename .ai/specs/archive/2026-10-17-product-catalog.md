# Status: wycofana — 2026-10-17

Implementacja katalogu została wycofana na polecenie użytkownika jako wykraczająca
poza migrację monorepo. Poniżej zachowano historyczną specyfikację; nie jest
zgodą na ponowne wdrożenie funkcji.

# Faza 5.1 — lokalny katalog produktów i wersje danych

Data: 2026-10-17. Status: wdrożona.

## Cel i zakres

Pierwszy przepływ fazy 5: ręczne utworzenie lokalnego produktu, wyszukanie,
odczyt po UUID/GTIN i jawne utworzenie nowej wersji. Źródła: MVP, strategia danych,
ADR-0003, ADR-0005–0008. Integracja iOS pozostaje fazą 4; nie blokuje backendu.
Kolejne kroki fazy 5: obliczenia nutrition, dziennik diary ze snapshotami,
kandydaci dostawców kodów kreskowych i ich potwierdzanie, następnie mealparser.

## Reguły

- Dane opcjonalne w domenie i kontraktach Java reprezentowane są przez Optional.
  Adaptery mapują nullable JSON/JPA. Złożone obiekty tworzymy builderami,
  prosty GTIN przez nazwaną fabrykę. Korekta użytkownika obowiązuje także testy.
- Brak użytkowników/auth. Właścicielem nowych tabel i migracji jest product.
- Produkt ma UUID, opcjonalny GTIN i niezmienny czas utworzenia. GTIN przyjmuje
  8/12/13/14 cyfr ASCII z poprawną cyfrą kontrolną; normalizacja do 14 cyfr zerami
  z lewej strony. Unikalność po normalizacji egzekwowana w bazie. W tej iteracji
  GTIN jest przypisany do tożsamości produktu i nie jest zmieniany przy korekcie danych.
- Wersja ma własny UUID, rosnący numer, nazwę (1–220), opcjonalną markę (do 160),
  podstawę PER_100_G/PER_100_ML i energię kcal, białko/tłuszcz/węglowodany w gramach.
  Wartości są nieujemnym BigDecimal mieszczącym się w numeric(12,4).
- Brakujące wartości pozostają null, nigdy zero. Komplet czterech wartości daje
  USER_CONFIRMED; pozostałe dane INCOMPLETE. AI nie dostarcza wartości.
- Snapshot ręcznego źródła: USER_LABEL dla produktu z GTIN, USER_INPUT dla produktu
  bez kodu, znacznik czasu oraz wszystkie wprowadzone pola w niezmiennej wersji.
- Korekta wymaga expectedVersion i tworzy nowy rekord; równoczesna lub nieaktualna
  korekta zwraca 409. Stare wersje są nadal odczytywalne i nie podlegają aktualizacji.
- Nieznane pola żądania są odrzucane (400); offset strony musi mieścić się w int.
- Zapis tożsamości i wersji jest atomowy. Duplikat GTIN daje 409. Błędy bazy nie
  ujawniają SQL ani komunikatów dostawcy. Błędy HTTP mają code/message/timestamp/requestId.

## HTTP i publiczny kontrakt modułu

- POST /api/v1/products — ręczne potwierdzenie danych; 201 z Location.
- GET /api/v1/products?query=&page=0&size=20 — wyszukiwanie nazwy, bez rozróżniania
  wielkości liter; fraza traktowana dosłownie, sortowanie po nazwie i UUID; size 1–100.
- GET /api/v1/products/{id} — bieżąca wersja.
- GET /api/v1/products/by-gtin/{gtin} — wyłącznie katalog lokalny; 404 nie oznacza
  sprawdzenia dostawców zewnętrznych.
- POST /api/v1/products/{id}/versions — expectedVersion i komplet pól kolejnej wersji.
- GET /api/v1/products/{id}/versions/{version} — zachowana wersja historyczna.
- Publiczne product.api.ProductCatalog udostępnia snapshot wersji przyszłemu diary;
  inne moduły nie otrzymują encji, repozytoriów ani domeny product.

## Plan i kryteria ukończenia

1. [x] Baseline; domena GTIN, danych odżywczych i wersjonowania z testami.
2. [x] Przypadki użycia, JPA i nowa migracja 002, bez modyfikacji change setu 001.
3. [x] HTTP/OpenAPI, wyszukiwanie, wersje i stabilne błędy.
4. [x] Testy PostgreSQL: migracja pustej i istniejącej bazy z danymi legacy,
   ograniczenia GTIN, atomowość i konflikty, niezmienność poprzedniej wersji.
   Testy HTTP i ArchUnit oraz pełne mvn clean verify przechodzą, próg JaCoCo 80% bez zmian.
5. [x] Dokumentacja i postęp planu, raport przebiegu, przeniesienie tej specyfikacji
   do implemented i osobny commit. Faza 5 pozostaje nieukończona do wdrożenia kolejnych modułów.

## Wynik — 2026-10-17

Baseline mvn clean verify: 70 testów, BUILD SUCCESS. Po wdrożeniu i korektach
użytkownika (Optional/buildery) pełne mvn clean verify: BUILD SUCCESS,
111 testów (89 backend, 11 ASR, 11 request-audit), 0 failures/errors/skipped.
Pokrycie linii backendu: 553/623 = 88,76%, próg 80% zachowany.
PostgreSQL potwierdza migrację pustej i istniejącej bazy, konflikty równoczesnych
korekt, rollback oraz niezmienność snapshotów. HTTP sprawdza walidację,
wyszukiwanie, odczyt, wersje, duplikaty i bezpieczne błędy.
OpenAPI YAML i wszystkie referencje wewnętrzne sprawdzone; 16 unikalnych operationId.
Change set 001 zachowany bez zmian; master rozszerzono wyłącznie o include 002.

Standardy zostały przeniesione do .ai/docs/engineering-standards.md i wdrożone
w bieżącym module. Research drugiego agenta pozostaje osobną specyfikacją rekomendacji.
Następny etap fazy 5: nutrition. Integracje dostawców i iOS nie są częścią 5.1.
