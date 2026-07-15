# Audyt żądań

Biblioteka wielokrotnego użytku do audytu żądań i odpowiedzi HTTP usług Mealio.

Zapewnia:

- audyt przychodzących żądań i odpowiedzi servletowych
- audyt wychodzących żądań i odpowiedzi `RestClient`
- propagację `X-Correlation-Id`, `X-Request-Id` i W3C `traceparent`
- ustrukturyzowane zdarzenia audytowe JSON
- maskowanie wrażliwych nagłówków
- podgląd treści tylko po jawnym włączeniu i wyłącznie dla danych tekstowych
- pomijanie treści binarnej, np. przesyłanego audio

Biblioteka jest pakowana jako JAR i używana przez moduły usług, m.in. `asr` i `backend`.

## Budowanie i testy

Z katalogu głównego repozytorium:

```bash
mvn -pl request-audit test
```

Budowanie usługi razem z biblioteką:

```bash
mvn -pl asr -am test
```

## Konfiguracja

```yaml
mealio:
  audit:
    http:
      service-name: mealio-asr
      environment: local
      enabled: true
      include-headers: false
      include-bodies: false
      max-body-bytes: 2048
      request-id-header-name: X-Request-Id
      correlation-id-header-name: X-Correlation-Id
      propagate-request-id: true
      propagate-correlation-id: true
      propagate-trace-context: true
      include-query-string: false
```

## Model zdarzeń

Każde zdarzenie audytowe jest zapisywane jako JSON ze stabilnymi polami korelacji:

- `correlationId` identyfikuje cały przepływ między usługami
- `traceId`, `spanId` i `parentSpanId` opisują drzewo wywołań
- `requestId` identyfikuje pojedynczą wymianę HTTP
- `serviceName`, `environment` i `eventType` opisują źródło
- `http.statusCode`, `http.durationMs` i `outcome` opisują wynik
- `peer.service` i `peer.host` opisują wywołania usług zewnętrznych

Żądania przychodzące używają bezpiecznych `X-Correlation-Id` i `X-Request-Id`, jeśli je
otrzymały, albo generują brakujące identyfikatory. Wywołania `RestClient` tworzą lokalny
`requestId` i potomny `spanId`, propagując kontekst korelacji i śledzenia.

Logowanie nagłówków i treści jest domyślnie wyłączone. Zdarzenia zachowują zwarte metadane,
np. typ i długość treści, rozmiar, status, czas trwania, wynik oraz identyfikatory dostawców,
takie jak OpenAI `x-request-id`. Pełne nagłówki i podgląd treści włączaj tylko na czas lokalnej
diagnostyki. Wartości zapytań są domyślnie maskowane, a parametry takie jak tokeny, klucze,
e-mail i hasło pozostają zamaskowane także po włączeniu logowania zapytań.

Obecna propagacja kontekstu jest przeznaczona dla synchronicznych przepływów Spring MVC
z `ThreadLocal` i MDC. Przed użyciem kontekstu przez `@Async`, własne executory, schedulery
lub WebFlux dodaj `TaskDecorator` executora.
