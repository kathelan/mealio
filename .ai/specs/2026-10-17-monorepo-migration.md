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
3. [x] PostgreSQL i powtarzalna weryfikacja (krok 7).
   - Testcontainers sprawdza migracje od pustej bazy, ograniczenia i persystencję PostgreSQL.
   - Wspólne polecenia i automatyzacja uruchamiają testy wszystkich modułów bez pomijania
     testów integracyjnych; `mvn verify` przechodzi z dostępnym Dockerem.
4. [ ] Struktura klienta SwiftUI w monorepo.
   - Przenieść prototyp do jednoznacznego katalogu iOS i utworzyć budowalny projekt Xcode.
   - Zachować zachowanie istniejącego prototypu. W tej migracji nie wdrażać
     nowych przepływów ani integracji HTTP klienta; kontrakty pozostają w OpenAPI.
   - Sprawdzić build klienta na dostępnym SDK; zaktualizować strukturę i instrukcje repozytorium.
## Granica zakresu — korekta 2026-10-17

Zadanie obejmuje wyłącznie migrację projektu i standardy implementacji.
Dodanie modułów product, nutrition, diary i mealparser jest rozwojem produktu,
a nie fazą migracji. Wymaga oddzielnego zlecenia i specyfikacji. Wcześniejsze
umieszczenie tych funkcji jako fazy 5 planu migracji było błędem zakresu.

Na polecenie użytkownika wycofano commit katalogu `11dbca4`: kod, jego migrację,
endpointy oraz zmiany OpenAPI. Specyfikacja katalogu trafiła do archive;
raport wdrożenia pozostaje oznaczony jako historyczny. Zachowano nowe standardy
w `.ai/docs/engineering-standards.md`, ich testy ArchUnit oraz osobny research.
Kolejnym zakresem migracyjnym pozostaje punkt 4, bez nowych funkcjonalności.

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

## Realizacja fazy 3 — plan techniczny

Baseline: faza 2, `mvn clean verify` przechodzi (67 testów).
Docker jest dostępny (29.2.1); PostgreSQL w testach używa wersji 17 jak Docker Compose.

1. Dodać zależności Testcontainers zarządzane przez istniejący BOM Spring Boot
   do backendu i ASR; testy PostgreSQL należą do standardowego `mvn test/verify`,
   brak Dockera powoduje błąd zamiast pomijania testów.
2. Sprawdzić migracje i Hibernate validate na pustych bazach, zapis i odczyt recipe,
   precyzję składników, unikalność kategorii i kaskadowe usuwanie powiązań.
   Osobny test ASR sprawdza zapis rekordu i migrację jego schematu.
3. Dodać `make verify` i CI z Java 25 oraz Dockerem uruchamiające pełny reaktor;
   udokumentować wymagania i polecenia. Nie zmieniać schematu ani OpenAPI.
4. Uruchomić pełne `mvn clean verify`, przejrzeć diff i zapisać wyniki oraz commit.

Faza 3 ukończona 2026-10-17: PostgreSQL/Testcontainers dla backendu i ASR,
`make verify` i workflow CI dodane. Pełne `mvn clean verify` poza sandboxem:
BUILD SUCCESS, 70 testów (48 backend, 11 ASR, 11 request-audit), bez błędów
ani pominięć; pokrycie linii backendu 86,57%. Rzeczywiste testy użyły PostgreSQL 17.11.
Migracje od pustych baz, Hibernate validate, powiązania recipe i kaskady przeszły.
CI sprawdzono lokalnie pod względem konfiguracji; nie uruchamiano zdalnego workflow.

Następna faza: 4 (iOS). Kontrola środowiska: `xcodebuild -version` zgłasza,
że aktywne Command Line Tools nie zawierają Xcode; `xcrun --sdk iphoneos --show-sdk-path`
zwraca `SDK "iphoneos" cannot be located`. Swift 6.4 jest dostępny, ale sam
kompilator nie pozwala zweryfikować builda aplikacji iOS. Faza 4 nie jest ukończona.

## Aktualny stan po korekcie

Fazy migracyjne 1–3 oraz standardy są zachowane. Nowe funkcje katalogu są wycofane.
Repozytorium wróciło do API i schematu sprzed ich implementacji. Research stacku
jest dokumentem rekomendacji i nie jest planem automatycznego wdrażania funkcji.
