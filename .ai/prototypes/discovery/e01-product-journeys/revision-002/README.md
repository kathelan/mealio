# E01 — dziennik jako ekran startowy, wybór i korekta produktu

- Brief bazowy: `.ai/specs/product-brief.md`; przepływy: `.ai/specs/2026-10-20-personal-mvp.md`.
- Wybrany przepływ: dziennik dnia → dodanie produktu do wybranej sekcji przez wyszukiwanie lub skan → powrót z aktualną sumą kcal.
- Rewizja: 2026-10-20, `revision-002`; poprzednia: [revision-001](../revision-001/README.md).
- Panel synthetic-users: brak raportu; makieta nie zawiera obserwacji panelu.

## Co się zmieniło względem revision-001

Ekran początkowy przedstawia dziennik dnia zamiast samego wejścia do katalogu. Zawiera sześć domyślnych sekcji, fikcyjne wpisy oraz podsumowanie dnia z kaloriami i sumą białka, tłuszczów oraz węglowodanów. Pod każdym posiłkiem jest teraz osobny, wyróżniony panel „Suma całego posiłku” z kcal, białkiem, tłuszczami i węglowodanami; podsumowanie dnia pokazuje te same cztery wartości dla całego dnia. Dodanie produktu z sekcji zachowuje wybrany posiłek i aktualizuje dzienną sumę w pamięci makiety. Dodano ekran personalizacji nazw/liczby sekcji. Ręczne dodanie produktu przekazuje teraz wpisane wartości odżywcze do karty produktu, pozycji w wybranym posiłku, subtotalu posiłku i sum dnia. Każda pozycja dziennika pokazuje własne kcal i makra, a osobny panel sumuje cały posiłek. Rozróżnianie produktów, skan, obsługa błędów dostawcy, konflikt GTIN i korekta pozostają dostępne.

## Uczestnik, granice i reguły

Uczestnik to osoba zapisująca zjedzone produkty. Dziennik jest tu kontekstową powierzchnią startową, a katalog/skaner sposobem dodania produktu do wybranej sekcji.

Właściciel produktu doprecyzował w rozmowie 2026-10-20, że ekran startowy aplikacji powinien być dziennikiem z sekcjami posiłków, a na dole ma pokazywać sumę kalorii zjedzonych danego dnia. To kierunek dla późniejszego UI dziennika; nie rozszerza zakresu implementacyjnego E01.

- **Dziennik E02** — `.ai/specs/2026-10-20-personal-mvp.md` i backlog: dowolna liczba oraz nazwy sekcji, domyślnie śniadanie, drugie śniadanie, obiad, lunch, kolacja i przekąski; posiłki zawierają produkty/przepisy; bilans pochodzi z API.
- **#17/#27** — `.ai/specs/backlog.md`: bilans dnia oraz SwiftUI dziennika z własnymi sekcjami. To miejsce implementacji właściwego dziennika, a nie #24/#25.
- **E01-C1–C4** — [decyzje korekt produktu](../../../../specs/research/decisions/2026-10-20-e01-product-corrections.md): ręczny wpis po awarii/limicie, zachowanie semantyki GTIN, niezmienne wersje i pochodzenie per pole, nazwy sklepów, skan lokalny bez popupu i makieta low-fi przed implementacją iOS.
- SDD E01: [mapa](../../../../specs/2026-10-20-e01-reusable-foundations.md), [katalog](../../../../specs/2026-10-20-e01-product-catalog.md), [skaner](../../../../specs/2026-10-20-e01-barcode-resolution.md), [iOS](../../../../specs/2026-10-20-e01-ios-product-foundations.md). SDD #24 nie implementuje dziennika; #27 należy do późniejszego E02.
- Nie przekazano raportu panelu, więc nie ma hipotez `[SYNTHETIC]`.

## Zakres symulacji

Włączone: dzień z sześcioma domyślnymi sekcjami, przykładowe wpisy i suma kcal; dodawanie, skanowanie i personalizowanie sekcji; cały poprzedni przepływ katalogu/skanera/korekty. Wartości żywieniowe i nazwy są fikcyjne.

Poza zakresem: prawdziwe zapisywanie posiłków, tygodniowy dziennik, backendowe sumy, cele kaloryczne, kamera, zewnętrzne katalogi i finalny styl. Wpisane ręcznie wartości makr są w tej makiecie używane w symulacji dla produktu i wybranego posiłku. Dane katalogowe pozostałych fikcyjnych produktów także są przykładowe; właściwe źródło i backendowe obliczenia dziennika realizuje późniejszy E02 (#17/#27). Ładowanie nie występuje, ponieważ lokalna symulacja nie wykonuje asynchronicznych odczytów.

## Założenia i ograniczenia

- **[ASSUMPTION] A1** — dzienny widok nie zawiera na tym etapie celu kcal ani wartości „pozostało”; właściciel poprosił o sumę spożycia. Można to sprawdzić przy specyfikacji #27.
- **[ASSUMPTION] A2** — ekran potwierdzenia wyboru zapisuje fikcyjną porcję 100 g, aby zademonstrować zmianę kalorii. Właściwe ilości i obliczenia dostarcza późniejsza integracja z #14/#17.
- **[ASSUMPTION] A3** — przykładowy dzienny widok zaczyna się z trzema wpisami (895 kcal); nie reprezentuje rzeczywistych danych ani nowego stanu konta.
- **[ASSUMPTION] A4** — usunięcie pustej sekcji jest dozwolone; sekcji z wpisami makieta nie pozwala usunąć, aby nie wymyślać zachowania przenoszącego wpisy. Regułę należy ustalić w SDD #27.
- **[ASSUMPTION] A5** — wybrany produkt jest dodawany jako fikcyjna porcja 100 g; wartości ręcznie wpisane na 100 g stają się wartościami tej pozycji. Dla pozycji bez danych makr makieta pokazuje „—”, nigdy domyślne zero; suma makra pozostaje nieznana, gdy choć jeden wpis nie ma tej wartości.

## Mapa ekranów i zachowań

| Ekran/stan | Akcja i następny stan | Reguły |
|---|---|---|
| `home` — dziennik | Pokaż sekcje i dzienną sumę; wybierz „Dodaj produkt”/skan w sekcji | sekcje z MVP, #17/#27 |
| `manage` | Zmień nazwę, dodaj nową lub usuń pustą sekcję; zapis wraca do dziennika | własne sekcje, A4 |
| `search` | Wybór produktu lub rozpoczęcie dodania własnego zachowuje wybraną sekcję | E01-C3, A2/A5 |
| `create` / `detail` | Wymagaj nazwy oraz kcal i trzech makr na 100 g; pokaż podgląd produktu, a „Dodaj do posiłku” zapisuje go w wybranej sekcji | symulacja E01, A2/A5 |
| `scan` lokalny | Produkt lokalny wraca bez pytania; osobna akcja rozpoczyna korektę | E01-C2 |
| `scan` kandydat | Potwierdzenie przypisuje do wybranej sekcji | E01-C1, A2/A5 |
| `scan` NOT_FOUND | Ręczny formularz → `create` | E01-C1 |
| `scan` niedostępny/limit | Stan błędu zachowany; możliwy wpis ręczny | E01-C1 |
| `scan` odmowa aparatu | Szukaj lub dodaj ręcznie | E01 |
| `scan` konflikt GTIN | Użyj istniejącego / popraw / anuluj | E01-C2 |
| `correct` | Zapis tworzy symulowaną wersję 2, historia wersji 1 zostaje | E01-C2 |
| `done` | Potwierdź i wróć do dziennika z aktualną sumą | A2/A5 |
| pusty/nieznany ekran | Pokaż odzyskanie do dziennika; reset przywraca fikcyjny stan startowy | kontrola prototypu |

## Odtwarzalna kontrola i dowody

Otwórz lokalnie `index.html` w przeglądarce. „Reset makiety” przywraca sześć domyślnych sekcji, trzy przykładowe wpisy, 895 kcal oraz 28,4 g białka, 24,2 g tłuszczów i 133 g węglowodanów. Żadne dane nie opuszczają karty ani nie są trwale zapisywane.

Sprawdzenie w odizolowanej sesji `agent-browser` na `file://`:

- Dziennik na starcie: sześć sekcji, trzy fikcyjne wpisy, 895 kcal oraz 28,4 g białka, 24,2 g tłuszczów i 133 g węglowodanów; `evidence/diary-home.png`. Każdy niepusty posiłek ma odrębny panel „Suma całego posiłku” z czterema wartościami; zrzut `evidence/diary-home.png` potwierdza czytelność panelu przy śniadaniu, drugim śniadaniu i obiedzie.
- Dodanie ziemniaków z sekcji Obiad zachowuje kontekst sekcji, wraca do dziennika i aktualizuje sumy: 895 → 1047 kcal, białko 28,4 → 30,8 g, tłuszcze 24,2 → 29,3 g, węglowodany 133 → 156 g; `evidence/diary-after-add.png`.
- Ręczne dodanie „Moje musli” z 410 kcal, 12,5 g białka, 8 g tłuszczów i 64 g węglowodanów: formularz → karta produktu → dodanie do Śniadania. Wpis pokazuje indywidualne wartości, subtotal śniadania zmienia się na 730 kcal / 22,5 g / 16 g / 114 g, a sumy dnia na 1305 kcal / 40,9 g / 32,2 g / 197 g; `evidence/custom-product-in-breakfast.png`.
- Personalizacja: zmiana „Lunch” na „Późny posiłek” i dodanie „Koktajle” zachowują edycje i odświeżają sekcje; `evidence/customize-sections.png`.
- Poprzednie stany wyszukiwarki, konflikt GTIN, korekty i awarii są odtworzone w revision-001. Ta rewizja pozostaje źródłem dowodów dla zachowań niezmienionych.
- Reset, historia wstecz/dalej, nieznany hash i dostęp klawiaturą pozostają w tej rewizji; widok dziennika przy szerokości 390 px; `evidence/diary-mobile.png`.

Makieta sprawdza wyłącznie lokalną symulację. Dziennik, jego API, bilans i klient iOS pozostają poza E01; przed implementacją #27 należy zweryfikować reguły dla ilości, bilansu częściowego i usuwania sekcji z wpisami.
