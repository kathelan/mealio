# Przebieg — synchronizacja briefu, issues i backlogu po grilling

Data: 2026-10-20. Cel: utrwalić zatwierdzone ustalenia właściciela, bez implementacji MVP.

## Działania i pliki

- Sprawdzono git status; dwa wcześniejsze nieśledzone raporty pozostawiono w głównym checkout.
- Utworzono osobny worktree i gałąź docs/mvp-grilling-sync-20261020 na origin/main.
- Dodano zapis decyzji .ai/specs/research/decisions/2026-10-20-diary-and-recipe-details.md.
- Uzgodniono product-brief.md, personal-mvp, mapy E02/E03, ADR-0004 i README.
- Zaktualizowano 11 istniejących issues; dodano task analizy/SDD #37 E02-T01.
- Zaktualizowano .ai/specs/backlog.md, zapis synchronizacji w research i .ai/lessons.md.

## Weryfikacja

- Ponowny odczyt source/id, stanu i blokad przed zapisem; readback body po każdym zapisie.
- Niezależny przegląd źródeł, briefu i wszystkich draftów issues: poprawiono dwa drobne
  problemy tabel/odsyłaczy; brak blocker/major w zakresie potwierdzonych decyzji.
- Kontrola 99 lokalnych odnośników w 11 zmienionych Markdown, Coverage i git diff --check: PASS. Ponowne mapowanie 19 issues/source/id, zachowanie etykiet i zaznaczeń ukończenia oraz ciągłość tabel: PASS.
- Testy Java/iOS i mvn clean verify nieuruchamiane: brak zmian kodu, kontraktów lub migracji;
  dokumentacyjna weryfikacja zgodnie z AGENTS.md/CODE_REVIEW.md. Nie deklarujemy testów funkcji.

## Otwarte kwestie

Q02: metoda celu kalorii; Q05: wersje przy korekcie starego wpisu; Q06: przelicznik bazy
sosu w g/ml. Q07 to propozycja kolejności sekcji. Bulion jest późniejszy. A01 niezmierzone.
Dostarczenie dokumentów: PR z tej gałęzi; nie zamyka issues implementacyjnych.
