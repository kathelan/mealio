# Wdrożenie standardów dla agentów

Data utworzenia i wdrożenia: 2026-10-17. Status: wdrożona.

Na polecenie użytkownika przeniesiono istniejące `docs/engineering-practices.md`
do kanonicznego `.ai/docs/engineering-standards.md`. W docs pozostaje odnośnik
zgodności; AGENTS.md wskazuje nowy plik, CLAUDE.md nadal odsyła do kanonicznego AGENTS.md.

Utrwalono preferencje użytkownika: Optional dla braku wartości w domenie
i kontraktach Java, mapowanie nullable JSON/JPA w adapterach, buildery dla złożonych
obiektów i nazwane fabryki dla value objects. Buildery chronią niezmienniki.
Nie wprowadzamy masowej refaktoryzacji niezmienianych modułów.

W aktualnie wdrażanym product zastosowano Optional, buildery Lombok rekordów i encji,
fabrykę Gtin.of, walidację w konstruktorach, osobne DTO i encje, przypadki użycia
z transakcjami, stabilne błędy i wersje oraz testy kontraktu i PostgreSQL.
Implementacja produktu i jego migracja są osobnym krokiem fazy 5.1.

ArchUnit dodatkowo egzekwuje constructor injection (zakaz @Autowired na polach)
oraz brak pól Optional w encjach JPA. Pozostałe reguły modułów pozostają bez wyjątków.

Kryteria ukończenia: standard ma jedno źródło, odnośniki są poprawne,
nowy kod stosuje preferencje, testy architektury oraz pełny build przechodzą.
Weryfikacja bieżącego drzewa wraz z product: mvn clean verify, 111 testów,
0 failures/errors/skipped, pokrycie backendu 88,76%. OpenAPI oraz dokumentacja
produktu aktualizowane są w jego osobnej specyfikacji.

## Korekta zakresu — 2026-10-17

Przykład implementacji product opisany powyżej został następnie wycofany jako
wykraczający poza migrację. Standardy Optional/builderów, kanoniczna lokalizacja
oraz testy ArchUnit pozostają wdrożone. Wynik 111 testów jest historyczny;
aktualne sprawdzenie opisuje raport migration-scope-correction.
