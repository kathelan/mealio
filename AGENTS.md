# Mealio — instrukcje dla agentów AI

## Cel produktu

Mealio to aplikacja iOS z klientem napisanym w SwiftUI, wspierana przez backend Spring Boot w Javie oraz bazę PostgreSQL. Służy do rejestrowania jedzenia i obliczania kalorii oraz makroskładników. MVP obejmuje ręczne dodawanie produktów, skanowanie kodów kreskowych oraz zamianę opisu posiłku na ustrukturyzowane pozycje. ASR jest opcjonalnym wejściem `audio -> tekst`; nie interpretuje posiłków i nie oblicza kalorii.

## Stos i podział odpowiedzialności

- Klient iOS używa SwiftUI do widoków i interakcji. Reguły żywieniowe oraz źródła danych pozostają po stronie backendu; klient komunikuje się z nim przez wersjonowane HTTP API opisane w OpenAPI.
- Backend działa w Spring Boot i Javie. Jest modularnym monolitem podzielonym według domen biznesowych, z granicami i kierunkiem zależności opisanymi w `docs/architecture.md`.
- PostgreSQL jest trwałym magazynem danych. Migracje schematu prowadzi Liquibase, a każda tabela i migracja ma wskazanego właściciela domenowego.
- Wartości odżywcze i ich obliczenia pochodzą z katalogu lub zweryfikowanego dostawcy; model AI może interpretować tekst, ale nie ustala wartości odżywczych.
- Zmiany kontraktów API wymagają aktualizacji OpenAPI i dokumentacji. Klient iOS nie powinien odtwarzać reguł domenowych backendu.

## Praca z pamięcią i specyfikacjami AI

Repozytorium używa katalogu `.ai/` jako wspólnego, wersjonowanego miejsca na specyfikacje,
logi autonomicznych przebiegów i trwałe wnioski. Każdy agent AI pracujący w tym repozytorium
musi stosować poniższe zasady:

- `.ai/specs/` przechowuje specyfikacje SDD. Nową specyfikację zapisuj jako
  `.ai/specs/YYYY-MM-DD-slug.md`, z datą utworzenia i krótkim, małymi literami slugiem
  rozdzielonym myślnikami. Specyfikacje wdrożone przenoś do `.ai/specs/implemented/`, a
  nieaktualne lub porzucone do `.ai/specs/archive/`; zachowaj pierwotną nazwę pliku.
- `.ai/runs/` przechowuje jeden raport dla każdego autonomicznego przebiegu AI. Zapisuj go
  jako `.ai/runs/YYYY-MM-DD-slug.md`. Raportuj cel, wykonane działania, zmienione pliki,
  weryfikację i jej wynik oraz otwarte kwestie. Nie zapisuj sekretów, danych osobowych,
  nagrań ani wrażliwych danych. Nie twórz raportu dla zwykłej, krótkiej pomocy konwersacyjnej;
  zapisuj przebiegi autonomiczne i zadania wykonywane przez agenta w repozytorium.
- `.ai/lessons.md` jest indeksem i krótką pamięcią wspólną, a `.ai/lessons/` zawiera
  rozwinięte ustalenia. Po naprawie błędu, odkryciu trwałej reguły lub usprawnienia przydatnego
  w przyszłych zadaniach dopisz zwięzły wpis z datą i kontekstem do `lessons.md`; większe
  wnioski zapisz w `.ai/lessons/YYYY-MM-DD-slug.md` i podlinkuj z indeksu. Nie zapisuj tam
  jednorazowych szczegółów ani sekretów.
- Przed rozpoczęciem zadania przejrzyj `.ai/lessons.md` oraz pasujące notatki z `.ai/lessons/`.
  Jeśli zadanie zmienia zachowanie lub zakres produktu, najpierw utwórz albo zaktualizuj
  specyfikację SDD, a potem realizuj ją; po wdrożeniu przenieś ją do `implemented/`.
- Przy złożonym zadaniu najpierw rozpisz krótki, etapowy plan oparty na specyfikacji i
  kryteriach ukończenia; jeśli implementacja ujawni błędne założenie lub nieoczekiwane
  przeszkody, wróć do planu i skoryguj go przed dalszymi zmianami.
- Na końcu autonomicznego przebiegu utwórz raport w `.ai/runs/` oraz zaktualizuj pamięć,
  jeśli pojawiły się trwałe wnioski. Gdy dana kategoria nie ma nowego materiału, nie dodawaj
  sztucznego wpisu.
- Po korekcie użytkownika lub naprawie błędu oceń, czy przyczyna wskazuje na trwałą regułę;
  jeśli tak, zapisz ją w `.ai/lessons.md` lub notatce w `.ai/lessons/`, zamiast powielać ją
  w wielu instrukcjach. Reguły ogólne dopisuj w `AGENTS.md` tylko wtedy, gdy dotyczą pracy
  wszystkich agentów w repozytorium.
- Nie nadpisuj istniejących specyfikacji, raportów ani lekcji bez potrzeby. Sprawdź istniejące
  nazwy i zawartość, aby uniknąć kolizji; w razie konfliktu użyj kolejnego czytelnego sluga.

Pliki `CLAUDE.md` i `AGENTS.md` opisują tę samą politykę. W razie rozbieżności należy je
uzgodnić, zachowując bardziej szczegółową regułę repozytorium.

Przed rozpoczęciem pracy przeczytaj dokumenty odnoszące się do zadania:

- `docs/mvp.md` — zakres produktu,
- `docs/architecture.md` — architektura i granice modułów,
- `.ai/docs/engineering-standards.md` — standardy implementacyjne i testowe,
- `docs/product-data-strategy.md` — źródła kodów kreskowych i danych żywieniowych,
- `docs/refactoring-plan.md` — kolejność porządkowania istniejącego kodu,
- `docs/decisions/` — zaakceptowane decyzje architektoniczne,
- README zmienianego modułu.

## Nienegocjowalne zasady

- Brak opcjonalnej wartości w domenie i kontraktach Java reprezentuj przez `Optional`,
  zamiast propagować `null` i pisać ręczne null-checki. Mapuj nullable JSON/JPA na
  `Optional` na granicy adaptera; pola encji JPA i format JSON zachowują typy właściwe
  dla używanej technologii. Wymagane dane nadal waliduj jawnie.
- Preferuj buildery dla obiektów z wieloma polami: agregatów, komend, DTO i encji.
  Builder musi prowadzić przez walidację niezmienników. Dla prostych obiektów wartości
  preferuj nazwane fabryki; konstrukcja wyjątków i obiektów frameworka nie wymaga buildera.
- Stosuj SOLID pragmatycznie; nie twórz abstrakcji bez granicy, wymiennej implementacji lub korzyści testowej.
- Organizuj backend najpierw według domen biznesowych, a dopiero wewnątrz nich według `api`, `application`, `domain`, `infrastructure`.
- Domena nie zależy od Springa, JPA, HTTP, klientów zewnętrznych ani DTO transportowych.
- Moduł nie korzysta z encji, repozytoriów ani infrastruktury innego modułu. Komunikacja synchroniczna odbywa się przez jego publiczne `api`; efekty uboczne mogą być obsługiwane zdarzeniami.
- Kontrolery mapują transport i wywołują przypadki użycia; nie zawierają logiki biznesowej.
- Encje JPA i Spring Data repositories pozostają w infrastrukturze właściciela danych.
- AI interpretuje tekst, ale nie jest źródłem prawdy o wartościach odżywczych. Kalorie i makroskładniki pochodzą z katalogu lub zweryfikowanego dostawcy.
- Nie zapisuj sekretów, nagrań ani wrażliwych danych w repozytorium lub logach.
- Pierwsza iteracja nie ma użytkowników ani auth. Nie dodawaj technicznego użytkownika,
  atrap autoryzacji ani `userId` bez zmiany zaakceptowanej decyzji ADR-0003.

## Pokrycie testami jednostkowymi

Każda nowa funkcja musi mieć 100% pokrycia wykonywalnych linii kodu testami
jednostkowymi. Dotyczy to również zmienianego istniejącego kodu Java we wszystkich
modułach. Weryfikuje to bramka JaCoCo zasilana wyłącznie przez Surefire;
testy integracyjne nie mogą uzupełniać brakującego pokrycia jednostkowego.
Nie dodawaj wykluczeń klas ani nie obniżaj progu, aby ominąć tę zasadę.

## Zasady zmian

- Każdą zmianę w repozytorium realizuj na osobnej gałęzi i dostarczaj przez pull request.
  Nie twórz commitów bezpośrednio na `main`; commity przypisane do zadania zapisuj
  na jego gałęzi przed otwarciem lub aktualizacją pull requesta.
- Gdy wszystkie wymagane checki PR są zielone, scal go metodą squash, chyba że użytkownik
  wskaże inną strategię. Nie scalaj PR-a z niezaliczonymi wymaganymi checkami.

Przed zmianą:

1. Sprawdź `git status` i zachowaj istniejące zmiany użytkownika.
2. Przeczytaj kod, kontrakty i testy zmienianego obszaru.
3. Uruchom testy bazowe adekwatne do zakresu.
4. Przy refaktoryzacji dodaj brakujące testy charakteryzacyjne przed przenoszeniem kodu.

W trakcie:

- Nie łącz czystej refaktoryzacji ze zmianą zachowania, API lub schematu danych.
- Wprowadzaj małe, kompilowalne kroki.
- Nie zmieniaj zastosowanych migracji Liquibase; dodaj kolejną migrację.
- Nie osłabiaj testów ani progów jakości, aby uzyskać zielony build.
- Nie wykonuj szerokiego porządkowania niezwiązanego z zadaniem.

Po zmianie:

1. Uruchom testy jednostkowe zmienionego modułu.
2. Uruchom jego testy integracyjne i architektoniczne.
3. Z katalogu głównego uruchom `mvn verify`.
4. Zaktualizuj OpenAPI i dokumentację, jeśli zmienił się kontrakt lub zachowanie.
5. Przejrzyj diff i opisz nieweryfikowane elementy oraz ryzyka.

Dobierz weryfikację do zakresu: dla zmian kodu uruchom odpowiednie testy i wymagane bramki
projektu; dla zmian dokumentacji sprawdź spójność instrukcji, odnośników i diffu. Nie uznawaj
zmiany za zweryfikowaną na podstawie samej inspekcji, jeśli wymagane testy nie zostały wykonane.

Nie deklaruj ukończenia, jeżeli wymagane testy nie zostały uruchomione. Podaj wtedy dokładne polecenie, błąd i przyczynę.

## Definition of Done

Zmiana jest ukończona, gdy:

- zachowuje granice modułów i kierunek zależności,
- przypadki użycia oraz reguły domenowe mają testy,
- odpowiednie testy integracyjne przechodzą,
- pełne `mvn verify` przechodzi,
- kontrakt OpenAPI i dokumentacja są aktualne,
- migracje są bezpieczne dla istniejących danych,
- w repozytorium nie ma sekretów ani przypadkowych zmian.
