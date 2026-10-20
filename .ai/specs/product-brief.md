# Mealio — product brief

- Date: 2026-10-20
- Mode: existing + sprawdzenie założeń własnej potrzeby; Owner: właściciel produktu prowadzący sesję; Pass: full
- Evidence basis: dwie rozmowy z tym samym właścicielem korzystającym z Fitatu, nie dwa niezależne wywiady. Brak relacji drugiej osoby, pomiarów i testu nowego przepływu. Decyzje są potwierdzone, oszczędność pracy nie jest zmierzona.
- Coverage: 53 claims — 52 sourced (interview 2, data 0, document 46, product 4, benchmark 0), 0 synthetic, 1 assumed
- Synthetic hypotheses outside Coverage: 0
- Definition of Ready signed by: właściciel potwierdził Q35 i odświeżenie briefu/backlogu; gotowe do planowania produktów, przepisów i dziennika. Kalkulator blokuje Q02; całość nie jest gotowa do bezpośredniej implementacji.
- Refresh po sesji grilling: [potwierdzone decyzje dziennika i przepisów](research/decisions/2026-10-20-diary-and-recipe-details.md); synchronizacja dokumentów i issues, bez implementacji i nowego pomiaru skuteczności.
- Refresh po review E01/E02/E03: synchronizacja potwierdzonych decyzji E01-C1–C4 i dziennika jako ekranu startowego; brak nowego badania.
- Refresh: [sesja grill-me](research/interviews/2026-10-20-owner-personal-mvp.md), [decyzja](research/decisions/2026-10-20-personal-mvp.md), [zakres](2026-10-20-personal-mvp.md).
- Sources: [relacja](research/interviews/2026-10-20-owner-food-diary.md), [decyzja](research/decisions/2026-10-20-mvp-development-stages.md), [research](research/README.md), [MVP](../../docs/mvp.md), [backend](../../backend/README.md), [ASR](../../asr/README.md).

## Decision summary

Budujemy osobisty dziennik z ponownie używanymi przepisami. Właściciel potwierdził
edycję pojedynczego spożycia, proste dodawanie/usuwanie sekcji z zachowaniem historii,
checkbox zmiennego inputu składnika, wiele etykiet oraz tydzień z wykresem dziennych kcal.
Aktualizacja masy i nowa propozycja celu wchodzą do MVP; Q02 nadal blokuje algorytm.
Bazy sosów pozostają w zakresie, bulion odłożono. Przelicznik bazy w g/ml i polityka
wersji przy edycji starego wpisu wymagają rozstrzygnięcia w SDD (Q05/Q06).
E01 ma projekty techniczne, E02 wymaga story-SDD; dokumenty i makieta nie są wdrożeniem.
A01 pozostaje nieprzetestowane: brak pomiaru ograniczenia pracy i niezależnego badania.

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

- **Now:** planowanie osobistego MVP D08–D24: produkty i skany, przepisy z etykietami i zmiennymi inputami, edytowalny dziennik i sekcje, dzień/tydzień z wykresem kcal, bazy sosów, aktualizacja masy i propozycji celu z ręczną korektą. Algorytm celu czeka na Q02; Q05/Q06 dotyczą wskazanych części SDD, nie blokują całego MVP.
- **Later:** bulion, Garmin, konta i świadome współdzielenie. AI poza MVP, bez decyzji o późniejszej realizacji.
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

Dziennik startowy → wybrana sekcja → wyszukanie/skan → wybór produktu i ilości → powrót do tej sekcji. Ten sam picker zwraca wybór również edytorowi przepisu, bez samodzielnego zapisu spożycia. [DOCUMENT] [D17](../prototypes/discovery/e01-product-journeys/revision-002/README.md), [granice E01](2026-10-20-e01-ios-product-foundations.md).

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
| R09 | GTIN jest unikalny; konflikt daje użyj istniejącego / popraw / anuluj. Korekta tworzy niezmienną wersję z pochodzeniem per pole; lokalny skan nie wymaga popupu i nie odpytuje źródeł w tle. | katalog/skan | [DOCUMENT] [E01-C2](research/decisions/2026-10-20-e01-product-corrections.md) | active | przed implementacją #13/#20 | akceptacja właściciela i SDD | właściciel | none |
| R10 | Wiele własnych nazw sklepów jest metadanymi produktu odrębnymi od marki, pochodzenia i wersji żywieniowej. | katalog | [DOCUMENT] [E01-C3](research/decisions/2026-10-20-e01-product-corrections.md) | active | przed implementacją #13/#24 | akceptacja właściciela i SDD | właściciel | none |
| R11 | Awaria/limit nie oznacza NOT_FOUND; ręczny formularz pozostaje dostępny z zachowaniem przyczyny. FatSecret jest opcjonalny. | skan | [DOCUMENT] [E01-C1](research/decisions/2026-10-20-e01-product-corrections.md) | active | przed implementacją #20/#25 | akceptacja właściciela i SDD | właściciel | none |
| R12 | Edycja spożycia zmienia tylko wskazany wpis i bilans jego dnia, także w historii; szablon i inne wpisy pozostają bez zmian. Przeniesienie zachowuje wartości; usunięcie oferuje Cofnij. | wpisy | [DOCUMENT] [sesja](research/decisions/2026-10-20-diary-and-recipe-details.md) | active | przed SDD zakresu | akceptacja właściciela i SDD | właściciel | none |
| R13 | Nazwa sekcji zmienia się globalnie. Usuń zachowuje dawne wpisy pod ostatnią nazwą i usuwa sekcję z wyboru. Brak przywracania; nowa o tej samej nazwie ma osobną tożsamość. | sekcje | [DOCUMENT] [sesja](research/decisions/2026-10-20-diary-and-recipe-details.md) | active | przed SDD zakresu | akceptacja właściciela i SDD | właściciel | none |
| R14 | Checkbox składnika wybiera zmienny input gramatury albo liczby sztuk na jedną porcję; spożyta liczba porcji mnoży wynik. Brak obowiązkowej pary ilość i masa sztuki. | przepisy | [DOCUMENT] [sesja](research/decisions/2026-10-20-diary-and-recipe-details.md) | active | przed SDD zakresu | akceptacja właściciela i SDD | właściciel | none |
| R15 | Wiele etykiet albo brak; startowa lista Śniadanie, Obiad, Kolacja, Przekąska jest edytowalna. Filtr w bibliotece i pickerze pozwala też pokazać wszystkie przepisy. Etykiety pozostają odrębne od sekcji. | etykiety | [DOCUMENT] [sesja](research/decisions/2026-10-20-diary-and-recipe-details.md) | active | przed SDD zakresu | akceptacja właściciela i SDD | właściciel | none |
| R16 | Start w dniu; przełączenie na tydzień pokazuje wykres dziennych kcal. Brak wpisów nie potwierdza braku spożycia, a niepełne dane nie stają się zerem. | dziennik | [DOCUMENT] [sesja](research/decisions/2026-10-20-diary-and-recipe-details.md) | active | przed SDD zakresu | akceptacja właściciela i SDD | właściciel | none |
| R17 | Aktualizacja/usunięcie bazy nie zmienia użytej wersji w istniejących przepisach. Sosy używają g/ml z jawnym przelicznikiem; pomiar gotowej potrawy nie jest obowiązkowy. Bulion później. | bazy | [DOCUMENT] [sesja](research/decisions/2026-10-20-diary-and-recipe-details.md) | active | przed SDD zakresu | akceptacja właściciela i SDD | właściciel | none |
| R18 | Zmiana masy w ustawieniach jednego dziennika przelicza propozycję celu do zatwierdzenia, z ręczną korektą. Dawne dni zachowują wcześniejszy cel; brak kont i userId. | cel kalorii | [DOCUMENT] [sesja](research/decisions/2026-10-20-diary-and-recipe-details.md) | active | przed SDD zakresu | akceptacja właściciela i SDD | właściciel | none |

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
| D10 | 2026-10-20 | Domyślne sekcje śniadanie, drugie śniadanie, obiad, lunch, kolacja i przekąski; użytkownik może zmienić nazwy i liczbę sekcji; widok dnia/tygodnia, osobna zakładka przepisów i płaskie etykiety | własna organizacja | właściciel | active | przed SDD | potwierdzenie i SDD | [DOCUMENT] [sesja](research/decisions/2026-10-20-personal-mvp.md) | D02 |
| D11 | 2026-10-20 | Przepis bazowy może być składnikiem innego przepisu | ponowne użycie bulionu | właściciel | superseded | przed SDD baz | potwierdzenie i SDD | [DOCUMENT] [sesja](research/decisions/2026-10-20-personal-mvp.md) | none |
| D12 | 2026-10-20 | Cel kalorii ze wzoru z ręczną korektą; wzór pozostaje Q02 | kontrola redukcji, później utrzymania/rekompozycji | właściciel | active | przed kalkulatorem | potwierdzenie i SDD | [DOCUMENT] [sesja](research/decisions/2026-10-20-personal-mvp.md) | część D05 |
| D13 | 2026-10-20 | Ręczny fallback zgodnie z R11 | brak zależności ręcznego wpisu od dostawcy | właściciel | active | przed #20/#25 | akceptacja i SDD | [DOCUMENT] [E01-C1](research/decisions/2026-10-20-e01-product-corrections.md) | none |
| D14 | 2026-10-20 | Konflikt i świadoma korekta zgodnie z R09 | zachowanie tożsamości i historii bez popupu przy każdym skanie | właściciel | active | przed #13/#20 | akceptacja i SDD | [DOCUMENT] [E01-C2](research/decisions/2026-10-20-e01-product-corrections.md) | none |
| D15 | 2026-10-20 | Sklepy zgodnie z R10 | rozróżnianie produktów bez centralnego słownika | właściciel | active | przed #13/#24 | akceptacja i SDD | [DOCUMENT] [E01-C3](research/decisions/2026-10-20-e01-product-corrections.md) | none |
| D16 | 2026-10-20 | Klikalne low-fi przed implementacją #24/#25; backend #13/#14 może postępować niezależnie | sprawdzenie przepływu bez zatwierdzania finalnego stylu | właściciel | active | przed implementacją iOS | akceptacja i SDD | [DOCUMENT] [E01-C4](research/decisions/2026-10-20-e01-product-corrections.md) | none |
| D17 | 2026-10-20 | Start aplikacji to dziennik z sekcjami i sumą spożytych kcal na dole; wdrożenie #17/#27 | dostęp do codziennego rejestrowania | właściciel | active | przed SDD #27 | akceptacja i SDD | [DOCUMENT] [potwierdzenie w kontekście low-fi](../prototypes/discovery/e01-product-journeys/revision-002/README.md) | none |
| D18 | 2026-10-20 | Edycja, przenoszenie i usuwanie spożycia zgodnie z R12 | korekta rzeczywistego spożycia | właściciel | active | przed SDD zakresu | potwierdzenie i SDD | [DOCUMENT] [sesja](research/decisions/2026-10-20-diary-and-recipe-details.md) | none |
| D19 | 2026-10-20 | Dodaj/Usuń sekcję i historia zgodnie z R13; bez przywracania | prosta organizacja dni | właściciel | active | przed SDD zakresu | potwierdzenie i SDD | [DOCUMENT] [sesja](research/decisions/2026-10-20-diary-and-recipe-details.md) | none |
| D20 | 2026-10-20 | Zmienny input składnika zgodnie z R14 | parametryzacja ilości bez osobnego systemu formuł | właściciel | active | przed SDD zakresu | potwierdzenie i SDD | [DOCUMENT] [sesja](research/decisions/2026-10-20-diary-and-recipe-details.md) | none |
| D21 | 2026-10-20 | Etykiety i filtrowanie zgodnie z R15 | wybór i odnajdywanie potraw | właściciel | active | przed SDD zakresu | potwierdzenie i SDD | [DOCUMENT] [sesja](research/decisions/2026-10-20-diary-and-recipe-details.md) | none |
| D22 | 2026-10-20 | Dzień startowy i tygodniowy wykres kcal zgodnie z R16 | przegląd zapisanych dni | właściciel | active | przed SDD zakresu | potwierdzenie i SDD | [DOCUMENT] [sesja](research/decisions/2026-10-20-diary-and-recipe-details.md) | none |
| D23 | 2026-10-20 | Bazy sosów i zachowanie wersji zgodnie z R17; bulion później | ponowne użycie bez obowiązku mierzenia gotowej potrawy | właściciel | active | przed SDD zakresu | potwierdzenie i SDD | [DOCUMENT] [sesja](research/decisions/2026-10-20-diary-and-recipe-details.md) | D11 |
| D24 | 2026-10-20 | Aktualizacja masy i propozycji celu w MVP zgodnie z R18; Q02 otwarte | cel reaguje na zmianę masy | właściciel | active | przed SDD zakresu | potwierdzenie i SDD | [DOCUMENT] [sesja](research/decisions/2026-10-20-diary-and-recipe-details.md) | none |

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
| Q05 | Jak wybierać wersję danych przy świadomej korekcie starego wpisu, w tym uzupełnianiu braków? | ta część SDD #17; izolacja wpisów jest potwierdzona | maintainer proponuje, właściciel zatwierdza | open |
| Q06 | Skąd jawny przelicznik bazy sosu w g/ml bez obowiązkowego pomiaru gotowej potrawy i jak obsłużyć jego brak? | ta część SDD #22; bez zgadywania ml/g | maintainer proponuje, właściciel zatwierdza | open |
| Q07 | Czy zmiana kolejności sekcji jest wymogiem MVP? | nie, opcjonalna propozycja | właściciel | proposal — odpowiedź może tak |

## Definition of Ready addendum (existing)

Własna relacja wspiera problem pierwszego użytkownika, bez dowodu popytu szerokiego rynku. Q35 potwierdza zakres, nie skuteczność aplikacji. Właściciel zlecił synchronizację briefu, issues i backlogu z D18–D24. Można planować osobne SDD; Q02/Q05/Q06 pozostają jawne. Zadanie analizy celu nie jest gotową dostawą algorytmu.
E01 ma projekty katalogu, obliczeń, skanera i klienta; E02 jest mapą wymagającą story-SDD.
Aktualna [revision-003](../prototypes/discovery/e01-product-journeys/revision-003/README.md)
przeszła kontrolę techniczną, co nie oznacza osobnej akceptacji przepływu przez właściciela
ani dowodu ograniczenia pracy. Kalkulator czeka na Q02. ADR-0004 uzgodniono w mapie E02; ADR-0005 wymaga uzgodnienia PIECE przy implementacji.
Kontrakty i migracje weryfikują osobne dostawy; ADR-0003 pozostaje w mocy. Nie ma repozytoryjnej Definition of Ready ani konfiguracji discovery. Bazy sosów wymagają projektu przelicznika Q06; bulion jest późniejszy. Ustalenia tej sesji są decyzjami jednego właściciela, bez nowego wywiadu o zachowaniu lub pomiaru skuteczności.

## Collection plan

Brak próśb o nowe materiały. Q02 jest decyzją dla kalkulatora; Q03 poza MVP, Q04 tylko dla rozszerzonych danych. Następny test A01 nie został jeszcze przeprowadzony.
