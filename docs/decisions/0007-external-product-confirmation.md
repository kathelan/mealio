# ADR-0007: Potwierdzanie produktów zewnętrznych

## Status

Accepted — 2026-10-16

## Kontekst

Open Food Facts i przyszli dostawcy mogą zwracać błędną nazwę, wariant, gramaturę albo dane
żywieniowe. Jednocześnie wymaganie potwierdzenia przy każdym użyciu wcześniej sprawdzonego
produktu utrudniałoby prowadzenie dziennika.

## Decyzja

Wynik pobrany pierwszy raz z zewnętrznego źródła jest `ExternalProductCandidate`. Nie jest
jeszcze lokalnym produktem i nie może zostać zapisany do dziennika bez potwierdzenia.

Potwierdzenie:

1. pokazuje użytkownikowi nazwę, markę, opakowanie, podstawę oraz wartości odżywcze,
2. pozwala poprawić lub uzupełnić dane,
3. tworzy lokalny `Product` i `ProductVersion`,
4. zachowuje `ProductSourceSnapshot` i pochodzenie pól,
5. nadaje wersji status `USER_CONFIRMED` albo `INCOMPLETE`.

Zatwierdzona lokalna wersja może być później używana bez ponownego potwierdzenia. Odświeżenie
u dostawcy nie modyfikuje jej automatycznie. Nowe dane tworzą propozycję aktualizacji, jeśli
różnią się w polach istotnych dla identyfikacji lub obliczeń.

Cache HTTP lub cache odpowiedzi dostawcy jest szczegółem infrastruktury. Nie jest katalogiem
domenowym i nie oznacza potwierdzenia produktu.

Jeżeli poprawny GTIN nie zostanie znaleziony, użytkownik może utworzyć lokalny produkt na
podstawie etykiety. GTIN pozostaje wypełniony i zwalidowany. Produkt zawierający energię,
białko, tłuszcz i węglowodany dla jawnej podstawy otrzymuje `USER_CONFIRMED`; przy brakach
otrzymuje `INCOMPLETE`. Snapshot wskazuje źródło `USER_LABEL`.

Brak produktu (`NOT_FOUND`) jest oddzielony od awarii dostawcy (`PROVIDER_UNAVAILABLE`) i
limitu zapytań (`PROVIDER_RATE_LIMITED`). Awaria nie jest dowodem nieistnienia produktu.

## Konsekwencje

- pierwsze użycie produktu wymaga dodatkowego kroku,
- kolejne użycia są szybkie i niezależne od dostępności dostawcy,
- błędna aktualizacja źródła nie zmienia lokalnych ani historycznych obliczeń,
- API musi rozróżniać lokalny produkt, zewnętrznego kandydata i propozycję aktualizacji,
- nieznany produkt można dodać bez zależności od zewnętrznej bazy,
- potrzebne są testy potwierdzenia, korekty, idempotencji i konfliktu wersji.
