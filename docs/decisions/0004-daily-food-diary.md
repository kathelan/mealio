# ADR-0004: Dziennik żywieniowy jako główny model MVP

## Status

Accepted — 2026-10-16; doprecyzowanie 2026-10-20 zgodnie z D08–D10/R06–R08.
Pierwotny model produktu i stałych sekcji rozszerzono o pojedynczy wpis przepisu i własne sekcje. D18/D19/D22 doprecyzowano w [sesji grilling](../../.ai/specs/research/decisions/2026-10-20-diary-and-recipe-details.md).

## Kontekst

Mealio ma umożliwiać rejestrowanie jedzenia w układzie dziennym podobnym funkcjonalnie do
popularnych dzienników kalorii. Jednorazowy kalkulator nie zapewnia historii ani sum dnia.
Model `Meal` jako kontener może niepotrzebnie komplikować dodawanie pojedynczych produktów.

## Decyzja

Podstawowym zapisywanym obiektem jest `FoodEntry`: spożyta ilość produktu albo przepisu,
w określonej dacie i sekcji. Przepis jest jednym wpisem z nazwą, kcal i makrami;
nie rozwijamy go na oddzielne wpisy składników. Ilości i wynik zachowują snapshot
wersji użytej przy zapisie, więc korekta produktu lub przepisu nie przelicza historii. `DailyDiary` jest modelem odczytu grupującym wpisy i obliczającym
sumy dnia; nie wymaga osobnej tabeli.

Domyślne sekcje to śniadanie, drugie śniadanie, obiad, lunch, kolacja i przekąski.
Użytkownik może zmieniać ich nazwy i liczbę, na przykład ograniczyć dzień do trzech własnych
posiłków. Własne sekcje są dostępne w MVP. Wpis można poprawić także w starszym dniu: ilość produktu,
porcje i skład konkretnego spożycia przepisu. Zmiana dotyczy tylko wskazanego wpisu i jego
bilansu, nie szablonu ani innych dni. Przeniesienie między sekcjami/datami zachowuje wartości.
Usunięcie oferuje krótkie Cofnij; szczegóły trwałości i polityki wersji przy świadomej korekcie
starego wpisu (Q05) należą do SDD. Zmiana nazwy sekcji dotyczy także historii. Usunięcie
sekcji zachowuje dawne wpisy pod ostatnią nazwą, ale usuwa ją z wyboru. UI nie ma przywracania;
nowa sekcja o tej samej nazwie ma osobną tożsamość. Kolejność pozostaje propozycją.
Start to widok dnia; można przejść do tygodnia z wykresem dziennych kcal.
Dzień bez wpisów nie jest potwierdzeniem braku spożycia.
Data dziennika jest przechowywana jawnie jako `LocalDate`, dzięki czemu nie zależy od strefy
czasowej serwera.

AI/parser i wejście głosowe są poza obecnym osobistym MVP (D08/N03). Historyczna
możliwość atomowego zatwierdzania propozycji tekstowych pozostaje wyłącznie kierunkiem
do ponownego rozstrzygnięcia przed ewentualnym powrotem parsera.

## Konsekwencje

- prosty model odpowiadający interfejsowi dziennika,
- łatwe sumowanie dnia i sekcji,
- oddzielna tożsamość wpisu i szablonu; świadoma korekta historii jest w MVP i wymaga SDD,
- przepisy pozostają szablonami i nie są utożsamiane ze spożyciem,
- kopiowanie dni pozostaje późniejsze; cel kalorii i aktualizacja masy są w MVP D24, ale algorytm czeka na Q02; nazwy i liczba sekcji są konfigurowalne w MVP.
