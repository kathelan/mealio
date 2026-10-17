# Mealio iOS — koncepcja portalu / aplikacji

## Założenia i granice

„Portal” rozumiemy jako doświadczenie klienta iOS. Prototyp to SwiftUI, lokalny stan demonstracyjny i przykładowe dane. Nie implementuje serwera, logowania ani trwałej synchronizacji. Tożsamość i ekran profilu nie sugerują, że MVP ma konta. Fitatu jest benchmarkiem przepływów dziennika i dodawania posiłku; nazwa, copy, layout i paleta Mealio są autorskie.

Produkt ma pomagać szybko zapisać jedzenie, rozumieć skąd pochodzą liczby oraz poprawić niepewną propozycję zanim trafi do dziennika. Brak wartości nie oznacza zera, a dane katalogowe nie są poradą medyczną.

## Użytkownicy i potrzeby

| Osoba | Potrzeba |
|---|---|
| Osoba prowadząca dziennik | Dodać jedzenie w kilka sekund, zobaczyć sumę dnia i makroskładniki, skorygować ilość lub posiłek. |
| Osoba skanująca opakowanie | Odczytać kod, poznać źródło i stan weryfikacji, potwierdzić lub poprawić dane. |
| Osoba jedząca danie domowe | Opisać posiłek zwykłym językiem, przejrzeć produkty i porcje, usunąć błędne sugestie. |
| Osoba bez kodu / brak wyniku | Wpisać nazwę i wartości z etykiety albo użyć produktu generycznego; nie blokować dziennika. |
| Osoba kontrolująca kompletność | Zauważyć brakujące dane i częściowy wynik, bez prezentowania go jako pełnego. |

MVP nie ma użytkowników systemowych, logowania ani wielu profili. Ustawienia w makiecie są poglądowe.

## Kluczowe use case'y i kompletne ścieżki

### UC1 — przegląd dnia i nawigacja dat
1. Start w Dzienniku na bieżącym dniu.
2. Użytkownik wybiera datę w selektorze.
3. Widzi energię, paski makroskładników i sekcje posiłków.
4. Każda sekcja prezentuje wpisy lub prosty empty state i akcję dodania.

### UC2 — szukanie i dodanie produktu
1. Dziennik → „Dodaj produkt” albo zakładka Produkty.
2. Wpisanie nazwy/kodu filtruje przykładowy katalog; wybór zapisuje wpis z domyślną porcją 100 g i sekcją Obiad.
3. W wersji docelowej ekran potwierdzenia powinien pozwolić wskazać sekcję, ilość i jednostkę przed zapisem.
4. Powrót do dziennika aktualizuje sumę i pozycję.

### UC3 — produkt ręczny
1. Wyszukiwarka → „Utwórz produkt ręcznie”.
2. Wpisanie nazwy, podstawy na 100 g/ml, ilości i znanych pól odżywczych.
3. Walidacja nazwy i liczb; puste makra pozostają brakujące, nie zero.
4. Zapis dodaje lokalny wpis. W docelowym UI produktu niekompletne powinny dostać widoczny znacznik i wyjaśnienie częściowego podsumowania.

### UC4 — kod kreskowy
1. Dziennik → „Skanuj kod”.
2. Kamera (w makiecie przycisk symuluje skan) → wyszukanie kodu.
3. Wynik lokalny może przejść dalej; kandydat zewnętrzny pokazuje źródło i prosi o potwierdzenie.
4. Użytkownik sprawdza nazwę, podstawę, makra i porcję; może odrzucić wynik.
5. Brak produktu prowadzi do ręcznego dodania z zachowaniem zeskanowanego kodu.
6. Po potwierdzeniu wpis trafia do dziennika.

### UC5 — opis tekstowy posiłku
1. Dziennik → „Opisz posiłek”.
2. Wpisanie naturalnego opisu, np. „2 jajka i 2 kromki chleba”.
3. Parser zwraca edytowalne propozycje produktów, ilości, niepewność i źródła danych.
4. Użytkownik zaznacza właściwe elementy, poprawia ilości/produkty i jawnie potwierdza.
5. Wybrane wpisy dodawane są atomowo; suma dnia odświeża się.

### UC6 — edycja i usuwanie wpisu (wymaganie docelowe)
1. Dotknięcie wpisu otwiera edycję ilości/jednostki/sekcji.
2. Zapis aktualizuje tylko wybrany wpis i wylicza dzień od nowa na snapshotach danych.
3. Usunięcie wymaga potwierdzenia i ma możliwość cofnięcia przez krótki snackbar.
4. Makieta wymaga dopracowania tej ścieżki przed wdrożeniem produkcyjnym.

## Mapa ekranów i nawigacji

```text
Tab Dziennik
 ├─ selektor dnia
 ├─ podsumowanie kalorii i makro
 ├─ sekcje posiłków → wpis → edycja / usuń (docelowo)
 ├─ Dodaj produkt → wyszukiwarka → potwierdź ilość → dziennik
 ├─ Skanuj kod → wynik/kandydat → potwierdź ─┐
 │                         └→ brak → formularz etykiety ─┤
 └─ Opisz posiłek → propozycje → popraw/wybierz → potwierdź ┘
Tab Produkty → wyszukiwarka → wybór / utwórz ręcznie
Tab Więcej → preferencje poglądowe / informacje
```

## Specyfikacja ekranów

Stany w poniższej tabeli są stanami projektowymi klienta online. W działającym SwiftUI demo dane statyczne, błędy sieci i ładowanie są symulowane/niepodłączone.

| Ekran (cel) | Funkcje i UI | Loading / empty / error / success | Walidacje | API klienta (proponowane, względem `/api/v1`) |
|---|---|---|---|---|
| Dziennik dnia — przegląd bilansu | selektor daty, karta kcal i makro, sekcje śniadanie…inne, plusy, szybkie akcje | loading skeleton; empty dzień z CTA; error z ponów i zachowaniem daty; success bilans wraz z `COMPLETE/PARTIAL` | data ISO, brakujące pola pokazać jako nieznane, sumy nie mogą udawać pełnych | `GET /diary/{date}`; `POST /diary/{date}/entries`; `PATCH /diary/entries/{id}`; `DELETE /diary/entries/{id}` |
| Wyszukiwanie katalogu — wybór produktu | pole nazwa/kod, wyniki z marką, kcal/100g, status/source, CTA ręczny | loading; empty/no match z ręcznym dodaniem; error offline i retry; success wybrany produkt | trim zapytania; nie odpalać wyszukiwania dla pustego tekstu; GTIN tekstem i kontrola cyfry | `GET /products?query=…`; `GET /products/barcode/{gtin}` |
| Potwierdzenie/ilość — świadomy wpis | karta danych i źródła, edytowalna ilość, sekcja dnia, podgląd wyliczenia, potwierdź | loading zapis; empty brak danych z możliwością odrzucenia; error walidacji/API przy formularzu; success wpis w dzienniku | ilość > 0, jednostka zgodna z podstawą g/ml, nie ufać kandydatowi zewnętrznemu bez potwierdzenia | `POST /diary/{date}/entries`; ewentualne `POST /products/candidates/{id}/confirm` — kontrakt do ustalenia |
| Skaner — szybki odczyt | podgląd kamery, ramka, latarka, wpis kodu ręcznie, status lookup | loading podczas lookup; empty NOT_FOUND → etykieta; error provider/offline rozróżniony od not found; success przejście do potwierdzenia | GTIN obsługiwanej długości i cyfra kontrolna; rate limit/offline nie oznacza braku | `GET /products/barcode/{gtin}` |
| Ręczne dodanie produktu — uzupełnienie etykiety | nazwa, kod opcjonalny, 100 g/ml, energia i makra, pochodzenie „etykieta” | loading zapisu; empty formularz; error inline per pole; success potwierdzenie produktu i wpisu | nazwa wymagana, GTIN poprawny; liczby nieujemne, ilość >0; brak pola pozostaje null; wymagane pełne bazowe dane do statusu confirmed | `POST /products`; następnie `POST /diary/{date}/entries` |
| Opis posiłku — wejście tekstowe | TextEditor, przykład, CTA „Pokaż propozycje” | loading parsera; empty opis z przykładem; error parsowania z zachowaniem tekstu i retry; success lista kandydatów | tekst po trim niepusty i limit długości; żadnego auto-zapisu | planowane `POST /meal-parses` (kontrakt do zaprojektowania) |
| Propozycje tekstowe — zatwierdzenie | checkbox, nazwa, ilość i jednostka edytowalne, confidence/source, częściowe makro | loading; empty brak dopasowań z ręcznym wyszukaniem; error per pozycja, pozostawiający wybory; success atomowy zapis | przynajmniej 1 wybrana pozycja; poprawne dodatnie ilości; wymagane jawne potwierdzenie | `POST /meal-parses/{id}/confirm` (kontrakt do zaprojektowania; atomowy zapis wpisów) |
| Więcej — preferencje/produkt | jednostki, język, wersja i informacja o danych lokalnych | loading ustawień; empty brak preferencji; error ustawień; success zapis | enumy jednostek/języka | brak dla lokalnych ustawień; auth poza MVP |

### Stany jakości danych

- Produkt zewnętrzny zawsze pokazuje źródło i wymaga pierwszego potwierdzenia.
- `INCOMPLETE` ma etykietę „Niepełne dane”; brak wartości nie jest renderowany jako 0 g.
- Sumy częściowe informują o liczbie niekompletnych wpisów oraz brakujących polach.
- Błąd dostawcy, limit, brak sieci i `NOT_FOUND` muszą mieć różne komunikaty.

## Zakres klikalnego prototypu SwiftUI

`MealioPrototype.swift` jest pojedynczym plikiem, który można dodać do nowego projektu iOS w Xcode (SwiftUI, iOS 17+). Zawiera taby Dziennik/Produkty/Więcej, selektor daty, podsumowanie makro, katalog z wyszukiwaniem, formularz ręczny, symulowany skaner oraz opis tekstowy z zatwierdzaniem selekcji. Dane są przykładowe, przechowywane w pamięci; wymaga późniejszego podłączenia kontraktów API i rzeczywistej kamery.

## Benchmark funkcjonalny i odrębność

Zachowujemy sprawdzone wzorce kategorii produktu: dzienny dziennik, czytelny bilans, szybkie dodanie z wyszukiwarki/skanera oraz przejrzenie propozycji. Mealio odróżnia jawne pochodzenie danych, status kompletności, potwierdzenie kandydata, przepływ tekstowy i spokojny, nieoceniający ton. Nie wykorzystujemy assetów, tekstów, kodu ani charakterystycznej identyfikacji Fitatu.
