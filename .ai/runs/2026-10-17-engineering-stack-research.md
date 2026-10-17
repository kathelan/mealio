# Research stacku i standardów Mealio

Data: 2026-10-17.

## Cel

Niezależny research zlecony drugiemu agentowi: ocenić stos, standardy kodu,
praktyki inżynieryjne i wzorce dla Mealio z zachowaniem zaakceptowanych ADR.

## Wykonane działania i pliki

- Przeczytano pamięć projektu, dokumentację architektury, MVP, strategię danych,
  standardy, plan migracji, ADR-0001–0008, README backendu, Maven i CI.
- Sprawdzono stan git; zastane zmiany pozostałych agentów i użytkownika zachowano.
- Stwierdzono brak `.ai/docs/engineering-standards.md` w chwili rozpoczęcia pracy;
  użytkownik doprecyzował, że standardy są w docs. Agent nadrzędny przenosi
  `docs/engineering-practices.md` do nowej kanonicznej ścieżki; research zaktualizowano.
- Zbadano oficjalną dokumentację Spring, JDK, PostgreSQL, Apple, ArchUnit,
  Testcontainers, OpenAPI, Lombok, Maven, Checkstyle i GitHub.
- Utworzono `.ai/specs/2026-10-17-engineering-stack-research.md`:
  rekomendacje, alternatywy, Optional/buildery, kryteria wdrożenia i źródła.
- Utworzono ten raport. Nie zmieniano kodu ani istniejących instrukcji/specyfikacji.

## Weryfikacja

Dokumentacyjna: przegląd spójności z ADR, rozdzielenia stanu od rekomendacji,
obecności daty dostępu i oficjalnych odnośników oraz `git diff --check`.
Wynik: brak błędów białych znaków; sprawdzenia dokumentów zakończone.
Nie uruchamiano Maven: zadanie nie zmienia kodu, a agent nadrzędny niezależnie
weryfikuje swoją implementację. Research nie stanowi weryfikacji jej działania.

## Otwarte kwestie

- Przeniesienie kanonicznego dokumentu standardów należy do pracy agenta nadrzędnego.
- Nowe rekomendacje nie są wdrożonymi bramkami i wymagają osobnych kroków.
- Minimalny target iOS, wybór modelu parsera i potrzeba dodatkowych narzędzi
  pozostają decyzjami wdrożenia; research nie zastępuje ewaluacji ani pomiarów.
- Nie wykonywano commitów.
