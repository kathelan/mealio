# Publikacja backlogu i zadań SwiftUI

Data: 2026-10-20. Gałąź: docs/mvp-personal-recipes, PR #19.

## Cel i autoryzacja

Po zatwierdzeniu pełnego drzewa i kryteriów słowami „akceptuje wszystko” wykonać
om-backlog, a po jawnym dodatkowym zleceniu utworzyć także osobne zadania SwiftUI.
Nie uruchamiać implementacji.

## Działania

- Ponownie sprawdzono tracker, źródło i pełne ID oraz brak aktywnych claimów.
- Zaktualizowano #10–#17, zachowując ich tożsamość i zastępując stare ustalenia
  zatwierdzonymi kryteriami: płaskie etykiety, obowiązkowe porcje, opcjonalna masa,
  jeden wpis przepisu i parametry ilości.
- Utworzono 4 stories: #20 skany, #21 parametry, #22 bazy, #23 zapis dziennika jako przepis.
- Utworzono 6 zadań SwiftUI: #24 produkty, #25 skaner, #26 biblioteka/edytor przepisów,
  #27 dziennik/sekcje/dzień/tydzień, #28 parametry przy dodawaniu, #29 wybór bazy.
- Nowe zadania utworzono workflow om-prepare-issue --no-spec --skip-dedupe
  z jawnym priorytetem i ryzykiem. Nie przypisywano pipeline ani claimów do issues.
- Zaktualizowano checklisty epików i stories, odwołania zależności oraz mapę backlogu.

## Pliki

.ai/specs/backlog.md, .ai/specs/product-brief.md, research/README.md,
research/2026-10-20-backlog-refresh-proposal.md (ścieżki research pod .ai/specs/)
oraz niniejszy raport. Poprzedni raport personal-mvp-refresh pozostaje zapisem wcześniejszego etapu.

## Weryfikacja

Odczyt po zapisie wszystkich 18 issues: PASS — unikalne źródło/ID, kryteria zgodne
z zatwierdzoną propozycją lub jawnym zadaniem SwiftUI, zależności, rodzice, checklisty,
etykiety category/priority/risk i brak claimów. Kontrola lokalnych odnośników,
struktury tabel, licznika Coverage, mapowania issues oraz git diff --check: PASS.
Nie uruchamiano testów aplikacji ani mvn verify: zmiany wyłącznie w trackerze i Markdown,
zgodnie z zakresem weryfikacji dokumentacji w AGENTS.md. Nie sprawdzano działania SwiftUI.

## Otwarte kwestie

Q02 blokuje story kalkulatora do wyboru wzoru, danych i modelu aktywności;
nie usuwa celu kalorii z docelowego MVP. Przed kodem nadal wymagane osobne SDD,
uzgodnienie kontraktów/ADR i docelowego projektu Xcode. Zadania iOS wymagają testów
na symulatorze i screenshotów; obecny prototyp z danymi przykładowymi nie jest dowodem integracji.
Nie zmieniono kodu, OpenAPI ani migracji. Zastane nieśledzone raporty z 2026-10-17 pozostawiono.
