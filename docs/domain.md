# Model domenowy

## Istniejące pojęcia

- MealCategory
- Recipe
- RecipeIngredient
- IngredientUnit

Kategorie i przepisy powstały w pierwszej iteracji backendu. Docelowe MVP dodaje katalog
produktów, dziennik posiłków, obliczenia żywieniowe i interpretację tekstu. Granice modułów
opisuje [architektura](architecture.md), a pochodzenie produktów
[strategia danych produktowych](product-data-strategy.md).

## MealCategory

Reprezentuje ogólne grupowanie przepisów lub posiłków. Kategorie są konfigurowane przez
użytkownika i mogą być zagnieżdżane w innych kategoriach.

Przykłady:

- śniadanie
- lunch
- obiad
- słodkie
- wytrawne

Oczekiwane pola:

- id
- parentId
- name
- slug
- path
- type
- sortOrder

Przykładowe drzewo:

- breakfast
- breakfast / sweet
- breakfast / savory
- dinner
- dinner / soup
- dinner / pasta

## Recipe

Reprezentuje zapisany przepis lub posiłek wraz z metadanymi.

Oczekiwane pola:

- id
- name
- description
- createdAt
- photoUrl
- categoryIds

## RecipeIngredient

Reprezentuje pojedynczą pozycję składnika w przepisie.

Oczekiwane pola:

- id
- recipeId
- name
- quantity
- unit
- calories

## Product

Produkt katalogowy reprezentuje konkretny produkt opakowany albo produkt generyczny.
Kod GTIN jest opcjonalny, ponieważ żywność bez opakowania nie zawsze go posiada.

Najważniejsze pojęcia:

- `ProductId` — wewnętrzna tożsamość Mealio,
- `Gtin` — zwalidowany identyfikator opakowania przechowywany jako tekst,
- `NutritionFacts` — wartości z jawną podstawą, np. 100 g lub 100 ml,
- `Serving` — porcja i przelicznik na masę lub objętość,
- `ProductSourceSnapshot` — pochodzenie i wersja danych zewnętrznych,
- `VerificationStatus` — informacja, czy dane są zweryfikowane lub potwierdzone przez użytkownika.

Brak wartości odżywczej nie oznacza zera. Zmiana danych katalogowych nie zmienia po cichu
historycznych wyliczeń zapisanych posiłków.

Produkt może być `INCOMPLETE`, jeśli brakuje co najmniej jednej wartości wymaganej do pełnego
podsumowania: energii, białka, tłuszczu albo węglowodanów. Uzupełnienie danych tworzy nową
wersję; nie nadpisuje snapshotów użytych przez historyczne wpisy.

## FoodEntry i DailyDiary

`FoodEntry` reprezentuje fakt spożycia określonej ilości produktu w danym dniu i sekcji
posiłku. Odwołuje się do produktu, jednostki, przeliczonej masy lub objętości oraz snapshotu
danych użytych do obliczenia. Wpis można edytować, przenieść do innej sekcji i usunąć.

Wpis przechowuje zarówno ilość podaną przez użytkownika, jak i znormalizowaną ilość bazową.
W MVP obie wartości używają `GRAM` albo `MILLILITER`. W przyszłości wpis może zachować np.
`2 PIECE`, ale obliczenie nadal użyje przeliczonego `35 GRAM`.

`DailyDiary` jest modelem odczytu agregującym wpisy dla daty. Nie musi być osobną tabelą ani
encją domenową. Zwraca wpisy pogrupowane według `MealSection` oraz dzienne sumy wartości
odżywczych.

Podsumowanie zawiera także kompletność, np. liczbę wszystkich i niekompletnych wpisów oraz
listę brakujących składników. Suma znanych wartości jest prezentowana jako częściowa, a nie
jako pełny wynik dnia.

W pierwszej iteracji wpis nie ma właściciela, ponieważ backend nie obsługuje kont. Po
wprowadzeniu tożsamości stanie się zasobem należącym do użytkownika. Suma kalorii i
makroskładników jest wynikiem deterministycznego obliczenia, a nie odpowiedzi modelu AI.

Początkowe `MealSection`:

- `BREAKFAST`,
- `SECOND_BREAKFAST`,
- `LUNCH`,
- `AFTERNOON_SNACK`,
- `DINNER`,
- `OTHER`.

Data dziennika jest jawnym `LocalDate`, a nie datą wyliczaną za każdym razem ze strefy
czasowej serwera. Opcjonalny czas spożycia może zostać dodany niezależnie.

## Quantity, Unit i ServingConversion

`Quantity` składa się z dodatniej wartości `BigDecimal` oraz jednostki. Jednostki dzielą się
na wymiary:

- masa — bazowo `GRAM`,
- objętość — bazowo `MILLILITER`,
- liczba/porcja — np. `PIECE`, `SERVING`, `PACKAGE`.

`ServingConversion` należy do konkretnego produktu lub wariantu opakowania, np.:

```text
1 PIECE = 17.5 GRAM
1 PACKAGE = 70 GRAM
1 TEASPOON = 5 MILLILITER
1 TABLESPOON = 15 MILLILITER
```

Łyżeczka i łyżka opisują objętość, nie masę. Przeliczenie `MILLILITER -> GRAM` wymaga znanej
gęstości konkretnego produktu. System nie stosuje ogólnego założenia `1 ml = 1 g`.

Dane dostawcy mogą sugerować podstawę `100 g`, `100 ml`, masę opakowania albo porcję, ale
adapter musi je zwalidować. Niepełny lub niejednoznaczny przelicznik nie jest używany
automatycznie.
