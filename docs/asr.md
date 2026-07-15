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

ASR is not responsible for category creation, food persistence, or calorie calculation.
The workflow orchestrator passes the transcript returned by ASR to downstream meal-parser
and nutrition-resolver services through versioned OpenAPI contracts. They return a proposal
for user confirmation.

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
  "id": "7ec72754-c2a3-4db8-a6d8-081780fe755a",
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
MEALIO_ASR_PROVIDER=openai OPENAI_API_KEY=... mvn -pl asr -am spring-boot:run
```

Docker Compose:

```bash
MEALIO_ASR_PROVIDER=openai OPENAI_API_KEY=... docker compose up --build asr
```

Smoke request:

```bash
curl -X POST http://localhost:8081/api/v1/transcriptions \
  -F "file=@records/Wilczak.m4a;type=audio/mp4" \
  -F "language=pl"
```

Sample recordings used for local manual testing live in `asr/records/`, not in
`src/main/resources`, so they are not packaged into the Spring Boot artifact.

`prompt` remains available as an optional provider hint, but the default ASR path does
not use domain-specific prompts. Meal interpretation should happen after
transcription.

The ASR module uses the local `request-audit` jar for HTTP request/response audit
logging. Run Maven commands from the repository root with `-pl asr -am` so Maven also
builds the shared jar.

## Next work

- Add provider-level timeout and retry policy.
- Extend the persisted transcription audit data with original audio metadata where useful.
- Add a meal parser module that converts transcript text into proposed food items and quantities.
- Add a nutrition resolver that calculates calories and records the data source and confidence.
- Add an explicit user-confirmation step before parsed results change backend data.
- Add fallback provider support once we have traffic and cost data.
