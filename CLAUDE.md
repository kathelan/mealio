# Mealio — instrukcje dla Claude

@AGENTS.md

Te zasady obowiązują Claude Code i inne agenty AI pracujące w repozytorium. `AGENTS.md` jest
kanonicznym, wspólnym źródłem instrukcji agentów. Przed pracą zapoznaj się z `AGENTS.md`,
`README.md`, dokumentacją wskazaną w instrukcjach głównych oraz pamięcią `.ai/lessons.md` i
pasującymi notatkami w `.ai/lessons/`. Nie pomijaj obowiązujących reguł projektu.

## Wymagana struktura `.ai/`

- `.ai/specs/YYYY-MM-DD-slug.md` — specyfikacje SDD. Stosuj datę i krótki slug małymi
  literami, ze słowami oddzielonymi myślnikami. Wdrożone specyfikacje przenoś do
  `.ai/specs/implemented/`, a nieaktualne lub porzucone do `.ai/specs/archive/`, zachowując
  nazwę pliku.
- `.ai/runs/YYYY-MM-DD-slug.md` — raport każdego autonomicznego przebiegu AI w repozytorium.
  Opisz cel, wykonane działania, zmienione pliki, weryfikację i wynik oraz otwarte kwestie.
  Nie zapisuj sekretów, danych osobowych, nagrań ani innych wrażliwych danych.
- `.ai/lessons.md` — indeks i krótkie, wspólne wnioski. Po naprawie błędu lub odkryciu
  trwałego ustalenia/usprawnienia przydatnego w przyszłości dodaj wpis z datą i kontekstem.
- `.ai/lessons/YYYY-MM-DD-slug.md` — dłuższe wnioski, podlinkowane z `lessons.md`.
  Nie utrwalaj jednorazowych szczegółów ani sekretów.

Jeśli zadanie zmienia zachowanie lub zakres produktu, przed implementacją utwórz albo
zaktualizuj specyfikację SDD. Po wdrożeniu przenieś ją do `implemented/`. Na końcu
autonomicznego przebiegu utwórz raport w `.ai/runs/` i zapisz trwałe wnioski, jeśli wystąpiły;
nie dodawaj sztucznej lekcji, gdy nie ma czego utrwalić. Przed utworzeniem pliku sprawdź
istniejące nazwy i unikaj nadpisywania materiałów.

## Pozostałe zasady

Przestrzegaj `AGENTS.md` w zakresie produktu Mealio, architektury, bezpieczeństwa, zmian,
testowania i Definition of Done. W razie rozbieżności uzgodnij instrukcje i zachowaj bardziej
szczegółowe wymaganie repozytorium. Nie zapisuj w repozytorium sekretów ani wrażliwych danych.
