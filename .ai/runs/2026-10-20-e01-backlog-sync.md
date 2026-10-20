# Synchronizacja istniejącego backlogu po review E01

Data: 2026-10-20. Cel: zastosować aktualizację przedstawioną po review PR #32–35,
po jednoznacznym potwierdzeniu właściciela „aktualizuj backlog”. Użyto om-backlog;
nie uruchomiono implementacji ani nowych decyzji produktu.

## Plan i wykonanie

1. Odczytać główny brief, zatwierdzoną propozycję, lokalną mapę oraz aktualne 18 issues;
   sprawdzić source/id, aktywne blokady i zachować istniejące zmiany użytkownika.
2. Uzupełnić 11 istniejących opisów: linki do SDD/map, 15 zatwierdzonych kryteriów oraz
   zależność #24 od #14. Zachować dotychczasową treść, checklisty i pozostałe metadane.
3. Odczytać wynik z API, sprawdzić pełne drzewo i idempotencję; zsynchronizować dokumenty.
4. Dostarczyć zmiany Markdown przez osobny PR po review i zielonym CI; squash merge
   z kontrolą dokładnego SHA, bez force/admin.

## Zmienione issues

- #10/#11/#12 — linki do map E01/E02/E03, bez uruchomienia wdrożenia.
- #13 — rozróżnianie produktów, sklepy jako metadane, konflikt GTIN i niezmienne korekty.
- #14 — odnośnik do właściwej SDD obliczeń, bez zmiany kryteriów.
- #20 — fallback po awarii/limicie, lokalny skan i serwerowe pochodzenie potwierdzenia.
- #17/#27 — diary-home, suma kcal, zachowanie sekcji i sumy znanych wartości z PARTIAL.
- #24 — dwa tryby pickera, niepełne dane, sklepy/korekta i low-fi; zależność także od #14.
- #25 — skaner zwraca produkt, a zapis i integracje hostów należą do E02.
- #26 — odnośnik do mapy; skaner pozostaje integracją warunkową po #25.

Zachowano 3 epiki, 9 stories i 6 zadań; nowe issues: 0. Bez zmian treści pozostałych
siedmiu issues. Nie zmieniono tytułów, pełnych identyfikatorów, rodziców, przypisań,
etykiet ani dotychczasowych stanów checkboxów. Wszystkie funkcjonalne issues pozostają otwarte.

## Zmienione pliki

- .ai/specs/backlog.md — stan synchronizacji i zależność #24 → #14.
- .ai/specs/research/2026-10-20-e01-review-backlog-sync.md — akceptacja i wynik wykonania.
- .ai/runs/2026-10-20-e01-backlog-sync.md — ten raport.

Poprzedni raport discovery zachowuje historyczny stan oczekiwania na potwierdzenie;
nowy raport rejestruje jego późniejsze otrzymanie i wykonanie.

## Weryfikacja i ograniczenia

- Aktualny source/id i brak aktywnych blokad przed każdym zapisem — PASS.
- Odczyt zapisanych opisów z API, zachowanie pól i starych checklist — PASS.
- Ponowne wyliczenie transformacji nie generuje zmian — PASS.
- Kontrola 18 unikalnych mapowań, rodziców i grafu zależności bez cykli — PASS.
- Odnośniki lokalne i do dokumentów repozytorium oraz git diff --check — PASS.
- Niezależny review zatwierdzonego zakresu i końcowego diffu — wynik publikowany na PR.
- Lokalne mvn clean verify i QA aplikacji pominięto jako nieadekwatne do aktualizacji
  opisów issues i Markdown. Nie zmieniono kodu Java/iOS, API ani migracji.
  Wynik wymaganych workflowów dokładnego SHA jest sprawdzany przed merge i publikowany na PR.

## Otwarte kwestie

Brak pozostałej decyzji do tej synchronizacji. Q02 nadal blokuje wyłącznie kalkulator
celu, a usuwanie zajętych sekcji i późniejsza edycja spożycia należą do właściwych SDD.
SDLC nie ma odrębnej Definition of Ready; nie dodawano warstwy discovery/pipeline setup.
