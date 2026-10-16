# ADR-0008: Kolejność źródeł produktów po kodzie kreskowym

## Status

Accepted — 2026-10-16

## Kontekst

Żaden zewnętrzny katalog nie zapewnia pełnego i bezbłędnego pokrycia polskich produktów.
Open Food Facts jest otwarty i zawiera produkty marek własnych, ale dane społecznościowe mogą
być niepełne. FatSecret Basic jest bezpłatny w określonym limicie, lecz udostępnia zbiór USA.
Brak rekordu nie może blokować zapisania posiłku.

## Decyzja

Po zeskanowaniu i walidacji GTIN aplikacja sprawdza kolejno:

1. zatwierdzony lokalny katalog Mealio,
2. Open Food Facts,
3. FatSecret Basic jako opcjonalną dodatkową próbę,
4. ręczne wprowadzenie danych lub OCR etykiety.

Znaleziony produkt zewnętrzny pozostaje kandydatem do potwierdzenia zgodnie z ADR-0007.
Potwierdzona wersja trafia do lokalnego katalogu i przy następnym odczycie ma pierwszeństwo.
Fallback jest orkiestracją warstwy aplikacyjnej; każdy dostawca jest osobnym adapterem tego
samego portu. `NOT_FOUND`, niedostępność i przekroczenie limitu pozostają różnymi wynikami.

FatSecret nie jest wymagany do działania MVP. Adapter można wyłączyć konfiguracją, a jego
użycie musi respektować limity, atrybucję oraz dozwolony zakres utrwalania danych.

## Konsekwencje

- podstawowy przepływ może działać wyłącznie z Open Food Facts i ręcznym dodawaniem,
- FatSecret może zwiększyć pokrycie bez sprzężenia domeny z konkretnym dostawcą,
- skuteczność każdego źródła mierzymy osobno,
- ręczne dodanie jest wspieranym przypadkiem użycia, a nie błędem technicznym,
- płatny polski katalog można później dołączyć jako kolejny adapter.
