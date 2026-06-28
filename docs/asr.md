# ASR module

The ASR module lives in `asr/` as a standalone Spring Boot Java module. It turns
uploaded meal audio into a transcript and deliberately stops at speech-to-text. Meal
parsing, nutrition lookup, confidence scoring, and user confirmation should be
separate modules built on top of this transcript.

## Goal

Build a production-shaped voice input path for Mealio:

```text
audio upload -> ASR provider -> transcript -> meal parser -> nutrition resolver -> confirmation
```

The first implementation exposes the ASR boundary and keeps the provider replaceable.
This lets us start with OpenAI and later add Deepgram, Google Speech-to-Text, or local
Whisper without changing the public API.

## Architecture

The module should follow hexagonal architecture. Application code depends on ports,
not directly on HTTP controllers, SDKs, or external ASR clients.

- Inbound adapter: `TranscriptionController`
- Inbound port: `TranscribeAudioUseCase`
- Application service: `TranscriptionService`
- Outbound port: `AsrProviderPort`
- Outbound adapters: `MockAsrProviderAdapter`, `OpenAiAsrProviderAdapter`

The intended dependency direction is:

```text
web/controller -> inbound port -> application service -> outbound port -> ASR provider adapter
```

This keeps the transcription use case stable while providers can be swapped or combined
behind the outbound port.

## API

```http
POST /api/v1/transcriptions
Content-Type: multipart/form-data

file=<audio file>
language=pl
prompt=<optional ASR provider hint>
```

Example response:

```json
{
  "transcript": "na sniadanie zjadlem trzy jajka i dwie kromki chleba",
  "language": "pl",
  "provider": "openai",
  "confidence": null,
  "transcribedAt": "2026-06-28T12:00:00Z"
}
```

## Providers

Default local mode uses a mock provider:

```yaml
mealio:
  asr:
    provider: mock
```

OpenAI mode:

```yaml
mealio:
  asr:
    provider: openai
    openai:
      api-key: ${OPENAI_API_KEY}
      model: gpt-4o-mini-transcribe
```

Local run:

```bash
cd asr
MEALIO_ASR_PROVIDER=openai OPENAI_API_KEY=... mvn spring-boot:run
```

Smoke request:

```bash
curl -X POST http://localhost:8080/api/v1/transcriptions \
  -F "file=@records/Wilczak.m4a;type=audio/mp4" \
  -F "language=pl"
```

Sample recordings used for local manual testing live in `asr/records/`, not in
`src/main/resources`, so they are not packaged into the Spring Boot artifact.

`prompt` remains available as an optional provider hint, but the default ASR path does
not use domain-specific prompts. Meal interpretation should happen after
transcription.

## Next work

- Add provider-level timeout and retry policy.
- Persist original audio metadata and transcript audit data.
- Add a meal parser module that converts transcript text into structured food items.
- Add fallback provider support once we have traffic and cost data.
