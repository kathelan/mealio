# Standardy inżynieryjne Mealio

## Projektowanie

- Stosuj SOLID jako narzędzie do kontroli odpowiedzialności i zależności, nie jako wymóg mnożenia klas.
- Preferuj prosty kod domenowy i kompozycję nad dziedziczeniem.
- Używaj języka domenowego w nazwach; unikaj ogólnych `Manager`, `Helper`, `Utils` i `CommonService`.
- Preferuj niemutowalne obiekty i rekordy dla danych bez tożsamości.
- Brak wartości modeluj przez `Optional` w domenie i publicznych kontraktach Java.
  Stosuj `map`, `flatMap`, `filter`, `orElseGet` i `orElseThrow` zamiast ręcznych
  sprawdzeń `== null`/`!= null`. W adapterach mapuj nullable JSON i JPA na `Optional`;
  nie zapisuj `Optional` jako pola encji JPA ani nie zmieniaj przez niego formatu JSON.
- Preferuj buildery dla obiektów z wieloma polami (agregaty, komendy, DTO, encje).
  Builder wywołuje walidowany konstruktor lub fabrykę, aby nie omijać niezmienników.
  Proste obiekty wartości twórz nazwanymi fabrykami. Wyjątki i typy narzucone przez
  framework mogą korzystać z konstruktorów. Istniejący kod dostosowuj w zmienianym
  obszarze; nie łącz zadania z masową refaktoryzacją pozostałych modułów.
- Niezmienniki sprawdzaj podczas tworzenia lub zmiany obiektu domenowego.
- Ilości, kalorie i makroskładniki reprezentuj przez `BigDecimal` oraz jawną jednostkę.
- Wstrzykuj `Clock`, generator identyfikatorów i klientów zewnętrznych, gdy deterministyczność ma znaczenie.

## Java i Spring

- Stosuj constructor injection; nie stosuj field injection.
- Konfigurację mapuj przez typowane `@ConfigurationProperties`.
- Transakcję umieszczaj na granicy przypadku użycia, nie w kontrolerze.
- Nie wystawiaj encji JPA przez API.
- Nie używaj Lombok `@Data` na encjach.
- `spring.jpa.open-in-view` pozostaje wyłączone.
- Schematem zarządza Liquibase; Hibernate nie tworzy go w środowisku produkcyjnym.
- Stosuj aktualne funkcje Javy wtedy, gdy poprawiają czytelność, a nie dla samej nowości.

## API

- Publiczne HTTP API jest wersjonowane pod `/api/v1` i opisane w OpenAPI.
- Request, response, model domenowy i encja persystencji są oddzielnymi typami.
- Walidacja formatu odbywa się na wejściu, a niezmienniki są ponownie chronione przez domenę.
- Błędy mają stabilny kod maszynowy, czytelny komunikat i identyfikator żądania.
- Chwile (`Instant`) są przesyłane w UTC i ISO 8601. Daty kalendarzowe, takie jak dzień
  dziennika, są przesyłane jako ISO `YYYY-MM-DD` bez sztucznej konwersji do UTC.
  Identyfikatory publiczne są UUID.
- Nie zwracaj stack trace, sekretów ani surowej odpowiedzi zewnętrznego dostawcy.

## Persystencja

- Każda tabela i migracja ma jednego właściciela domenowego.
- Zastosowanych change setów Liquibase nie modyfikujemy; dodajemy kolejny.
- Ograniczenia krytyczne dla spójności są egzekwowane także w bazie.
- Relacje JPA projektuj świadomie; unikaj niekontrolowanego lazy loading i N+1.
- Testy zgodności z produkcyjną bazą korzystają docelowo z PostgreSQL przez Testcontainers.

## Integracje

- Ustaw jawne timeouty.
- Ponawiaj wyłącznie operacje bezpieczne lub idempotentne.
- Mapuj błędy dostawcy na własne błędy portu/aplikacji.
- Nie pozwalaj, by DTO lub SDK dostawcy przenikały do aplikacji i domeny.
- Sekrety pochodzą z konfiguracji środowiska i nigdy nie trafiają do repozytorium.

## Testowanie

Stosujemy piramidę testów:

1. szybkie testy domeny i przypadków użycia bez Springa,
2. testy adapterów i persystencji z ograniczonym kontekstem,
3. testy integracyjne kontraktu HTTP,
4. nieliczne testy end-to-end.

Zasady:

- testuj zachowanie i kontrakt, nie prywatną implementację,
- mockuj porty na granicy, nie każdą wewnętrzną klasę,
- każdy naprawiony błąd otrzymuje test regresyjny,
- testy są deterministyczne i niezależne od kolejności,
- refaktoryzację poprzedzają testy charakteryzacyjne,
- test architektury jest częścią builda,
- H2 nie jest wystarczającym dowodem zgodności z PostgreSQL.

## Refaktoryzacja

Refaktoryzacja nie zmienia obserwowalnego zachowania. Nie łącz w jednej zmianie przenoszenia pakietów, zmiany kontraktu HTTP, zmiany reguł biznesowych i zmiany schematu danych.

Każdy krok pozostawia kompilowalny projekt i kończy się adekwatnym testem oraz `mvn verify` z katalogu głównego.
