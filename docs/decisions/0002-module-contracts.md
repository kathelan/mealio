# ADR-0002: Publiczne kontrakty i komunikacja modułów

## Status

Accepted — 2026-10-16

## Kontekst

Obecny moduł `recipe` odwołuje się bezpośrednio do elementów persystencji kategorii. Takie połączenie omija reguły właściciela, utrudnia testowanie i wiąże cykle życia modułów.

## Decyzja

Moduł może udostępniać mały pakiet `api` z przypadkami użycia, stabilnymi typami odpowiedzi i zdarzeniami. Synchroniczne zapytania korzystają z tego API. Luźno powiązane efekty uboczne mogą korzystać ze zdarzeń aplikacyjnych.

DTO HTTP, encje JPA, Spring Data repositories i DTO zewnętrznych dostawców nie są publicznym kontraktem modułu. Wspólna baza danych nie upoważnia do dostępu do cudzej persystencji.

## Konsekwencje

- moduły nie omijają reguł aplikacyjnych właściciela,
- testy mogą zastępować publiczny port bez uruchamiania infrastruktury,
- kontrakty trzeba projektować świadomie i utrzymywać małe,
- operacje wymagające spójności między modułami muszą jawnie określić granicę transakcji lub model zdarzeniowy.
