# Usuwanie przepisów przez API

Data: 2026-10-17
Status: implemented

## Cel

Pozwolić klientom API i osobom używającym backendu usunąć niepotrzebny przepis wraz ze składnikami i przypisaniami kategorii. Główne README dokumentuje tę operację.

## Zakres

- Dodać `DELETE /api/v1/recipes/{id}`.
- Zwracać `204 No Content` po usunięciu.
- Dla nieistniejącego UUID zwracać istniejący błąd `404 RECIPE_NOT_FOUND`.
- Usuwanie przepisu usuwa jego składniki i powiązania kategorii; istniejące klucze obce Liquibase już deklarują `ON DELETE CASCADE`.
- Zaktualizować OpenAPI, główne README i testy.
- Nie zmieniać schematu bazy ani zachowania innych endpointów.

## Implementacja

1. Rozszerzyć wejściowy port `ManageRecipesUseCase` i wyjściowy `RecipeRepository` o usuwanie.
2. W `RecipeService` sprawdzić istnienie przepisu, rzucić `RecipeNotFoundException` przy braku, a następnie delegować usunięcie do repository.
3. Dodać metodę kontrolera zwracającą 204 i użyć istniejącego handlera błędu dla 404.
4. Adapter persystencji deleguje usunięcie do Spring Data; błędy bazy mapować istniejącym `RecipePersistenceException`.
5. Opisać odpowiedź i błędy w `docs/openapi/backend.yaml`, dodać operację do README.
6. Dodać testy jednostkowe portów/usługi/adaptera oraz testy HTTP i PostgreSQL potwierdzające kaskadowe usunięcie.

## Weryfikacja

- Test jednostkowy potwierdza przekazanie usunięcia po znalezieniu przepisu i brak delegacji dla brakującego ID.
- Test HTTP potwierdza 204, 404 dla nieznanego ID i usunięcie powiązanych danych.
- Test PostgreSQL sprawdza istniejące FK cascade na składnikach i przypisaniach kategorii.
- Uruchomić pełne `mvn clean verify`.

## Kompatybilność

Addytywna zmiana publicznego kontraktu `/api/v1`: nowa metoda DELETE nie zmienia istniejących operacji. Wymaga aktualizacji OpenAPI. Bez migracji bazy.
