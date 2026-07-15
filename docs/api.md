# API

API rozpoczyna się od niewielkiego zestawu operacji.

Kontrakty usług znajdują się w [OpenAPI ASR](openapi/asr.yaml) i
[OpenAPI backendu](openapi/backend.yaml). Przyszłe usługi parsera posiłków i resolvera
żywieniowego również otrzymają osobne kontrakty OpenAPI i będą integrowane przez
generowanych klientów REST.

## Kontrola stanu

- `GET /api/v1/health`
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`

## ASR

Kontrakt OpenAPI: [OpenAPI ASR](openapi/asr.yaml)

`POST /api/v1/transcriptions`

Żądanie:

```http
Content-Type: multipart/form-data

file=<audio file>
language=pl
prompt=<optional provider hint>
```

Odpowiedź:

```json
{
  "id": "7ec72754-c2a3-4db8-a6d8-081780fe755a",
  "transcript": "zjadlem trzy jajka",
  "language": "pl",
  "provider": "mock",
  "confidence": null,
  "transcribedAt": "2026-06-28T12:00:00Z"
}
```

## Kategorie

Kategorie są zagnieżdżonymi etykietami przepisów, np. śniadanie, zupa, słodkie i wytrawne.
Są identyfikowane przez UUID i udostępniają generowany `slug` oraz rozdzielaną ukośnikami `path`.

- `GET /api/v1/categories`
- `GET /api/v1/categories/{id}`
- `POST /api/v1/categories`
- `PATCH /api/v1/categories/{id}`
- `DELETE /api/v1/categories/{id}`

Żądanie utworzenia:

```json
{
  "parentId": null,
  "name": "Breakfast",
  "type": "MEAL",
  "sortOrder": 10
}
```

Aktualizacja obsługuje wyłącznie `name`, `parentId`, `type` i `sortOrder`. Nieznane pola,
puste żądania, niecałkowite wartości `sortOrder`, niedozwolone przeniesienia i błędne typy
zwracają `400`.

Usunięcie kategorii z dziećmi zwraca `409 CATEGORY_HAS_CHILDREN`; klient musi najpierw
przenieść lub usunąć dzieci.

Błędy kategorii mają postać:

```json
{
  "code": "CATEGORY_NOT_FOUND",
  "message": "Category not found: 7ec72754-c2a3-4db8-a6d8-081780fe755a",
  "timestamp": "2026-07-13T12:00:00Z"
}
```

## Przepisy

- `GET /api/v1/recipes`
- `POST /api/v1/recipes`
- `GET /api/v1/recipes/{id}`
- `POST /api/v1/recipes/{id}/ingredients`

Tworzenie przepisu przyjmuje `name` oraz opcjonalne `description`, `createdOn`, `imageUrl`
i `categoryIds`. Każda kategoria musi istnieć. Gdy pominięto `createdOn`, backend używa
bieżącej daty.

Składniki przyjmują `name`, dodatnią `quantity`, `unit` i opcjonalne nieujemne `calories`.
Jednostki można przesłać jako skróty (`g`, `t`, `w`, `ml`, `pcs`) albo nazwy enumów.
`GET /api/v1/recipes/{id}` zwraca przepis razem ze składnikami.

Błędy przepisów używają tej samej struktury co kategorie, z kodami takimi jak
`RECIPE_NOT_FOUND`, `INVALID_RECIPE_REQUEST` i `RECIPE_PERSISTENCE_ERROR`.

## Planowane API głosowego wprowadzania posiłków

Parser posiłków przyjmie tekst transkrypcji przekazany przez orkiestrator i zwróci
proponowane produkty, ilości, jednostki, kategorie i poziom pewności. Resolver żywieniowy
wzbogaci je o kalorie, pochodzenie danych żywieniowych i poziom pewności.

Żadna z tych usług nie zapisuje przepisów, kategorii ani spożytych posiłków. Wynik pozostaje
propozycją do czasu zatwierdzenia przez użytkownika; potwierdzone dane są następnie zapisywane
przez API backendu. Operacje modyfikujące powinny być idempotentne, aby ponowienia nie tworzyły duplikatów.
