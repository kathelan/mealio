# Wspólna pamięć projektu

Trwałe ustalenia i usprawnienia przydatne agentom AI. Wpisy powinny mieć datę, krótki kontekst
oraz odnośnik do rozwiniętej notatki, jeśli istnieje. Nie zapisuj sekretów ani danych wrażliwych.

## Wnioski

- 2026-10-17 — Migracja pakietów JPA: po przeniesieniu encji uruchamiaj
  `mvn clean verify`, aby pozostałe pliki `.class` nie wprowadzały starych encji
  do skanowania Hibernate. Dług architektoniczny zapisuj jako konkretne pary klas;
  ArchUnit widzi właściciela odziedziczonej metody, co może różnić się od typu w imporcie.
