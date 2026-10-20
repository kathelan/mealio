# E01 — analiza reużycia i szkielet specyfikacji

Data: 2026-10-20. Cel: rozszerzyć planowanie issue #10 o fundamenty backendu i SwiftUI
wynikające z pozostałego backlogu, zgodnie z jawnym zleceniem właściciela.

## Działania i zakres

Przeczytano issue #10, bieżący backlog, zaakceptowany zakres, brief, architekturę,
standardy, ADR-0002/0005, README modułów i wybrane symbole Java/SwiftUI.
Utworzono szkielet `.ai/specs/2026-10-20-e01-reusable-foundations.md` z mapą
powtarzalnych potrzeb i pytaniem Q1 o rozdzielenie dokumentu przekrojowego
od osobnych wdrażalnych SDD. Zlecono dwa odczytowe przeglądy agentów na podstawie
jawnej zgody właściciela: backend oraz SwiftUI/backlog. Oba przeglądy zakończono i uwzględniono: brak implementacji product/nutrition/diary,
prywatny enum recipe, brak targetu Xcode i klienta API oraz realne powtórzenia UI.
Zachowano rozdzielenie faktów od kandydatów do projektu.

Nie opracowano pełnego designu ani nie rozpoczęto implementacji: om-spec-writing
wymaga przedstawienia szkieletu i rozstrzygnięcia Open Questions przed dalszym projektem.
Nie zmieniono issues, API, migracji ani zachowania produktu.

## Weryfikacja

Zakres dokumentacyjny: sprawdzenie ścieżek źródłowych, odnośników, tabel i diffu — PASS.
Maven i testy aplikacji nie dotyczą tego szkieletu i nie były uruchamiane.
Nie deklarujemy ukończenia specyfikacji technicznej ani weryfikacji jej implementacji.

## Otwarte

Q1: czy rozdzielić mapę fundamentów od wdrażalnych SDD funkcji backend/iOS?
Rekomendacja: tak, wdrażać fundamenty razem z pierwszym rzeczywistym konsumentem.
