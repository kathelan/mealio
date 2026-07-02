# API

The API starts with a very small surface.

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

## MVP endpoints to add next

- `GET /api/v1/categories`
- `POST /api/v1/categories`
- `GET /api/v1/recipes`
- `POST /api/v1/recipes`
- `GET /api/v1/recipes/{id}`
- `POST /api/v1/recipes/{id}/ingredients`
