# Mealio

Mealio to aplikacja do prowadzenia dziennika żywieniowego, projektowana przede wszystkim
dla urządzeń mobilnych. Klient iOS jest budowany w SwiftUI, backend działa w Javie i Spring
Boot, a dane trwałe przechowuje PostgreSQL. Backend udostępnia wersjonowane HTTP API i
rozwija się jako modularny monolit z granicami domenowymi oraz architekturą heksagonalną
wewnątrz modułów.

## Obecny zakres

- klient iOS w SwiftUI oraz API dla aplikacji mobilnej
- backend Spring Boot na Javie 25
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

`backend/` zawiera modularny monolit API, `asr/` opcjonalny adapter audio na tekst, a
`infra/` konfigurację środowiskową. Katalog `ios-prototype 3/` zawiera prototyp iOS;
docelowy klient SwiftUI wywołuje backendowe API i nie powiela logiki domenowej ani
obliczeń żywieniowych.

## Architektura

Backend pozostaje jednym procesem i wdrożeniem, podzielonym na moduły biznesowe takie jak
`product`, `diary`, `nutrition` i `mealparser`. Moduły udostępniają małe publiczne API;
adaptery HTTP i persystencji pozostają wewnątrz granic właściciela. PostgreSQL jest wspólną
bazą wdrożenia, ale moduły nie odczytują cudzych tabel ani repozytoriów. Liquibase zarządza
migracjami, a encje JPA pozostają w infrastrukturze właściwego modułu.

Przepływ klienta przebiega przez wersjonowane `/api/v1` API opisane w OpenAPI. Aplikacja
iOS odpowiada za prezentację i interakcje, backend za walidację przypadków użycia, zapis
danych i deterministyczne obliczenia. Parser AI może zaproponować strukturę opisu posiłku;
wartości odżywcze pochodzą z katalogu Mealio albo jawnie wskazanego dostawcy, a użytkownik
zatwierdza niepewne propozycje.

Szczegóły granic modułów, komunikacji i integracji opisuje [dokument architektury](docs/architecture.md).

## Uruchomienie lokalne

```bash
docker compose up --build
```

Aplikacja udostępnia:

- `GET /api/v1/health`
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`

## Dostępne API backendu

Backend udostępnia obecnie operacje kategorii i przepisów:

- Kategorie (`/api/v1/categories`): listowanie i tworzenie; dla konkretnego ID także pobieranie, częściowa aktualizacja oraz usuwanie pustej kategorii.
- Przepisy (`/api/v1/recipes`): listowanie i tworzenie; dla konkretnego ID pobieranie przepisu ze składnikami.
- Składniki przepisów (`/api/v1/recipes/{id}/ingredients`): dodawanie składnika do przepisu.

Pełne ścieżki, formaty żądań i odpowiedzi oraz kody błędów opisuje [specyfikacja OpenAPI backendu](docs/openapi/backend.yaml).

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
