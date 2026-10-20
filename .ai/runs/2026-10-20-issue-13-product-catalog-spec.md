# Przebieg: doprecyzowanie SDD katalogu produktów

- Data: 2026-10-20
- Cel: dopracować specyfikację katalogu z issue #13 w ramach epic #10, zgodnie z decyzją właściciela o podobnych nazwach produktów.
- Działania: sprawdzono issue #13, brief, MVP, architekturę, standardy inżynieryjne, strategię danych, plan refaktoryzacji i ADR-y 0001–0008; zaktualizowano SDD o rozróżnianie produktów oraz osobne pola nazwy, marki i pochodzenia; przeprowadzono przegląd spójności zakresu.
- Zmienione pliki: `.ai/specs/2026-10-20-e01-product-catalog.md`, `.ai/lessons.md`, `.ai/runs/2026-10-20-issue-13-product-catalog-spec.md`.
- Weryfikacja: `git diff --check` — PASS. Przegląd zakresu: jedna samodzielna zdolność katalogu; zakres zależny od #14, skanera, przepisów i dziennika pozostaje poza SDD.
- Otwarte kwestie: issue #13 nadal ma kryterium akceptacji, które można odczytać jako zakaz duplikatów nazw; przed implementacją należy doprecyzować je zgodnie z SDD. Przy implementacji jawnie uzgodnić ADR-0005 dla przelicznika PIECE.
