# API

API rozpoczyna się od niewielkiego zestawu operacji.

Publiczne kontrakty HTTP znajdują się w [OpenAPI ASR](openapi/asr.yaml) i
[OpenAPI backendu](openapi/backend.yaml). Parser posiłków, katalog produktów i obliczenia
żywieniowe powstają początkowo jako moduły jednego backendu. Nie otrzymują osobnego REST API
wyłącznie po to, by komunikować się wewnątrz procesu.

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

## Lokalny katalog produktów

- `POST /api/v1/products` — ręczne utworzenie produktu i pierwszej wersji,
- `GET /api/v1/products?query=rice&page=0&size=20` — bieżące wersje pasujące do nazwy,
- `GET /api/v1/products/{id}` — bieżąca wersja,
- `GET /api/v1/products/by-gtin/{gtin}` — odczyt wyłącznie lokalnego katalogu,
- `POST /api/v1/products/{id}/versions` — korekta przez utworzenie nowej wersji,
- `GET /api/v1/products/{id}/versions/{version}` — niezmienna wersja historyczna.

Przykład ręcznego potwierdzenia etykiety:

```json
{
  "name": "Mleko",
  "brand": "Przykładowa marka",
  "gtin": "5901234123457",
  "basis": "PER_100_ML",
  "energyKcal": 62.25,
  "protein": 3,
  "fat": 3,
  "carbohydrates": 4
}
```

Wymagane są nazwa i podstawa `PER_100_G` lub `PER_100_ML`. Każda wartość odżywcza
jest opcjonalna; null jest brakiem danych. Komplet energii i trzech makroskładników
daje `USER_CONFIRMED`, pozostałe rekordy `INCOMPLETE`. Wartości są nieujemne i mieszczą
się w numeric(12,4). GTIN-8/12/13/14 musi mieć poprawną cyfrę kontrolną; przechowywany
i zwracany jest jako tekst o 14 cyfrach. Brak GTIN jest dozwolony dla produktów generycznych.

Nowa wersja przyjmuje ponownie pola danych oraz `expectedVersion`; GTIN pozostaje
przypisany do tożsamości. Odpowiedzi 201 mają Location. Równoczesna lub nieaktualna
korekta oraz duplikat GTIN zwracają 409. Nieznane pola żądania zwracają 400.
Wyszukiwanie traktuje `%`, `_` i `!` dosłownie; size ma zakres 1–100, offset mieści
się w int. Wynik jest tablicą uporządkowaną po nazwie i UUID.

Odpowiedź zawiera versionId/versionNumber, źródło USER_LABEL albo USER_INPUT,
recordedAt i verificationStatus. Snapshot ręczny zachowuje pola wprowadzonej wersji.
Błędy produktu mają code/message/timestamp/requestId; błędy bazy nie ujawniają SQL.
404 przy odczycie po GTIN nie oznacza sprawdzenia Open Food Facts ani innych dostawców.

## Planowane API wprowadzania posiłków

Backend przyjmie wpisany tekst lub transkrypcję i zwróci proponowane produkty, ilości,
jednostki, źródła danych oraz poziom pewności. Moduł żywieniowy obliczy wynik na podstawie
zatwierdzonych danych katalogowych.

Wynik pozostaje propozycją do czasu zatwierdzenia przez użytkownika. Operacje zatwierdzające
powinny być idempotentne, aby ponowienia nie tworzyły duplikatów.

Ręczny katalog powyżej jest dostępny. Pobieranie i potwierdzanie kandydatów
zewnętrznych jest kolejnym krokiem. Szczegóły źródeł opisuje
[strategia danych produktowych](product-data-strategy.md).

## Planowane API dziennika

Podstawowym modelem zapisu jest pojedynczy wpis spożycia. Widok dnia jest projekcją wpisów
pogrupowanych według sekcji, wraz z sumami.

```http
GET    /api/v1/diary/{date}
POST   /api/v1/diary/{date}/entries
PATCH  /api/v1/diary/entries/{entryId}
DELETE /api/v1/diary/entries/{entryId}
```

Dodawanie wyniku parsera tekstowego może utworzyć kilka wpisów atomowo dopiero po
potwierdzeniu propozycji. `date` ma format ISO `YYYY-MM-DD` i reprezentuje dzień wybrany
przez klienta, niezależnie od strefy czasowej serwera.
