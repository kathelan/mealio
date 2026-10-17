# Plan modularizacji i refaktoryzacji

## Cel

Przekształcić obecny układ mieszający globalne warstwy i moduły domenowe w modularny monolit bez zmiany istniejącego kontraktu i zachowania.

## Baseline — 2026-10-16

Polecenie: `mvn verify` z katalogu głównego.

Wynik: `BUILD SUCCESS`, Java 25.0.2, Spring Boot 4.1.0.

| Moduł | Testy | Wynik |
|---|---:|---|
| request-audit | 11 | 0 failures, 0 errors |
| mealio-asr | 10 | 0 failures, 0 errors |
| mealio-backend | 27 | 0 failures, 0 errors |
| Razem | 48 | zielony baseline |

Backend spełnia obecny próg JaCoCo 80%. Testy integracyjne persystencji używają H2. [x] Build emituje ostrzeżenie o dynamicznym dołączaniu Mockito/Byte Buddy na Javie 25 oraz ostrzeżenie Lombok dotyczące `sun.misc.Unsafe`; nie są to obecnie błędy builda.

## Stan po fazie 2 — 2026-10-17

- Modele, encje i repozytoria należą do category lub recipe; usunięto globalne warstwy legacy.
- Recipe sprawdza kategorie wyłącznie przez `category.api.CategoryLookup`, a powiązania
  przechowuje jako UUID w istniejącej tabeli. Moduły mają własne bazowe encje.
- Adaptery HTTP znajdują się w infrastrukturze właściciela; recipe ma własne DTO błędu.
- Health zwraca wynik aplikacyjny, a konfiguracja i mapowanie HTTP należą do infrastruktury.
- ArchUnit kontroluje granice bez wyjątków migracyjnych.
- Pozostają szerokie porty `Manage*UseCase`; ich podział wymaga konkretnej potrzeby.
- Testy Testcontainers/PostgreSQL 17 dla backendu i ASR są częścią standardowej bramki.

## Kolejność prac

1. [x] Zabezpieczyć zachowanie `category` testami charakteryzacyjnymi logiki, błędów i HTTP.
2. [x] Dodać Spring Modulith lub ArchUnit i jawnie rejestrować istniejące naruszenia.
3. [x] Przenieść encję, mapper i Spring Data repository kategorii do `category.infrastructure.persistence` bez zmiany API i schematu.
4. [x] Zabezpieczyć testami `recipe`, przenieść jego model i persystencję do właściciela.
5. [x] Zastąpić dostęp `recipe` do persystencji kategorii małym publicznym API `category`.
6. [x] Usunąć pozostałości globalnych pakietów dopiero po migracji wszystkich użytkowników.
7. [x] Dodać testy PostgreSQL przez Testcontainers i zweryfikować migracje od pustej bazy.
8. Poza zakresem migracji: przyszłe moduły MVP `product`, `diary`, `nutrition`,
   `mealparser` wymagają oddzielnego zlecenia; nie są kolejnym krokiem tej migracji.

ASR pozostaje niezależnym wejściem audio i nie blokuje przepływu tekstowego.

Specyfikacja, fazy i wyniki bieżącej migracji:
[plan migracji monorepo](../.ai/specs/2026-10-17-monorepo-migration.md).

## Bramka każdego kroku

- testy zmienianego obszaru przechodzą przed i po,
- kontrakt OpenAPI nie zmienia się przypadkowo,
- migracje uruchamiają się od pustej bazy,
- liczba naruszeń architektury nie rośnie,
- pełne `mvn verify` przechodzi,
- diff nie zawiera niezwiązanych zmian.
