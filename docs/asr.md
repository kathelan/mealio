# Moduł ASR

Moduł ASR znajduje się w `asr/` jako samodzielny moduł Java Spring Boot. Zamienia audio
posiłku w transkrypcję i celowo kończy odpowiedzialność na zamianie mowy na tekst. Parsowanie
posiłków, wyszukiwanie wartości odżywczych, ocena pewności i potwierdzenie użytkownika należą
do osobnych modułów korzystających z transkrypcji.

## Cel

Zbudowanie produkcyjnego przepływu wprowadzania głosowego:

```text
audio upload -> ASR provider -> transcript -> meal parser -> nutrition resolver -> confirmation
```

Pierwsza implementacja udostępnia granicę ASR i pozwala wymienić dostawcę. Możemy zacząć
od OpenAI, a później dodać Deepgram, Google Speech-to-Text lub lokalny Whisper bez zmiany API.

ASR nie odpowiada za tworzenie kategorii, zapis jedzenia ani obliczanie kalorii. Orkiestrator
przekazuje transkrypcję do parsera posiłków i resolvera żywieniowego przez wersjonowane
kontrakty OpenAPI. Usługi zwracają propozycję do zatwierdzenia przez użytkownika.

## Architektura

Moduł stosuje architekturę heksagonalną. Kod aplikacji zależy od portów, a nie bezpośrednio
od kontrolerów HTTP, SDK ani zewnętrznych klientów ASR.

- Inbound adapter: `TranscriptionController`
- Inbound port: `TranscribeAudioUseCase`
- Application service: `TranscriptionService`
- Outbound port: `AsrProviderPort`
- Outbound adapters: `MockAsrProviderAdapter`, `OpenAiAsrProviderAdapter`

Kierunek zależności:

```text
web/controller -> inbound port -> application service -> outbound port -> ASR provider adapter
```

Dzięki temu przypadek użycia transkrypcji pozostaje stabilny, a dostawców można wymieniać
lub łączyć za portem wyjściowym.

## API

```http
POST /api/v1/transcriptions
Content-Type: multipart/form-data

file=<audio file>
language=pl
prompt=<optional ASR provider hint>
```

Przykładowa odpowiedź:

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

## Dostawcy

Domyślny tryb lokalny używa dostawcy mock:

```yaml
mealio:
  asr:
    provider: mock
```

Tryb OpenAI:

```yaml
mealio:
  asr:
    provider: openai
    openai:
      api-key: ${OPENAI_API_KEY}
      model: gpt-4o-mini-transcribe
```

Uruchomienie lokalne:

```bash
MEALIO_ASR_PROVIDER=openai OPENAI_API_KEY=... mvn -pl asr -am spring-boot:run
```

Docker Compose:

```bash
MEALIO_ASR_PROVIDER=openai OPENAI_API_KEY=... docker compose up --build asr
```

Żądanie smoke testu:

```bash
curl -X POST http://localhost:8081/api/v1/transcriptions \
  -F "file=@records/Wilczak.m4a;type=audio/mp4" \
  -F "language=pl"
```

Przykładowe nagrania do testów ręcznych znajdują się w `asr/records/`, a nie w
`src/main/resources`, dlatego nie trafiają do artefaktu Spring Boot.

`prompt` pozostaje opcjonalną wskazówką dla dostawcy, ale domyślny przepływ ASR nie używa
promptów domenowych. Interpretacja posiłku następuje po transkrypcji.

Moduł ASR używa lokalnego JAR `request-audit` do audytu żądań i odpowiedzi HTTP. Polecenia
Mavena uruchamiaj z katalogu głównego z `-pl asr -am`, aby zbudować również wspólny JAR.

## Następne prace

- dodać limity czasu i politykę ponowień dla dostawców
- rozszerzyć audyt transkrypcji o przydatne metadane oryginalnego audio
- dodać parser posiłków zamieniający tekst na proponowane produkty i ilości
- dodać resolver żywieniowy obliczający kalorie oraz zapisujący źródło i pewność danych
- dodać jawne potwierdzenie użytkownika przed zmianą danych backendu
- dodać dostawcę zapasowego po zebraniu danych o ruchu i kosztach
