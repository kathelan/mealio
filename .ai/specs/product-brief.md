# Mealio — product brief

- Date: 2026-10-20
- Mode: existing; Owner: właściciel produktu prowadzący sesję; Pass: full
- Evidence basis: jedna bezpośrednia relacja korzystającego z Fitatu właściciela; dokumentacja repozytorium i decyzja z tej samej sesji. Brak niezależnej rozmowy z żoną, danych użycia i pomiarów. Research z 20 października jest kompilacją dokumentów, nie dodatkowym badaniem.
- Coverage: 21 claims — 20 sourced (interview 2, data 0, document 14, product 4, benchmark 0), 0 synthetic, 1 assumed
- Synthetic hypotheses outside Coverage: 0
- Definition of Ready signed by: właściciel potwierdził kierunek; gotowe do przygotowania backlogu i planowania specyfikacji etapów 1–2, nie do bezpośredniej implementacji. Przegląd zgodności kontraktów wymagany przy specyfikacji.
- Sources: [relacja](research/interviews/2026-10-20-owner-food-diary.md), [decyzja](research/decisions/2026-10-20-mvp-development-stages.md), [research](research/README.md), [MVP](../../docs/mvp.md), [backend](../../backend/README.md), [ASR](../../asr/README.md).

## Decision summary

Właściciel zatwierdził sześć etapów D01–D06. Pierwszy kompletny przepływ ma ograniczyć ponowne tworzenie potraw przez jedną osobę. Współdzielenie pozostaje później, więc problem przepisywania przez żonę nie zostanie jeszcze rozwiązany. Dobór kalkulatora i retencja wymagają osobnych rozstrzygnięć Q02–Q03. Następny proponowany krok to specyfikacja pierwszego przepływu, bez uruchamiania implementacji.

## Vision

Ułatwić właścicielowi ponowne zapisywanie potraw i kontrolowanie dziennego bilansu. [DOCUMENT] [decyzja](research/decisions/2026-10-20-mvp-development-stages.md).

## Target group and stakeholders

- Pierwszy użytkownik: właściciel używający Fitatu; żona jest przyszłą odbiorczynią współdzielenia wyłącznie według jego relacji. [INTERVIEW] [relacja](research/interviews/2026-10-20-owner-food-diary.md).
- Decider: właściciel sesji; płacący klient i monetyzacja nieustaleni. [DOCUMENT] [decyzja](research/decisions/2026-10-20-mvp-development-stages.md).

## Problems, with evidence

- Rozmówca opisuje ponowne tworzenie potraw i przepisywanie ich przez żonę; koszt i częstotliwość niezmierzone. Nie potwierdzono ograniczenia samego Fitatu. [INTERVIEW] [relacja](research/interviews/2026-10-20-owner-food-diary.md).

## Product and how it stands out

- Istnieją API kategorii i przepisów oraz ASR zapisujący transkrypcje; klient SwiftUI to lokalny prototyp. [PRODUCT] [backend](../../backend/README.md), [ASR](../../asr/README.md), [iOS](../../ios-prototype%203/README.md).

| reference | what it does well | where it falls short for our users | checked on | link |
|---|---|---|---|---|

Benchmark odroczony: relacja o Fitatu nie jest niezależną inspekcją produktu; przewaga Mealio niezweryfikowana.

## Goals and success criteria

Wynik funkcjonalny: utworzenie potrawy raz, ponowne dodanie wybranej porcji do dziennika i otrzymanie przeliczonych sum. Właściciel zatwierdził ten kierunek, nie liczbowy próg sukcesu. [DOCUMENT] [decyzja](research/decisions/2026-10-20-mvp-development-stages.md).

Pomiar czasu, próg poprawy i data testu nieustalone; proponowany test A01. Chronione zasady: R01–R03.

## Scope

- **Now:** planowanie etapów 1–2 jako całości: katalog → potrawa → ponowne użycie → dziennik → bilans; D01–D02.
- **Later:** D03–D06 i współdzielenie po kontach; N01.
- **Not doing:** Non-goals.

## Domain glossary

| Term | Meaning | Owned by | Visible to |
|---|---|---|---|
| Potrawa | Przepis/szablon do ponownego użycia; nie fakt spożycia | recipe | użytkownik |
| Wpis | Spożyta ilość w dniu i sekcji | diary | użytkownik |
| Sekcja | Własny podział dnia, odrębny od kategorii potraw | diary | użytkownik |
| Transkrypcja | Tekst z audio, nie nagranie | asr | korekta przed zatwierdzeniem |
| Zero | Oszacowane zapotrzebowanie na utrzymanie masy | nutrition | użytkownik |

Definicje docelowe: D02, D04–D05; granice modułów wymagają specyfikacji.

## Key flows

Obecny problem opisano w Problems. Docelowy przepływ: produkt z katalogu → zapisana potrawa i kategoria → wybór dnia/sekcji/porcji → obliczenie → potwierdzenie → bilans; D01–D02. Etap backendu nie dowodzi użyteczności późniejszego interfejsu SwiftUI.

## Business rules

| Id | Rule | Applies to | Source | Status | Review by | Required path to change | Owner | Supersedes |
|---|---|---|---|---|---|---|---|---|
| R01 | Katalog dostarcza wartości; AI interpretuje, użytkownik zatwierdza propozycje. | wpisy | [PRODUCT] [MVP](../../docs/mvp.md) | active | przed parserem | akceptacja właściciela i SDD | właściciel | none |
| R02 | Brak danych nie jest zerem; bilans częściowy jest jawny, historia zachowuje snapshot. | bilans | [PRODUCT] [ADR-0006](../../docs/decisions/0006-incomplete-nutrition.md) | active | przed dziennikiem | akceptacja właściciela i ADR | właściciel | none |
| R03 | g/ml; sztuki wymagają jawnych przeliczników. | ilości | [PRODUCT] [ADR-0005](../../docs/decisions/0005-quantities-and-units.md) | active | przed porcjami | akceptacja właściciela i ADR | właściciel | none |
| R05 | Przepis dzieli się na porcje o jawnej masie w gramach; kcal i makro wynikają ze składników i podziału na porcje. | przepisy i wpisy | [DOCUMENT] [porcje](research/decisions/2026-10-20-recipe-portions.md) | active | przed D02 | akceptacja właściciela i SDD | właściciel | none |
| R04 | Tekst służy korekcie; brak archiwum odsłuchu, limity i usuwanie do określenia. | głos | [DOCUMENT] [decyzja](research/decisions/2026-10-20-mvp-development-stages.md) | active | przed D04 | akceptacja właściciela i SDD | właściciel | none |

## Non-goals

| Id | We are not building | Why | Owner | Status | Review by | Required path to change | Source | Supersedes |
|---|---|---|---|---|---|---|---|---|
| N01 | Konta i współdzielenie teraz | osobny późniejszy etap; lokalnie/kontrolowane demo | właściciel | active | przed współdzieleniem | akceptacja i SDD/ADR | [DOCUMENT] [decyzja](research/decisions/2026-10-20-mvp-development-stages.md) | none |
| N02 | Obowiązkowe nasycone, cukry, sól i mikroskładniki | opcjonalne dane | właściciel | active | przed rozszerzeniem bilansu | akceptacja i SDD | [DOCUMENT] [decyzja](research/decisions/2026-10-20-mvp-development-stages.md) | none |

## Decisions

Wszystkie decyzje: właściciel produktu, 2026-10-20; źródło i uzasadnienie w zapisie sesji. Kolejność była propozycją agenta zaakceptowaną przez właściciela.

| Id | Date | Decision | Why | Owner | Status | Review by | Required path to change | Source | Supersedes |
|---|---|---|---|---|---|---|---|---|---|
| D01 | 2026-10-20 | 1. Produkty i obliczenia | fundament | właściciel | active | przed SDD | potwierdzenie nowej decyzji | [DOCUMENT] [zapis](research/decisions/2026-10-20-mvp-development-stages.md) | none |
| D02 | 2026-10-20 | 2. Potrawy, kategorie, własne sekcje i dziennik | ponowne użycie | właściciel | active | przed SDD | potwierdzenie oraz uzgodnienie MVP/ADR | [DOCUMENT] [zapis](research/decisions/2026-10-20-mvp-development-stages.md) | none |
| D03 | 2026-10-20 | 3. Kody kreskowe | dodatkowe wejście | właściciel | active | przed SDD | potwierdzenie nowej decyzji | [DOCUMENT] [zapis](research/decisions/2026-10-20-mvp-development-stages.md) | none |
| D04 | 2026-10-20 | 4. Tekst i głos do wpisu | korekta i potwierdzenie | właściciel | active | przed SDD | potwierdzenie nowej decyzji | [DOCUMENT] [zapis](research/decisions/2026-10-20-mvp-development-stages.md) | none |
| D05 | 2026-10-20 | 5. Zero/deficyt, ręczne makro i edytowalne propozycje celów makro uwzględniające aktywność, pozostały budżet | kontrola celu | właściciel | active | przed SDD | potwierdzenie oraz uzgodnienie MVP | [DOCUMENT] [zapis](research/decisions/2026-10-20-mvp-development-stages.md) | none |
| D06 | 2026-10-20 | 6. Docelowy SwiftUI na końcu | preferowana kolejność | właściciel | active | przed SDD | potwierdzenie nowej decyzji | [DOCUMENT] [zapis](research/decisions/2026-10-20-mvp-development-stages.md) | none |
| D07 | 2026-10-20 | Porcje przepisu zgodnie z R05, zamiast wyłącznie gramów spożycia z całego dania | doprecyzowanie właściciela | właściciel | active | przed SDD | potwierdzenie nowej decyzji | [DOCUMENT] [porcje](research/decisions/2026-10-20-recipe-portions.md) | none |

## Riskiest assumptions

| Id | Assumption | Importance | Evidence today | If false | Smallest test | Owner | By when | Result |
|---|---|---|---|---|---|---|---|---|
| A01 | Ponowne użycie potraw ograniczy pracę. [ASSUMPTION] interpretacja relacji | high | jedna relacja | zmiana przepływu | właściciel ponownie zapisuje rzeczywistą potrawę; obserwować odtwarzanie składników i korekty | właściciel | po pierwszym przepływie | untested |

## Kill criteria

Propozycja: jeśli ponowny zapis nadal wymaga odtwarzania składników, poprawić przepływ przed kolejnymi wejściami. Kryterium i termin oceny wymagają decyzji właściciela; brak decyzji o zatrzymaniu całego produktu.

## Hypotheses to test

Nie przeprowadzono panelu syntetycznego.

## Open questions

| Id | Question | Blocking | Who can answer | Status |
|---|---|---|---|---|
| Q01 | Porcja ma określoną masę w gramach; obliczenia zgodnie z R05/D07, historia zgodnie z R02. Szczegóły techniczne w SDD. | nie; decyzja rozwiązana | właściciel i maintainer | closed — D07 |
| Q02 | Wzór, aktywność i granice propozycji celu? | D05 | maintainer z weryfikacją źródeł, właściciel zatwierdza | open |
| Q03 | Limity i retencja tekstu/audio? | D04 | właściciel i maintainer | open |
| Q04 | Typy cukrów i mapowanie opcjonalnych danych? | tylko rozszerzone pola | maintainer | open |

## Definition of Ready addendum (existing)

Problem i pierwszy odbiorca mają wsparcie jednej relacji. Nie potwierdza ona popytu innych osób. Kierunek i sposób porcjowania zatwierdzone; Q01 zamknięte przez D07. Wymagane SDD i sprawdzenie kontraktów przed implementacją D02. Obecne MVP/ADR odkładają przepisy, własne sekcje i cele: należy je uzgodnić przed zmianą zachowania. N01 zachowuje ADR-0003. Brak konfiguracji warstwy discovery; `om-setup-discovery-pipeline` może dodać kontrole gotowości, ale nie jest uruchamiany tutaj.

## Collection plan

Brak dodatkowych próśb o materiały w tej sesji. Q02–Q04 to decyzje i prace specyfikacyjne dla kolejnych etapów; nie są wykonanymi badaniami.
