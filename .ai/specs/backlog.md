# Backlogs

## .ai/specs/product-brief.md

Backlog source: .ai/specs/product-brief.md
Last filed: 2026-10-20

Po sesji grill-me właściciel zatwierdził pełne drzewo i kryteria, a następnie osobno
zlecił utworzenie zadań SwiftUI. Zachowano #10–#17 i ich pełne identyfikatory;
dodano 4 stories oraz 6 zadań SwiftUI. Stan: 3 epiki, 9 stories, 6 zadań.
Kryteria akceptacji są w issues oraz w
[zatwierdzonym zapisie](research/2026-10-20-backlog-refresh-proposal.md).
Nowe issues utworzono workflow om-prepare-issue --no-spec --skip-dedupe;
om-backlog odświeżył istniejące treści i checklisty bez tworzenia duplikatów.

Po review #32/#33/#34 i odświeżeniu briefu #35 właściciel potwierdził „aktualizuj backlog”.
Zastosowano [synchronizację E01/E02/E03](research/2026-10-20-e01-review-backlog-sync.md):
11 istniejących opisów otrzymało linki do aktualnych SDD/map i zatwierdzone dopiski;
#24 zależy teraz także od #14 dla preview. Nadal jest 18 issues bez nowych numerów,
zmiany etykiet, rodziców lub oznaczania implementacji jako wykonanej.

E03 jest zaparkowany i nie uruchamia implementacji. Skan i UI są w E01/E02.
Q01 zamknięto przez D09/R06: porcje obowiązkowe, masa końcowa opcjonalna.
Kalkulator pozostaje częścią docelowego MVP, ale Q02 blokuje jego story do wyboru
wzoru i modelu aktywności. Q03 jest poza MVP, Q04 dotyczy tylko rozszerzonych danych.
E01 ma osobne SDD, a mapa E02 wymaga story-SDD przed implementacją. Kontrakty i migracje
weryfikują właściwe dostawy; ADR-0004 uzgodniono z zakresem, ADR-0005 wymaga uzgodnienia
PIECE przy implementacji. ADR-0003 pozostaje w mocy. SDLC nie ma odrębnej Definition of Ready;
om-setup-discovery-pipeline może dodać tę warstwę. Nie uruchomiono implementacji.

| Id | Issue | Title | Epic | Depends on | Adopted |
|---|---|---|---|---|---|
| E01 | [#10](https://github.com/kathelan/mealio/issues/10) | Produkty i obliczenia | — | — | no |
| E02 | [#11](https://github.com/kathelan/mealio/issues/11) | Potrawy wielokrotnego użytku i dziennik | — | E01 (#10) | no |
| E03 | [#12](https://github.com/kathelan/mealio/issues/12) | Późniejsze etapy — zaparkowane | — | — | no |
| E01-S01 | [#13](https://github.com/kathelan/mealio/issues/13) | Własny produkt, wyszukiwanie i przelicznik sztuk | #10 | none | no |
| E01-S02 | [#14](https://github.com/kathelan/mealio/issues/14) | Obliczenie kcal i makro dla ilości | #10 | E01-S01 (#13) | no |
| E01-S03 | [#20](https://github.com/kathelan/mealio/issues/20) | Zapamiętany produkt ze skanu kodu | #10 | E01-S01 (#13) | no |
| E02-S01 | [#15](https://github.com/kathelan/mealio/issues/15) | Przepisy, porcje i własne etykiety | #11 | E01-S01 (#13), E01-S02 (#14) | no |
| E02-S02 | [#16](https://github.com/kathelan/mealio/issues/16) | Własne sekcje dnia | #11 | none | no |
| E02-S03 | [#17](https://github.com/kathelan/mealio/issues/17) | Porcja przepisu i bilans dnia lub tygodnia | #11 | E02-S01 (#15), E02-S02 (#16) | no |
| E02-S04 | [#21](https://github.com/kathelan/mealio/issues/21) | Ilości składników przy dodawaniu przepisu | #11 | E02-S01 (#15), E02-S03 (#17) | no |
| E02-S05 | [#22](https://github.com/kathelan/mealio/issues/22) | Własna baza jako składnik przepisu | #11 | E02-S01 (#15), E01-S02 (#14) | no |
| E02-S06 | [#23](https://github.com/kathelan/mealio/issues/23) | Zapis pozycji dziennika jako przepisu | #11 | E02-S01 (#15), E02-S03 (#17) | no |
| E01-S01-T01 | [#24](https://github.com/kathelan/mealio/issues/24) | SwiftUI: wyszukiwanie i formularz własnego produktu | #10 | E01-S01 (#13), E01-S02 (#14) | no |
| E01-S03-T01 | [#25](https://github.com/kathelan/mealio/issues/25) | SwiftUI: skaner kodów i potwierdzenie produktu | #10 | E01-S03 (#20), E01-S01-T01 (#24) | no |
| E02-S01-T01 | [#26](https://github.com/kathelan/mealio/issues/26) | SwiftUI: biblioteka i edytor przepisów | #11 | E02-S01 (#15), E01-S01-T01 (#24) | no |
| E02-S03-T01 | [#27](https://github.com/kathelan/mealio/issues/27) | SwiftUI: dziennik dnia i tygodnia z własnymi sekcjami | #11 | E02-S03 (#17), E02-S02 (#16), E02-S01-T01 (#26), E02-S06 (#23) | no |
| E02-S04-T01 | [#28](https://github.com/kathelan/mealio/issues/28) | SwiftUI: parametry ilości i wariant przepisu przy dodawaniu | #11 | E02-S04 (#21), E02-S01-T01 (#26), E02-S03-T01 (#27) | no |
| E02-S05-T01 | [#29](https://github.com/kathelan/mealio/issues/29) | SwiftUI: wybór własnej bazy jako składnika potrawy | #11 | E02-S05 (#22), E02-S01-T01 (#26) | no |

Zadania `*-T01` mają w issue pole `Story:` i checklistę w nadrzędnej story.
