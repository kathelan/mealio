# Synchronizacja po sesji grilling — dziennik, przepisy i cel kalorii

Data: 2026-10-20. Autoryzacja: właściciel zatwierdzał odpowiedzi i korekty w rozmowie,
następnie zlecił aktualizację briefu, tasków w issues i backlogu. Nie uruchomiono implementacji.
Źródło: [decyzje](decisions/2026-10-20-diary-and-recipe-details.md), brief D18–D24/R12–R18.
Brak odrębnej Definition of Ready w SDLC; Q02/Q05/Q06 pozostają jawne dla właściwych SDD.

## Plan i kryteria ukończenia

1. Zachować rozróżnienie decyzji, propozycji Q07 i otwartych kwestii; nie wybrać wzoru za właściciela.
2. Uzgodnić brief, zakres, mapy E02/E03, ADR-0004 i README; zachować historię wcześniejszych decyzji.
3. Sprawdzić tożsamości source/id i blokady; zaktualizować wyłącznie autoryzowane opisy.
4. Dodać task analizy celu bez gotowej story algorytmu; zachować istniejące numery i checklisty.
5. Zweryfikować odczyt z GitHuba, lokalne linki, Coverage, diff i niezależny przegląd; dostarczyć dokumenty przez PR.

## Całe drzewo po synchronizacji

| Id | Issue | Tytuł | Zmiana |
|---|---|---|---|
| E01 | [#10](https://github.com/kathelan/mealio/issues/10) | Produkty i obliczenia | bez zmian |
| E02 | [#11](https://github.com/kathelan/mealio/issues/11) | Potrawy wielokrotnego użytku i dziennik | potwierdzone kryteria/linki |
| E03 | [#12](https://github.com/kathelan/mealio/issues/12) | Późniejsze etapy — zaparkowane | potwierdzone kryteria/linki |
| E01-S01 | [#13](https://github.com/kathelan/mealio/issues/13) | Własny produkt, wyszukiwanie i przelicznik sztuk | bez zmian |
| E01-S02 | [#14](https://github.com/kathelan/mealio/issues/14) | Obliczenie kcal i makro dla ilości | bez zmian |
| E02-S01 | [#15](https://github.com/kathelan/mealio/issues/15) | Przepisy, porcje i własne etykiety | potwierdzone kryteria/linki |
| E02-S02 | [#16](https://github.com/kathelan/mealio/issues/16) | Własne sekcje dnia | potwierdzone kryteria/linki |
| E02-S03 | [#17](https://github.com/kathelan/mealio/issues/17) | Porcja przepisu i bilans dnia lub tygodnia | potwierdzone kryteria/linki |
| E01-S03 | [#20](https://github.com/kathelan/mealio/issues/20) | Zapamiętany produkt ze skanu kodu | bez zmian |
| E02-S04 | [#21](https://github.com/kathelan/mealio/issues/21) | Ilości składników przy dodawaniu przepisu | potwierdzone kryteria/linki |
| E02-S05 | [#22](https://github.com/kathelan/mealio/issues/22) | Własna baza jako składnik przepisu | potwierdzone kryteria/linki |
| E02-S06 | [#23](https://github.com/kathelan/mealio/issues/23) | Zapis pozycji dziennika jako przepisu | bez zmian |
| E01-S01-T01 | [#24](https://github.com/kathelan/mealio/issues/24) | SwiftUI: wyszukiwanie i formularz własnego produktu | bez zmian |
| E01-S03-T01 | [#25](https://github.com/kathelan/mealio/issues/25) | SwiftUI: skaner kodów i potwierdzenie produktu | bez zmian |
| E02-S01-T01 | [#26](https://github.com/kathelan/mealio/issues/26) | SwiftUI: biblioteka i edytor przepisów | potwierdzone kryteria/linki |
| E02-S03-T01 | [#27](https://github.com/kathelan/mealio/issues/27) | SwiftUI: dziennik dnia i tygodnia z własnymi sekcjami | potwierdzone kryteria/linki |
| E02-S04-T01 | [#28](https://github.com/kathelan/mealio/issues/28) | SwiftUI: parametry ilości i wariant przepisu przy dodawaniu | potwierdzone kryteria/linki |
| E02-S05-T01 | [#29](https://github.com/kathelan/mealio/issues/29) | SwiftUI: wybór własnej bazy jako składnika potrawy | potwierdzone kryteria/linki |
| E02-T01 | [#37](https://github.com/kathelan/mealio/issues/37) | Analiza i SDD celu kalorii aktualizowanego po zmianie masy | nowy task badawczy pod #11 |

## Wynik i granice

11 opisów zaktualizowano po ponownej kontroli source/id i braku aktywnych blokad;
każdy zapis potwierdzono odczytem pełnego body. Etykiety i stany istniejących issues
pozostały bez zmian. #37 ma documentation/priority-medium/risk-low: dotyczy wyłącznie
analizy/SDD. 19 otwartych issues: 3 epiki, 9 stories i 7 tasków. W epiku #11 dopisano
checklistę taska #37. Nie zmieniono dotychczasowych zależności ani zaznaczeń ukończenia.

Bulion pozostaje późniejszy, bazy sosów wymagają Q06, a aktualizacja masy nie zamyka Q02.
Kolejność sekcji ma status proposal. Nie zatwierdzono tabeli makr/średnich tygodnia ani
obowiązku mierzenia gotowej potrawy. Nie wprowadzono danych użytkownika lub kodu auth.

Niezależny przegląd dokumentów i draftów issues: brak blocker/major; poprawiono ciągłość
tabel briefu i odsyłacze z historycznego D11 do D23. Testów Java/iOS nie uruchamiano:
zmiany są wyłącznie dokumentacyjne i nie dowodzą działania planowanych funkcji.
