# E01 — deterministyczne obliczenie wartości dla ilości

Data: 2026-10-20. Status: SDD do review, bez implementacji. Source issue: #14.
Zależność: [katalog #13](2026-10-20-e01-product-catalog.md).
Mapa: [fundamenty](2026-10-20-e01-reusable-foundations.md).

## 📝 TLDR

Dla konkretnej wersji produktu i ilości zwrócić kcal/makro wraz z jawnie brakującymi
polami. Czysta matematyka i jeden kontrakt normalizacji będą podstawą późniejszych
przepisów i dziennika. Ta SDD nie tworzy kalkulatora zapotrzebowania ani porcji przepisu.
Q1 podziału rozstrzygnięto; brak dalszych blokujących decyzji produktowych tego zakresu.

## 📝 Architecture i kontrakty

NormalizeQuantity w product.api: normalize(Quantity, ProductSnapshot) -> BaseQuantity.
Implementacja czysta, bez repozytorium i sieci. GRAM wymaga PER_100_G, MILLILITER PER_100_ML;
PIECE wymaga przelicznika przypiętego do tej wersji produktu. 3 szt. po 55 g daje 165 g.
Brak konwersji i niezgodny wymiar są błędami, nie częściowym wynikiem żywieniowym.
BaseQuantity ma oddzielny limit do 24 cyfr/12 miejsc (iloczyn dwóch wejść 12/6); nie jest
obcinany do limitu Quantity przeznaczonego do przyjmowania danych użytkownika.

nutrition.api.NutritionCalculator.calculate(NutritionFacts, BaseQuantity) -> CalculationResult.
Publiczny wynik: ilość bazowa i Optional wartości; metadane brakujących pól i COMPLETE/PARTIAL.
Czysta implementacja w nutrition.domain. Nutrition application CalculateProductNutrition
pobiera snapshot przez product.api.ProductCatalog, wywołuje NormalizeQuantity i kalkulator.
Wynik aplikacyjny dodaje productId, versionId, inputQuantity oraz basis.

Zależność jest nutrition -> product.api. Product nie importuje nutrition. Kalkulator nie
odczytuje bazy i nie zna HTTP DTO, Recipe ani DiaryEntry. W testach podstawiamy ProductCatalog,
a matematyka nie wymaga mocków.
Spring konfiguruje porty w nutrition.infrastructure.config. Core api/application/domain
nie importuje frameworków; HTTP jest adapterem wejściowym. Czyste obliczenie nie otwiera
transakcji, a odczyt snapshotu kończy się po stronie adaptera product przed liczeniem. Opcjonalny brak w domenie/kontrakcie Java to Optional.

## 📝 Reguły i Data Model

Brak nowych tabel i encji. Wynik jest niemutowalnym obiektem wartości w pamięci.
Dane wejściowe pochodzą z konkretnego snapshotu, a nie bieżącego katalogu odczytanego
ponownie w środku obliczenia. W przyszłości diary utrwali wynik oraz revision reference.

Dla znanego pola: sourceValue * baseAmount / 100. BigDecimal ze stringów, mnożenie i dzielenie
przez 100 są dokładne. Wejściowe dane katalogu mają 12 cyfr/6 miejsc; wynik może mieć do 20
miejsc ułamkowych po normalizacji i obliczeniu i jest wysyłany dokładnie, bez zaokrąglania
na potrzeby ekranu. To nie zmienia precision źródłowych tabel. Serializacja używa napisu
z kropką bez exponentu. Limit wejścia utrzymuje wynik pojedynczego obliczenia w maksymalnie
36 cyfrach znaczących, zgodnych z Decimal klienta; normalizacja PIECE może dać 20 miejsc ułamkowych. Testy obejmują maksymalną precyzję oraz odrzucenie nadmiaru wejścia.

Każde nieznane pole pozostaje Optional.empty. Znane 0 pozostaje 0. Nie stosujemy 4/4/9,
nie wyliczamy automatycznie energii z makro. COMPLETE wymaga czterech znanych pól.
Sumowanie, dzielenie przez liczbę porcji i snapshoty recipe nie są implementowane zawczasu:
ich kontrakty planuje mapa, a pierwszy rzeczywisty konsument #15 je dostarczy.

## 📝 API Contracts i UI

POST /api/v1/nutrition/calculations

Request: productId UUID, versionId UUID oraz quantity { value: "165", unit: "GRAM" }.
Brak versionId jest błędem — snapshot produktu został zwrócony przez picker/katalog.
Response 200: productId/versionId, inputQuantity, baseQuantity, basis, nutrition
{ energyKcal, proteinGrams, fatGrams, carbohydrateGrams } jako napisy albo null,
completeness COMPLETE/PARTIAL i missingFields.

400: INVALID_QUANTITY / INCOMPATIBLE_UNIT / CONVERSION_REQUIRED / VALIDATION_FAILED.
404: PRODUCT_NOT_FOUND lub VERSION_NOT_FOUND. 503: PRODUCT_CATALOG_UNAVAILABLE.
Wszystkie błędy mają wspólną obwiednię adaptera. Obliczenie nie zmienia stanu, jest bezpieczne
do ponowienia. API nie akceptuje dowolnie podsuniętych przez klienta źródłowych makro;
przepisy korzystają z czystego publicznego API po stronie backendu.

UI wyświetla wynik backendu i brakujące pola. Ilość zmieniona podczas oczekiwania
unieważnia starszą odpowiedź. UI nie powiela normalizacji ani nie zastępuje braku zerem.

## 📝 Risks & Impact Review

Powtarzalność wymaga tej samej wersji, jednostki i wartości, nie tylko productId.
Wzorzec nie wprowadza zależności nutrition -> recipe. Dzielenie potraw i przeliczanie baz
muszą pozostać osobnymi decyzjami #15/#22; nie udajemy, że PIECE załatwia wszystkie porcje.
Brak nowej persystencji oznacza rollback kodu bez migracji; nowe API jest addytywne.
R01–R03 i ADR-0005/0006 zachowane; zapotrzebowanie Q02 pozostaje poza zakresem.

## 📋 Phasing

Jeden wynik: deterministyczne obliczenie dla wersji i ilości. Etapy: normalizacja, matematyka, API, weryfikacja.

## 📋 Implementation Plan

1. NormalizeQuantity i testy g/ml/PIECE: tożsamość jednostek, 3*55, konwersja wersyjna,
   brak przelicznika, niezgodny wymiar, zero/ujemne ilości i maksymalna skala.
2. Czysty kalkulator i wszystkie kombinacje braków czterech pól: 100/200 g i ml,
   znane zera, małe ułamki, wynik dokładny, identyczny snapshot -> identyczny wynik.
3. Use case po product.api + HTTP/OpenAPI: poprawne odczyty i błędy katalogu/versionId,
   kontrolowany requestId, brak zapisu i brak bezpośredniego importu JPA.
4. Realna integracja HTTP/PostgreSQL z produktem #13, testy ArchUnit oraz 100% wykonywalnych
   linii unit tests Surefire i pełne mvn clean verify. Nie dodawać wykluczeń coverage.

Kolejne kroki zostawiają działający projekt. Pierwszy feature do reuse przez klienta:
wybór produktu i zmiana ilości daje deterministyczny wynik z metadanymi braków.
