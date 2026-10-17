# Notatki deweloperskie

## Sposób pracy

- utrzymuj małą bazę kodu, dopóki pierwszy przypadek użycia nie działa end-to-end
- dokumentuj decyzje raz, zwięźle, i aktualizuj je
- preferuj jedno zwarte źródło prawdy w `docs/`

## Stan historyczny

1. zainicjalizować projekt backendu
2. dodać lokalne środowisko Docker i PostgreSQL
3. udostępnić sondy stanu
4. dodać persystencję przepisów i konfigurowalnych drzew kategorii
5. dodać przypadki użycia, porty, adaptery i endpointy REST kategorii — gotowe
6. dodać przypadki użycia, adaptery persystencji, endpointy REST i testy przepisów — gotowe
7. opublikować API kategorii, przepisów i składników jako `docs/openapi/backend.yaml` — gotowe
Pierwsze siedem kroków utworzyło obecną bazę kodu. Dalszy plan został zastąpiony przez
[plan modularizacji](refactoring-plan.md), [aktualne MVP](mvp.md) i
[strategię danych produktowych](product-data-strategy.md). Parser oraz resolver powstają
początkowo jako moduły modularnego monolitu, a nie osobne usługi REST.

## Polecenia

```bash
make verify
```

Wymagane: Java 25, Maven i działający Docker. Cel wykonuje `mvn clean verify`
z katalogu głównego: testy wszystkich modułów, granice ArchUnit i bramkę JaCoCo
backendu oraz testy PostgreSQL 17 przez Testcontainers. Pierwszy przebieg pobiera
obrazy PostgreSQL i kontenera sprzątającego Ryuk. Testy nie korzystają z lokalnej
bazy Compose ani jej danych i nie są pomijane przy braku Dockera.

Szybsze sprawdzenie zmienianego modułu: `make test-backend` albo `make test-asr`.
Oba polecenia również obejmują jego testy PostgreSQL. Po przenoszeniu klas lub
wykryciu powielonych plików w `target` uruchom pełne `make verify` z czyszczeniem.
Workflow `.github/workflows/verify.yml` uruchamia tę samą pełną bramkę na push i PR.

```bash
docker compose up --build
```

## Uwagi dotyczące wykorzystania tokenów

Dokumentacja repozytorium powinna być zwięzła. Aktualizuj istniejące pliki zamiast tworzyć
wiele równoległych notatek, aby kolejne sesje Codex mogły czytać mniej i robić więcej.
