# E01 — uruchamialny klient iOS i wybór produktu

Data: 2026-10-20. Status: SDD do review, bez implementacji. Source issue: #24, parent #13.
Zależności: [katalog](2026-10-20-e01-product-catalog.md),
[obliczenia](2026-10-20-e01-nutrition-calculation.md) dla preview.
Mapa: [fundamenty](2026-10-20-e01-reusable-foundations.md).

## 📝 TLDR

Utworzyć uruchamialny target SwiftUI i pierwszy przepływ: wyszukaj lub utwórz produkt,
wybierz ilość i zwróć wybór do wywołującego. Transport, formularz, picker i prezentacja
wartości będą używane przez kolejne funkcje. Ta SDD nie buduje dziennika, przepisów ani skanera.
Q1 rozdzielenia dokumentów potwierdzono. Proponowany target iOS 17+ jest zgodny z README prototypu.

## 📝 Problem Statement i Architecture

Obecny pojedynczy plik prototypu nie ma projektu Xcode ani klienta HTTP. Jego wybór
produktu dopisuje wpis do zadanego posiłku, co uniemożliwia czyste użycie w edytorze przepisu.
Docelowy wybierak zwraca ProductSelection; skutek zapisania wpisu należy do wywołującego.

`ios/Mealio.xcodeproj`: aplikacja Mealio, unit test target i UI test target, współdzielony
scheme Mealio. Jeden moduł aplikacji, iOS 17+, systemowe SwiftUI/Foundation/Observation.
Struktura App/Core/DesignSystem/Features z mapy; bez zewnętrznej biblioteki architektonicznej.
Minimalny projekt zawiera konfigurację Debug/Release, Info i uprawnienia tylko używane
przez tę funkcję. Kamera i opis uprawnienia dochodzą dopiero w SDD skanera.

| Granica | Kontrakt / odpowiedzialność |
|---|---|
| APITransport | send(HTTPRequest) async throws -> HTTPResponse; body Data, status i niezbędne headers; URLSessionTransport implementuje transport, nie retry zapisów |
| ProductSearching | search(query, page, size) async throws -> ProductPage |
| ProductRegistering | register(draft) async throws -> ProductSnapshot |
| ProductCalculating | preview(productId, versionId, quantity) async throws -> CalculationResult |
| RemoteProductAdapter | mapuje endpointy, DTO/napisy dziesiętne i kody błędów; korzysta z transportu; nie zawiera reguł nutrition |
| ProductPickerModel | @MainActor @Observable; stan wyszukiwania, draft i wynik wyboru; dostaje porty przez konstruktor |

APITransport znajduje się w Core/Networking. DTO i mapowanie błędów biznesowych są przy
adapterze Products; Core zna tylko błędy sieci/HTTP/dekodowania. Porty są podmieniane w testach,
nie tworzymy abstrakcyjnej klasy bazowej ViewModel ani jednego repozytorium wszystkich domen.
App składa zależności i jawnie konfiguruje baseURL. Żadne dane API nie są sekretami w kodzie.

## 📝 Data Model i kontrakt wyboru

Brak lokalnej bazy i nowych encji backendu. Modele to draft i niemutowalne modele ekranowe.
Draft ilości/odżywiania jest String podczas edycji; poprawna wartość Decimal. JSON nowych
operacji używa kanonicznego napisu, DTO konwertuje bez Double; skala wejściowa do 6, maksymalnie 6 cyfr części całkowitej i 12 cyfr
łącznie; 1000000 jest odrzucane niezależnie od liczby cyfr łącznie. Preview może mieć do 36 cyfr znaczących. Nadmiar/niepoprawny tekst jest błędem,
nie wartością domyślną. Parser używa jawnego locale, testuje pl_PL i en_US oraz odrzuca
niejednoznaczne separatory, NaN/infinity i exponent niedozwolony przez kontrakt.

ProductPicking oferuje dwa jawne tryby:

- productOnly: zwraca productId i versionId, bez ilości.
- productWithQuantity: zwraca ten sam identyfikator i zwalidowaną inputQuantity.

Nie pytamy o ilość w trybie productOnly. Zwrot ProductSelection nie tworzy składnika
ani FoodEntry. Rejestracja własnego produktu jest osobnym zapisem katalogu; anulowanie
kolejnego wyboru go nie usuwa i nie zapisuje spożycia. Preview jest nieutrwalającym odczytem.

## 📝 UI/UX — podstawowe komponenty

| Komponent | Granica i reuse |
|---|---|
| QuantityInput | Binding tekstu + dozwolone jednostki + etykieta kontekstu; parsing, bez przeliczania sztuk na g |
| NutritionSummary | model wartości/basis/missingFields; wyświetla unknown i PARTIAL z odpowiedzi backendu, bez sumowania |
| AsyncContent | loading, empty, content i error; view builder oraz action retry dostarcza feature, nie generyczny loader |
| FormField / FieldError | label, hint, binding i lokalny/serwerowy komunikat pola |
| SubmitState | idle/submitting/failed/completed; blokuje wielokrotny submit i zachowuje draft |
| Semantyczne tokeny | role typografii, powierzchni i odstępów, oparte na standardach SwiftUI; finalne wartości wizualne zależą od osobnych mockupów |

Pierwsze realne użycie każdego komponentu jest w pickerze lub formularzu produktu. Nie
powstaje przyszły IngredientEditor (#26), ale picker można osadzić w dwóch testowych
hostach „wybór do przepisu” i „wybór do dziennika”, bez implementacji tych modułów.
Nie dodajemy pustych zakładek Przepisy/Dziennik ani aktywnego przycisku AI.

Flow: lista/search -> szczegóły i opcjonalna ilość -> zwrot wyboru. Wynik pokazuje markę,
sklepy, pochodzenie pól, podstawę, kompletność i przelicznik, jeśli są dostępne. Utwórz produkt ->
formularz -> zapis API -> powrót do tego samego flow wyboru. Paginacja ma jawną akcję
wczytania kolejnej strony. Stany braku sieci/błędu/ładowania/braku wyników są osobne.
Wyjście z widoku anuluje zadania. Debounce wyszukiwania jest wstrzykiwalny w testach;
każde żądanie ma generation token i stare wyniki nie nadpisują nowych nawet gdy fake
ignoruje cancellation. Tak samo działa preview po zmianie ilości.

VoiceOver, Dynamic Type, brak polegania tylko na kolorze i przyciski z opisami. Po błędzie
save tekst pozostaje. Niejednoznaczny błąd zapisu nie uruchamia automatycznego POST ponownie;
przy GTIN można wskazać istniejący produkt, bez GTIN komunikat uprzedza o możliwym zapisie.
Edycja produktu pozwala ręcznie poprawić pola i zapisać pochodzenie każdego z nich; lista
sklepów jest edytowalna niezależnie od wersji żywieniowej. Korekta danych tworzy nową wersję.
Przed implementacją UI powstanie klikalne low-fi dla wyszukiwania/utworzenia, skanu,
awarii dostawcy, lokalnego wyniku, konfliktu GTIN, korekty i powrotu do hosta. Low-fi nie
przesądza finalnego stylu ani nie zastępuje testów na urządzeniu.
Wyliczenia żywieniowe i jednostkowe wykonuje backend, klient jedynie formatuje otrzymane dane.

## 📝 API Contracts i zgodność

Adapter konsumuje wyłącznie nowe operacje katalogu i nutrition. Nie kopiuje encji Java.
Fixture JSON pochodzi z przykładów OpenAPI; test dekodowania wykrywa drift. Unknown
wartości nie stają się 0, a unit unsupported wymaga czytelnego błędu wersji kontraktu.
Nie zmieniamy backendowych API w tej SDD; potrzebną korektę kontraktu dostarcza jego właściciel
w zgodnym rozszerzeniu wraz z OpenAPI i testem konsumenta.

Obecny katalog prototypu pozostaje materiałem referencyjnym. Nowy klient ma osobny katalog
ios i nie przenosi lokalnej logiki Double ani przykładowych celów do produkcyjnego modelu.
Rollback aplikacji to poprzedni build; backendowe produkty pozostają, bez usuwania danych.
Auth, offline sync, diary, recipes, Garmin, AI i finalne mockupy nie są zakresem tej SDD.

## 📋 Phasing

Jeden wynik: działający klient z wyborem produktu. Etapy: target, transport, formularz/picker, preview i testy.

## 📋 Implementation Plan

1. Target, scheme, dependency composition i README. xcodebuild -list -project ios/Mealio.xcodeproj,
   xcodebuild build -project ios/Mealio.xcodeproj -scheme Mealio -destination 'generic/platform=iOS Simulator'.
   Weryfikacja na ustalonej w implementacji wersji Xcode; bez deklarowania działającego targetu przed buildem.
2. Transport, DTO i adaptery produktów. Unit tests URLProtocol dla poprawnego JSON, błędu HTTP,
   uszkodzonej odpowiedzi, anulowania, dokładnego Decimal i Optional/null. Brak automatycznego retry POST.
3. Picker i formularz z komponentami. Unit tests port fakes, parser locale, brak/pusta ilość,
   walidacja, stary wynik po nowym, zachowanie draftu, double-submit i cancellation.
4. Preview po SDD #14, dwa hosty pickera i testy kontraktu JSON; UI tests search -> wybór,
   create -> wybór, błąd/empty/loading i brak zapisu dziennika przez picker.
5. xcodebuild test ze scheme Mealio i konkretnym dostępnym symulatorem wybranym z
   xcrun simctl list devices available. Raport zapisuje Xcode/OS/urządzenie, interakcje,
   asercje i screenshoty, w tym długie nazwy i Dynamic Type. Dostępny backend uruchomiony
   z rewizji #13/#14 sprawdzany w scenariuszu integracji — sam fake nie dowodzi integracji.

Kroki utrzymują uruchamialną aplikację. Java nie jest zmieniana, jej bramki pozostają
obowiązkowe, jeśli implementacja wymaga korekt backendu. Każda nowa funkcja otrzymuje
unit tests; pełne pokrycie nowych wykonywalnych linii klienta sprawdzane raportem coverage.
N01/N03 i backendowa własność reguł pozostają zachowane. Badania: notatka źródłowa mapy.
