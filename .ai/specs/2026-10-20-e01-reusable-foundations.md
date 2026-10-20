# E01 — fundamenty wielokrotnego użytku dla backendu i SwiftUI

Status: szkielet specyfikacji, przed projektem szczegółowym.
Data: 2026-10-20. Właściciel: właściciel produktu.
Źródło: [issue #10](https://github.com/kathelan/mealio/issues/10),
[backlog](backlog.md), [zakres MVP](2026-10-20-personal-mvp.md)
oraz zlecenie właściciela, aby funkcjonalność E01 rozwijała fundamenty dla kolejnych obszarów.

## 📝 TLDR

Pierwszy przepływ produktów i obliczeń ma dostarczyć także podstawy, które kolejne
przepisy, dziennik i widoki SwiftUI wykorzystają ponownie. Planowanie obejmuje mapę
potrzeb z backlogu, właścicieli komponentów, granice zależności i sposób weryfikacji
reużycia. Poniższe pozycje są kandydatami do projektu, nie zatwierdzonymi klasami lub API.

## Open Questions

Q1. Czy rozdzielić dokument przekrojowy fundamentów backend/SwiftUI od osobnych wdrażalnych SDD funkcji E01 i ich UI? Rekomendacja: tak; wspólna mapa komponentów i zależności, osobne specyfikacje produktów, obliczeń, skanowania i klienta iOS, dostarczające potrzebne fundamenty wraz z pierwszą funkcją.

Pytanie dotyczy podziału dokumentów i odpowiedzialności dostaw, nie zasadności reużycia.
E01 obejmuje niezależnie wdrażalne możliwości, więc przed pełnym projektem trzeba
potwierdzić podział zgodnie z om-spec-writing. Nazwy typów, bibliotek i fizycznych
modułów zostaną ocenione dopiero po rozstrzygnięciu Q1.

## 📝 Problem Statement i stan istniejący

Właściciel chce rozwijać osobisty produkt bez powtarzania tych samych mechanizmów
w każdej funkcji. Backlog potwierdza wielokrotne użycie wyboru produktu, ilości,
obliczeń i edycji składników. To wymaganie projektowe właściciela; nie zmierzono
jeszcze kosztu duplikacji ani poprawy czasu implementacji.

Backend ma moduły category i recipe, małe publiczne API CategoryLookup i granice
sprawdzane testami architektury. Publiczny kontrakt innego modułu jest dopuszczonym
miejscem reużycia; jego domena i infrastruktura pozostają prywatne.

Punkty odniesienia (potwierdzone dwoma niezależnymi odczytami agentów):

- [Architektura](../../docs/architecture.md) i [ADR-0002](../../docs/decisions/0002-module-contracts.md).
- [Recipe MeasurementUnit](../../backend/src/main/java/com/mealio/recipe/domain/model/MeasurementUnit.java)
  jest dziś typem domeny recipe; sama obecność nie oznacza publicznego kontraktu całej aplikacji.
- [CategoryLookup](../../backend/src/main/java/com/mealio/category/api/CategoryLookup.java)
  pokazuje istniejący wzorzec małego kontraktu między modułami.
- Adaptery recipe/category mają osobne ApiErrorResponse — kandydat do analizy spójności
  transportu, bez automatycznego łączenia wyjątków i reguł domenowych.
- [SwiftUI](../../ios-prototype%203/MealioPrototype.swift) to lokalny prototyp z przykładowymi
  danymi i wyliczeniami. Współdzielenie MealioModel nie dowodzi gotowej architektury klienta API.

## 📝 Wstępna mapa potrzeb i kandydatów

| Powtarzalna potrzeba | Konsumenci z backlogu | Kandydat backendu / UI do dalszego projektu |
|---|---|---|
| Ilość, jednostka i przelicznik sztuki | #13, #14, #15, #21, #22; UI #24, #26, #28, #29 | Jawne typy ilości i konwersji po stronie właściciela reguły; pole ilości z jednostką i formatowaniem w SwiftUI |
| Kcal/makro i niekompletne dane | #14, #15, #17, #21, #22; UI #24, #26–#29 | Deterministyczne operacje nutrition; prezentacja wartości i niekompletności korzystająca z wyniku API |
| Wyszukanie i wybór produktu | #13, #15, #17, #20; UI #24–#28 | Publiczny odczyt katalogu; wybierak produktu działający w różnych kontekstach |
| Kod kreskowy i potwierdzanie kandydata | #20; UI #25, używane przez dziennik i edytor | Port dostawcy i rozróżnione wyniki odczytu; przepływ skanu oraz potwierdzenia zwracający wybór do wywołującego |
| Lista i edycja składników | #15, #21, #22, #23; UI #26, #28, #29 | Właściwe recipe przypadki użycia; kompozycja wiersza składnika, ilości i wyboru produktu |
| Odczyt/zapis przez HTTP | UI #24–#29 | Typowany klient API, mapowanie błędów i wstrzykiwana implementacja testowa; reguły żywieniowe pozostają na backendzie |
| Formularz, walidacja i stany operacji | #13, #15, #16, #20–#23; UI #24–#29 | Spójne błędy adapterów; pola formularza, błędy pól, ładowanie, brak wyników i ponowienie |
| Historia i rozdzielenie szablonu od spożycia | #15, #17, #21, #22 | Snapshot faktu spożycia i jawne kontrakty właścicieli; niezależny stan edycji w UI |
| Powtarzalne testy kontraktów i stanów | backend #13–#23; UI #24–#29 | Kontrolowane fixtures, stuby na granicach i scenariusze UI, z odrębną weryfikacją każdej reguły |

To wstępna klasyfikacja na podstawie wymagań. Konsumenci są planowani; dokument
nie twierdzi, że istnieją już w kodzie lub że pojedyncza abstrakcja obsłuży każdy przypadek.

## 📝 Kryteria dalszego projektu

Każda proponowana abstrakcja ma otrzymać: potrzeby i konsumujące issues,
właściciela, odpowiedzialność, publiczny kontrakt, kierunek zależności, alternatywę
bez wydzielenia oraz uzasadnienie reużycia lub testowej podmiany. Wskazujemy też,
kiedy zostaje wdrożona i jak kolejny przepływ użyje jej bez kopiowania kodu.

Wspólne komponenty SwiftUI są kompozycjami widoków i obsługi interakcji; backend
pozostaje właścicielem obliczeń. Reguły domenowe zachowują granice modułów.
Zmiany istniejących kontraktów wymagają OpenAPI, planu zgodności i nowych migracji,
gdy dotykają danych. Przegląd wykaże, co można już wykorzystać i co wymaga nowej decyzji.

Po Q1 pełny projekt uzupełni: mapę zależności, kontrakty, decyzje o rozmieszczeniu
pakietów i targetów, etapy wdrażania wraz z pierwszymi konsumentami oraz weryfikację.
Kalkulator celu, auth, AI, Garmin i współdzielenie pozostają poza tym zakresem.
Projekt wizualny i mockupy do akceptacji pozostają osobnym, nierozstrzygniętym tematem.
