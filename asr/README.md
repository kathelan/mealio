# Mealio ASR

Samodzielny moduł Spring Boot do zamiany mowy na tekst.

## Przeznaczenie

Moduł przyjmuje nagranie posiłku i zwraca transkrypcję. Nie oblicza kalorii ani nie
rozpoznaje jeszcze produktów. Te odpowiedzialności należą do osobnych modułów.

```text
audio upload -> ASR module -> transcript -> meal parser -> nutrition resolver
```

## Architektura

Moduł jest zbudowany w architekturze heksagonalnej:

- adapter wejściowy: `TranscriptionController`
- port wejściowy: `TranscribeAudioUseCase`
- serwis aplikacyjny: `TranscriptionService`
- port wyjściowy: `AsrProviderPort`
- adaptery wyjściowe: `MockAsrProviderAdapter`, `OpenAiAsrProviderAdapter`

Kierunek zależności:

```text
web/controller -> inbound port -> application service -> outbound port -> ASR provider adapter
```

Warstwa aplikacji zależy wyłącznie od interfejsów. SDK dostawców, klienci HTTP i zewnętrzne
API pozostają za adapterami wyjściowymi.

## Uruchomienie

Tryb domyślny używa dostawcy mock:

```bash
mvn -pl asr -am spring-boot:run
```

Uruchamiaj Maven z katalogu głównego repozytorium. Moduł ASR zależy od sąsiednich modułów,
więc reactor automatycznie przebuduje wymagane współdzielone pliki JAR.

Tryb OpenAI:

```bash
MEALIO_ASR_PROVIDER=openai OPENAI_API_KEY=... mvn -pl asr -am spring-boot:run
```

Docker Compose:

```bash
docker compose up --build asr
```

Tryb OpenAI z Docker Compose:

```bash
MEALIO_ASR_PROVIDER=openai OPENAI_API_KEY=... docker compose up --build asr
```

Smoke test Docker Compose z lokalnym plikiem audio:

```bash
curl -X POST http://localhost:8081/api/v1/transcriptions \
  -F "file=@records/Wilczak.m4a;type=audio/mp4" \
  -F "language=pl"
```

Repozytorium zawiera przykładowe nagranie w `records/` do testów ręcznych. Nie umieszczaj
nagrań w `src/main/resources`, ponieważ pliki z tego katalogu trafiają do artefaktu aplikacji.

## API

```http
POST /api/v1/transcriptions
Content-Type: multipart/form-data

file=<audio file>
language=pl
prompt=<optional ASR provider hint>
```

Przykład lokalny z Mavenem:

```bash
curl -X POST http://localhost:8080/api/v1/transcriptions \
  -F "file=@records/Wilczak.m4a;type=audio/mp4" \
  -F "language=pl"
```

Odpowiedź:

```json
{
  "id": "7ec72754-c2a3-4db8-a6d8-081780fe755a",
  "transcript": "Raz, dwa, trzy.",
  "language": "pl",
  "provider": "openai",
  "confidence": null,
  "transcribedAt": "2026-06-28T12:00:00Z"
}
```

## Konfiguracja

```yaml
mealio:
  asr:
    provider: mock
    openai:
      api-key: ${OPENAI_API_KEY:}
      base-url: https://api.openai.com/v1
      model: gpt-4o-mini-transcribe
```

Zmienne środowiskowe:

```bash
MEALIO_ASR_PROVIDER=openai
OPENAI_API_KEY=sk-...
MEALIO_ASR_OPENAI_MODEL=gpt-4o-mini-transcribe
```

Adapter OpenAI wywołuje `POST /audio/transcriptions` z danymi multipart:
`file`, `model`, `response_format`, `language`, and optional `prompt`.

`prompt` jest celowo opcjonalny. Moduł ASR powinien skupiać się na transkrypcji;
interpretacja domenowa, np. parsowanie posiłków, należy do osobnego modułu.

Moduł zależy od lokalnego JAR `request-audit`, który audytuje przychodzące i wychodzące
żądania HTTP. Polecenia Mavena uruchamiaj z katalogu głównego z `-pl asr -am`.

Jeśli uruchamiasz Maven bezpośrednio z `asr/`, najpierw zainstaluj współdzielone JAR-y:

```bash
cd ..
mvn -pl request-audit install -DskipTests
cd asr
mvn test
```

## Następne kroki

- dodać limity czasu, ponowienia i politykę dostawcy zapasowego
- rozwijać persystencję audytu transkrypcji
- dodać osobny parser posiłków zamieniający transkrypcję na ustrukturyzowane produkty
