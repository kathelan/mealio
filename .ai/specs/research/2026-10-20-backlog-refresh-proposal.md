# Propozycja odświeżenia backlogu — do potwierdzenia

Data: 2026-10-20. Źródło: [brief](../product-brief.md) i
[zatwierdzony zakres](../2026-10-20-personal-mvp.md).

Status: om-backlog, krok 4 — przedstawienie drzewa i kryteriów przed zapisem do trackera.
Nie zmieniono issues ani istniejącej mapy backlogu. Nie jest to zlecenie implementacji.
SDLC nie ma osobnej Definition of Ready: pominięto tę dodatkową bramkę; może ją dodać
om-setup-discovery-pipeline. Problem i odbiorca mają wsparcie relacji właściciela.
Poszczególne implementacje nadal wymagają SDD, uzgodnienia ADR i kontraktów.

## Drzewo i dopasowanie

E01 i E02 zachowują źródło `.ai/specs/product-brief.md` i numery #10/#11. E03 zachowuje #12.
Sprawdzono wszystkie istniejące pełne identyfikatory i oznaczenia źródła w #10–#17;
brak aktywnych claimów. Wyszukiwanie po źródle i kilku wariantach nazw nie wykazało
dodatkowych kandydatów do adopcji. Nowe ID wymagają ponownego sprawdzenia przed zapisem.

E01 obejmuje produkty/obliczenia i skan. E02 obejmuje ponowne użycie potraw oraz dziennik.
E03 pozostaje zaparkowany; usuwa z listy „później” skany przeniesione do E01.
UI jest częścią docelowego MVP, a nie warunkiem przyszłego epika D06.

Etykiety włączone. Stories: `feature`, `priority-medium`, `risk-high` (trwałe dane,
kontrakty i migracje), z wyjątkiem E01-S02: `risk-medium` (obliczenia).
E01/E02 dziedziczą `risk-high`. E03: `feature`, `priority-low`, `risk-medium`.
Ryzyko dokumentacyjnego PR jest odrębne: `risk-low`. Na issues nie nakładamy etykiet pipeline.

| ID | Docelowy tytuł | Tracker | Kryteria | Zależności |
|---|---|---|---|---|
| E01-S01 | Własny produkt, wyszukiwanie i przelicznik sztuk | aktualizacja #13 | 4 | none |
| E01-S02 | Obliczenie kcal i makro dla ilości | aktualizacja #14 | 3 | E01-S01 (#13) |
| E01-S03 | Zapamiętany produkt ze skanu kodu | nowe | 4 | E01-S01 (#13) |
| E02-S01 | Przepisy, porcje i własne etykiety | aktualizacja #15 | 5 | E01-S01 (#13), E01-S02 (#14) |
| E02-S02 | Własne sekcje dnia | aktualizacja #16 | 3 | none |
| E02-S03 | Porcja przepisu i bilans dnia lub tygodnia | aktualizacja #17 | 5 | E02-S01 (#15), E02-S02 (#16) |
| E02-S04 | Ilości składników przy dodawaniu przepisu | nowe | 4 | E02-S01 (#15), E02-S03 (#17) |
| E02-S05 | Własna baza jako składnik przepisu | nowe | 4 | E02-S01 (#15), E01-S02 (#14) |
| E02-S06 | Zapis pozycji dziennika jako przepisu | nowe | 3 | E02-S01 (#15), E02-S03 (#17) |

## Epiki — proponowane treści

### E01 — Produkty i obliczenia (#10)

Rezultat: raz zapisać produkt, znaleźć go ponownie nazwą lub kodem i obliczyć wartości ilości.
Kryteria: przechodzą E01-S01–S03. Zależności: none. Lista stories: #13, #14, E01-S03.
Poza zakresem: AI, konta i automatyczne współdzielenie. Decyzje D01/D08; R01–R03.

### E02 — Potrawy wielokrotnego użytku i dziennik (#11)

Rezultat: ponownie zapisać potrawę jako pojedynczy wpis, poprawiając tylko potrzebne ilości,
z zachowaniem historii. Kryteria: przechodzą E02-S01–S06. Zależność: E01 (#10).
Lista stories: #15–#17 oraz E02-S04–S06. Poza zakresem: konta, Garmin, AI i kalkulator.
Decyzje D09–D11; R02–R03/R06–R08.

### E03 — Późniejsze etapy — zaparkowane (#12)

Konta, prywatność własnych produktów po dodaniu tożsamości, wybór osób do współdzielenia
oraz Garmin. AI poza MVP, bez zatwierdzonej realizacji. Cel kalorii należy do docelowego MVP,
ale jego story jest wstrzymana do Q02 — nie traktujemy jej jako usuniętej z MVP.
Kryterium: przed uruchomieniem zakres ma osobno rozstrzygnięte zależności i SDD.
Nie implementować zbiorczo E03. N01/N03, D08/D12.

## Proponowane treści stories

Wspólne sekcje treści issues:

- Problem i odbiorca: właściciel opisuje powtarzane skanowanie i odtwarzanie potraw;
  źródło: [relacja](interviews/2026-10-20-owner-personal-mvp.md). Nie jest to pomiar ani
  niezależna weryfikacja Fitatu.
- Oczekiwany wynik: rezultat wskazany w tytule i kryteriach konkretnej story.
- Poza zakresem: AI, konta, współdzielenie, Garmin i zmiana historii przez edycję szablonu.
- Otwarte kwestie: szczegóły techniczne w SDD; Q02 blokuje tylko kalkulator.
- Specyfikacja: zatwierdzony zakres produktu nie jest specyfikacją implementacyjną.
  ADR-0003 pozostaje w mocy, ADR-0004/0005 wymagają uzgodnienia; OpenAPI, nowe migracje,
  100% pokrycia jednostkowego, integracje i mvn clean verify obowiązują przy kodzie.
- Dane źródła: `Backlog source: .ai/specs/product-brief.md`; pełny `Backlog id` i `Epic`
  zgodnie z każdą pozycją. Nie zmieniamy ani nie renumerujemy istniejących tożsamości.

### E01-S01 — Własny produkt, wyszukiwanie i przelicznik sztuk

Backlog source: .ai/specs/product-brief.md
Backlog id: E01-S01
Epic: #10
Depends on: none

Decisions in play: D01, R01–R03, N01. Właściciel wszystkich wyborów: właściciel produktu.

- [ ] Po zapisaniu produktu z nazwą, podstawą 100 g lub 100 ml i wartościami można wyszukać go ponownie bez tworzenia duplikatu.
- [ ] Gdy produkt ma przelicznik 1 szt. = 55 g, wybór 3 sztuk normalizuje ilość do 165 g; przy braku przelicznika system prosi o g/ml.
- [ ] Przy brakujących wartościach produkt pozostaje niekompletny; brak nie staje się zerem. Nieprawidłowa podstawa, ujemne wartości lub niedodatni przelicznik są odrzucane.
- [ ] Własny produkt nie uruchamia publikacji ani udostępnienia. MVP ma lokalną bibliotekę bez pozorowanej izolacji kont.

### E01-S02 — Obliczenie kcal i makro dla ilości

Backlog source: .ai/specs/product-brief.md
Backlog id: E01-S02
Epic: #10
Depends on: E01-S01 (#13)

Decisions in play: D01, R01–R03. Właściciel wszystkich wyborów: właściciel produktu.

- [ ] Gdy produkt ma dane na 100 g, podanie 200 g daje ich dwukrotność; analogicznie dla ml oraz jawnego przelicznika sztuk.
- [ ] Przy tych samych wejściach wynik jest powtarzalny; niezgodne jednostki i niedodatnia ilość są odrzucane bez założenia 1 ml = 1 g.
- [ ] Przy brakującym makroskładniku wynik zachowuje znane wartości i wskazuje niekompletność.

### E01-S03 — Zapamiętany produkt ze skanu kodu

Backlog source: .ai/specs/product-brief.md
Backlog id: E01-S03
Epic: #10
Depends on: E01-S01 (#13)

Decisions in play: D08, R01–R03, N01, N03. Właściciel wszystkich wyborów: właściciel produktu.

- [ ] Gdy zatwierdzony produkt istnieje lokalnie, ponowny skan znajduje go bez ponownego przepisywania danych lub zatwierdzania.
- [ ] Gdy lokalnie nie ma produktu, wynik dostawcy wymaga potwierdzenia korzystającego; zapis zachowuje pochodzenie zgodnie z ADR-0007/0008.
- [ ] Gdy kod nie został znaleziony, użytkownik może przejść do ręcznego uzupełnienia; timeout lub limit dostawcy nie jest przedstawiany jako brak produktu.
- [ ] Skan działa przy dodawaniu produktu do dziennika i do przepisu, bez AI.

### E02-S01 — Przepisy, porcje i własne etykiety

Backlog source: .ai/specs/product-brief.md
Backlog id: E02-S01
Epic: #11
Depends on: E01-S01 (#13), E01-S02 (#14)

Decisions in play: D09–D10, R06, R08. Właściciel wszystkich wyborów: właściciel produktu.

- [ ] W osobnej zakładce Przepisy można zapisać nazwę, składniki z ilościami i obowiązkową dodatnią liczbę porcji, w tym 1. Brak liczby porcji jest odrzucany.
- [ ] Gdy suma składników daje 2000 kcal, przepis na 4 porcje daje 500 kcal na porcję bez wymaganej masy końcowej; makro dzieli się analogicznie.
- [ ] Zmiana samej ilości wody nie zmienia sumy kcal ani wartości równej porcji. Końcowa masa jest opcjonalna.
- [ ] Można zadeklarować własne płaskie etykiety i wybierać je z listy; etykieta nie ogranicza przepisu do danej sekcji posiłku.
- [ ] Zapis nie wymaga zdjęć, publikacji ani akceptacji administratora.

### E02-S02 — Własne sekcje dnia

Backlog source: .ai/specs/product-brief.md
Backlog id: E02-S02
Epic: #11
Depends on: none

Decisions in play: D10, R08, N01. Właściciel wszystkich wyborów: właściciel produktu.

- [ ] Początkowy zestaw zawiera śniadanie, drugie śniadanie, obiad, lunch, kolację i przekąski.
- [ ] Korzystający może tworzyć własne nazwy i liczbę sekcji; lista nie jest zamkniętym enumem produktu.
- [ ] Ten sam przepis można dodać do dowolnej sekcji niezależnie od jego etykiet.

### E02-S03 — Porcja przepisu i bilans dnia lub tygodnia

Backlog source: .ai/specs/product-brief.md
Backlog id: E02-S03
Epic: #11
Depends on: E02-S01 (#15), E02-S02 (#16)

Decisions in play: D09–D10, R02, R06–R07. Właściciel wszystkich wyborów: właściciel produktu.

- [ ] Dziennik otwiera wybrany dzień i umożliwia przejście do widoku tygodnia.
- [ ] Dodaj zapisuje domyślnie jedną porcję; wybranie 0,5 lub 2 porcji skaluje kcal i makro.
- [ ] Przepis jest jednym wpisem z nazwą, kcal i makro, bez osobnych wpisów składników. Można też dodać pojedynczy produkt.
- [ ] Późniejsza zmiana przepisu lub produktu nie zmienia zapisanej historii. Sumy dnia wynikają z zapisanych wpisów.
- [ ] Niekompletne wpisy powodują jawnie częściowy bilans, bez podstawiania zer.

### E02-S04 — Ilości składników przy dodawaniu przepisu

Backlog source: .ai/specs/product-brief.md
Backlog id: E02-S04
Epic: #11
Depends on: E02-S01 (#15), E02-S03 (#17)

Decisions in play: D09, R06–R07. Właściciel wszystkich wyborów: właściciel produktu.

- [ ] Autor przepisu może przy składniku zaznaczyć Zapytaj o ilość. Przy dodawaniu aplikacja pyta o tę ilość na jedną porcję.
- [ ] Przy parametrze 3 jajka na porcję i wyborze 2 porcji obliczenie obejmuje 6 jajek; pozostałe składniki korzystają z ilości na porcję.
- [ ] Przed zapisem można zmienić ilość, zamienić produkt, dodać lub usunąć składnik; wariant jest nadal jednym wpisem przepisu.
- [ ] Zmiany tego dodania nie nadpisują szablonu. Osobne akcje aktualizują przepis lub zapisują nowy, zachowując wcześniejszą historię.

### E02-S05 — Własna baza jako składnik przepisu

Backlog source: .ai/specs/product-brief.md
Backlog id: E02-S05
Epic: #11
Depends on: E02-S01 (#15), E01-S02 (#14)

Decisions in play: D11, R02–R03, R06. Właściciel wszystkich wyborów: właściciel produktu.

- [ ] Zapisany bulion można wskazać jako składnik zupy bez ponownego przepisywania jego składników.
- [ ] Wybrana ilość bazy wnosi do przepisu jej obliczone kcal i makro zgodnie z jawną jednostką i przelicznikiem.
- [ ] Brak przelicznika nie powoduje zgadywania ml/g; sposób podania ilości jest czytelny.
- [ ] Późniejsza edycja bazy nie zmienia wcześniejszych wpisów dziennika. Specyfikacja techniczna definiuje wersjonowanie i zapobieganie cyklom przed implementacją.

### E02-S06 — Zapis pozycji dziennika jako przepisu

Backlog source: .ai/specs/product-brief.md
Backlog id: E02-S06
Epic: #11
Depends on: E02-S01 (#15), E02-S03 (#17)

Decisions in play: D10, R06–R07. Właściciel wszystkich wyborów: właściciel produktu.

- [ ] Po zaznaczeniu produktów dodanych do posiłku można wybrać Zapisz jako przepis, podać nazwę, obowiązkową liczbę porcji i opcjonalne etykiety.
- [ ] Nowy przepis trafia do zakładki Przepisy i jest dostępny przy kolejnym dodawaniu.
- [ ] Zapis szablonu nie dodaje ponownie spożycia i nie zmienia bilansu istniejącego dnia.

## Held back — kalkulator celu kalorii

D12 jest zatwierdzonym zakresem MVP, ale Q02 nadal blokuje wdrażalną story: wzór,
dane wejściowe i model aktywności wymagają rozstrzygnięcia przez właściciela na podstawie
przygotowanej propozycji. Nie tworzymy pozornie gotowego issue implementacyjnego.
Ręczna korekta celu jest wymagana. Utrzymanie i rekompozycja to kierunki, nie ustalone algorytmy.
Po zamknięciu Q02 ponownie uruchomić om-backlog dla odpowiedniego zakresu.

## Co nastąpi po zatwierdzeniu

Zaktualizować istniejące #10–#17, utworzyć E01-S03 i E02-S04–S06 przez om-prepare-issue,
odświeżyć checklisty epików oraz `.ai/specs/backlog.md`. Zachować numery i pełne identyfikatory.
Przed każdym zapisem ponownie sprawdzić źródło, ID i claim. Do tego czasu tracker i mapa
pozostają wcześniejszym stanem; powyższe kryteria są propozycją, nie opublikowanym backlogiem.
