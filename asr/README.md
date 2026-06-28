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

Docker Compose:

```bash
docker compose up --build asr
```

OpenAI mode with Docker Compose:

```bash
MEALIO_ASR_PROVIDER=openai OPENAI_API_KEY=... docker compose up --build asr
```

Docker Compose smoke test with a local audio file:

```bash
curl -X POST http://localhost:8081/api/v1/transcriptions \
  -F "file=@records/Wilczak.m4a;type=audio/mp4" \
  -F "language=pl"
```

The repository includes one local sample recording under `records/` for manual smoke
tests. Do not put sample recordings under `src/main/resources`, because files from
that directory are packaged into the application artifact.

## API

```http
POST /api/v1/transcriptions
Content-Type: multipart/form-data

file=<audio file>
language=pl
prompt=<optional ASR provider hint>
```

Local Maven example:

```bash
curl -X POST http://localhost:8080/api/v1/transcriptions \
  -F "file=@records/Wilczak.m4a;type=audio/mp4" \
  -F "language=pl"
```

Response:

```json
{
  "transcript": "Raz, dwa, trzy.",
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

Environment variables:

```bash
MEALIO_ASR_PROVIDER=openai
OPENAI_API_KEY=sk-...
MEALIO_ASR_OPENAI_MODEL=gpt-4o-mini-transcribe
```

The OpenAI adapter calls `POST /audio/transcriptions` with multipart form data:
`file`, `model`, `response_format`, `language`, and optional `prompt`.

`prompt` is intentionally optional. Keep the ASR module focused on transcription;
domain-specific interpretation, such as meal parsing, belongs in a separate module.

## Next steps

- Add timeout, retry, and fallback policies around providers.
- Add transcript audit persistence.
- Add a separate meal parser module that turns transcript text into structured food
  items.
