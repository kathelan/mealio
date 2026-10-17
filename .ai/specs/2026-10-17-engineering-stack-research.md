# Mealio — research stosu i standardów inżynieryjnych

Data utworzenia i dostępu do źródeł: 2026-10-17.
Status: research zakończony; dodatkowe rekomendacje oczekują na wdrożenie.

## Cel i hierarchia decyzji

Wybrać rozwiązania, które zapewniają powtarzalne obliczenia żywieniowe, szybkie
wdrożenie MVP i utrzymanie granic domen. „Najlepszy” oznacza tutaj dopasowanie do
Mealio, kosztu utrzymania i istniejącego kodu; research nie dowodzi uniwersalnej
przewagi wydajnościowej poszczególnych technologii.

Obowiązują AGENTS.md, docs/architecture.md, .ai/docs/engineering-standards.md oraz
zaakceptowane ADR-0001–0008. Ta specyfikacja nie zastępuje ADR i nie autoryzuje
zmiany stacku. Rozróżniamy stan repozytorium, przyjęte reguły i nowe rekomendacje.
W chwili rozpoczęcia researchu standardy znajdowały się w
`docs/engineering-practices.md`. Użytkownik następnie polecił przenieść je do
`.ai/docs/engineering-standards.md`; migrację wykonuje agent nadrzędny.
Research odwołuje się do nowej ścieżki i nie zastępuje kanonicznych standardów.

## Stos bazowy i rozważone warianty

| Obszar | Stan / zalecenie | Uzasadnienie i koszt alternatywy |
|---|---|---|
| Backend | Zachować Java 25, Spring Boot 4.1.x, Spring MVC | Zgodne z repozytorium; MVC pasuje do blokującego JPA i istniejących testów. WebFlux wymaga innego modelu pracy i nie usuwa blokowania JPA. |
| Wersje Spring | Repo: 4.1.0; osobno zaplanować aktualizację patch | Oficjalna dokumentacja wskazuje stabilne 4.1.1, zgodne z Java 25. Aktualizacja wymaga przejścia pełnego builda; research jej nie wdraża. [S1] |
| Architektura | Modularny monolit, domeny z portami i adapterami | ADR-0001/0002; jeden backend i małe publiczne API. Mikroserwisy, broker i service discovery dopiero przy udokumentowanej potrzebie operacyjnej. |
| Baza | Zachować PostgreSQL 17 i Liquibase | Taki major działa w Compose i Testcontainers. Spójność wersji środowisk jest obecnie cenniejsza niż migracja major. PostgreSQL publikuje zasady wsparcia i aktualizacji. [S9] |
| Persystencja | Spring Data JPA w adapterach właściciela | Wykorzystuje istniejący kod. jOOQ/JDBC rozważyć dla konkretnego złożonego zapytania po pomiarze; bez drugiego stosu persystencji na zapas. |
| Kontrakt | REST `/api/v1`, obecne OpenAPI 3.0.3 | Aktualizacja wersji OpenAPI jest oddzielną zmianą narzędziową; wersja schematu OpenAPI nie jest wersją HTTP API. [S10] |
| Klient | SwiftUI, Swift Concurrency, URLSession | Natywne narzędzia Apple wystarczają do formularzy, dziennika i komunikacji HTTP. `@Observable` wymaga iOS 17 lub nowszego; minimalny target pozostaje decyzją produktu. [S13, S14] |
| Generowanie klienta | Kandydat: Apple Swift OpenAPI Generator | Generuje klienta z OpenAPI i ogranicza rozbieżności typów; koszt: plugin i dodatkowe typy transportowe. Nie zastępuje modelu prezentacji. [S15] |
| Testy | JUnit ze startera Boot, ArchUnit, Testcontainers PostgreSQL | Zachować obecne testy i próg JaCoCo 80%. H2 nadaje się do szybkich testów, ale nie dowodzi zgodności PostgreSQL. [S7, S8] |
| Build i CI | Zachować Maven i GitHub Actions | Jeden build reaktora, Java 25 i Docker. Zmiana na Gradle nie rozwiązuje obecnego problemu produktowego. |
| Obserwowalność | Actuator, Micrometer; eksport dopiero według środowiska | Wykorzystać istniejące mechanizmy Boot. Włączyć metryki opóźnień, błędów i źródeł bez treści posiłków w etykietach. [S12] |

ASR pozostaje opcjonalnym wejściem audio → tekst. Parser tekstu jest osobnym portem.
Model AI zwraca kandydatów, ilości i niepewność; wartości odżywcze pochodzą z katalogu.
Nie rekomendujemy konkretnego modelu AI bez ewaluacji polskich opisów posiłków,
kosztu, latencji i zgodności ze schematem wyjścia. Adapter dostawcy nie przenika do domeny.

## Standardy kodu Java — reguły przyjęte i doprecyzowania

1. **Brak wartości.** Optional reprezentuje opcjonalność w domenie i kontraktach
   Java. Adapter mapuje nullable JSON/JPA przez `Optional.ofNullable`; zwracając JSON
   odtwarza udokumentowany typ i nullable. Optional nie jest polem encji JPA.
   Korzystamy z `map`, `flatMap`, `filter`, `orElseGet`, `orElseThrow`; nie stosujemy
   sekwencji `isPresent()` → `get()` jako domyślnego stylu. Sam Optional nie może być
   null. Dokumentacja JDK zaleca go głównie dla wyników metod; szersze zastosowanie
   w modelach domenowych jest świadomą regułą Mealio wynikającą z decyzji użytkownika. [S2]
2. **Dane wymagane.** Wymagany identyfikator, podstawa wartości i nazwa są
   walidowane jawnie. Optional nie służy do ukrywania błędów kontraktu ani do
   zastępowania każdej walidacji wymaganej referencji. Pusta kolekcja reprezentuje
   brak elementów; nie wprowadzamy `Optional<List<...>>` bez odrębnej semantyki.
3. **Tworzenie obiektów.** Złożone agregaty, komendy, DTO i encje tworzymy przez
   builder. `build()` musi wywołać walidowany konstruktor/fabrykę. Lombok jest już
   zależnością projektu; `@Builder` umieszczamy na właściwej drodze konstrukcji,
   aby nie generować alternatywnej drogi omijającej walidację. [S3]
   Dla małych value objects używamy nazwanych fabryk, np. `Gtin.of(...)`.
   Konstrukcja wyjątków i obiektów frameworka może używać `new`.
4. **Niemutowalność.** Rekordy dla danych bez tożsamości, kopie kolekcji przy
   przekraczaniu granicy agregatu. Encje JPA pozostają osobnymi, świadomie mutowalnymi
   typami infrastruktury. Bez Lombok `@Data` na encjach i bez publicznych setterów
   domenowych obchodzących niezmienniki.
5. **Liczby i jednostki.** BigDecimal dla ilości i żywienia; dane z literałów
   dziesiętnych/stringów, bez pośredniego `double`. Jawne `MathContext`, skala i
   `RoundingMode` w specyfikacji obliczeń. Nie zaokrąglamy po każdym mnożeniu,
   jeśli kontrakt wymaga zaokrąglenia wyniku końcowego. Kolumny `numeric(p,s)`
   muszą pomieścić zadany zakres i precyzję; test sprawdza także zapis/odczyt. [S4, S5]
6. **Czas i identyfikatory.** `Instant` dla chwili, `LocalDate` dla dnia dziennika,
   UUID w publicznych kontraktach; `Clock` i generator ID wstrzykiwane tam, gdzie
   wpływają na deterministyczność. Nie przeliczamy dnia kalendarzowego na północ UTC.
7. **Nazwy i zakres.** Pakiety według domeny, nazwy według biznesu, constructor
   injection. Bez globalnych `Manager`/`Utils`, generycznych bazowych serwisów i
   interfejsu dla każdej klasy. Publiczne pozostają tylko rzeczy potrzebne na granicy.
8. **Zmiany istniejącego kodu.** Dostosowujemy zmieniany obszar; masowe formatowanie,
   przenoszenie pakietów i zmiana zachowania są osobnymi, kontrolowanymi krokami.

## Wzorce projektowe przypisane do problemów Mealio

| Problem | Wzorzec i granica | Kryterium poprawnego użycia |
|---|---|---|
| GTIN i ilości | Value Object / nazwana fabryka | Walidacja i normalizacja w jednym miejscu; jednostki g i ml nie są wymienne. |
| Niepełne żywienie | Jawna opcjonalność i wynik kompletności | Brak makra nigdy nie zmienia się w zero; odpowiedź oznacza sumę częściową. |
| Tworzenie produktu | Aggregate + Builder | Konstrukcja chroni niezmienniki; zapis i zmiana wersji przechodzą przez przypadek użycia. |
| Historia dziennika | Niezmienny snapshot wersji produktu | Odświeżenie katalogu nie przelicza historii. To wersjonowanie danych, bez potrzeby Event Sourcing. |
| Wielu dostawców | Port + Adapter / Strategy | Każdy adapter zwraca własny wynik portu; SDK i DTO dostawcy nie wychodzą poza infrastrukturę. |
| Kolejność źródeł | Jawna orkiestracja aplikacji | Lokalny katalog → OFF → opcjonalny FatSecret → formularz zgodnie z ADR-0008; limit/awaria i brak wyniku to różne stany. |
| Dziennik dnia | Model odczytu | Grupowanie wpisów bez osobnej tabeli DailyDiary i bez osobnej infrastruktury CQRS. |
| Potwierdzenie wielu wpisów | Transakcja przypadku użycia | Cały zaakceptowany zestaw zapisuje się atomowo; błąd nie zostawia części wpisów. |
| Równoległa edycja | Optymistyczna kontrola wersji | Konflikt zwraca stabilny błąd; nie nadpisuje po cichu nowej wersji. |
| Powtórzony request mobilny | Klucz idempotencji, gdy operacja tego wymaga | Jeden zaakceptowany zapis przy powtórzeniu; test współbieżności i konfliktu treści. |

Transakcję nakładamy na granicę aplikacji. Nie opieramy poprawności na wywołaniu
metody z `@Transactional` z innej metody tego samego obiektu: proxy Spring nie
przechwytuje takiej ścieżki. Wywołań zewnętrznych o nieograniczonej latencji nie
utrzymujemy wewnątrz długiej transakcji bazy. [S6]

Zdarzenia aplikacyjne służą efektom ubocznym. Outbox wprowadzamy dopiero przy
wymaganiu trwałego dostarczenia skutku po commit; zwykłe zdarzenie w pamięci nie
jest gwarancją dostarczenia po awarii. Spring Modulith można rozważyć przy potrzebie
weryfikacji nazwanych interfejsów lub trwałości zdarzeń; istniejący ArchUnit już
egzekwuje granice, więc drugi framework wymaga konkretnej korzyści. [S7, S16]

## HTTP, integracje i ochrona danych

- Osobne request/response, kontrakt modułu, domena i JPA. OpenAPI opisuje nullable,
  pola wymagane, błędy, jednostki, wersje i metadane kompletności.
- Nowe błędy powinny mieć stabilny kod, komunikat i identyfikator żądania.
  RFC 9457 / Spring `ProblemDetail` jest rekomendacją dla przyszłej standaryzacji;
  istniejącego JSON błędu nie zmieniamy bez specyfikacji i testów kompatybilności. [S11]
- `RestClient` pasuje do synchronicznych adapterów Spring MVC. Jawne limity czasu,
  maksymalny rozmiar odpowiedzi, mapowanie błędów, budżet ponowień i testowy serwer
  dostawcy są ważniejsze niż wybór dodatkowej biblioteki. [S17]
- Retry tylko dla bezpiecznych/idempotentnych operacji; respektować `Retry-After`
  i limit dostawcy. Circuit breaker/cache dodawać po zdefiniowaniu zachowania i metryk.
- Sekrety z konfiguracji środowiska; logi bez treści posiłków, nagrań i surowych
  odpowiedzi dostawców. Metryki mają ograniczoną liczbę etykiet: bez UUID, GTIN
  i opisów użytkownika jako tagów.
- ADR-0003 pozostaje w mocy: bez auth, User i technicznego `userId` w pierwszej
  iteracji. Przed publicznym środowiskiem potrzebna osobna specyfikacja identity,
  migracja własności danych oraz testy izolacji.

## Standardy klienta iOS — zalecenia do fazy 4

- SwiftUI z małym modelem stanu ekranu; `@MainActor` dla stanu UI, `async/await`
  dla I/O i anulowanie zadań przy zmianie ekranu/wyszukiwania. Nie kopiować reguł
  obliczeń backendu do klienta.
- Rozdzielić transport, mapowanie DTO, modele prezentacji i widoki. Model ekranu
  ma jawne stany: ładowanie, wynik, brak danych, błąd, propozycja do potwierdzenia.
- URLSession albo klient generowany z OpenAPI za jednym protokołem testowym;
  nie dodawać równocześnie drugiej biblioteki sieciowej bez potrzeby. [S14, S15]
- Wybór `@Observable` jest uzależniony od minimalnego targetu iOS 17+. [S13]
- Skaner ma ręczny fallback; potwierdzenie kandydata i oznaczenie niepełnych sum
  muszą być dostępne w UI. Tryb offline i synchronizacja wymagają osobnej
  specyfikacji konfliktów; nie wprowadzamy SwiftData jako drugiego źródła prawdy.
- Kryterium ukończenia obejmuje rzeczywisty `xcodebuild` z iOS SDK, testy mapowania
  i stanów ekranu oraz smoke test integracji z backendem. Sam Swift CLI tego nie dowodzi.

## Bramka jakości i priorytety wdrożenia

**P0 — istniejące zasady w implementacji modułów MVP.**
Optional i buildery stosować od katalogu produktów; doprecyzować automatyczne
testy granic dla nowych domen. Każdy nowy przepływ ma test domeny, aplikacji,
kontraktu HTTP i PostgreSQL tam, gdzie korzysta z persystencji. Szczególnie testować:
brak danych ≠ zero, rozbieżność g/ml, historię snapshotów, konflikty i rollback.
Kryterium: baseline i końcowe `mvn clean verify` przechodzą, próg 80% zachowany,
OpenAPI i migracje zgodne z zachowaniem; żaden test PostgreSQL nie jest pominięty.

**P1 — spójny build i automatyczne standardy.**
W osobnej specyfikacji ocenić Maven Wrapper/Enforcer (JDK 25 i wersja Maven), jeden
formatter Java i automatyczną walidację OpenAPI. Checkstyle służy sprawdzalnym
regułom, formatter formatowaniu; nie dublować sprzecznych wymagań. Narzędzia muszą
obsługiwać Java 25 i Lombok, mieć przypięte wersje oraz ten sam wynik lokalnie i CI.
Kryterium: celowo błędny przykład odrzucany przez daną bramkę, poprawny build
zielony; zmiany formatowania nie mieszają się ze zmianą zachowania. [S18, S19]

**P2 — utrzymanie i iOS.**
Osobno aktualizować Boot patch, automatyzować przegląd zależności, przygotować
metryki dostawców i po instalacji Xcode wdrożyć generowanie klienta próbnie na
jednym endpointzie. Kryterium: pełny build Java i odpowiednie sprawdzenia iOS,
brak zmiany zaakceptowanego kontraktu, udokumentowany koszt nowych zależności.
Dependabot jest kandydatem do kontrolowanych aktualizacji, a nie automatycznym
pozwoleniem na merge aktualizacji major. [S20]

**Odroczone do potrzeby:** mikroserwisy, Kafka, Redis, Elasticsearch, pełny CQRS,
Event Sourcing, Native Image, wymiana JPA i replatforming klienta. Każde wymaga
konkretnego problemu, kryterium pomiaru i oddzielnego ADR.

## Źródła pierwotne

Wszystkie sprawdzone 2026-10-17. Rekomendacje dla Mealio są wnioskami z dokumentacji
i lokalnych ADR; dokumentacja narzędzi sama nie określa architektury produktu.

- S1: [Spring Boot — System Requirements](https://docs.spring.io/spring-boot/system-requirements.html).
- S2: [JDK 25 — Optional](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Optional.html).
- S3: [Lombok — Builder](https://projectlombok.org/features/Builder).
- S4: [JDK 25 — BigDecimal](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/math/BigDecimal.html).
- S5: [PostgreSQL — Numeric Types](https://www.postgresql.org/docs/current/datatype-numeric.html).
- S6: [Spring Framework — Proxying Mechanisms](https://docs.spring.io/spring-framework/reference/core/aop/proxying.html).
- S7: [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html).
- S8: [Testcontainers — PostgreSQL](https://java.testcontainers.org/modules/databases/postgres/).
- S9: [PostgreSQL — Versioning Policy](https://www.postgresql.org/support/versioning/).
- S10: [OpenAPI 3.0.3](https://spec.openapis.org/oas/v3.0.3.html).
- S11: [Spring Framework — Error Responses](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html).
- S12: [Spring Boot — Observability](https://docs.spring.io/spring-boot/reference/actuator/observability.html).
- S13: [Apple — Managing model data in your app](https://developer.apple.com/documentation/swiftui/managing-model-data-in-your-app/).
- S14: [Apple — URLSession](https://developer.apple.com/documentation/foundation/urlsession).
- S15: [Apple — Swift OpenAPI Generator](https://github.com/apple/swift-openapi-generator).
- S16: [Spring Modulith — Verifying Application Module Structure](https://docs.spring.io/spring-modulith/reference/verification.html).
- S17: [Spring Framework — REST Clients](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html).
- S18: [Apache Maven Enforcer](https://maven.apache.org/enforcer/maven-enforcer-plugin/).
- S19: [Checkstyle](https://checkstyle.org/).
- S20: [GitHub — Dependabot version updates](https://docs.github.com/en/code-security/concepts/supply-chain-security/dependabot-version-updates).

## Postęp

- [x] Odczytać istniejące zasady, stack, CI, plan i ADR-0001–0008.
- [x] Zweryfikować możliwości i ograniczenia w oficjalnych źródłach internetowych.
- [x] Zapisać warianty, standardy kodu, wzorce i kryteria wdrożenia.
- [ ] Wdrożyć dodatkowe rekomendacje według osobnych zadań i wyników pomiarów.

Research nie oznacza wdrożenia narzędzi, aktualizacji zależności ani ukończenia fazy 5.
