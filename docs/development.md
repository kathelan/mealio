# Notatki deweloperskie

## Sposób pracy

- utrzymuj małą bazę kodu, dopóki pierwszy przypadek użycia nie działa end-to-end
- dokumentuj decyzje raz, zwięźle, i aktualizuj je
- preferuj jedno zwarte źródło prawdy w `docs/`

## Stan historyczny

1. zainicjalizować projekt backendu
2. dodać lokalne środowisko Docker i PostgreSQL
3. udostępnić sondy stanu
4. dodać persystencję przepisów i konfigurowalnych drzew kategorii
5. dodać przypadki użycia, porty, adaptery i endpointy REST kategorii — gotowe
6. dodać przypadki użycia, adaptery persystencji, endpointy REST i testy przepisów — gotowe
7. opublikować API kategorii, przepisów i składników jako `docs/openapi/backend.yaml` — gotowe
Pierwsze siedem kroków utworzyło obecną bazę kodu. Dalszy plan został zastąpiony przez
[plan modularizacji](refactoring-plan.md), [aktualne MVP](mvp.md) i
[strategię danych produktowych](product-data-strategy.md). Parser oraz resolver powstają
początkowo jako moduły modularnego monolitu, a nie osobne usługi REST.

## Polecenia

```bash
make verify
```

Wymagane: Java 25 i Maven; dla integracji także działający Docker.
Root POM jest wspólnym parentem trzech modułów Java.

| Sprawdzenie CI | Polecenie z katalogu głównego |
| --- | --- |
| Build Java | `mvn --batch-mode --no-transfer-progress clean package -DskipTests` |
| Lint Java | `mvn --batch-mode --no-transfer-progress validate` |
| Unit tests and 100% coverage | `mvn --batch-mode --no-transfer-progress clean verify -DskipITs` |
| Integration tests PostgreSQL | `mvn --batch-mode --no-transfer-progress clean verify -Pintegration-tests -DskipUnitTests` |

Workflow `.github/workflows/mealio-ci.yml` wykonuje cztery obowiązkowe joby dla każdego PR
oraz push zawierającego pliki inne niż Markdown. Zmiany wyłącznie w plikach `.md`
omijają build, lint i testy; wymagane checki są wtedy raportowane jako pominięte, czyli
zaliczone przez GitHub. Job wykrywający zmienione ścieżki uruchamia pełne joby, jeśli sam
nie może wiarygodnie wykryć zakresu zmian. Opcjonalny skan SonarQube jest częścią joba unit.
Nazwy checków to `Build (Java)`, `Lint (Checkstyle)`, `Unit tests (Java, 100% lines)`
i `Integration tests (PostgreSQL/Testcontainers)`. W ustawieniach ochrony gałęzi
GitHub należy wskazać te nazwy obowiązkowych jobów
jako wymagane sprawdzenia. Repozytorium ma aktywny ruleset `Protect main`, który
egzekwuje pull request, linear history, te cztery checki oraz blokuje force-push
i usunięcie `main`.
Prototyp SwiftUI nie ma projektu Xcode ani targetu do budowania; CI obejmuje
istniejące aplikacje Java. Build iOS wymaga osobnego etapu migracji klienta.
TODO: po dodaniu projektu Xcode i uruchamialnego targetu iOS dodać do CI testy
end-to-end XCUITest na symulatorze dla głównych ścieżek użytkownika aplikacji.

JUnit Jupiter + Mockito + AssertJ służą do testów jednostkowych. Surefire uruchamia
`*Test`, z wyłączeniem `*IntegrationTest`. Testy jednostkowe nie wymagają Dockera ani
połączeń sieciowych. ArchUnit sprawdza granice modułów w tej samej fazie.
Profil Maven `integration-tests` uruchamia Failsafe dla `*IntegrationTest` w fazach integration-test/verify.
Profil jest aktywny domyślnie, więc standardowe `mvn verify` nadal uruchamia pełny zestaw.
W jobie CI dla integracji Surefire pomija unit tests przez `-DskipUnitTests`.
Testy
PostgreSQL 17 korzystają z Testcontainers, migracji Liquibase i Hibernate validate;
każda usługa startuje na losowym porcie i udostępnia rzeczywisty HTTP health check.
Nie korzystają z lokalnej bazy Compose. Brak Dockera przerywa integracje, bez pomijania testów.

JaCoCo wymaga **100% wykonywalnych linii** w każdym module Java, z danych wyłącznie
Surefire. Raporty zawierają także pokrycie gałęzi, dla którego nie ustalamy progu.
Kod generowany jest filtrowany standardowo przez JaCoCo; nie stosujemy wykluczeń klas.
Wyniki: `MODUŁ/target/site/jacoco-unit/index.html` i `jacoco-integration/index.html`,
JUnit XML: `target/surefire-reports/` i `target/failsafe-reports/`. CI zapisuje te
raporty jako artefakty także po błędzie.

`make test` uruchamia szybkie testy i raport; `make unit` także egzekwuje próg.
`make lint`, `make build` i `make integration` odpowiadają pozostałym jobom.
SonarQube wymaga `SONAR_HOST_URL`, `SONAR_PROJECT_KEY` oraz sekretu `SONAR_TOKEN`.
URL i klucz projektu można dodać jako Actions variables albo secrets. SonarQube
Cloud Automatic Analysis wykonuje analizę przez GitHub integration. Workflow
domyślnie pomija Maven scanner dla Cloud, bo tryby Automatic Analysis i CI
analysis kolidują. Aby przesłać coverage JaCoCo Maven scannerem, wyłącz Automatic
Analysis w ustawieniach projektu i ustaw `SONAR_CI_ANALYSIS=true` (Actions
variable lub secret) oraz `SONAR_ORGANIZATION`. Dla własnego SonarQube Server
Maven scanner uruchamia się po unit tests przy pushu i PR. Bez wymaganej
konfiguracji skan jest pomijany; po uruchomieniu błędy analizy blokują etap unit.
Skan używa już skompilowanych klas i raportu JaCoCo, więc nie uruchamia drugi raz
testów.
Dla pojedynczego modułu: `mvn -pl backend -am verify -DskipITs` (analogicznie `asr`).
Po przenoszeniu klas albo przy powielonych artefaktach lokalnych uruchom `make verify`
z czyszczeniem. Nie uruchamiaj równocześnie buildów zapisujących ten sam `target`.

```bash
docker compose up --build
```

## Uwagi dotyczące wykorzystania tokenów

Dokumentacja repozytorium powinna być zwięzła. Aktualizuj istniejące pliki zamiast tworzyć
wiele równoległych notatek, aby kolejne sesje Codex mogły czytać mniej i robić więcej.
