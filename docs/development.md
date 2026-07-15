# Notatki deweloperskie

## Sposób pracy

- utrzymuj małą bazę kodu, dopóki pierwszy przypadek użycia nie działa end-to-end
- dokumentuj decyzje raz, zwięźle, i aktualizuj je
- preferuj jedno zwarte źródło prawdy w `docs/`

## Proponowana kolejność prac

1. zainicjalizować projekt backendu
2. dodać lokalne środowisko Docker i PostgreSQL
3. udostępnić sondy stanu
4. dodać persystencję przepisów i konfigurowalnych drzew kategorii
5. dodać przypadki użycia, porty, adaptery i endpointy REST kategorii — gotowe
6. dodać przypadki użycia, adaptery persystencji, endpointy REST i testy przepisów — gotowe
7. opublikować API kategorii, przepisów i składników jako `docs/openapi/backend.yaml` — gotowe
8. zdefiniować kontrakty OpenAPI parsera posiłków i resolvera żywieniowego — następne
9. zaimplementować propozycje posiłków z transkrypcji, obliczanie kalorii i pochodzenie danych
10. wygenerować klientów REST i dodać testy kontraktowe między usługami
11. dodać potwierdzenie użytkownika przed zapisaniem proponowanych danych w backendzie

## Polecenia

```bash
cd backend
mvn test
```

```bash
docker compose up --build
```

## Uwagi dotyczące wykorzystania tokenów

Dokumentacja repozytorium powinna być zwięzła. Aktualizuj istniejące pliki zamiast tworzyć
wiele równoległych notatek, aby kolejne sesje Codex mogły czytać mniej i robić więcej.
