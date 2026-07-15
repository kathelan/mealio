# API

The API starts with a very small surface.

The ASR contract currently lives in [ASR OpenAPI](openapi/asr.yaml). The next contract
task is to publish categories, recipes, and ingredients in `openapi/backend.yaml`.
Future meal-parser and nutrition-resolver services will also own separate OpenAPI
contracts and will be integrated through generated REST clients.

## Health

- `GET /api/v1/health`
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`

## ASR

OpenAPI contract: [ASR OpenAPI](openapi/asr.yaml)

`POST /api/v1/transcriptions`

Request:

```http
Content-Type: multipart/form-data

file=<audio file>
language=pl
prompt=<optional provider hint>
```

Response:

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

## Categories

Categories are nested labels for recipes, such as breakfast, soup, sweet, and savory.
They are addressed by UUID and expose a generated `slug` and slash-separated `path`.

- `GET /api/v1/categories`
- `GET /api/v1/categories/{id}`
- `POST /api/v1/categories`
- `PATCH /api/v1/categories/{id}`
- `DELETE /api/v1/categories/{id}`

Create request:

```json
{
  "parentId": null,
  "name": "Breakfast",
  "type": "MEAL",
  "sortOrder": 10
}
```

Patch supports only `name`, `parentId`, `type`, and `sortOrder`. Unknown fields,
empty payloads, non-integer `sortOrder` values, invalid parent moves, and invalid
types return `400`.

Deleting a category with children returns `409 CATEGORY_HAS_CHILDREN`; clients must
move or delete children first.

Category errors use:

```json
{
  "code": "CATEGORY_NOT_FOUND",
  "message": "Category not found: 7ec72754-c2a3-4db8-a6d8-081780fe755a",
  "timestamp": "2026-07-13T12:00:00Z"
}
```

## Recipes

- `GET /api/v1/recipes`
- `POST /api/v1/recipes`
- `GET /api/v1/recipes/{id}`
- `POST /api/v1/recipes/{id}/ingredients`

Creating a recipe accepts `name`, optional `description`, `createdOn`, `imageUrl`, and
`categoryIds`. Every category id must already exist. When `createdOn` is omitted, the
backend uses the current date.

Ingredients accept `name`, a positive `quantity`, `unit`, and optional non-negative
`calories`. Units can be sent as their short codes (`g`, `t`, `w`, `ml`, `pcs`) or enum
names. `GET /api/v1/recipes/{id}` returns the recipe together with its ingredients.

Recipe errors use the same error envelope as categories, with codes such as
`RECIPE_NOT_FOUND`, `INVALID_RECIPE_REQUEST`, and `RECIPE_PERSISTENCE_ERROR`.

## Proposed voice meal APIs

The meal parser will accept transcript text supplied by the workflow orchestrator and return
proposed food items with quantities, units, suggested categories, and confidence. The
nutrition resolver will enrich those items with calories, nutrition data provenance,
and confidence.

Neither service persists recipes, categories, or consumed meals. Its result remains a
proposal until the user confirms it; the confirmed payload is then written through the
backend API. Mutating calls should support idempotency so retries cannot create duplicate
records.
