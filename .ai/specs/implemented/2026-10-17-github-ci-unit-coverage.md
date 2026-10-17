# CI i pokrycie jednostkowe Java

Data: 2026-10-17. Zakres: infrastruktura jakości, bez funkcji produktu.

## Kryteria

Każdy PR uruchamia oddzielne sprawdzenia build, lint, unit i integration.
JUnit Jupiter, Mockito i AssertJ pozostają frameworkiem jednostkowym.
Failsafe uruchamia testy integracyjne; PostgreSQL 17 przez Testcontainers
jest wymagany, a testy uruchamiają serwisy na losowych portach.
JaCoCo mierzy oddzielnie testy jednostkowe i integracyjne. Każdy moduł Java
musi osiągać 100% pokrycia wykonywalnych linii w testach jednostkowych;
raport zawiera także gałęzie. Brak własnych wykluczeń klas z pomiaru.
Kod generowany jest filtrowany standardowym mechanizmem JaCoCo.
AGENTS.md wymaga tego także dla każdej nowej funkcji.

## Fazy

- [x] 1. Wspólna konfiguracja Maven, lint, Surefire/Failsafe, JaCoCo i workflow.
  Ukończenie: oddzielne joby PR, raporty i komendy lokalne; kompilacja i lint przechodzą.
- [x] 2. Uzupełnienie jednostkowych testów istniejącego kodu i testów aplikacji z PostgreSQL.
  Ukończenie: 100% linii każdego modułu z samych unit tests; działające testy HTTP na losowym porcie.
- [x] 3. Weryfikacja wszystkich poleceń CI, próby negatywne lint/pokrycia,
  pełne clean verify, przegląd diffu i raport.
  Ukończenie: polecenia CI przechodzą lokalnie; raport rozróżnia lokalny przebieg od GitHub Actions.

## Realizacja

Root POM staje się wspólnym parentem zachowując Spring Boot 4.1.0/Java 25.
Checkstyle kontroluje podstawowe, jednoznaczne reguły bez zmiany formatowania całego repo.
Testy *IntegrationTest są wyłączone z Surefire i włączone w Failsafe.
Weryfikacja ma nie korzystać z sekretów, płatnych dostawców ani lokalnych baz.

Źródła: [Failsafe](https://maven.apache.org/surefire/maven-failsafe-plugin/),
[JaCoCo](https://www.jacoco.org/jacoco/trunk/doc/check-mojo.html),
[Checkstyle Maven](https://maven.apache.org/plugins/maven-checkstyle-plugin/usage.html).

### Ustalenie z implementacji

Raport linii ujawnił redundantny końcowy warunek `bodyOmittedReason` oraz
zewnętrzny catch `saveAll`: metoda `save` już mapuje każdy DataAccessException.
Najpierw dodajemy testy charakterystycznych wyników i propagacji błędów,
następnie usuwamy nieosiągalne ścieżki, bez zmiany publicznego zachowania.

## Wynik

Wszystkie fazy ukończone. Cztery polecenia workflow przeszły lokalnie:
build, lint, unit i pełne integration/verify. Actionlint 1.7.12: PASS.
Kontrolne naruszenia Checkstyle i niepokryte linie powodują oczekiwany błąd builda;
pliki próbne usunięto przed końcowym pełnym przebiegiem.

Końcowy `mvn clean verify`: 83 testy jednostkowe (20 audit, 14 ASR, 49 backend,
w tym 7 architektonicznych), 29 integracyjnych (8 ASR, 21 backend), bez błędów
ani pominięć. PostgreSQL 17.11 uruchomiony przez Testcontainers;
backend i ASR odpowiedziały HTTP 200 z losowych portów.
Pokrycie jednostkowe linii: audit 416/416, ASR 173/173, backend 430/430.
Pokrycie gałęzi jest raportowane oddzielnie, nie deklarujemy dla niego 100%.
Workflow nie został uruchomiony na GitHubie; wymaga wysłania zmian.
Szczegóły: `.ai/runs/2026-10-17-github-ci-unit-coverage.md`.
