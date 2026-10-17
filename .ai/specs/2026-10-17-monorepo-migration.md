# Migracja Mealio do monorepo i modularnego monolitu

Data utworzenia: 2026-10-17. Status: w realizacji.

## Specyfikacja

Źródła: `AGENTS.md`, `README.md`, `docs/architecture.md`,
`docs/refactoring-plan.md`, ADR-0001–0003. Repozytorium już agreguje backend, ASR
i request-audit przez Maven. Migracja porządkuje istniejący kod oraz klienta SwiftUI;
nie wprowadza mikroserwisów dla domen ani kont użytkowników.

Zachowujemy Java 25, Spring Boot, PostgreSQL, Liquibase oraz wersjonowane OpenAPI.
Refaktoryzacja nie zmienia odpowiedzi HTTP, schematu ani reguł biznesowych.
ASR pozostaje opcjonalnym wejściem audio -> tekst. AI nie dostarcza wartości odżywczych.

## Fazy i kryteria ukończenia

1. [x] Zabezpieczenie i własność kategorii (kroki 1–3 planu refaktoryzacji).
   - Uruchomić baseline oraz uzupełnić testy charakteryzacyjne kategorii przed przenosinami.
   - Włączyć ArchUnit: czystość domen, niezależność aplikacji od adapterów,
     brak nowych zależności między wnętrzami modułów i cykli.
   - Przenieść encję oraz Spring Data repository do `category.infrastructure.persistence`;
     mapper już należy do właściciela. Usunąć zależność mappera od globalnego enum kategorii.
   - Jawnie ograniczyć wyjątki architektury do istniejących zależności recipe, health i BaseEntity;
     nie poszerzać listy podczas kolejnych faz.
   - Testy jednostkowe, HTTP, schematu od pustej bazy H2 i architektury oraz pełne
     `mvn clean verify` muszą przejść. OpenAPI i change sety pozostają identyczne.
2. [x] Domknięcie granic recipe/category (kroki 4–6).
   - Zabezpieczyć recipe testami, przenieść modele i persystencję do właściciela.
   - Wprowadzić małe publiczne API kategorii, usunąć zależności recipe od jej JPA,
     oddzielić DTO błędów recipe od kategorii, odwrócić zależności health,
     przenieść adaptery webowe do infrastruktury i usunąć zbędne globalne pakiety.
   - Zlikwidować wyjątki ArchUnit; zachować API i schemat; pełne `mvn verify` przechodzi.
3. [ ] PostgreSQL i powtarzalna weryfikacja (krok 7).
   - Testcontainers sprawdza migracje od pustej bazy, ograniczenia i persystencję PostgreSQL.
   - Wspólne polecenia i automatyzacja uruchamiają testy wszystkich modułów bez pomijania
     testów integracyjnych; `mvn verify` przechodzi z dostępnym Dockerem.
4. [ ] Klient SwiftUI w monorepo.
   - Przenieść prototyp do jednoznacznego katalogu iOS i utworzyć budowalny projekt Xcode.
   - Połączyć klienta z istniejącym API według OpenAPI, z konfiguracją adresu backendu;
     obliczenia i reguły żywieniowe pozostają w backendzie.
   - Sprawdzić build klienta na dostępnym SDK; zaktualizować strukturę i instrukcje repozytorium.
5. [ ] Moduły MVP (krok 8; osobne specyfikacje przed implementacją zachowania).
   - Kolejno wdrażać product, nutrition, diary i mealparser zgodnie z ADR-0004–0008.
   - Każdy przepływ ma testy, aktualne OpenAPI i integrację iOS; pełne bramki przechodzą.

## Weryfikacja i postęp

- Stan początkowy: brak wcześniejszych specyfikacji w `.ai/specs`.
- Zmiany użytkownika przed przebiegiem: `AGENTS.md` i `README.md`; zachowujemy je.
- Baseline `mvn verify` w sandboxie: testy ASR nie mogą otwierać lokalnych portów
  (`SocketException: Operation not permitted`). Ponowienie poza sandboxem: BUILD SUCCESS,
  48 testów (11 request-audit, 10 ASR, 27 backend).
- Weryfikacja PostgreSQL należy do fazy 3; H2 nie stanowi dowodu zgodności z PostgreSQL.
- Korekta po pierwszym uruchomieniu ArchUnit: istnieją również zależności
  `RecipeExceptionHandler -> category.ApiErrorResponse` oraz
  `HealthService -> HealthResponse/MealioApplicationProperties`. Rejestrujemy dokładne
  pary klas; ich usunięcie należy do fazy 2. Odwołanie `JpaRecipeRepository` do
  odziedziczonego `getId` wskazuje w bytecode na `BaseEntity`, nie encję kategorii.
- Faza 1 ukończona 2026-10-17: przeniesione encja i repository kategorii, mapper
  używa domenowego `CategoryType`; wszystkie importy użytkowników poprawione.
- Przed przenosinami `mvn -pl backend -am test`: BUILD SUCCESS, 31 testów backendu
  oraz 11 request-audit (w tym cztery nowe testy charakteryzacyjne).
- Pierwsze `mvn clean verify` wykryło dwie rozbieżności z zakładanym zestawem
  zależności. Lista wyjątków została dopasowana do rzeczywistego bytecode i uzupełniona
  o istniejący health; nie zmieniono zachowania ani progów jakości.
- Końcowe `mvn clean verify` z katalogu głównego poza sandboxem: BUILD SUCCESS,
  57 testów (11 request-audit, 10 ASR, 36 backend, w tym 5 architektonicznych),
  0 failures/errors/skipped. Bramka JaCoCo 80% przechodzi.
- `git diff --check`, kontrola odnośników lokalnych i brak zmian OpenAPI/change setów: OK.
- Następna do realizacji: faza 2. Cała specyfikacja pozostaje aktywna w `.ai/specs`,
  ponieważ pozostałe fazy nie są jeszcze wdrożone.

## Realizacja fazy 2 — plan techniczny

1. Zabezpieczyć serwis recipe testami jednostkowymi i usuwanie kategorii z przepisu
   testem HTTP; uruchomić je na dotychczasowej implementacji.
2. Przenieść modele i persystencję recipe do jego pakietów, adaptery HTTP do infrastruktury.
   Relację do kategorii mapować jako zbiór UUID przez istniejącą tabelę `recipe_categories`
   (ElementCollection), bez JPA encji kategorii ani zmiany schematu i kluczy obcych.
3. Udostępnić `category.api.CategoryLookup` zwracające istniejące identyfikatory;
   sprawdzanie kategorii przenieść do przypadku użycia recipe. Błędy odczytu mapować
   na dotychczasowy błąd persystencji recipe. Każdy moduł ma własną bazową encję.
4. Oddzielić DTO błędów i przenieść health do właściciela: aplikacja zwraca swój wynik,
   infrastruktura mapuje go na HTTP i konfiguruje serwis. Usunąć nieużywane modele legacy.
5. Usunąć wszystkie wyjątki ArchUnit, uruchomić pełne `mvn clean verify`, sprawdzić
   niezmienność OpenAPI i migracji, zapisać wynik i przygotować commit fazy 2.

Korekta planu przed przenosinami: testy bazowe wykryły NPE przy pominiętym
`categoryIds`. Zgodnie z zasadą oddzielania zmiany zachowania od refaktoryzacji
najpierw powstaje osobny commit według `implemented/2026-10-17-recipe-optional-categories.md`.
Testy HTTP usuwania kategorii oraz pozostałe testy charakteryzacyjne przeszły
na dotychczasowym mapowaniu JPA.

Faza 2 ukończona 2026-10-17: modele i persystencja recipe należą do właściciela,
adaptery HTTP do infrastruktury modułów, lookup kategorii korzysta z publicznego API.
Usunięto globalne modele legacy, wspólną encję i wszystkie wyjątki ArchUnit.
Health nie zależy w aplikacji od konfiguracji ani DTO HTTP.
`mvn clean verify`: BUILD SUCCESS, 67 testów (46 backend, 10 ASR, 11 request-audit),
bez błędów i pominięć. Próg JaCoCo 80% zachowany. OpenAPI i change sety bez zmian.
Następna faza: 3 (PostgreSQL/Testcontainers i automatyzacja pełnej weryfikacji).
