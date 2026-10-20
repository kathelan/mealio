# Backlogs

## .ai/specs/product-brief.md

Backlog source: .ai/specs/product-brief.md
Last filed: 2026-10-20

Zatwierdzono w rozmowie pełne drzewo i kryteria: 3 epiki, 5 stories, bez zadań technicznych.
Kryteria akceptacji znajdują się w issues. E03 jest zaparkowany i nie uruchamia implementacji.
Wszystkie kwestie utworzono przez workflow `om-prepare-issue --no-spec --skip-dedupe`.
SDD nadal wymagana przed implementacją; Q01 zamknięte przez D07. Q02–Q04 dotyczą późniejszych etapów.
Nie znaleziono duplikatów ani kandydatów do adopcji; zamknięte #4 dotyczy innego, wdrożonego zakresu.
SDLC.md nie definiuje osobnej Definition of Ready; `om-setup-discovery-pipeline` może dodać tę warstwę.

| Id | Issue | Title | Epic | Depends on | Adopted |
|---|---|---|---|---|---|
| E01 | [#10](https://github.com/kathelan/mealio/issues/10) | Produkty i obliczenia | — | — | no |
| E02 | [#11](https://github.com/kathelan/mealio/issues/11) | Potrawy wielokrotnego użytku i dziennik | — | E01 (#10) | no |
| E03 | [#12](https://github.com/kathelan/mealio/issues/12) | Późniejsze etapy — zaparkowane | — | — | no |
| E01-S01 | [#13](https://github.com/kathelan/mealio/issues/13) | Dodanie i wyszukanie produktu | #10 | — | no |
| E01-S02 | [#14](https://github.com/kathelan/mealio/issues/14) | Obliczenie kcal i makro dla ilości | #10 | E01-S01 (#13) | no |
| E02-S01 | [#15](https://github.com/kathelan/mealio/issues/15) | Potrawy, porcje i zagnieżdżone kategorie | #11 | E01 (#10) | no |
| E02-S02 | [#16](https://github.com/kathelan/mealio/issues/16) | Własne sekcje dnia | #11 | — | no |
| E02-S03 | [#17](https://github.com/kathelan/mealio/issues/17) | Ponowne dodawanie potraw i bilans dnia | #11 | E02-S01 (#15), E02-S02 (#16) | no |
