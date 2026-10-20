# E01 — katalog produktów i wersje danych

Data: 2026-10-20. Status: SDD do review, bez implementacji. Source issue: #13, epic #10.
Mapa nadrzędna: [fundamenty](2026-10-20-e01-reusable-foundations.md).

## 📝 TLDR

Zapisać własny produkt raz, wyszukać go ponownie i udostępnić niezmienny snapshot innym
modułom. Ta dostawa wprowadza fakty żywieniowe, ilości i jawny przelicznik sztuki;
nie implementuje kalkulatora, dostawcy kodów, przepisów ani dziennika.
Q1 podziału SDD potwierdzono. Właściciel potwierdził, że podobieństwo nazwy nie blokuje
utworzenia osobnych produktów; wyszukiwanie ma pokazać dane rozróżniające wariant.

## 📝 Problem Statement i Proposed Solution

#13 wymaga własnego produktu i wyszukiwania. #14/#15/#17/#20 potrzebują tych samych
faktów i tożsamości bez zależności od JPA. Publiczny kontrakt product.api i niezmienne
wersje zastępują kopiowanie DTO lub wczytywanie tabel innego modułu.
Nie wykorzystujemy archiwalnej specyfikacji katalogu jako dowodu wdrożenia — kod product
nie istnieje. Istniejące CategoryLookup i testy PostgreSQL są wzorcem granicy i weryfikacji.
[Open Food Facts API](https://openfoodfacts.github.io/documentation/docs/Product-Opener/v3/products/get-api-v3-product-code/)
udostępnia nazwę i marki jako osobne pola; Mealio zachowuje to rozdzielenie, a dodatkowo
pokazuje źródło danych i przelicznik, by rozróżnić podobne produkty bez sztucznej unikalności
nazwy. Nie kopiujemy zewnętrznej taksonomii ani jej zasad dopasowania.

## 📝 Architecture i kontrakty Java

product.api eksportuje Quantity/QuantityUnit (GRAM/MILLILITER/PIECE), NutritionBasis
(PER_100_G/PER_100_ML), ServingConversion, NutritionFacts i ProductSnapshot.
To niemutowalne typy biznesowe, nie HTTP DTO. NutritionFacts zawiera NutritionBasis
i cztery Optional<BigDecimal>;
ProductSnapshot zawiera productId, versionId, versionNumber, nazwę, opcjonalną markę/GTIN,
podstawę, fakty, konwersje, kompletność oraz metadane źródła.
Wynik wyszukiwania zawiera także listę sklepów powiązanych z produktem. To opcjonalne,
własne nazwy wpisywane przez użytkownika; nie ma centralnego słownika sklepów.

ProductDraft to pełny stan danych: name (wymagane), brand (opcjonalne), basis (wymagane),
nutrition { energyKcal, proteinGrams, fatGrams, carbohydrateGrams } z czterema
opcjonalnymi polami oraz opcjonalny pieceConversion { baseAmount, baseUnit }. Pominięcie/null pola
opcjonalnego oznacza brak wartości w nowym pełnym stanie, nie zachowanie poprzedniej.
Draft nie zawiera id/versionId/GTIN/source ani statusu potwierdzenia. RegisterProduct command
łączy draft i opcjonalny GTIN; ReviseProduct łączy draft z id/expectedVersionId.

Porty wejściowe: RegisterProduct.handle(command), ReviseProduct.handle(command),
SearchProducts.search(query), ProductCatalog.findCurrent(id)/findVersion(id, versionId).
Brak produktu zwraca Optional.empty; niedostępność katalogu to typowany wyjątek granicy,
nie pusty wynik. Zapis używa builderów prowadzących przez walidację domeny.
Prywatny ProductRepository należy do application.port.out; adapter JPA do infrastructure.
Nowe api/application/domain są czyste. Spring @Configuration/@Bean składa core i adaptery.
Transakcyjny adapter portu wejściowego obejmuje cały RegisterProduct/ReviseProduct,
zgodnie z heksagonem w mapie. Read adapters materializują snapshot w krótkiej transakcji.
Nie dodajemy @Service/@Transactional do nowych klas core ani JPA do portów.
Wewnętrzny ProductRegistration wykonuje wspólną walidację i zapis tożsamości/wersji;
RegisterProduct deleguje do niego, a późniejsze ConfirmExternalProduct wykorzysta go
w swojej szerszej transakcji z receipt. Nie jest to publiczny serwis dowolnego CRUD.

Quantity i przelicznik są wprowadzane tu jako dane. Czysta normalizacja jest implementowana
przez SDD #14, bez dodawania w #13 nieużywanych serwisów. Publiczne API nie zależy od Spring/JPA.

## 📝 Data Model

Właściciel wszystkich nowych tabel i change setów: product. Nowe pliki migracji
z następnym wolnym ID i include w istniejącym changelogu; zastosowany 001 bez zmian.

| Nowy rekord | Dane i ograniczenia |
|---|---|
| Product | UUID PK, opcjonalny kanoniczny GTIN unikalny, createdAt; bieżąca wersja wybierana po największym versionNumber |
| ProductVersion | UUID PK, productId FK, versionNumber > 0, unique(productId, number), nazwa 1–220, marka do 160, podstawa i cztery nullable numeric(12,6), nieujemne znane wartości |
| ServingConversion | versionId FK, jednostka PIECE, dodatni baseAmount numeric(12,6), baseUnit g albo ml zgodny z podstawą; unique(versionId, unit) |

Backend nadaje ręcznemu zapisowi USER_LABEL, jeśli ma GTIN, a USER_INPUT bez GTIN.
Klient nie nadaje źródła dostawcy ani statusu USER_CONFIRMED; status wynika z obecności
czterech wartości i podstawy.
Przy korekcie istniejącego produktu serwer zachowuje pochodzenie pól niezmienionych,
a zmienione pola oznacza jako USER_LABEL lub USER_INPUT (odpowiednio do GTIN), z opcjonalną
referencją podaną przez użytkownika. Klient nie może zadeklarować zewnętrznego dostawcy jako
źródła korekty. Nazwa sklepu pozostaje osobną relacją/metadanym produktu, nie pochodzeniem.
Pochodzenie danych jest częścią ProductVersion i zapisuje się osobno dla każdego pola
(typ źródła oraz opcjonalna referencja/identyfikator; dozwolone znormalizowane wartości,
bez pełnej odpowiedzi dostawcy). Dzięki temu np. ręcznie poprawiona marka nie przypisuje
pochodzenia użytkownika do wartości odżywczych, które nadal pochodzą z etykiety. Rewizja
po zapisie jest niezmienna. Lista sklepów jest odrębnym, edytowalnym metadanym Product;
jej aktualizacja nie tworzy nowej wersji żywieniowej. Nie ma userId ani technicznego właściciela.

```mermaid
flowchart LR
  classDef newEntity fill:#2f6feb,color:#fff,stroke:#1b4fb0
  entity_1["Product — nowy"]:::newEntity
  entity_2["ProductVersion z sourceSnapshot — nowa"]:::newEntity
  entity_3["ServingConversion — nowa"]:::newEntity
  entity_1 -->|"1 do wielu"| entity_2
  entity_2 -->|"1 do zera lub jednej konwersji PIECE"| entity_3
```

Nie tworzymy relacji do legacy recipe_ingredients: tamte calories nie opisują faktów na 100 g.

## 📝 API Contracts — nowe /api/v1

| Operacja | Kontrakt |
|---|---|
| POST /products | nazwa, optional brand/gtin, basis, nutrition, optional pieceConversion i stores; 201 + Location i ProductSnapshot |
| GET /products?query=&page=0&size=20 | dosłowna fraza nazwy bez case, stabilny sort name/brand/id; size 1–100; wyniki, total, page/size |
| GET /products/{id} | bieżący snapshot; 404 PRODUCT_NOT_FOUND |
| GET /products/{id}/versions/{versionId} | historyczny snapshot należący do produktu; 404 VERSION_NOT_FOUND |
| POST /products/{id}/versions | expectedVersionId i komplet nowych danych; 201; nieaktualna wersja 409 PRODUCT_VERSION_CONFLICT |
| GET /products/by-gtin/{gtin} | wyłącznie lokalny katalog; 404 nie oznacza sprawdzenia dostawcy |
| PUT /products/{id}/stores | pełna lista własnych nazw sklepów; aktualizuje metadane Product niezależnie od wersji żywieniowej |

W powyższej tabeli ścieżki są względne do /api/v1. Kwoty/ilości to kanoniczne napisy
np. "55", "12.500000", maks. 6 cyfr części całkowitej i 6 ułamkowej, bez exponentu;
wejściowe liczby JSON zamiast napisu są odrzucane. DTO mapują do BigDecimal.
Brak wartości może być pominięty/null, wynik zawiera jawne missingFields i completeness.
Nazwa jest trimowana; pusta nazwa, ujemne fakty, zero/ujemny przelicznik i niezgodna podstawa:
400 VALIDATION_FAILED z violations. Nowe requesty odrzucają nieznane pola bez globalnej
zmiany deserializacji starych endpointów. Błędy mają code/message/timestamp/requestId.

GTIN: 8/12/13/14 cyfr ASCII z poprawną cyfrą kontrolną, normalizacja do 14 cyfr.
Duplikat kodu: 409 PRODUCT_GTIN_CONFLICT z odwołaniem do istniejącego produktu.
Tworzenie tożsamości, wersji i konwersji jest jedną transakcją. Rewizja blokuje rekord
produktu na czas sprawdzenia expectedVersionId i zapisu; unikalność numeru chroni też baza.
Tożsamość i GTIN nie są zmieniane przez nową wersję; request rewizji nie przyjmuje tych pól.
Nazwa nie jest kluczem unikalności. Produkty bez GTIN mogą mieć tę samą lub bardzo podobną
nazwę i zostać zapisane jako osobne pozycje; dotyczy to np. mrożonych ziemniaków różnych
marek oraz własnego produktu jajka z indywidualnym przelicznikiem. Marka pozostaje osobnym
polem; pochodzenie opisuje dane, a sklep — sieć, z którą produkt jest powiązany. Żadne z nich
samo nie definiuje tożsamości. Wyniki wyszukiwania z tą samą nazwą udostępniają markę,
sklepy, pochodzenie pól, podstawę, kompletność oraz przelicznik sztuki, gdy występuje.
Ten sam produkt może mieć wiele nazw sklepów. Jawny GTIN nadal jest unikalny i jego konflikt
zwraca 409.

Przy konflikcie GTIN interfejs pyta użytkownika, czy użyć istniejącego produktu, przejść do
korekty jego danych, czy anulować. Korekta nigdy nie nadpisuje ProductVersion: tworzy nową
wersję z pochodzeniem zapisanym oddzielnie dla zmienianych pól. Sklepy można dodać lub usunąć
niezależnie od wersji żywieniowej. Nie pobieramy automatycznie dodatkowych katalogów w tej
ścieżce; użytkownik może ręcznie wpisać poprawne dane i ich pochodzenie.
UI nie ponawia zapisu automatycznie po utracie odpowiedzi; trwały klucz idempotencji
potwierdzania jest osobnym zakresem SDD skanera.

## 📝 Edge Cases, zgodność i cofnięcie

Znane 0 to wartość; brak nie jest zerem. Kompletność wymaga energii/B/T/W i podstawy.
Nie wyliczamy energii z makro. Zmiana danych tworzy wersję, nie aktualizuje starszej.
Własny produkt pozostaje lokalny bez publikacji. Brak gęstości nie pozwala na ml -> g.
Wywołanie repozytorium kończące się błędem daje kontrolowane 503, bez SQL w odpowiedzi.

Nowe endpoints i tabele są addytywne. Obecny enum/kontrakty recipe nie są zmieniane.
Przy wdrożeniu dodać do ADR-0005 jawnie zaakceptowane PIECE z przelicznikiem; zachować
pozostałe ograniczenia. Testować API starego recipe oraz migrację bazy z istniejącymi danymi.
Cofnięcie aplikacji pozostawia nowe tabele; rollback DDL tylko na pustej/testowej bazie
lub po jawnym zabezpieczeniu nowych danych. Nie kasować danych przy rutynowym rollbacku.

## 📋 Phasing

Jeden wynik: zapisany i ponownie wyszukiwalny produkt. Etapy: model, persystencja, kontrakt, weryfikacja.

## 📋 Implementation Plan

1. Typy/fabryki i komendy: walidacja nazw, Optional, zakresów, GTIN, jednostek i konwersji.
   Unit tests 100% linii; brak frameworków w domenie/API.
2. Repository/JPA i nowa migracja: transakcje create/version, constraints i sourceSnapshot.
   PostgreSQL: pusta baza, istniejący legacy rekord, konflikty/równoległe rewizje,
   błąd w środku zapisu bez częściowej tożsamości i niezmienność poprzedniej wersji.
3. Publiczny katalog i HTTP/OpenAPI: lookup/revisions/search/paginacja, błędy i requestId.
   Integracje realnego HTTP/PostgreSQL; nazwa z procentem i underscore jest literalna.
4. Kontrakt fake ProductCatalog i fixtures snapshotu użyte przez test przypadku odczytu;
   testy wyszukiwania podobnych nazw z różnymi markami/źródłami i własnym przelicznikiem,
   ArchUnit, testy zgodności legacy i pełne mvn clean verify.

Każdy krok kończy się działającym buildem. Pierwszy użytkowy wynik: zapis i wyszukanie
produktu. UI dostarcza osobna SDD. D01/D08, R01–R03, N01/N03 zachowane.
