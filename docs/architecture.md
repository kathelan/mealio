# Architektura

Mealio powstaje najpierw jako mobilne API backendowe w architekturze heksagonalnej.

## Cele

- izolacja domeny od frameworków
- małe i testowalne pierwsze MVP
- proste wdrożenie na OpenShift
- zwarta i użyteczna dokumentacja

## Proponowana struktura backendu

```text
backend/src/main/java/com/mealio/
  domain/
    model/
    port/
      in/
      out/
  application/
    service/
  adapter/
    in/
      web/
    out/
      persistence/
  infrastructure/
    config/
    security/
    database/
    observability/
  shared/
    exception/
    validation/
```

## Zasady projektowe

- kod domenowy nie zależy od Springa
- serwisy aplikacyjne koordynują przypadki użycia
- adaptery tłumaczą dane między HTTP, persystencją i obiektami domenowymi
- OpenAPI i sondy stanu należą do warstwy infrastruktury

## Przepływ głosowego wprowadzania posiłku

Obsługa głosu jest podzielona na małe usługi o osobnych odpowiedzialnościach:

```text
audio
  -> ASR: transcript
  -> meal parser: proposed foods, quantities, units, and categories
  -> nutrition resolver: calories, nutrition data source, and confidence
  -> user confirmation
  -> backend: persist accepted categories, recipes, ingredients, or meals
```

ASR wyłącznie zamienia mowę na tekst. Nie tworzy danych backendu ani nie oblicza wartości
odżywczych. Parser posiłków i resolver żywieniowy zwracają propozycje; ich wynik może zostać
zapisany dopiero po jawnym potwierdzeniu.

## Integracja usług

Integracje REST są projektowane contract-first. Każda usługa posiada dokument OpenAPI
swojego publicznego API, a konsumenci powinni w miarę możliwości używać generowanych
klientów i DTO.

- przechowuj kontrakty OpenAPI usług w `docs/openapi/`
- używaj stabilnych identyfikatorów operacji i wspólnej semantyki błędów
- propaguj identyfikatory korelacji i śledzenia przez wszystkie wywołania usług
- definiuj jawne limity czasu połączenia i odpowiedzi
- ponawiaj tylko operacje bezpieczne lub chronione kluczami idempotencji
- weryfikuj zgodność dostawcy i konsumenta testami kontraktowymi
- nie współdziel encji persystencji ani tabel baz danych między modułami
