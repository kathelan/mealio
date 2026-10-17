# Baza danych

Mealio używa PostgreSQL jako trwałego magazynu danych aplikacji. Lokalnie PostgreSQL jest
uruchamiany przez Docker Compose. Kolejne środowiska zachowują ten sam kontrakt bazy, aby
przejście z lokalnego Dockera przez OpenShift Sandbox do VPS wymagało jedynie zmiany połączenia.

## Środowisko lokalne

Lokalna baza działa jako usługa `postgres` zdefiniowana w `docker-compose.yml`:

```text
database: mealio
user: mealio
schemas:
  backend
  asr
```

Schematy izolują tabele należące do usług, jednocześnie pozwalając używać jednej instancji
PostgreSQL w MVP. Backend korzysta ze schematu `backend`, a ASR ze schematu `asr`.

## Własność danych

```text
backend schema:
  meal categories
  recipes and recipe ingredients
  future users
  future products and meals
  future meal diary

asr schema:
  transcription records
  audio metadata
  future provider audit metadata
```

Pliki audio nie są trwale przechowywane w MVP. Aplikacja mobilna wysyła audio, ASR przekazuje
je dostawcy transkrypcji, zapisuje tekst i metadane, a następnie odrzuca bajty audio.

Schemat backendu przechowuje zagnieżdżone kategorie, przepisy, relacje przepis–kategoria
i składniki. Kategorie używają generowanego `slug` oraz rozdzielanej ukośnikami `path`.
Usunięcie kategorii z dziećmi jest odrzucane, dzięki czemu drzewo pozostaje jednoznaczne.

Właścicielem `meal_categories` jest moduł `category`. Moduł `recipe` posiada
`recipes`, `recipe_categories` i `recipe_ingredients`. Powiązania w `recipe_categories`
przechowują UUID kategorii; recipe nie odczytuje jej encji ani repozytorium JPA.
Istnienie kategorii sprawdza przez publiczne `category.api.CategoryLookup`.
Klucz obcy nadal zapewnia spójność, a usunięcie kategorii usuwa jej powiązania
z przepisami przez dotychczasowe `ON DELETE CASCADE`. Istniejący change set 001
pozostaje niezmieniony; własność jego tabel jest określona powyżej.

## Migracje

Każda usługa posiada własny changelog Liquibase:

```text
backend/src/main/resources/db/changelog/db.changelog-master.yaml
asr/src/main/resources/db/changelog/db.changelog-master.yaml
```

Hibernate nie może bezpośrednio tworzyć ani aktualizować tabel. Zachowaj:

```yaml
spring.jpa.hibernate.ddl-auto: none
```

Zmiany schematu są wykonywane przez changesety Liquibase.

## Weryfikacja migracji

`make verify` uruchamia testy Testcontainers na PostgreSQL 17 dla backendu i ASR.
Każdy moduł dostaje pustą, odizolowaną bazę z własnym schematem. Liquibase stosuje
pełny changelog, a Hibernate sprawdza zgodność mapowania (`ddl-auto=validate`).
Test backendu sprawdza także zapis przepisu i składników, precyzję liczb, unikalność
ścieżek kategorii oraz klucze obce i kaskadowe usuwanie powiązań. Test ASR zapisuje
rekord transkrypcji i metadane audio. Wymagany jest dostęp do Dockera i możliwość
pobrania obrazów przy pierwszym uruchomieniu; testy nie modyfikują bazy deweloperskiej.

## Uruchomienie lokalne

Uruchomienie całego stosu:

```bash
docker compose up --build
```

Uruchomienie tylko PostgreSQL:

```bash
docker compose up -d postgres
```

Sprawdzenie schematów:

```bash
docker compose exec postgres psql -U mealio -d mealio -c "\dn"
```

Reset danych lokalnych:

```bash
docker compose down -v
docker compose up -d postgres
```

## Ścieżka wdrożenia

Planowana ścieżka:

```text
local Docker Compose -> OpenShift Sandbox -> VPS Docker Compose
```

OpenShift Sandbox może przechowywać tymczasową instancję PostgreSQL na potrzeby demo,
ale należy traktować ją jako nietrwałą. Przed migracją wykonaj `pg_dump` i sprawdź odtworzenie.

Na VPS PostgreSQL powinien używać trwałego wolumenu Dockera, a zaszyfrowane kopie zapasowe
powinny być wykonywane cyklicznie i kopiowane poza serwer.
