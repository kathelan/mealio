# Przebieg: opis backendowego API w README

- Cel: uzupełnić główne README o przegląd istniejących endpointów kategorii, przepisów i składników oraz wskazać OpenAPI.
- Działania: sprawdzono issue #4, kontrakt `docs/openapi/backend.yaml` i kontrolery kategorii oraz przepisów; dodano do README sekcję „Dostępne API backendu” z obsługiwanymi operacjami i linkiem do kontraktu.
- Zmienione pliki: `README.md`.
- Weryfikacja: `git diff --check` — powodzenie; sprawdzenie skryptem Python wszystkich względnych linków README — 11 linków, brak brakujących; sprawdzenie obecności pięciu opisanych tras w OpenAPI — powodzenie. Maven pominięty, bo zmiana obejmuje wyłącznie Markdown.
- Otwarte kwestie: zmiana nie została jeszcze zatwierdzona ani opublikowana w PR; następny etap pipeline to commit i otwarcie PR.
