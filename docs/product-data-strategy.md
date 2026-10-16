# Strategia danych produktowych i kodów kreskowych

## Cel

Po zeskanowaniu GTIN/EAN aplikacja powinna szybko zwrócić produkt oraz dane wystarczające do
obliczenia kalorii i makroskładników. Brak produktu nie może kończyć przepływu: użytkownik
powinien móc zeskanować etykietę lub uzupełnić minimalne dane i zapisać produkt do własnego
katalogu.

Nie zakładamy istnienia jednej kompletnej bazy produktów Lidla, Aldi i Biedronki. Kod GTIN
identyfikuje produkt, a nie sklep. Ten sam produkt może być sprzedawany w wielu sieciach,
natomiast produkty marek własnych mogą mieć słabsze pokrycie w otwartych bazach.

## Rekomendacja dla MVP

Kolejność odczytu po zeskanowaniu kodu:

```text
GTIN ze skanera
  -> walidacja cyfry kontrolnej i normalizacja
  -> lokalny katalog Mealio
  -> Open Food Facts
  -> FatSecret Basic (dodatkowa próba; katalog USA)
  -> formularz/OCR etykiety i potwierdzenie użytkownika
  -> zapis zatwierdzonego snapshotu w katalogu Mealio
```

Lokalny katalog jest źródłem używanym przez obliczenia. Zewnętrzna odpowiedź jest kandydatem,
który mapujemy, walidujemy i zapisujemy razem z pochodzeniem. Nie obliczamy historycznych
posiłków ponownie po cichej zmianie danych u dostawcy.

Odczyt lokalnego, zatwierdzonego produktu może zostać użyty bez dodatkowego potwierdzenia.
Pierwszy wynik z dostawcy zewnętrznego wymaga potwierdzenia przed utworzeniem lokalnego
produktu i wpisu dziennika. Cache techniczny odpowiedzi dostawcy nie jest katalogiem domenowym
i nie nadaje rekordowi statusu `USER_CONFIRMED`.

Jeśli żaden dostawca nie zna poprawnego GTIN, backend zwraca wynik `NOT_FOUND` z możliwością
utworzenia produktu. Formularz zachowuje zeskanowany kod i wymaga nazwy, podstawy `100 g` lub
`100 ml` oraz dostępnych wartości z etykiety. Pełny rekord otrzymuje `USER_CONFIRMED`, a
rekord z brakami `INCOMPLETE`. Jego źródłem jest `USER_LABEL`, nie Open Food Facts.

`NOT_FOUND` jest wynikiem biznesowym i nie jest tym samym co timeout, limit lub niedostępność
dostawcy. Przy błędzie dostawcy aplikacja może nadal znaleźć istniejący produkt lokalny, ale
nie powinna twierdzić, że kod nie istnieje w zewnętrznej bazie.

## Źródła

### Open Food Facts

Pierwszy dostawca dla MVP. Oferuje odczyt produktu po kodzie, składniki, zdjęcia i wartości
odżywcze. Dane są społecznościowe, więc mogą być niepełne lub błędne. Integracja korzysta z
aktualnego API v3 i żąda tylko potrzebnych pól.

Warunki techniczne:

- własny `User-Agent` w formacie wymaganym przez dostawcę,
- cache lokalny oraz timeout,
- respektowanie limitów; dokumentacja podaje obecnie 15 odczytów produktu/min/IP i
  10 wyszukiwań/min/IP,
- brak wyszukiwania „as you type” bez własnego indeksu,
- dla większej synchronizacji użycie eksportu danych zamiast masowego odpytywania API,
- retry z backoffem tylko dla 429, 5xx i błędów sieciowych.

Dane bazy są objęte ODbL, zawartość Database Contents License, a zdjęcia osobną licencją
CC BY-SA. Przed komercyjną publikacją połączonego katalogu trzeba potwierdzić obowiązki
atrybucji i share-alike. Dane każdego źródła przechowujemy z jawnym pochodzeniem, zamiast
bezpowrotnie mieszać surowe rekordy.

Źródła: [API Open Food Facts](https://openfoodfacts.github.io/documentation/docs/Product-Opener/api/),
[odczyt produktu](https://openfoodfacts.github.io/documentation/docs/Product-Opener/v3/products/get-api-v3-product-code/),
[licencje](https://openfoodfacts.github.io/openfoodfacts-server/api/tutorials/license-be-on-the-legal-side/).

### FatSecret Basic

Drugi dostawca wywoływany po wyniku `NOT_FOUND` z Open Food Facts. Bezpłatny plan Basic
udostępnia obecnie do 5000 wywołań dziennie, wymaga OAuth i atrybucji, ale obejmuje wyłącznie
zbiór USA. Dla polskich produktów — szczególnie marek własnych dyskontów — traktujemy go jako
opcjonalną próbę zwiększenia pokrycia, a nie gwarantowany fallback. Brak wyniku prowadzi od razu
do ręcznego wprowadzenia lub OCR etykiety.

Nie odpytujemy FatSecret, gdy produkt został już zatwierdzony w lokalnym katalogu. Przed
wdrożeniem adaptera potwierdzamy w warunkach usługi, które pola wolno utrwalać; dane zapisujemy
wyłącznie w dozwolonym zakresie wraz z pochodzeniem. Dostęp do polskiego zbioru wymaga osobnego
uzgodnienia planu Premier i nie jest wymaganiem MVP.

Źródła: [edycje i limity FatSecret](https://platform.fatsecret.com/api-editions),
[wyszukiwanie po kodzie](https://platform.fatsecret.com/docs/v1/food.find_id_for_barcode),
[OAuth 2.0](https://platform.fatsecret.com/docs/guides/authentication/oauth2).

### GS1 Polska — eProdukty

GS1 jest dobrym źródłem do potwierdzania poprawności GTIN, nazwy produktu, marki i właściciela
numeru. Platforma oferuje wyszukiwanie, eksport oraz integrację API; specyfikacja API jest
dostępna po rejestracji. Nie zakładamy, że każdy rekord zawiera komplet wartości odżywczych.

Włączenie GS1 wymaga sprawdzenia aktualnego pakietu, ceny, regulaminu, dozwolonego cache i
redystrybucji. Dlatego jest przyszłym, opcjonalnym adapterem, a nie zależnością wymaganą do
uruchomienia MVP.

Źródła: [GS1 eProdukty](https://www.eprodukty.gs1.pl/),
[opis integracji i zakresu danych](https://www.eprodukty.gs1.pl/how_it_works),
[regulamin](https://www.eprodukty.gs1.pl/terms).

### Lidl, Aldi i Biedronka

Na potrzeby tej decyzji nie znaleziono oficjalnego, publicznego i stabilnego API katalogu
spożywczego z GTIN oraz pełnymi wartościami odżywczymi dla tych sieci. Nie budujemy MVP na
scrapowaniu stron, gazetek ani prywatnych endpointów aplikacji mobilnych. Takie źródła są
niestabilne, mogą nie zawierać GTIN i wymagają osobnej analizy regulaminu oraz praw do danych.

Preferowane drogi uzyskania danych marek własnych:

1. Open Food Facts,
2. FatSecret Basic jako dodatkowa próba,
3. dane wprowadzone lub sfotografowane przez użytkownika,
4. GS1 eProdukty albo oficjalna umowa/feed, jeśli projekt osiągnie odpowiednią skalę.

Sieć sprzedaży może być opcjonalną obserwacją użytkownika, ale nie jest właściwością samego
GTIN. Cenę traktujemy jako osobny, zależny od sklepu, lokalizacji i czasu model poza MVP.

### Produkty generyczne

Produkty bez opakowania i kodu, np. jabłko, banan czy pierś z kurczaka, wymagają osobnej bazy
produktów generycznych. Polska baza składu żywności NIZP PZH istnieje, ale jest oferowana
licencyjnie; przed użyciem trzeba ustalić warunki. Nie należy przypisywać produktu generycznego
do losowego kodu kreskowego.

Źródło: [NIZP PZH — zakres i licencjonowana baza](https://www.pzh.gov.pl/zaklady/).

## Minimalny model danych

`Product`:

- wewnętrzne `ProductId`,
- opcjonalny znormalizowany `Gtin`,
- nazwa, marka i ilość opakowania,
- wartości na 100 g lub 100 ml: energia, białko, tłuszcz, węglowodany,
- opcjonalne cukry, kwasy nasycone, błonnik i sól,
- typ podstawy wartości: `PER_100_G`, `PER_100_ML` albo jawna porcja,
- opcjonalne, zweryfikowane przeliczniki sztuki, porcji i opakowania na gramy lub mililitry,
- stan: `VERIFIED`, `USER_CONFIRMED`, `UNVERIFIED`, `INCOMPLETE`,
- wersja danych i czas ostatniego sprawdzenia.

`ProductSourceSnapshot`:

- dostawca i jego identyfikator,
- czas pobrania,
- wersja API lub importu,
- surowa odpowiedź albo jej kontrolowany snapshot,
- zmapowane pola i informacja, które źródło dostarczyło każde pole,
- licencja i wymagana atrybucja,
- po wprowadzeniu kont: identyfikator użytkownika zatwierdzającego korektę.

Unikalność GTIN egzekwujemy w bazie, ale pozwalamy zachować historię wersji danych produktu.

## Walidacja

- GTIN przechowujemy jako tekst, nigdy jako typ liczbowy.
- Akceptujemy wspierane długości GTIN i sprawdzamy cyfrę kontrolną.
- Normalizacja zer wiodących ma jedną implementację domenową i testy kontraktowe z adapterami.
- Wartości odżywcze muszą mieć jednostkę i podstawę przeliczenia.
- Brak pola nie jest zerem.
- Odrzucamy wartości ujemne i oznaczamy podejrzane wartości zamiast po cichu je poprawiać.
- Konflikt źródeł nie jest rozstrzygany przez model językowy; stosujemy ranking źródeł i
  potwierdzenie użytkownika.
- Nie zakładamy, że 1 ml waży 1 g; konwersja objętości na masę wymaga gęstości produktu.
- Liczba sztuk i masa opakowania ze źródła zewnętrznego są osobnymi polami i podlegają
  walidacji przed utworzeniem przelicznika.

## Porty i adaptery

Moduł `product` definiuje port, np. `ExternalProductProvider`, oraz osobne adaptery:

- `OpenFoodFactsProductProvider`,
- `FatSecretProductProvider`,
- przyszły `Gs1ProductProvider`,
- opcjonalny import administracyjny.

Orkiestracja fallbacków należy do warstwy aplikacyjnej. Kontroler i aplikacja iOS nie znają
konkretnego dostawcy. Odpowiedź API Mealio zawsze zawiera źródło, świeżość i stan weryfikacji.

## Kolejne kroki

1. Kontynuować [eksperyment pokrycia](product-data-experiment.md) na ręcznej próbce co
   najmniej 100 produktów z Lidla, Aldi i Biedronki.
2. Zapisać osobno: znaleziono produkt, znaleziono komplet makro, zgodność z etykietą.
3. Zmierzyć osobno przyrost pokrycia uzyskany przez FatSecret Basic po wynikach `NOT_FOUND`.
4. Dopiero na podstawie wyniku zdecydować, czy płatny polski katalog lub GS1 jest potrzebny.
5. Zaimplementować lokalny katalog oraz adaptery Open Food Facts i FatSecret za jednym portem.
6. Dodać przepływ uzupełnienia brakującego produktu oraz potwierdzenia danych.
