# E01 — źródła decyzji o fundamentach

Data sprawdzenia: 2026-10-20. Status: badania architektury, nie dowód popytu ani test implementacji.
Trzy odczytowe analizy agentów obejmowały domeny/kontrakty, klienta iOS oraz dostawców.
Właściciel potwierdził osobne SDD i doprecyzował heksagonalność ze Springiem.

## Repozytorium

- [Architektura](../../../docs/architecture.md), ADR-0002 i ModuleArchitectureTest:
  domeny mają publiczne api i prywatną infrastrukturę, testowane bez cykli.
- [Backlog](../backlog.md) i kryteria #13/#14/#20/#24/#25 pokazują pierwszy feature;
  #15/#17/#21/#22 i #26–#29 pokazują konkretne późniejsze miejsca użycia.
- `recipe.domain.model.MeasurementUnit` zawiera również legacy SPOON/SLICE; ingredient
  calories jest nullable i nie opisuje wartości PER_100_G. Nie jest gotowym kontraktem nutrition.
- Dwa ApiErrorResponse mają code/message/timestamp. Dodanie requestId jest rozszerzeniem
  nowego fundamentu transportowego, a nie dowodem istnienia wspólnego mechanizmu dziś.
- `ios-prototype 3/MealioPrototype.swift` ma lokalne Double, stałe cele, dopisywanie do
  „Obiad” i zamianę braków na 0. Nie przenosimy tych zachowań do klienta API.

## Inspiracje otwartego oprogramowania

[Wger Ingredient](https://github.com/wger-project/wger/blob/master/wger/nutrition/models/ingredient.py)
oddziela informacje o składniku, źródle i jednostkach. Przydatny jest jawny rodowód danych;
Mealio zachowuje własne kontrakty i regułę potwierdzenia. Automatyczny import nie zastępuje
kandydata wymagającego akceptacji z ADR-0007. Nie kopiujemy kodu ani całego modelu aplikacji.
[Wger LogItem](https://github.com/wger-project/wger/blob/master/wger/nutrition/models/log.py)
stanowi odniesienie rozdzielenia definicji i użycia; samo przejrzenie modelu nie dowodzi
niezmiennych snapshotów historii. Potrzeba Mealio wynika z R02, nie z tej inspiracji.

[Spring Modulith — Fundamentals](https://docs.spring.io/spring-modulith/reference/fundamentals.html)
rozróżnia interfejs modułu i implementację wewnętrzną. Mealio już ma właściwy kierunek
api i ArchUnit; nie dodajemy zależności Modulith tylko dla powtórzenia istniejącej kontroli.
Heksagon jest układem zależności portów i adapterów, nie wymogiem nowej biblioteki.

[Spring — transakcje programatyczne](https://docs.spring.io/spring-framework/reference/data-access/transaction/programmatic.html)
opisuje TransactionTemplate. Proponujemy użycie go na infrastrukturalnej granicy portu,
aby nowy core application/domain nie importował Springa, a zapis kilku rekordów był atomowy.
To decyzja projektu Mealio, nie narzucony przez dokumentację układ pakietów.

## Liczby i integracje

[BigDecimal Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/math/BigDecimal.html)
obsługuje jawną precyzję i zaokrąglenia. Limity źródła numeric(12,6), dokładne E01 i planowana
polityka dzielenia to decyzje tej SDD, nie normy fizjologiczne ani limity dostawcy.

[OFF GET v3](https://openfoodfacts.github.io/documentation/docs/Product-Opener/v3/products/get-api-v3-product-code/)
udostępnia dane produktu i projekcję fields. Adapter izoluje format i żąda potrzebnych pól.
[OFF dokumentacja API](https://openfoodfacts.github.io/documentation/docs/Product-Opener/api/)
opisuje identyfikację klienta i limity; parametry operacyjne trzeba sprawdzić ponownie przed
wdrożeniem. Nie zakładamy kompletności rekordu ani gwarantowanego pokrycia kodów.

## iOS

- [Observation w SwiftUI](https://developer.apple.com/documentation/swiftui/migrating-from-the-observable-object-protocol-to-the-observable-macro):
  natywne zarządzanie stanem dla iOS 17+ wystarcza jako podstawa pierwszego feature.
- [URLSession](https://developer.apple.com/documentation/foundation/urlsession): adapter
  transportu wykorzystuje systemowe HTTP; port funkcji pozostaje osobnym kontraktem aplikacji.
- [Decimal](https://developer.apple.com/documentation/foundation/decimal) i jego formatowanie:
  parser locale oraz kanoniczny format API są rozdzielone. Weryfikacja precyzji należy do testów.
- [task(id:)](https://developer.apple.com/documentation/swiftui/view/task(id:priority:_:)):
  lifecycle zadania wspiera cancellation; generation token zabezpiecza przed spóźnionym wynikiem.
- [DataScannerViewController](https://developer.apple.com/documentation/visionkit/datascannerviewcontroller):
  dostępność i wsparcie trzeba sprawdzać w czasie działania. Ręczny fallback i test urządzenia
  pozostają wymagane; symulator nie jest dowodem działania aparatu.

To dokumentacja techniczna i inspiracje, nie benchmark wydajności. Nie porównywano czasu
implementacji, nie uruchomiono aplikacji i nie zaakceptowano mockupów.
