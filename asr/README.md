# Mealio ASR

Standalone Spring Boot module for speech-to-text ingestion.

## Purpose

This module accepts meal audio and returns a transcript. It does not calculate
calories and does not parse food items yet. Those concerns should live in separate
modules that consume the transcript.

```text
audio upload -> ASR module -> transcript -> meal parser -> nutrition resolver
```

## Architecture

The module is built with hexagonal architecture:

- inbound adapter: `TranscriptionController`
- inbound port: `TranscribeAudioUseCase`
- application service: `TranscriptionService`
- outbound port: `AsrProviderPort`
- outbound adapters: `MockAsrProviderAdapter`, `OpenAiAsrProviderAdapter`

Dependency direction:

```text
web/controller -> inbound port -> application service -> outbound port -> ASR provider adapter
```

The application layer depends only on interfaces. Provider SDKs, HTTP clients, and
external APIs stay behind outbound adapters.

## Run

Default mode uses the mock provider:

```bash
mvn spring-boot:run
```

OpenAI mode:

```bash
MEALIO_ASR_PROVIDER=openai OPENAI_API_KEY=... mvn spring-boot:run
```

## API

```http
POST /api/v1/transcriptions
Content-Type: multipart/form-data

file=<audio file>
language=pl
prompt=Meal logging in Polish
```

Example:

```bash
curl -X POST http://localhost:8080/api/v1/transcriptions \
  -F "file=@meal.wav" \
  -F "language=pl" \
  -F "prompt=Meal logging in Polish"
```

Response:

```json
{
  "transcript": "na sniadanie zjadlem trzy jajka",
  "language": "pl",
  "provider": "openai",
  "confidence": null,
  "transcribedAt": "2026-06-28T12:00:00Z"
}
```

## Configuration

```yaml
mealio:
  asr:
    provider: mock
    openai:
      api-key: ${OPENAI_API_KEY:}
      base-url: https://api.openai.com/v1
      model: gpt-4o-mini-transcribe
```

## Next steps

- Add integration test for multipart upload.
- Add timeout, retry, and fallback policies around providers.
- Add transcript audit persistence.
- Add a separate meal parser module that turns transcript text into structured food
  items.
