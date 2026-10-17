# Opcjonalne kategorie przepisu

Data: 2026-10-17. Status: wdrożona.

Testy charakteryzacyjne fazy 2 wykryły NPE dla `categoryIds == null`:
`Set.of().contains(null)` rzuca wyjątek podczas walidacji. OpenAPI dopuszcza
pominięcie kategorii. Przepis bez kategorii powinien powstać z pustym zbiorem,
zarówno przy pominiętym polu, jak i przy jawnej pustej kolekcji.

Zakres: poprawić tworzenie pustego zbioru przed walidacją i dodać regresję HTTP
oraz test serwisu. Bez zmian schematu i kontraktu OpenAPI. Poprawka stanowi osobny
commit przed refaktoryzacją fazy 2.

Weryfikacja: regresje serwisu i HTTP oraz pełne `mvn clean verify` przeszły;
OpenAPI i migracje pozostają bez zmian.
