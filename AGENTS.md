# Mealio — instrukcje dla agentów AI

## Cel produktu

Mealio to aplikacja iOS wspierana przez backend do rejestrowania jedzenia oraz obliczania kalorii i makroskładników. MVP obejmuje ręczne dodawanie produktów, skanowanie kodów kreskowych oraz zamianę opisu posiłku na ustrukturyzowane pozycje. ASR jest opcjonalnym wejściem `audio -> tekst`; nie interpretuje posiłków i nie oblicza kalorii.

Przed rozpoczęciem pracy przeczytaj dokumenty odnoszące się do zadania:

- `docs/mvp.md` — zakres produktu,
- `docs/architecture.md` — architektura i granice modułów,
- `docs/engineering-practices.md` — standardy implementacyjne i testowe,
- `docs/product-data-strategy.md` — źródła kodów kreskowych i danych żywieniowych,
- `docs/refactoring-plan.md` — kolejność porządkowania istniejącego kodu,
- `docs/decisions/` — zaakceptowane decyzje architektoniczne,
- README zmienianego modułu.

## Nienegocjowalne zasady

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

## Zasady zmian

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
