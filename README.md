# Mealio

Mealio to platforma do monitorowania żywienia, projektowana przede wszystkim dla urządzeń
mobilnych. Pierwszy etap backendu obejmuje małe, przejrzyste API przepisów, ogólnych
kategorii posiłków i kontroli stanu, zbudowane w architekturze heksagonalnej i przygotowane
do wdrożenia na OpenShift.

## Obecny zakres

- rozpoczęcie prac od backendu
- API Spring Boot na Javie 25
- persystencja PostgreSQL
- środowisko deweloperskie w Dockerze
- sondy stanu zgodne z OpenShift

## Struktura repozytorium

```text
asr/
backend/
request-audit/
docs/
infra/
docker-compose.yml
README.md
```

## Uruchomienie lokalne

```bash
docker compose up --build
```

Aplikacja udostępnia:

- `GET /api/v1/health`
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`

Samodzielny moduł ASR znajduje się w `asr/`:

```bash
mvn -pl asr -am spring-boot:run
```

Ponieważ moduły usług zależą od lokalnych współdzielonych plików JAR, Maven najlepiej
uruchamiać z katalogu głównego repozytorium, używając `-pl <module> -am`.

Pomocnicze cele:

```bash
make test-asr
make run-asr
make install-local-libs
```

Uruchomienie Mavena bezpośrednio z katalogu usługi, np. `cd asr && mvn test`, korzysta
z artefaktów w `~/.m2` i nie przebudowuje automatycznie modułów sąsiednich. Przy takim
trybie pracy najpierw zainstaluj lokalne współdzielone pliki JAR:

```bash
make install-local-libs
cd asr && mvn test
```

## Dokumentacja

- [Architektura](docs/architecture.md)
- [MVP](docs/mvp.md)
- [Model domenowy](docs/domain.md)
- [API](docs/api.md)
- [ASR OpenAPI](docs/openapi/asr.yaml)
- [Backend OpenAPI](docs/openapi/backend.yaml)
- [Baza danych](docs/database.md)
- [Moduł ASR](docs/asr.md)
- [Notatki deweloperskie](docs/development.md)

## Następny krok

Dodanie strukturalnej interpretacji posiłków na podstawie transkrypcji ASR:

- przekształcanie tekstu transkrypcji w proponowane produkty i ilości
- rozpoznawanie produktów i danych żywieniowych bez wiązania logiki z dostawcą ASR
- obliczanie kalorii z zachowaniem pewności i pochodzenia każdej propozycji
- wymaganie potwierdzenia użytkownika przed zapisaniem kategorii, przepisów lub posiłków
- publikacja kontraktów OpenAPI usług i integracja przez generowane klienty REST
