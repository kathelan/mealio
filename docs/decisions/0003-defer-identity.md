# ADR-0003: Odroczenie użytkowników i uwierzytelniania

## Status

Accepted — 2026-10-16

## Kontekst

Najważniejszym ryzykiem MVP jest poprawny przepływ od produktu lub tekstu do zatwierdzonego
posiłku i powtarzalnego obliczenia wartości odżywczych. Uwierzytelnianie nie jest potrzebne do
zweryfikowania tego przepływu. Docelowo planowane jest logowanie Google albo email i hasło.

## Decyzja

Pierwsza iteracja nie zawiera `User`, technicznego użytkownika, atrap tokenów ani autoryzacji.
Modele produktów, posiłków i obliczeń nie przyjmują `userId`. Backend bez auth działa wyłącznie
lokalnie lub jako kontrolowane demo i nie zapewnia izolacji danych klientów.

Nie dodajemy stałego UUID użytkownika do rekordów. Taki identyfikator tworzyłby fałszywą
granicę bezpieczeństwa, komplikował testy i nadal nie identyfikował rzeczywistej osoby.

## Późniejsza migracja

Przed publicznym użyciem z danymi wielu osób:

1. dodać moduł `identity` oraz wybrany mechanizm logowania,
2. utworzyć `User` i powiązania z zewnętrznymi tożsamościami,
3. dodać nullable `owner_id` do danych należących do użytkownika,
4. przypisać istniejące dane deweloperskie do jawnie utworzonego konta albo je usunąć,
5. ustawić `owner_id` jako wymagane i dodać klucze obce oraz indeksy,
6. pobierać właściciela wyłącznie z uwierzytelnionego kontekstu, nigdy z dowolnego pola requestu,
7. dodać testy izolacji i autoryzacji przed udostępnieniem środowiska.

Produkty globalnego katalogu mogą pozostać współdzielone. Posiłki, korekty prywatne i dane
osobiste będą należeć do użytkownika.

## Konsekwencje

- szybsza implementacja głównego przepływu MVP,
- brak sztucznego użytkownika w domenie,
- konieczna przyszła migracja schematu oraz przypadków użycia,
- środowisko bez auth nie może bezpiecznie obsługiwać niezaufanych użytkowników ani danych
  wielu osób.
