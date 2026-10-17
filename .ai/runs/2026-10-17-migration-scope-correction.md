# Korekta zakresu: migracja bez nowych funkcji

Data: 2026-10-17.

## Cel

Na polecenie użytkownika wycofać dodany katalog produktów i ograniczyć zadanie
do migracji monorepo, zachowując nowe standardy implementacji.

## Działania i pliki

- Wycofano zmiany commita `11dbca4` przez git revert --no-commit, bez przepisywania
  historii. Usunięto nowy moduł product, jego testy, migrację 002 i endpointy;
  przywrócono wcześniejsze OpenAPI, dokumentację API i schemat migracji.
- Specyfikację katalogu przeniesiono z implemented do archive z oznaczeniem
  wycofania. Raport katalogu zachowano jako historyczny, z informacją o wycofaniu.
- Plan migracji nie zawiera już wdrażania modułów MVP jako fazy 5. Kolejny zakres
  migracyjny to struktura klienta iOS i build, z zachowaniem istniejącego zachowania,
  bez nowych przepływów i integracji HTTP. Rozwój produktu wymaga osobnego zlecenia.
- Zapisano trwały wniosek w lessons oraz doprecyzowano docs/refactoring-plan.md.
- Zachowano .ai/docs/engineering-standards.md, reguły Optional/builderów,
  dodatkowe testy ArchUnit i osobny research drugiego agenta. Specyfikacja
  wdrożenia standardów wskazuje, że przykład product został wycofany.
- Zachowano wcześniejsze zmiany użytkownika w AGENTS.md i README.md.
  Nie wykonywano operacji na bazie deweloperskiej; sprawdzenia używają Testcontainers.

## Weryfikacja

- Przed korektą kod odpowiadał uprzednio zweryfikowanemu commitowi katalogu
  (111 testów); zastane niezacommitowane zmiany dotyczyły AGENTS.md i README.md.
- `DEVELOPER_DIR=/Library/Developer/CommandLineTools mvn --batch-mode
  --no-transfer-progress clean verify`: BUILD SUCCESS, 72 testy:
  backend 50, ASR 11, request-audit 11; 0 failures/errors/skipped.
- PostgreSQL, testy HTTP, architektura i bramka JaCoCo przechodzą.
  Pokrycie linii backendu 86,57%, próg 80% zachowany.
- Porównanie backendu, OpenAPI i dokumentacji API/domeny/bazy z commitem
  `d00a50f` sprzed katalogu: identyczne. Kanoniczne standardy również bez zmian.
- git diff --check i kontrola lokalnych odnośników: OK.

## Stan końcowy

Nowe funkcje są wycofane; migracja i standardy pozostają. Fazy migracyjne 1–3
ukończone, struktura iOS pozostaje do migracji. Nie rozpoczynamy nutrition,
diary, dostawców produktów ani mealparser w ramach tego zadania.
