# Odświeżenie osobistego MVP, briefu i propozycji backlogu

Data: 2026-10-20. Gałąź: docs/mvp-personal-recipes.

## Cel

Utrwalić zatwierdzone w sesji grill-me ustalenia, odświeżyć brief przez om-discover
oraz przygotować aktualizację backlogu przez om-backlog. Bez implementacji.

## Działania i pliki

- Nowa specyfikacja zakresu `.ai/specs/2026-10-20-personal-mvp.md`, bez pozorowania gotowej SDD technicznej.
- Zaktualizowany `.ai/specs/product-brief.md` z zachowanymi ID i statusem dawnych decyzji.
- Nowa relacja oraz decyzja `2026-10-20-personal-mvp.md` pod research; stare decyzje
  o etapach i porcjach oznaczone jako historyczne, bez usuwania ich treści.
- README research i docs/mvp.md wskazują nowy zakres; lessons zawiera trwałe korekty.
- `.ai/specs/research/2026-10-20-backlog-refresh-proposal.md` przedstawia kompletne drzewo,
  kryteria, zależności i propozycję aktualizacji #10–#17 oraz czterech nowych stories.
- Nie zmieniono issues ani `.ai/specs/backlog.md`: om-backlog wymaga potwierdzenia
  pokazanego drzewa i kryteriów przed zapisem do trackera. Nie uruchomiono implementacji ani merge.

## Weryfikacja

Odrębny skeptical reading briefu wymagany przez om-discover: brak usterek krytycznych.
Poprawiono nieaktualny termin przeglądu R04, który wskazywał zastąpione D04.
Sprawdzono zgodność z rozmową, lokalne odnośniki, strukturę tabel, licznik Coverage
oraz git diff --check. Wyszukiwanie i odczyt trackera potwierdziły mapowania #10–#17
po pełnym źródle/ID i brak claimów. Wynik kontroli dokumentacyjnych: PASS.

Nie uruchamiano mvn verify ani testów aplikacji: zmiany wyłącznie Markdown,
zgodnie z dopasowaniem weryfikacji dokumentacji w AGENTS.md. Nie jest to dowód wdrożenia
ani weryfikacji działania planowanych funkcji.

## Otwarte kwestie

Potwierdzenie proponowanego drzewa backlogu, wybór wzoru i modelu aktywności kalkulatora,
szczegóły techniczne przepisów bazowych, jednostek, migracji i API w osobnych SDD.
Konta, współdzielenie, AI i Garmin pozostają poza MVP. Lokalna biblioteka nie zapewnia
izolacji danych osób. Zastane dwa nieśledzone raporty z 2026-10-17 pozostawiono bez zmian.
