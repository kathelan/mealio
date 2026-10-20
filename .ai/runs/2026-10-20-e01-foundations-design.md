# E01 — mapa fundamentów i osobne SDD

Data: 2026-10-20. Gałąź: docs/e01-reusable-foundations, PR #30.

## Cel i potwierdzenia

Właściciel zlecił wyprowadzenie wspólnych komponentów/backendu i SwiftUI z całego
backlogu podczas specyfikacji #10. Zaakceptował podział wspólna mapa + osobne wdrażalne
SDD oraz doprecyzował heksagonalność ze Springiem. Wcześniej jawnie dopuścił agentów
w tle do badań. Nie zlecono implementacji.

## Wykonane działania i pliki

- Rozwinięto `.ai/specs/2026-10-20-e01-reusable-foundations.md`: właściciele, konsumenci,
  kontrakty, graf modułów, heksagon, transakcje, komponenty UI, fazy i kryteria reużycia.
- Utworzono cztery SDD z tym samym prefiksem e01: product-catalog, nutrition-calculation,
  ios-product-foundations, barcode-resolution. Każda ma spójny wynik, granice, API/flow,
  failure modes, zgodność, plan testów i kroki dostawy.
- Trzy analizy agentów obejmowały kontrakty domen, iOS i dostawców. Zweryfikowano
  źródła pierwotne Spring/Java/Apple/OFF i wzorce w źródłach Wger.
- Dodano notatki research/e01-foundations-research i review oraz lekcję o heksagonie.
  Pełne nazwy to `2026-10-20-e01-foundations-research.md` i analogicznie `-review.md`.
- Osobny świeży agent wykonał przegląd spójności zakresu. Zamknięto dwie uwagi major
  i trzy minor; re-review bez pozostałych usterek. Nie zmieniono issues i istniejącej mapy.

## Weryfikacja

Kontrola dokumentacyjna: odnośniki lokalne, tabele, sekcje SDD, grafy Mermaid, spójność
limitów wejścia/wyniku, przykłady liczbowe i git diff --check — PASS.
Nie uruchamiano mvn verify, xcodebuild ani testów aplikacji: zakres wyłącznie Markdown,
zgodnie z AGENTS.md. Raport nie jest dowodem działania przyszłych komponentów.

## Ograniczenia i dalszy zakres

Specyfikacje techniczne podlegają review. Nowe kontrakty, wartości precision/scale,
retencja kandydata i szczegóły targetu są propozycjami tego PR, a nie stanem produktu.
Wdrożenie wymaga osobnego zlecenia i PR, uzgodnienia ADR-0005, OpenAPI, migracji i testów.
Kalkulator zapotrzebowania Q02, auth, AI, współdzielenie i Garmin są poza zakresem.
Mockupy/finalna estetyka pozostają do odrębnej rozmowy i akceptacji właściciela.
Zastane dwa nieśledzone raporty z 2026-10-17 pozostawiono bez zmian.
