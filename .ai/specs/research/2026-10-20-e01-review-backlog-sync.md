# Synchronizacja backlogu po review E01/E02/E03

Status: propozycja przedstawiona właścicielowi; zapis do trackera oczekuje na potwierdzenie.

2026-10-20. Projekt aktualizacji istniejących issues; bez nowych numerów, wdrożenia i zamykania funkcji.
Źródła: potwierdzone E01-C1–C4, diary-home w README revision-002, R02/R03 i SDD po review.
Dodatkowe reguły briefu: R09–R11 i D13–D17; stare identyfikatory oraz historia pozostają.

## Całe istniejące drzewo

| Id | Issue | Rodzic | Zależności po synchronizacji | Zmiana |
|---|---|---|---|---|
| E01 | #10 | — | — | linki do SDD i decyzji |
| E01-S01 | #13 | #10 | — | dopisane AC poniżej |
| E01-S01-T01 | #24 | #13 | #13, #14 | preview wymaga #14; AC poniżej |
| E01-S02 | #14 | #10 | #13 | link do SDD; AC bez zmian |
| E01-S03 | #20 | #10 | #13 | AC poniżej |
| E01-S03-T01 | #25 | #20 | #20, #24 | AC poniżej |
| E02 | #11 | — | #10 | link do mapy E02 |
| E02-S01 | #15 | #11 | #13, #14 | bez zmiany AC |
| E02-S01-T01 | #26 | #15 | #15, #24 | link do mapy; skaner nadal warunkowy po #25 |
| E02-S02 | #16 | #11 | — | bez zmiany AC; usunięcie zajętej sekcji do SDD |
| E02-S03 | #17 | #11 | #15, #16 | AC diary-home poniżej |
| E02-S03-T01 | #27 | #17 | #17, #16, #26, #23 | AC diary-home poniżej |
| E02-S04 | #21 | #11 | #15, #17 | bez zmiany |
| E02-S04-T01 | #28 | #21 | #21, #26, #27 | bez zmiany |
| E02-S05 | #22 | #11 | #15, #14 | bez zmiany |
| E02-S05-T01 | #29 | #22 | #22, #26 | bez zmiany |
| E02-S06 | #23 | #11 | #15, #17 | bez zmiany |
| E03 | #12 | — | — | link do mapy parkowania; Q02 pozostaje |

Etykiety bez zmian: E01/E02 i trwałe stories risk-high; #14 i zadania iOS risk-medium;
E03 risk-medium/priority-low. Pozostałe priority-medium. Brak nowych etykiet procesu.
Pozostają 3 epiki, 9 stories i 6 zadań. Żadna checklista implementacji nie zostaje zaznaczona.

## Dodatkowe kryteria akceptacji istniejących issues

### #13 E01-S01

- Podobne nazwy nie blokują oddzielnych produktów; wynik pokazuje markę, pochodzenie, sklepy, podstawę, kompletność i przelicznik. GTIN jest unikalny.
- Produkt może mieć wiele własnych nazw sklepów; ich edycja nie tworzy nowej wersji żywieniowej i nie zmienia marki ani źródeł danych.
- Konflikt GTIN umożliwia użycie istniejącego produktu, świadomą korektę albo anulowanie. Korekta tworzy nową niezmienną wersję, zachowuje poprzednią i pochodzenie niezmienionych pól.

### #20 E01-S03

- Po PROVIDER_UNAVAILABLE/PROVIDER_RATE_LIMITED można przejść do ręcznego formularza z zachowanym GTIN i przyczyną błędu; błąd nie jest NOT_FOUND. FatSecret nie jest wymagany do MVP.
- Lokalny produkt wraca bez popupu i zapytań w tle; oddzielna akcja uruchamia korektę. Konflikt GTIN ma trzy powyższe ścieżki i nie nadpisuje wcześniejszej wersji.
- Potwierdzenie kandydata zachowuje pochodzenie per pole; ręcznie poprawione dane nie otrzymują źródła dostawcy. Powtórzenie tej samej operacji jest idempotentne zgodnie z SDD.

### #24 E01-S01-T01

- Picker obsługuje productOnly i productWithQuantity; zwraca wybór do hosta bez samodzielnego zapisu składnika lub spożycia. Preview pochodzi z #14, dlatego zadanie zależy również od #14.
- Formularz przyjmuje niepełne kcal/makra, zachowuje jawne zero i obsługuje wiele sklepów oraz świadomą korektę wersji z serwerowym pochodzeniem pól.
- Low-fi istnieje przed implementacją UI; aktualnym artefaktem jest revision-003. Przejście techniczne makiety nie zastępuje akceptacji właściciela, finalnego stylu ani QA iOS.

### #25 E01-S03-T01

- UI realizuje lokalny skan bez ponownego potwierdzenia, korektę na żądanie, trzy ścieżki konfliktu GTIN oraz ręczny formularz po awarii/limicie bez mylenia ich z brakiem.
- Używa aktualnego low-fi i zwraca trwały produkt do hosta. Faktyczne osadzenie w recipe/diary realizują odpowiednie hosty E02; skaner nie implementuje ich zapisu.

### #17 E02-S03 i #27 E02-S03-T01

- Ekran startowy aplikacji to dziennik dnia z sekcjami; na dole pokazuje sumę spożytych kcal. Produkt jest wybierany/skanowany w kontekście sekcji, a powrót zachowuje tę sekcję.
- Suma znanych wartości i metadane PARTIAL pochodzą z backendu; braki nie zmieniają się w zero i nie ukrywają znanej sumy.

## Granice

Bez nowych funkcji: nie ustalamy usuwania zajętej sekcji, późniejszej edycji spożycia,
układu paneli makr ani celu/pozostałego budżetu. Q02 blokuje wyłącznie kalkulator.
Wszędzie zachowujemy Backlog source, pełne Backlog id, Epic/Story i checklisty.
Aktualizacja treści zachodzi tylko po ponownej kontroli tożsamości i aktywnych blokad.
