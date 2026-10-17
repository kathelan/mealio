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

Backend spełnia obecny próg JaCoCo 80%. Testy integracyjne persystencji używają H2. Build emituje ostrzeżenie o dynamicznym dołączaniu Mockito/Byte Buddy na Javie 25 oraz ostrzeżenie Lombok dotyczące `sun.misc.Unsafe`; nie są to obecnie błędy builda.

## Znane naruszenia docelowych granic

- Encja i Spring Data repository kategorii należą już do `category.infrastructure.persistence`;
  encja nadal dziedziczy po globalnym `BaseEntity` (dług do usunięcia przy porządkowaniu persystencji).
- `recipe` korzysta z modeli w globalnym `com.mealio.domain.model`.
- `recipe.infrastructure` odwołuje się bezpośrednio do encji oraz repository persystencji kategorii.
- Porty wejściowe są zgrupowane w szerokie interfejsy `Manage*UseCase`.
- ArchUnit kontroluje czystość domen, zależności aplikacji, dostęp do wnętrz modułów
  i cykle między modułami biznesowymi. Istniejące zależności recipe/globalnej encji recipe
  do kategorii oraz health do DTO HTTP i konfiguracji są jawnymi wyjątkami wskazującymi
  konkretne pary klas do usunięcia w kolejnej fazie.
- `RecipeExceptionHandler` korzysta z DTO błędu kategorii, a globalny `HealthService`
  tworzy DTO HTTP i korzysta z konfiguracji infrastruktury.
- Testy bazodanowe nie sprawdzają PostgreSQL.

## Kolejność prac

1. Zabezpieczyć zachowanie `category` testami charakteryzacyjnymi logiki, błędów i HTTP.
2. Dodać Spring Modulith lub ArchUnit i jawnie rejestrować istniejące naruszenia.
3. Przenieść encję, mapper i Spring Data repository kategorii do `category.infrastructure.persistence` bez zmiany API i schematu.
4. Zabezpieczyć testami `recipe`, przenieść jego model i persystencję do właściciela.
5. Zastąpić dostęp `recipe` do persystencji kategorii małym publicznym API `category`.
6. Usunąć pozostałości globalnych pakietów dopiero po migracji wszystkich użytkowników.
7. Dodać testy PostgreSQL przez Testcontainers i zweryfikować migracje od pustej bazy.
8. Implementować moduły MVP: `product`, `diary`, `nutrition`, `mealparser`.

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
