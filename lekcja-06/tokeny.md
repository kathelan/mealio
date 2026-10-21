# Tokeny: Mealio

Data: 2026-10-21. Status: zatwierdzone przez właściciela w rozmowie, łącznie
z jasnym i ciemnym wariantem. Źródło decyzji: [referencje](referencje.md).
Dokument nie wdraża stylów w prototypach ani w kliencie iOS.

## Baza i konwencje

Nie znaleziono osobnego pliku tokenów ani paczki UI. Bazą kolorów jest blok
`:root` w [revision-003](../.ai/prototypes/discovery/e01-product-journeys/revision-003/index.html).
Systemowa typografia i przypisanie kolorów makr pochodzą z
[prototypu SwiftUI](../ios-prototype%203/MealioPrototype.swift).
Zachowano przypisanie: białko — pomarańcz, węglowodany — niebieski,
tłuszcz — fiolet. Antyreferencja gradientu AI nie zakazuje fioletu jako koloru danych.

Dotychczasowe `--paper`, `--ink`, `--muted`, `--soft`, `--green` i `--red`
mapują się odpowiednio na `surface-page`, `text-primary`, `text-muted`,
`surface-selected`, `action-primary` i `status-error`. `--muted` wymaga korekty
kontrastu. Nazwy opisujące wygląd, np. `--green`, są wejściem do warstwy surowej,
nie publicznymi nazwami dla widoków. Nie dublujemy starego i nowego API tokenów.

Przed zatwierdzeniem nie było spójnego ciemnego motywu: SwiftUI miesza kolory
systemowe z białymi powierzchniami. Poniższe pary definiują zatwierdzony kierunek
dla obu motywów, a nie opis istniejącej implementacji.

`p.*` oznacza wartości surowe używane tylko wewnątrz definicji tokenów.
Kod widoków używa nazw znaczeniowych lub tokenów komponentu.
Wartości rozdzielone ukośnikiem oznaczają **jasny / ciemny** motyw.
Wymiary podano w **punktach iOS**, nie pikselach. Konkretne wartości były
propozycją agenta i zostały zatwierdzone; numery referencji wskazują pochodzenie
ich przeznaczenia, nie pomiar wartości ze zdjęć.

## Tabela tokenów

| Token | Warstwa | Wartość | Pochodzenie | Gdzie używać | Gdzie nie używać |
|---|---|---|---|---|---|
| `p.neutral.page` | Surowa | `#FBFCF9` / `#171C18` | Obecne `--paper`; ciemny: zatwierdzona propozycja | Definicje tokenów | Bezpośrednio w widokach |
| `p.neutral.panel` | Surowa | `#FFFFFF` / `#232B25` | Białe powierzchnie prototypu; ciemny: zatwierdzona propozycja | Definicje tokenów | Bezpośrednio w widokach |
| `p.neutral.tint` | Surowa | `#EDF5EE` / `#293A2D` | Obecne `--soft`; ciemny: zatwierdzona propozycja | Definicje tokenów | Bezpośrednio w widokach |
| `p.neutral.ink` | Surowa | `#20251F` / `#F1F5EF` | Obecne `--ink`; ciemny: zatwierdzona propozycja | Definicje tokenów | Bezpośrednio w widokach |
| `p.neutral.muted` | Surowa | `#626B60` / `#B1BBAE` | Korekta kontrastu obecnego `--muted` | Definicje tokenów | Bezpośrednio w widokach |
| `p.neutral.stroke` | Surowa | `#626B60` / `#82917F` | Zatwierdzona propozycja dostępnej granicy kontrolki | Definicje tokenów | Bezpośrednio w widokach |
| `p.green` | Surowa | `#2C6C4C` / `#8FCCA3` | Obecne `--green`; ciemny: zatwierdzona propozycja | Definicje tokenów | Bezpośrednio w widokach |
| `p.amber` | Surowa | `#875A1F` / `#E5BC76` | Kolor obecny w HTML; rodzina `.orange` w SwiftUI | Definicje tokenów | Bezpośrednio w widokach |
| `p.blue` | Surowa | `#245DA8` / `#8FB8F0` | Zatwierdzone doprecyzowanie `.blue` z SwiftUI | Definicje tokenów | Bezpośrednio w widokach |
| `p.purple` | Surowa | `#7545A0` / `#C4ADF0` | Zatwierdzone doprecyzowanie `.purple` z SwiftUI | Definicje tokenów | Bezpośrednio w widokach |
| `p.red` | Surowa | `#9B4337` / `#F2A79A` | Obecne `--red`; ciemny: zatwierdzona propozycja | Definicje tokenów | Bezpośrednio w widokach |
| `p.space` | Surowa skala | `0, 4, 8, 12, 16, 24, 32 pt` | R01.5, R01.12; zatwierdzony wybór skali | Definicje odstępów | Dowolne wartości między stopniami |
| `p.radius` | Surowa skala | `0, 12 pt` | Zatwierdzone uproszczenie promieni prototypów | Definicje promieni | Automatyczne zaokrąglanie każdego kontenera |
| `p.stroke-width` | Surowa | `1 pt` | Zatwierdzona propozycja | Definicje obramowań | Jako jedyne oznaczenie stanu |
| `p.touch-min` | Surowa | `44 pt` | Zatwierdzona propozycja obsługi dotykiem iOS | Definicje obszarów dotyku | Wymuszanie wysokości tekstu |
| `p.type` | Surowe receptury | Systemowe `.title2`, `.headline`, `.body`, `.subheadline`, `.footnote`; warianty `.bold()` i `.monospacedDigit()` według aliasów poniżej | Style istniejące w SwiftUI i zatwierdzone receptury | Definicje typografii | Sztywne rozmiary niezależne od Dynamic Type |
| `surface-page` | Znaczeniowa | → `p.neutral.page` | R01.1, R01.7; obecne `--paper` | Tło ekranu | Pod tekst na zdjęciu |
| `surface-panel` | Znaczeniowa | → `p.neutral.panel` | R01.5, R01.12; obecne białe powierzchnie | Formularz, wydzielony panel, arkusz | Osobna karta wokół każdego wiersza |
| `surface-selected` | Znaczeniowa | → `p.neutral.tint` | R01.7, R01.12; obecne `--soft` | Zaznaczony wiersz lub filtr | Duże dekoracyjne połacie koloru |
| `text-primary` | Znaczeniowa | → `p.neutral.ink` | R01.5; obecne `--ink` | Nazwy, ilości, wartości, nagłówki | Na kolorowym przycisku |
| `text-muted` | Znaczeniowa | → `p.neutral.muted` | R01.5; poprawione `--muted` | Jednostki, marka, dodatkowy opis | Ukrywanie ważnego błędu lub brakujących danych |
| `action-primary` | Znaczeniowa | → `p.green` | R01.3; mapowanie obecnego `--green` | Główna akcja, aktywna zakładka, zewnętrzny fokus | Wszystkie nagłówki i makra |
| `text-on-action` | Znaczeniowa | Jasny → `p.neutral.panel` (`#FFFFFF`); ciemny → `p.neutral.page` | R01.3 + wynik kontrastu | Tekst i ikona na `action-primary` | Dowolne inne kolorowe tło |
| `border-control` | Znaczeniowa | → `p.neutral.stroke` | R01.12 + dostępność; zatwierdzona propozycja | Granica własnej kontrolki, gdy potrzebna do jej rozpoznania | Obramowanie wszystkich sekcji |
| `nutrition-energy` | Znaczeniowa | → `p.green` | R01.7; zielony bilans w prototypie | Oznaczenie energii | Zastępowanie etykiety „kcal” samym kolorem |
| `nutrition-protein` | Znaczeniowa | → `p.amber` | R01.7; `.orange` w SwiftUI | Oznaczenie białka | Kolor ostrzeżeń bez osobnej semantyki |
| `nutrition-carbs` | Znaczeniowa | → `p.blue` | R01.7; `.blue` w SwiftUI | Oznaczenie węglowodanów | Pozostałe makra |
| `nutrition-fat` | Znaczeniowa | → `p.purple` | R01.7; `.purple` w SwiftUI | Oznaczenie tłuszczu | Gradienty i dekoracyjne oznaczenia AI |
| `status-error` | Znaczeniowa | → `p.red` | Obecne `--red`; zatwierdzone uzupełnienie techniczne | Błąd z opisem tekstowym | Zwykłe przekroczenie wartości bez ustalonej reguły |
| `meal-accent-01` | Znaczeniowa | → `p.green` | R01.2, R01.7 | Znacznik wybranej sekcji posiłku | Trwałe znaczenie „śniadanie” |
| `meal-accent-02` | Znaczeniowa | → `p.amber` | R01.2, R01.7 | Alternatywny znacznik sekcji | Stan błędu |
| `meal-accent-03` | Znaczeniowa | → `p.blue` | R01.2, R01.7 | Alternatywny znacznik sekcji | Kodowanie makr wewnątrz posiłku |
| `meal-accent-04` | Znaczeniowa | → `p.purple` | R01.2, R01.7 | Alternatywny znacznik sekcji | Automatyczne kolorowanie całej listy produktów |
| `space-inline` | Znaczeniowa | → `4 pt` ze skali `p.space` | R01.5; zatwierdzona propozycja | Między ściśle związanymi elementami | Między osobnymi grupami |
| `space-in-group` | Znaczeniowa | → `8 pt` ze skali `p.space` | R01.5, R01.12; zatwierdzona propozycja | Między nazwą, ilością i opisem | Między posiłkami |
| `space-between-groups` | Znaczeniowa | → `16 pt` ze skali `p.space` | R01.5; zatwierdzona propozycja | Między grupami danych lub pól | Między etykietą i jej wartością |
| `space-panel-inset` | Znaczeniowa | → `12 pt` ze skali `p.space` | R01.12; zwarty iOS — zatwierdzona propozycja | Wewnątrz własnego panelu | Margines całego ekranu |
| `space-screen-inset` | Znaczeniowa | → `16 pt` ze skali `p.space` | Istniejący prototyp SwiftUI; R01.1 | Własny poziomy margines treści | Dodatkowo na natywną listę mającą już margines |
| `space-section` | Znaczeniowa | → `24 pt` ze skali `p.space` | R01.1, R01.5; zatwierdzona propozycja | Między posiłkami i bilansem | Wewnątrz wiersza produktu |
| `radius-control` | Znaczeniowa | → `12 pt` ze skali `p.radius` | Uproszczenie prototypu; zatwierdzona propozycja | Własne pola i prostokątne przyciski | Nadpisywanie kształtu natywnej kontrolki |
| `type-screen-title` | Znaczeniowa | → receptura `p.type`: `.title2.bold()` | Kierunek natywnego iOS; zatwierdzona propozycja | Niewielki tytuł własnego ekranu | Duży tytuł powtarzany w każdej sekcji |
| `type-section-title` | Znaczeniowa | → receptura `p.type`: `.headline` | R01.2, R01.5; SwiftUI | Nazwa posiłku | Każda nazwa produktu |
| `type-entry` | Znaczeniowa | → receptura `p.type`: `.subheadline` | R01.5; istniejący `EntryRow` | Wiersze produktów i przepisów | Zmniejszanie wszystkich formularzy |
| `type-form` | Znaczeniowa | → receptura `p.type`: `.body` | R01.12; natywny iOS | Wpisywane dane i podstawowe etykiety formularza | Opisy techniczne |
| `type-supporting` | Znaczeniowa | → receptura `p.type`: `.footnote` | R01.5; zatwierdzona propozycja | Jednostki i dodatkowy opis | Główna wartość bilansu lub jedyna informacja o błędzie |
| `type-metric` | Znaczeniowa | → receptura `p.type`: `.title2.bold().monospacedDigit()` | R01.1; zatwierdzona propozycja | Główna wartość bilansu dnia | Każda liczba w liście |
| `touch-target-min` | Znaczeniowa | → `p.touch-min` | R01.3, R01.6; zatwierdzona propozycja iOS | Minimalny obszar dotyku własnych kontrolek | Sztywny maksymalny wymiar kontrolki |
| `meal-add.hit-area-min` | Komponentu | → `touch-target-min` | R01.3 | Plus przy posiłku | Wymuszanie symbolu plusa wielkości 44 pt |

Warstwa surowa zawiera skale i receptury zbiorcze; docelowy format pliku tokenów
może rozbić je na pojedyncze wartości bez zmiany zatwierdzonego zestawu.
Alias do stopnia skali nie oznacza wpisywania liczby bezpośrednio w widoku.

Nie dodano osobnych tokenów tła, fontu i promienia dla każdego komponentu:
korzystają ze wspólnych znaczeń. Nie wybrano cieni ani czasów animacji, ponieważ
referencje nie ustalają potrzeby ich użycia. Cztery akcenty posiłków są paletą do
wyboru, nie limitem liczby sekcji; mogą się powtarzać, a sekcję identyfikuje nazwa.

## Pary kolorów i kontrast

Obliczenia wykorzystują luminancję względną sRGB według WCAG:
`(L jaśniejsze + 0,05) / (L ciemniejsze + 0,05)`. Wartości poniżej zaokrąglono
do dwóch miejsc; kwalifikację wykonano na pełnej precyzji.
To obliczenia palety, nie pomiary gotowego klienta iOS.

Próg: **4,5:1 dla całego tekstu** oraz **3:1 dla istotnych oznaczeń kontrolek**.
Źródła: [kontrast tekstu — W3C](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum),
[kontrast elementów interfejsu — W3C](https://www.w3.org/WAI/WCAG22/Understanding/non-text-contrast.html).

Każda komórka podaje trzy osobne pary: dany kolor na **`surface-page` /
`surface-panel` / `surface-selected`** tego samego motywu.

- `text-primary`: jasny **15,15 / 15,60 / 14,04**; ciemny **15,67 / 13,19 / 10,96** — PASS.
- `text-muted`: jasny **5,38 / 5,54 / 4,98**; ciemny **8,71 / 7,33 / 6,09** — PASS.
- `action-primary`, `nutrition-energy`, `meal-accent-01`: jasny **6,08 / 6,26 / 5,63**; ciemny **9,32 / 7,85 / 6,52** — PASS.
- `nutrition-protein`, `meal-accent-02`: jasny **5,81 / 5,98 / 5,38**; ciemny **9,70 / 8,16 / 6,78** — PASS.
- `nutrition-carbs`, `meal-accent-03`: jasny **6,36 / 6,55 / 5,89**; ciemny **8,46 / 7,12 / 5,92** — PASS.
- `nutrition-fat`, `meal-accent-04`: jasny **6,55 / 6,74 / 6,07**; ciemny **8,70 / 7,33 / 6,09** — PASS.
- `status-error`: jasny **6,27 / 6,46 / 5,81**; ciemny **8,86 / 7,46 / 6,20** — PASS.
- `border-control` (próg 3:1): jasny **5,38 / 5,54 / 4,98**; ciemny **5,19 / 4,37 / 3,63** — PASS.
- `text-on-action` na `action-primary`: jasny **6,26**; ciemny **9,32** — PASS.

Wszystkie powyższe pary przechodzą właściwy próg. Inne kombinacje nie są
zatwierdzone: w szczególności tekst na zdjęciach, kolorowych znacznikach i
systemowych materiałach wymaga osobnego sprawdzenia. Oznaczenia akcji, makr i
posiłków na powierzchniach są tekstem lub ikoną, nie arbitralnym kolorowym tłem
z tekstem położonym na nim. Nie należy stosować przezroczystości zmieniającej
zatwierdzone pary bez ponownego obliczenia kontrastu.

Korekty istniejącej palety:

- Stare `--muted: #71786F` na `--paper`: **4,42:1 — FAIL**; `#626B60` daje **5,38:1 — PASS**.
- Stare `--muted` na `--soft`: **4,09:1 — FAIL**; `#626B60` daje **4,98:1 — PASS**.
- Stopka `#999F97` na `--paper`: **2,63:1 — FAIL**; `text-muted` daje **5,38:1 — PASS**.
- Linia `#E6E9E3` na białym: **1,23:1 — FAIL**, jeżeli to konieczna granica kontrolki; `border-control` daje **5,54:1 — PASS**. Dekoracyjny separator nie musi pełnić tej roli.

Po każdej zmianie koloru należy ponownie sprawdzić wszystkie dozwolone pary,
także aliasy wskazujące tę samą wartość i oba motywy.

## Decyzje, które nie są tokenami

- **R01.1 — zasada i komponent:** dziennik jest startem, bilans dnia poprzedza listę posiłków.
- **R01.2 — cecha komponentu:** własne nazwy i liczba sekcji; wartości domyślne są konfiguracją produktu, nie tokenami.
- **R01.3 — cecha komponentu:** plus dodaje do wskazanego posiłku i zachowuje kontekst powrotu.
- **R01.4 — przepływ:** wyszukiwanie, skan i wybór zapisanego przepisu prowadzą do wyboru ilości.
- **R01.5 — komponent:** pozycja pokazuje spożytą ilość, kcal i makra; posiłek ma sumę. Układ korzysta z tokenów, ale sam nie jest tokenem.
- **R01.6 — nawigacja:** dolne zakładki Dziennik, Przepisy, Produkty.
- **R01.7 — zasada:** kolor makr ma stałe znaczenie; akcent posiłku służy orientacji. Nazwy i jednostki pozostają widoczne.
- **R01.8 — cecha komponentu:** opcjonalny obraz przepisu. Bez obrazu nie podstawiamy fikcyjnego zdjęcia.
- **R01.9, R01.13 — przepływ:** własne przepisy są dostępne przy dodawaniu; nie trzeba ponownie odtwarzać składników.
- **R01.10 — model organizacji:** klasyfikacja przepisów i sekcje dnia są niezależne. Hierarchii kategorii nie rozstrzygają tokeny.
- **R01.11 — zasada produktu:** brak reklam i bezpłatne funkcje.
- **R01.12 — zasada kompozycji:** ograniczamy konkurujące kontrolki; szczegółowy układ dodawania wymaga projektu.
- **Antyreferencje — zasady:** bez dekoracyjnych gradientów AI, powtarzalnych szklanych kart i iskierek jako głównego motywu.
- **iOS — zasada wykonania:** zwarta lista może zwiększać wysokość przy większym tekście; nie blokujemy Dynamic Type. Obszar dotyku 44 × 44 pt nie oznacza symbolu lub tekstu tej wielkości. [Apple: skalowanie typografii](https://developer.apple.com/documentation/uikit/scaling-fonts-automatically).
- **Zakres:** współdzielenie pozostaje poza MVP.
