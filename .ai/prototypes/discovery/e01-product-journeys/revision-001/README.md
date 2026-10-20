# E01 — wybór, skanowanie i korekta produktu

- Brief: `.ai/specs/product-brief.md`.
- Wybrany przepływ: katalog produktu → skan kodu → potwierdzenie, ręczne dodanie lub korekta.
- Rewizja: 2026-10-20, `revision-001`; poprzednia rewizja: brak.
- Panel synthetic-users: brak raportu; makieta powstała bez wyników panelu.

## Granice i uczestnik

Uczestnik to osoba dodająca produkt do posiłku. Punkt startowy to wybór wyszukiwarki albo skanera; sukcesem jest wybranie istniejącego produktu albo dodanie produktu i zobaczenie go jako pozycji posiłku. Skaner jest symulowany selektorem scenariuszy; nie uruchamia kamery, backendu ani dostawcy danych.

## Zakres

Włączone są: ekran wejściowy, wyszukiwanie, dwa podobnie nazwane fikcyjne produkty odróżnione marką i sklepem, ręczne dodanie, lokalny wynik skanowania, kandydat zewnętrzny, NOT_FOUND, niedostępność i limit dostawcy, odmowa dostępu do kamery, konflikt GTIN z decyzją użyj/popraw/anuluj, widok produktu, korekta jako nowa wersja, dodanie do posiłku, walidacja i reset. Dane liczbowe i nazwy są fikcyjne.

Poza zakresem: rzeczywisty aparat, backend, autoryzacja, trwały zapis, prawdziwy katalog, pobieranie wielu źródeł, edycja posiłków, finalny wygląd i popyt użytkowników.

## Reguły i decyzje

- **E01** — `.ai/specs/2026-10-20-e01-reusable-foundations.md`; decyzja właściciela zaakceptowana: makieta low-fi przed implementacją iOS #24/#25.
- **E01-C1** — `.ai/specs/research/decisions/2026-10-20-e01-product-corrections.md`; właściciel: produkt. Awaria/limit dostawcy nie oznacza NOT_FOUND; ręczny zapis pozostaje dostępny.
- **E01-C2** — ten sam zapis. Konflikt GTIN oferuje użycie istniejącego produktu, korektę albo anulowanie.
- **E01-C3** — ten sam zapis. Korekta tworzy niezmienną wersję, a pochodzenie jest przypisane do pola.
- **E01-C4** — ten sam zapis. Produkt może mieć wiele ręcznie wpisanych nazw sklepów; sklepy nie są marką ani źródłem faktów.
- Katalog i skaner: `.ai/specs/2026-10-20-e01-product-catalog.md`, `.ai/specs/2026-10-20-e01-barcode-resolution.md`; reguły wymagają natychmiastowego zwrotu lokalnego wyniku bez popupu przy każdym skanie.
- Zmiana reguł produktowych wymaga aktualizacji briefu/specyfikacji przez zatwierdzoną ścieżkę decyzji; makieta ich nie zmienia.

Nie ma etykiet `[SYNTHETIC]`, bo nie przekazano raportu panelu.

## Założenia symulacji

- **[ASSUMPTION] A1** — skrót do posiłku po wybraniu produktu prowadzi do przykładowego widoku „Dodano do posiłku”; brak docelowego klienta iOS do sprawdzenia tej nawigacji.
- **[ASSUMPTION] A2** — przykładowy produkt zewnętrzny może zostać potwierdzony bez dodatkowego ekranu szczegółów; do walidacji w projekcie interfejsu przed implementacją.
- **[ASSUMPTION] A3** — przełącznik scenariuszy testowych jest wystarczający do rozmowy o odzyskiwaniu po błędach; w produkcie takie przełączniki nie wystąpią.
- **[ASSUMPTION] A4** — demo ma wartość odżywczą i widok szczegółów pokazujące pochodzenie oraz wersję; liczby nie są prawdziwymi danymi katalogowymi.

## Mapa ekranów

| Ekran/stan | Akcja i następny stan | Reguły |
|---|---|---|
| `home` | Szukaj → `search`; skanuj → `scan` | E01, A1 |
| `search` | Wybierz → `done`; dodaj własny → `create`; zero wyników → komunikat i dodanie | E01-C4 |
| `create` | Pusta nazwa pokazuje błąd; poprawna → `detail` | E01-C3, A4 |
| `scan` lokalny | Pokaż → `detail`; jawna korekta → `correct` | E01-C1, E01-C3 |
| `scan` zewnętrzny | Potwierdź → `done`; popraw → `correct` | E01-C3, A2 |
| `scan` NOT_FOUND | Ręczne dodanie → `create` | E01-C1 |
| `scan` niedostępny/limit | Stan pozostaje odróżniony; ręczne dodanie lub powrót | E01-C1 |
| `scan` odmowa aparatu | Wyszukaj/dodaj ręcznie | E01-C1 |
| `scan` konflikt GTIN | Użyj → `detail`; popraw → `correct`; anuluj → `home` | E01-C2 |
| `correct` | Zapis → `detail`, wersja 2; anuluj → szczegóły | E01-C2, E01-C3, E01-C4 |
| `done` | Gotowe → `home`; kolejny → `search` | A1 |
| nieznany hash | Odzyskaj → `home`; reset czyści pola i wraca do startu | test prototypu |

## Odtwarzalna kontrola i dowody

Otwórz lokalnie `index.html` w przeglądarce. Przycisk „Reset makiety” czyści wpisane pola i wraca do początku. Wszystkie dane istnieją wyłącznie w pamięci strony.

Sprawdzenie w `agent-browser` na lokalnym `file://` (sesja zamknięta po próbie):

- Start i dostępne akcje — przegląd accessibility snapshot; zrzut `evidence/home.png`.
- Wyszukiwarka: wpis „ziemniaki”, lista zawęża się do dwóch podobnych produktów; wybór prowadzi do ekranu sukcesu; `evidence/search.png`.
- Niedostępność dostawcy: wybór scenariusza zachowuje komunikat, że brak dostępności nie jest brakiem produktu, i oferuje wpis ręczny; `evidence/provider-unavailable.png`.
- Konflikt GTIN: obserwowane trzy akcje użyj/popraw/anuluj; korekta prowadzi do nowej wersji i zachowuje wersję 1; `evidence/gtin-conflict.png`, `evidence/correction.png`.
- Pusta nazwa pokazuje komunikat walidacyjny; wpisanie nazwy i ponowny zapis prowadzą do szczegółów produktu; `evidence/validation.png`.
- Reset wraca do początku i czyści pola; nieznany hash oferuje powrót; browser back/forward przełącza `home` ↔ `search` zgodnie z historią.
- Tab ustawia widoczny focus na przycisku resetu; zrzut `evidence/mobile-keyboard.png`. Szerokość 390 px zachowuje wszystkie główne akcje.
- Ładowanie nie jest osobnym stanem: makieta nie wykonuje asynchronicznych odczytów ani zapytań sieciowych.
- Lokalne sprawdzenie symuluje zapis i nową wersję; nie weryfikuje trwałości danych ani prawdziwej integracji z API/kamerą.

Kontrola statyczna: brak zewnętrznych skryptów, arkuszy, obrazów, `fetch`, `XMLHttpRequest`, storage i akcji formularza wysyłających dane; wszystkie źródła są inline. Makieta nie dowodzi prawdziwości założeń, popytu ani gotowości produkcyjnej.
