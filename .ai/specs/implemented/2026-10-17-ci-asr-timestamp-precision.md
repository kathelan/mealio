# Stabilny test precyzji czasu ASR w CI

Data: 2026-10-17. Zakres: naprawa testu, bez zmiany zachowania produktu/API/schematu.

Przebieg GitHub 39028331489: Docker i PostgreSQL/Testcontainers działają;
TranscriptionControllerIntegrationTest porównuje Instant z odpowiedzi HTTP
z odczytem TIMESTAMP z H2. Runner Linux generuje nanosekundy, kolumna bazy
zachowuje mikrosekundy. Mac lokalnie nie ujawnił różnicy.

## Plan i kryteria

- [x] Odtworzyć błąd z Clock.fixed i wartością z logu zawierającą nanosekundy.
  Kryterium: stara asercja pada lokalnie z tą samą różnicą precyzji.
- [x] Sprawdzać jawnie dokładny czas odpowiedzi i dokładny czas zapisany zgodnie
  z precyzją kolumny, zachowując wszystkie dotychczasowe sprawdzenia danych.
  Kryterium: brak tolerancji czasowej, test niezależny od zegara systemowego.
- [x] Uruchomić integracje ASR i pełne mvn clean verify, zapisać wyniki i lekcję.
  Kryterium: wszystkie testy i bramka 100% jednostkowych linii przechodzą.

Nie zmieniamy migracji ani nie obniżamy progu pokrycia. Ostrzeżenia o wersjach
GitHub Actions nie spowodowały tej awarii i nie są częścią tej poprawki.

## Odtworzenie

Stała wartość `2026-10-17T11:21:45.548394163Z` z logu GitHub odtworzyła błąd
lokalnie w starej asercji (baza: `2026-10-17T11:21:45.548394Z`).
Test sprawdza teraz oddzielnie dokładny wynik HTTP i dokładny wynik persystencji,
bez tolerancji i bez zależności od precyzji zegara systemowego.

## Wynik końcowy

Integracje ASR oraz pełne `mvn clean verify`: PASS, 112 testów, bez błędów
ani pominięć. Pokrycie jednostkowe linii wszystkich modułów Java nadal 100%.
Poprawka lokalna, nowy przebieg GitHub wymaga wysłania commita.
