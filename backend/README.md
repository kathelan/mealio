# Backend

Backend Mealio oparty na Javie 25 i Spring Boot 4.

## Budowanie i testy

```bash
mvn -pl backend -am test
```

Pełna bramka z katalogu głównego: `mvn clean verify`. Po przenoszeniu klas JPA
używaj `clean`, aby stare pliki `.class` nie zostały wykryte jako dodatkowe encje.
Testy architektury (`ModuleArchitectureTest`) są częścią standardowego zestawu testów.

## Uruchomienie w Dockerze

```bash
docker compose up --build
```

## Struktura

```text
src/main/java/com/mealio/
  category/
    domain/
    application/
    adapter/in/web/
    infrastructure/persistence/
      adapter/
      entity/
      mapper/
      repository/
  recipe/
  domain/          # modele legacy recipe i kategorii
  application/     # health
  adapter/         # health i persystencja legacy recipe oraz BaseEntity
  infrastructure/  # konfiguracja aplikacji
```

Tabela `meal_categories`, jej encja, mapper i repository należą do `category`.
Zależności persystencji recipe od kategorii oraz wspólna klasa `BaseEntity` są
jawnym długiem migracyjnym kontrolowanym przez ArchUnit. Następna faza usuwa te
zależności i porządkuje pozostałe pakiety według [planu migracji](../.ai/specs/2026-10-17-monorepo-migration.md).
