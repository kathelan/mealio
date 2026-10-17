# Wspólna pamięć projektu

Trwałe ustalenia i usprawnienia przydatne agentom AI. Wpisy powinny mieć datę, krótki kontekst
oraz odnośnik do rozwiniętej notatki, jeśli istnieje. Nie zapisuj sekretów ani danych wrażliwych.

## Wnioski

- 2026-10-17 — Korekta zakresu przez użytkownika: migracja monorepo obejmuje
  porządkowanie struktury, builda, testów i standardów przy zachowaniu zachowania.
  Nie planuj nowych funkcji jako faz migracji. Product/nutrition/diary/mealparser
  wymagają oddzielnego zlecenia; katalog dodany w tym przebiegu został wycofany.

- 2026-10-17 — Preferencja użytkownika przy fazie 5: opcjonalne dane w domenie
  i kontraktach Java reprezentujemy przez Optional, a złożone obiekty tworzymy
  builderami z zachowaną walidacją. Nullable JSON/JPA mapujemy w adapterach.
  Szczegóły są w .ai/docs/engineering-standards.md i wspólnych instrukcjach AGENTS.md.
- 2026-10-17 — Migracja pakietów JPA: po przeniesieniu encji uruchamiaj
  `mvn clean verify`, aby pozostałe pliki `.class` nie wprowadzały starych encji
  do skanowania Hibernate. Dług architektoniczny zapisuj jako konkretne pary klas;
  ArchUnit widzi właściciela odziedziczonej metody, co może różnić się od typu w imporcie.
- 2026-10-17 — Opcjonalne kategorie recipe: `Set.of().contains(null)` rzuca NPE.
  Przed walidacją obecności null normalizuj opcjonalną kolekcję do zbioru,
  który obsługuje takie sprawdzenie; testuj oddzielnie pominięte pole i pustą kolekcję.

- 2026-10-17 — Bramka pokrycia jednostkowego korzysta wyłącznie z danych Surefire.
  Failsafe zapisuje oddzielny plik JaCoCo; wspólny plik pozwoliłby integracjom
  ukryć brakujące testy jednostkowe. Sprawdzaj bramki próbą negatywną i uruchamiaj
  polecenia workflow lokalnie przed uznaniem konfiguracji CI za zweryfikowaną.
