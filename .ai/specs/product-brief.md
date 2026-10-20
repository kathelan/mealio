# Mealio — product brief

- Date: 2026-10-20
- Mode: existing + sprawdzenie założeń własnej potrzeby; Owner: właściciel produktu prowadzący sesję; Pass: full
- Evidence basis: dwie rozmowy z tym samym właścicielem korzystającym z Fitatu, nie dwa niezależne wywiady. Brak relacji drugiej osoby, pomiarów i testu nowego przepływu. Decyzje są potwierdzone, oszczędność pracy nie jest zmierzona.
- Coverage: 30 claims — 29 sourced (interview 2, data 0, document 23, product 4, benchmark 0), 0 synthetic, 1 assumed
- Synthetic hypotheses outside Coverage: 0
- Definition of Ready signed by: właściciel potwierdził Q35 i odświeżenie briefu/backlogu; gotowe do planowania produktów, przepisów i dziennika. Kalkulator blokuje Q02; całość nie jest gotowa do bezpośredniej implementacji.
- Refresh: [sesja grill-me](research/interviews/2026-10-20-owner-personal-mvp.md), [decyzja](research/decisions/2026-10-20-personal-mvp.md), [zakres](2026-10-20-personal-mvp.md).
- Sources: [relacja](research/interviews/2026-10-20-owner-food-diary.md), [decyzja](research/decisions/2026-10-20-mvp-development-stages.md), [research](research/README.md), [MVP](../../docs/mvp.md), [backend](../../backend/README.md), [ASR](../../asr/README.md).

## Decision summary

Budujemy osobiste narzędzie ograniczające powtarzanie wpisywania jedzenia. Nowe D08–D12 doprecyzowują wcześniejsze decyzje: jeden dziennik, osobne przepisy, parametry ilości na porcję i opcjonalna masa końcowa. Współdzielenie nie rozwiąże jeszcze problemu obu osób — świadomie pozostaje poza MVP. Dalszy krok to aktualizacja istniejącego backlogu po pokazaniu drzewa właścicielowi, następnie osobne SDD. Q02 blokuje kalkulator, nie podstawowy przepływ jedzenia; korzyść sprawdzi A01.

## Vision

Ograniczyć właścicielowi ponowne skanowanie i odtwarzanie potraw przez własne konfigurowalne narzędzie. [DOCUMENT] [decyzja](research/decisions/2026-10-20-personal-mvp.md).

## Target group and stakeholders

- Użytkownik MVP i decydent: właściciel opisujący własną pracę z Fitatu. [INTERVIEW] [relacja](research/interviews/2026-10-20-owner-personal-mvp.md).
- Docelowo również druga osoba w gospodarstwie, bez niezależnego wywiadu; brak celu monetyzacji i szerokiego rynku. [DOCUMENT] [decyzja](research/decisions/2026-10-20-personal-mvp.md).

## Problems, with evidence

- Właściciel opisuje powtarzane skanowanie i odtwarzanie potraw oraz trudność wspólnego korzystania z nich w obecnym sposobie używania Fitatu. Czas i częstotliwość niezmierzone; nie zweryfikowano ograniczeń samego Fitatu. [INTERVIEW] [relacja](research/interviews/2026-10-20-owner-personal-mvp.md).

## Product and how it stands out

- Istnieją API kategorii i przepisów oraz ASR zapisujący transkrypcje; klient SwiftUI to lokalny prototyp. [PRODUCT] [backend](../../backend/README.md), [ASR](../../asr/README.md), [iOS](../../ios-prototype%203/README.md).

| reference | what it does well | where it falls short for our users | checked on | link |
|---|---|---|---|---|

Benchmark odroczony: relacja o Fitatu nie jest niezależną inspekcją produktu; przewaga Mealio niezweryfikowana.

## Goals and success criteria

Wynik funkcjonalny: utworzenie potrawy raz, ponowne dodanie wybranej porcji do dziennika i otrzymanie przeliczonych sum. Właściciel zatwierdził ten kierunek, nie liczbowy próg sukcesu. [DOCUMENT] [decyzja](research/decisions/2026-10-20-personal-mvp.md).

Pomiar czasu, próg poprawy i data testu nieustalone; proponowany test A01. Chronione zasady: R01–R03.

## Scope

- **Now:** planowanie osobistego MVP D08–D12: produkty i skany, przepisy, własne sekcje, dzień/tydzień, cel ze wzoru z ręczną korektą. Specyfikacja kalkulatora czeka na Q02; pozostałe zakresy można specyfikować niezależnie.
- **Later:** Garmin, konta i świadome współdzielenie. AI poza MVP, bez decyzji o późniejszej realizacji.
- **Not doing:** N01–N03. Dawna kolejność D03–D06 jest historyczna; nie ustalono nowego harmonogramu.

## Domain glossary

| Term | Meaning | Owned by | Visible to |
|---|---|---|---|
| Potrawa | Przepis/szablon do ponownego użycia; nie fakt spożycia | recipe | użytkownik |
| Wpis | Spożyta ilość w dniu i sekcji | diary | użytkownik |
| Sekcja | Własny podział dnia, odrębny od kategorii potraw | diary | użytkownik |
| Transkrypcja | Tekst z audio, nie nagranie | asr | korekta przed zatwierdzeniem |
| Zero | Oszacowane zapotrzebowanie na utrzymanie masy | nutrition | użytkownik |

Definicje: D09–D12; transkrypcja pozostaje poza MVP. Granice modułów wymagają specyfikacji.

## Key flows

Produkt lub skan → zapisany przepis → sekcja i liczba zjadanych porcji → ilości wymagane checkboxem → jeden wpis z kcal/makro. Produkty można też dodawać bez przepisu; wybrane pozycje dziennika zapisać jako przepis bez dublowania spożycia. Szczegóły i przykłady: [zakres](2026-10-20-personal-mvp.md).

## Business rules

| Id | Rule | Applies to | Source | Status | Review by | Required path to change | Owner | Supersedes |
|---|---|---|---|---|---|---|---|---|
| R01 | Katalog dostarcza wartości; AI interpretuje, użytkownik zatwierdza propozycje. | wpisy | [PRODUCT] [MVP](../../docs/mvp.md) | active | przed parserem | akceptacja właściciela i SDD | właściciel | none |
| R02 | Brak danych nie jest zerem; bilans częściowy jest jawny, historia zachowuje snapshot. | bilans | [PRODUCT] [ADR-0006](../../docs/decisions/0006-incomplete-nutrition.md) | active | przed dziennikiem | akceptacja właściciela i ADR | właściciel | none |
| R03 | g/ml; sztuki wymagają jawnych przeliczników. | ilości | [PRODUCT] [ADR-0005](../../docs/decisions/0005-quantities-and-units.md) | active | przed porcjami | akceptacja właściciela i ADR | właściciel | none |
| R05 | Historycznie: jawna masa każdej porcji. | przepisy | [DOCUMENT] [porcje](research/decisions/2026-10-20-recipe-portions.md) | superseded | zastąpione R06 | zachowaj historię | właściciel | none |
| R04 | Tekst służy korekcie; brak archiwum odsłuchu, limity i usuwanie do określenia. | głos | [DOCUMENT] [decyzja](research/decisions/2026-10-20-mvp-development-stages.md) | active | przed ewentualnym powrotem głosu poza MVP | akceptacja właściciela i SDD | właściciel | none |
| R06 | Liczba porcji obowiązkowa, masa końcowa opcjonalna; kcal/makro ze składników. Checkbox pyta o ilość na jedną porcję, spożyta liczba porcji ją mnoży. | przepisy | [DOCUMENT] [D09](research/decisions/2026-10-20-personal-mvp.md) | active | przed SDD | akceptacja i SDD | właściciel | R05 |
| R07 | Przepis daje jeden wpis dziennika; korekta przy dodaniu nie zmienia szablonu ani historii. Aktualizacja szablonu jest osobną akcją. | dziennik | [DOCUMENT] [D09–D10](research/decisions/2026-10-20-personal-mvp.md) | active | przed SDD | akceptacja i SDD | właściciel | none |
| R08 | Etykiety są opcjonalne i płaskie, niezależne od własnych sekcji dnia. | organizacja | [DOCUMENT] [D10](research/decisions/2026-10-20-personal-mvp.md) | active | przed SDD | akceptacja i SDD | właściciel | none |

## Non-goals

| Id | We are not building | Why | Owner | Status | Review by | Required path to change | Source | Supersedes |
|---|---|---|---|---|---|---|---|---|
| N01 | Konta i współdzielenie teraz | osobny późniejszy etap; lokalnie/kontrolowane demo | właściciel | active | przed współdzieleniem | akceptacja i SDD/ADR | [DOCUMENT] [decyzja](research/decisions/2026-10-20-mvp-development-stages.md) | none |
| N02 | Obowiązkowe nasycone, cukry, sól i mikroskładniki | opcjonalne dane | właściciel | active | przed rozszerzeniem bilansu | akceptacja i SDD | [DOCUMENT] [decyzja](research/decisions/2026-10-20-mvp-development-stages.md) | none |
| N03 | AI, opłaty za AI i Garmin w MVP | prostsze osobiste narzędzie; Garmin później | właściciel | active | po MVP | potwierdzenie nowego zakresu | [DOCUMENT] [D08](research/decisions/2026-10-20-personal-mvp.md) | none |

## Decisions

Właściciel produktu, 2026-10-20. D01–D07 zachowują historię poprzedniej sesji; D08–D12 pochodzą z potwierdzonej sesji grill-me. Rekomendacje agenta zaakceptowane przez właściciela są decyzjami, nie dowodami skuteczności.

| Id | Date | Decision | Why | Owner | Status | Review by | Required path to change | Source | Supersedes |
|---|---|---|---|---|---|---|---|---|---|
| D01 | 2026-10-20 | 1. Produkty i obliczenia | fundament | właściciel | active | przed SDD | potwierdzenie nowej decyzji | [DOCUMENT] [zapis](research/decisions/2026-10-20-mvp-development-stages.md) | none |
| D02 | 2026-10-20 | 2. Potrawy, kategorie, własne sekcje i dziennik | ponowne użycie | właściciel | superseded | przed SDD | potwierdzenie oraz uzgodnienie MVP/ADR | [DOCUMENT] [zapis](research/decisions/2026-10-20-mvp-development-stages.md) | none |
| D03 | 2026-10-20 | 3. Kody kreskowe | dodatkowe wejście | właściciel | superseded | przed SDD | potwierdzenie nowej decyzji | [DOCUMENT] [zapis](research/decisions/2026-10-20-mvp-development-stages.md) | none |
| D04 | 2026-10-20 | 4. Tekst i głos do wpisu | korekta i potwierdzenie | właściciel | superseded | przed SDD | potwierdzenie nowej decyzji | [DOCUMENT] [zapis](research/decisions/2026-10-20-mvp-development-stages.md) | none |
| D05 | 2026-10-20 | 5. Zero/deficyt, ręczne makro i edytowalne propozycje celów makro uwzględniające aktywność, pozostały budżet | kontrola celu | właściciel | superseded | przed SDD | potwierdzenie oraz uzgodnienie MVP | [DOCUMENT] [zapis](research/decisions/2026-10-20-mvp-development-stages.md) | none |
| D06 | 2026-10-20 | 6. Docelowy SwiftUI na końcu | preferowana kolejność | właściciel | superseded | przed SDD | potwierdzenie nowej decyzji | [DOCUMENT] [zapis](research/decisions/2026-10-20-mvp-development-stages.md) | none |
| D07 | 2026-10-20 | Porcje przepisu zgodnie z R05, zamiast wyłącznie gramów spożycia z całego dania | doprecyzowanie właściciela | właściciel | superseded | przed SDD | potwierdzenie nowej decyzji | [DOCUMENT] [porcje](research/decisions/2026-10-20-recipe-portions.md) | none |
| D08 | 2026-10-20 | Osobiste MVP ze skanami; bez AI i Garmina. Nowa kolejność dostawy nieustalona. | ograniczenie codziennej pracy | właściciel | active | przed SDD | potwierdzenie zmiany | [DOCUMENT] [sesja](research/decisions/2026-10-20-personal-mvp.md) | D03, D04, D05, D06 |
| D09 | 2026-10-20 | Porcje i parametry według R06–R07 | elastyczne ponowne użycie | właściciel | active | przed SDD | potwierdzenie i SDD | [DOCUMENT] [sesja](research/decisions/2026-10-20-personal-mvp.md) | D07 |
| D10 | 2026-10-20 | Własne sekcje, dzień/tydzień, osobna zakładka przepisów i płaskie etykiety | własna organizacja | właściciel | active | przed SDD | potwierdzenie i SDD | [DOCUMENT] [sesja](research/decisions/2026-10-20-personal-mvp.md) | D02 |
| D11 | 2026-10-20 | Przepis bazowy może być składnikiem innego przepisu | ponowne użycie bulionu | właściciel | active | przed SDD baz | potwierdzenie i SDD | [DOCUMENT] [sesja](research/decisions/2026-10-20-personal-mvp.md) | none |
| D12 | 2026-10-20 | Cel kalorii ze wzoru z ręczną korektą; wzór pozostaje Q02 | kontrola redukcji, później utrzymania/rekompozycji | właściciel | active | przed kalkulatorem | potwierdzenie i SDD | [DOCUMENT] [sesja](research/decisions/2026-10-20-personal-mvp.md) | część D05 |

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
| Q01 | Liczba porcji obowiązkowa, masa opcjonalna; R06 zastępuje R05. | nie | właściciel | closed — D09 |
| Q02 | Wzór, aktywność i granice propozycji celu? | kalkulator D12 | maintainer z weryfikacją źródeł, właściciel zatwierdza | open |
| Q03 | Limity i retencja tekstu/audio? | tylko ewentualny powrót AI/głosu, poza MVP | właściciel i maintainer | open |
| Q04 | Typy cukrów i mapowanie opcjonalnych danych? | tylko rozszerzone pola | maintainer | open |

## Definition of Ready addendum (existing)

Własna relacja wspiera problem pierwszego użytkownika, bez dowodu popytu szerokiego rynku. Q35 potwierdza zakres, nie skuteczność aplikacji. Można odświeżyć backlog i planować osobne SDD, a kalkulator czeka na Q02. Specyfikacje muszą uzgodnić ADR-0004/0005 i kontrakty przed implementacją; ADR-0003 pozostaje w mocy. Nie ma repozytoryjnej Definition of Ready ani konfiguracji discovery. Nowe szczegóły baz przepisów wymagają projektu technicznego, nie założenia przelicznika ml/g.

## Collection plan

Brak próśb o nowe materiały. Q02 jest decyzją dla kalkulatora; Q03 poza MVP, Q04 tylko dla rozszerzonych danych. Następny test A01 nie został jeszcze przeprowadzony.
