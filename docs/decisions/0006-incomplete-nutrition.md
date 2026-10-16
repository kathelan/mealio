# ADR-0006: Niepełne dane żywieniowe

## Status

Accepted — 2026-10-16

## Kontekst

Otwarte i ręcznie tworzone rekordy produktów mogą nie zawierać wszystkich wartości
odżywczych. Odrzucenie każdego niepełnego produktu uniemożliwiałoby prowadzenie dziennika,
a traktowanie brakującej wartości jako zera dawałoby fałszywe wyniki.

## Decyzja

Produkt może zostać zapisany ze statusem `INCOMPLETE` i użyty we wpisie dziennika. `null`
oznacza brak danych i nigdy nie jest zamieniany na zero.

Za pełne podstawowe dane uznajemy obecność energii, białka, tłuszczu i węglowodanów dla
jawnej podstawy `PER_100_G` albo `PER_100_ML`.

Podsumowanie dnia zwraca sumy znanych wartości oraz metadane kompletności:

- liczbę wszystkich wpisów,
- liczbę niekompletnych wpisów,
- brakujące pola,
- stan `COMPLETE` albo `PARTIAL`.

Klient nie może prezentować wyniku `PARTIAL` jako pełnego bilansu dnia. Uzupełnienie produktu
tworzy nową wersję danych; historyczny wpis zachowuje wcześniejszy snapshot.

## Konsekwencje

- użytkownik może rejestrować jedzenie mimo braków w zewnętrznej bazie,
- obliczenia nie zaniżają po cichu wartości przez podstawienie zera,
- API i UI muszą jawnie prezentować kompletność,
- potrzebne są testy sum częściowych i wersjonowania snapshotów.
