# Odczyt nieudanego GitHub CI

Data: 2026-10-17. Cel: ustalić przyczynę błędu zgłoszonego przez użytkownika.

Publiczne API GitHub potwierdza przebieg 39028331489 dla commita c703528:
build, lint i unit tests/100% coverage zakończone sukcesem.
Integration tests PostgreSQL zakończone błędem w `mvn clean verify`.
Poprzedzający krok `docker info` zakończony sukcesem.

Pełne logi wymagają zalogowania. Publiczne pobranie logu zwróciło HTTP 403,
a dostępna lokalna autoryzacja nie pozwoliła na odczyt (HTTP 401).
Narzędzie obsługi komputera odmówiło dostępu do Chrome.
Publiczne adnotacje zawierają tylko exit code 1 i ostrzeżenia o wersjach Actions;
nie ujawniają wyjątku Mavena ani przyczyny awarii testu.

Weryfikacja: odczyt statusów i kroków przez GitHub REST API oraz strony joba.
Nie uruchamiano nowych testów i nie zmieniano konfiguracji ani kodu.
Zmieniony plik: ten raport. Zastane modyfikacje AGENTS.md i README.md zachowane.
Otwarta kwestia: potrzebny log błędnego kroku lub artefakt integration-reports,
aby wskazać i naprawić faktyczną przyczynę, bez zgadywania.

Przebieg: https://github.com/kathelan/mealio/actions/runs/39028331489

## Kontynuacja po zalogowaniu GitHub CLI

Użytkownik udostępnił autoryzowany GitHub CLI. Log pobrano przez
`gh run view 39028331489 --repo kathelan/mealio --log-failed`.
Przyczyna: `TranscriptionControllerIntegrationTest` porównuje Instant z nanosekundami
z wynikiem odczytanym z H2 o precyzji mikrosekund. PostgreSQL/Testcontainers
uruchomił się poprawnie, oba jego testy przeszły. Poprawka i weryfikacja są opisane
w `.ai/runs/2026-10-17-ci-asr-timestamp-precision.md`.
