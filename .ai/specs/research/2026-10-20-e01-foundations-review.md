# Review architektury fundamentów E01

Data: 2026-10-20. Zakres: mapa fundamentów i cztery wskazane SDD.
Przegląd zakresu wykonany przez świeżego agenta z samą ścieżką mapy jako kontekstem;
ponowny przegląd objął poprawki i doprecyzowanie heksagonalności przez właściciela.

## Werdykt

Dokumenty mają spójny zakres i wykonalne granice; po poprawkach brak pozostałych
blocker/major/minor w przeglądanym zakresie. Mapa nie jest planem wdrażania wielu
niezależnych możliwości w jednym PR; osobne SDD mają własne wyniki i zależności.

## Ustalenia pierwszego przejścia i zamknięcie

- Major: ProductDraft i correctedFields nie określały pełnego stanu, null i GTIN.
  Uzupełniono nazwany draft, pełny stan i dane tożsamości wyłącznie z kandydata.
- Major: sam hash body nie odróżniał innego candidateId. Tożsamość operacji obejmuje
  candidateId, expectedRevision i kanoniczny draft; dodano test innego kandydata.
- Minor: backendowe nadawanie USER_LABEL/USER_INPUT nie było jawne. Doprecyzowano.
- Minor: wyłączenie zakresu numerami obejmowało także realizowane #20/#24/#25.
  Zastąpiono je konkretną listą późniejszych konsumentów z jawnymi wyjątkami E01.
- Minor: limit parsera iOS nie ograniczał jawnie części całkowitej. Ujednolicono 6+6 cyfr.

## Heksagon i odpowiedzialności

ProductRegistration jest prywatnym mechanizmem walidacji/zapisu, używanym w transakcji
RegisterProduct lub szerszej ConfirmExternalProduct razem z receipt. Nie otwiera swojej
niezależnej transakcji. Spring wiring i dekoratory znajdują się w infrastructure,
nowe core/porty pozostają czyste; sieć dostawcy jest poza transakcją. Wersjonowane
snapshoty przecinają granice modułów, a encje/repozytoria pozostają prywatne.

## Ograniczenia

To review projektu dokumentów, nie działającego kodu. Nie wykonano builda Java/iOS,
testów nowych funkcji ani scenariuszy aparatu. Źródła techniczne i projekt to propozycje
tego PR, nie wdrożenie. Mockupy i finalny wygląd pozostają do osobnej akceptacji.
