# Eksperyment pokrycia danych produktowych

Celem eksperymentu jest sprawdzenie rzeczywistego pokrycia produktów z Lidla, Aldi i
Biedronki. Rekord uznajemy za znaleziony po zgodności GTIN, a za zweryfikowany dopiero po
porównaniu nazwy, marki, opakowania oraz wartości z fizyczną etykietą.

## Metryki

- `lookup` — produkt znaleziony po GTIN,
- `identity` — zgodna marka, wariant i gramatura,
- `core nutrition` — obecne energia, tłuszcz, węglowodany i białko,
- `label match` — podane pola zgodne z fizyczną etykietą,
- `complete label` — dostępne wszystkie pola wymagane przez Mealio.

## Próbka

| # | Sklep | GTIN | Produkt | Źródło | Lookup | Identity | Core nutrition | Label match | Complete label |
|---:|---|---|---|---|---|---|---|---|---|
| 1 | Lidl | `4056489623182` | Sondey wafle ryżowe w czekoladzie mlecznej, 4 szt., 70 g | Open Food Facts | tak | tak | tak | tak, 5/5 pól | nieustalone |

### 1. Sondey — `4056489623182`

Open Food Facts zwrócił markę Sondey, opakowanie 70 g oraz nazwę `RISE CAKES MILK
CHOCOLATE`. Nazwa ma literówkę i wymaga normalizacji, a rekord nie zawiera liczby sztuk.

Porównanie na 100 g:

| Pole | Etykieta | Open Food Facts | Wynik |
|---|---:|---:|---|
| energia | 485 kcal | 485 kcal | zgodne |
| tłuszcz | 22 g | 22 g | zgodne |
| węglowodany | 63 g | 63 g | zgodne |
| błonnik | 2,9 g | 2,9 g | zgodne |
| białko | 7,3 g | 7,3 g | zgodne |

Na tym etapie nie porównano kwasów tłuszczowych nasyconych, cukrów i soli. Rekord źródłowy
ma tag kraju `Greece`, mimo zakupu w polskim Lidlu, więc kraju źródła nie należy utożsamiać
z rynkiem sprzedaży.

Wniosek: podstawowe dane są dokładne, ale rekord powinien przejść normalizację nazwy i
uzupełnienie porcji. Po potwierdzeniu użytkownika może zostać zapisany jako
`USER_CONFIRMED`.

Publiczna polska strona FatSecret zawiera produkt Sondey o tych samych wartościach
podstawowych, co stanowi niezależne potwierdzenie treści etykiety. Nie potwierdzono jednak,
czy rekord jest powiązany z GTIN `4056489623182` w API. Test adaptera FatSecret wymaga
klucza OAuth oraz dostępu do właściwego rynku, więc nie zaliczamy go jeszcze jako udanego
`lookup` w tabeli eksperymentu.
