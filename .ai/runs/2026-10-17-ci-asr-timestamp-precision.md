# Naprawa nieudanego testu CI ASR

Data: 2026-10-17.

## Cel i diagnoza

Po zalogowaniu użytkownika w GitHub CLI pobrano log nieudanego przebiegu:
`gh run view 39028331489 --repo kathelan/mealio --log-failed`.
Build, lint, unit coverage i Docker przeszły. Testy PostgreSQL ASR przeszły (2/2).
Awaria dotyczyła testu HTTP z H2: oczekiwano Instant
`2026-10-17T11:21:45.548394163Z`, odczytano `2026-10-17T11:21:45.548394Z`.
Test błędnie zakładał identyczną precyzję zegara JVM i kolumny TIMESTAMP.

## Zmiany

- `asr/src/test/java/com/mealio/asr/adapter/in/web/TranscriptionControllerIntegrationTest.java`:
  lokalna konfiguracja Clock.fixed z wartością nanosekundową z logu GitHub.
  Odpowiedź HTTP jest porównywana z dokładnym Instant, a odczyt z bazy z dokładną
  wartością odpowiadającą sześciu cyfrom ułamkowym kolumny. Bez tolerancji czasowej;
  wszystkie wcześniejsze sprawdzenia danych pozostały.
- `.ai/specs/implemented/2026-10-17-ci-asr-timestamp-precision.md`: plan i wyniki.
- `.ai/lessons.md`: trwały wniosek o deterministycznym zegarze i precyzji persystencji.
- `.ai/runs/2026-10-17-github-ci-failure-investigation.md`: uzupełniona diagnoza
  po uzyskaniu autoryzowanego dostępu do logów. Ten plik: raport naprawy.

Nie zmieniono kodu produkcyjnego, API, schematu ani progów jakości.
Zastane zmiany użytkownika w AGENTS.md i README.md zachowane poza commitem.

## Weryfikacja

- Odtworzenie: `mvn --batch-mode --no-transfer-progress -pl asr -am clean verify
  -Dit.test=TranscriptionControllerIntegrationTest -Dfailsafe.failIfNoSpecifiedTests=false`:
  oczekiwany FAIL starej asercji z dokładnie taką różnicą jak na GitHubie.
- Po poprawce: `mvn --batch-mode --no-transfer-progress -pl asr -am clean verify`:
  PASS, testy jednostkowe, integracje HTTP i PostgreSQL oraz próg JaCoCo.
- Pełne `mvn --batch-mode --no-transfer-progress clean verify`: PASS, 112 testów
  (83 jednostkowe, 29 integracyjnych), zero błędów i pominięć; JaCoCo unit LINE 100%
  w każdym module Java.
- `git diff --check`: PASS.

W tym środowisku polecenia Maven uruchamiano z
`DEVELOPER_DIR=/Library/Developer/CommandLineTools`.
Poprawka wymaga wysłania commita, aby wykonała się w nowym przebiegu GitHub Actions.
Nie uruchamiano ponownie starego commita, który nie zawiera poprawki.
