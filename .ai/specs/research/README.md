# Mealio — materiały MVP i biznesowe TODO

Data zebrania: 2026-10-20. Status: kompilacja materiałów repozytorium, do uzupełnienia
oryginalnymi notatkami i eksportami z warsztatów.

## Materiały

- [Ustalenia i pomysły MVP](2026-10-20-mvp-ustalenia-i-pomysly.md) — zakres, przepływy,
  reguły i pomysły odłożone na później.
- [Biznesowe TODO](2026-10-20-biznesowe-todo.md) — pytania, zadania badawcze oraz warunki
  podjęcia kolejnych decyzji.
- [Źródła i braki warsztatowe](2026-10-20-zrodla-i-materialy-warsztatowe.md) — pochodzenie
  ustaleń, dostępne wyniki i materiały wymagające dostarczenia.

## Jak czytać i uzupełniać

- **Ustalone** oznacza zapis w dokumentacji produktu lub zaakceptowanym ADR, nie dowód wdrożenia.
- **Pomysł na później** oznacza temat odroczony albo poza zakresem pierwszej iteracji.
- **Propozycja badawcza** oznacza sugestię porządkującą dalszą pracę, nie wynik warsztatów.
- **Brak materiału** oznacza, że nie ma dostępnego źródła pozwalającego odtworzyć ustalenie.

To repozytorium badań nie zmienia zaakceptowanego zakresu ani nie stanowi zlecenia
implementacji. Źródłem zakresu pozostają [MVP](../../../docs/mvp.md) i
[ADR](../../../docs/decisions/). W razie rozbieżności należy najpierw uzgodnić decyzję
w dokumentacji źródłowej. Materiały badawcze pozostają w `research/`; specyfikacje
implementacyjne mają osobny cykl życia.

Nowy zapis warsztatu powinien zawierać datę, temat, odnośnik do źródła, obserwacje,
rozważone opcje, decyzje, pytania otwarte i dalsze zadania. Rozdzielaj wypowiedzi uczestników
od interpretacji. Nie zapisuj danych osobowych, nagrań ani sekretów. Nie uzupełniaj braków
wymyślonymi cytatami, personami lub wynikami badań.

## Aktualizacja po sesji grill-me

- [Decyzje osobistego MVP](decisions/2026-10-20-personal-mvp.md) zastępują wskazane
  wcześniejsze ustalenia. Starsze kompilacje materiałów pozostają zapisem historycznym.
- [Zatwierdzone odświeżenie backlogu](2026-10-20-backlog-refresh-proposal.md) zawiera
  kryteria opublikowanych stories oraz zlecone osobno zadania SwiftUI. Numery są w mapie backlogu.

## Fundamenty techniczne E01

- [Mapa fundamentów](../2026-10-20-e01-reusable-foundations.md) odsyła do czterech
  osobnych SDD i przypisuje komponenty do właścicieli i konsumentów z backlogu.
- [Badania](2026-10-20-e01-foundations-research.md) i
  [review](2026-10-20-e01-foundations-review.md) opisują źródła oraz granice weryfikacji.
  To projekt do review, bez implementacji i bez zatwierdzania mockupów.
