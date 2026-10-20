# Mealio — biznesowe TODO

Data: 2026-10-20. Wszystkie zadania pozostają otwarte; kolejność jest propozycją organizacji
pracy, a nie zatwierdzonym harmonogramem. Właściciele i terminy: do ustalenia.

## Zadania wynikające z istniejących materiałów

| ID | TODO | Oczekiwany rezultat | Źródło |
|---|---|---|---|
| B01 | Rozszerzyć próbkę do co najmniej 100 produktów z Lidla, Aldi i Biedronki. | Zestawienie GTIN, zgodności produktu, kompletności makro i zgodności z fizyczną etykietą; wyniki osobno dla sklepów i dostawców. | [Eksperyment](../../../docs/product-data-experiment.md), [strategia](../../../docs/product-data-strategy.md) |
| B02 | Zmierzyć przyrost pokrycia FatSecret po braku wyniku w Open Food Facts. | Oddzielny wynik API po GTIN; obecność produktu na stronie WWW nie liczy się jako udany lookup. | [Eksperyment](../../../docs/product-data-experiment.md) |
| B03 | Zweryfikować warunki wykorzystania danych przed integracją i publikacją. | Ustalenia dotyczące cache, utrwalania, redystrybucji, atrybucji, zdjęć i łączenia katalogów; aktualne źródła warunków. | [Strategia](../../../docs/product-data-strategy.md) |
| B04 | Ocenić potrzebę płatnego katalogu polskiego lub GS1 po pomiarze pokrycia. | Porównanie kosztu, dostępności makro, przyrostu pokrycia i dozwolonego użycia; decyzja z uzasadnieniem. | [Strategia](../../../docs/product-data-strategy.md) |
| B05 | Wybrać źródło produktów generycznych i sprawdzić licencję. | Legalnie dostępny zestaw danych dla produktów bez kodu, z pochodzeniem i podstawą wartości. | [Strategia](../../../docs/product-data-strategy.md) |
| B06 | Oddzielić kontrolowane demo od przyszłego publicznego pilotażu. | Jawna decyzja o odbiorcach i danych demo; przed wieloma użytkownikami realizacja wymagań tożsamości i izolacji. | [ADR-0003](../../../docs/decisions/0003-defer-identity.md) |

## Propozycje badawcze do omówienia

Poniższe punkty dodano podczas porządkowania materiałów. Nie są ustaleniami warsztatowymi
ani dowodem potrzeb użytkowników.

| ID | Pytanie / TODO | Co zebrać, żeby podjąć decyzję |
|---|---|---|
| B07 | Dla kogo jest pierwsza wersja i w jakiej sytuacji użycia? | Wybrany segment, obecny sposób prowadzenia dziennika, przykłady trudności z ostatnich rzeczywistych posiłków. |
| B08 | Jaką konkretną trudność Mealio ma rozwiązać lepiej? | Porównanie rzeczywistych zadań wykonywanych dziś w innych aplikacjach lub ręcznie; powody porzucania zapisu. |
| B09 | Która ścieżka dodawania daje największą wartość? | Obserwacja ręcznego dodania, skanu i tekstu; czas do zatwierdzonego wpisu, korekty, przerwania oraz przyczyny. |
| B10 | Czy użytkownik rozumie niepewność i kompletność danych? | Próby z błędnym dopasowaniem, brakującym makro i nieznanym kodem; sprawdzenie interpretacji bilansu częściowego. |
| B11 | Jak kosztowny jest brak porcji i sztuk? | Obserwacje przy prośbie o g/ml; przypadki, w których brak przelicznika uniemożliwia zapis. Wynik może uzasadnić osobną zmianę zakresu. |
| B12 | Kiedy uznamy eksperyment MVP za sukces albo powód do zmiany kierunku? | Przed testem ustalić metryki, progi, czas obserwacji i regułę decyzji. Brak obecnie zatwierdzonych wartości liczbowych. |
| B13 | Jak dotrzeć do pierwszych testerów? | Plan rekrutacji, kanał dotarcia, warunki udziału i metoda zbierania problemów bez danych wrażliwych w repozytorium. |
| B14 | Jaki jest koszt obsługi kluczowego przepływu? | Założenia i pomiar kosztów katalogów, AI, opcjonalnego ASR oraz infrastruktury w przeliczeniu na użycie. |
| B15 | Czy i za jaką wartość ktoś zapłaci? | Badanie zachowania i ograniczeń obecnych rozwiązań; warianty monetyzacji z kosztami. Nie wdrażać subskrypcji w obecnym MVP. |
| B16 | Jakie warunki musi spełnić przyszła publiczna wersja iOS? | Osobna lista wymagań publikacji, prywatności, usuwania danych, wsparcia i bezpieczeństwa, zweryfikowana przed wydaniem. |

Proponowana kolejność: uzupełnienie zapisów warsztatowych → B07–B08 → pomiar B01–B05
oraz próby B09–B11 → kryteria B12 ustalone przed właściwym testem → decyzja o kolejnym
etapie. B06 i B16 wyznaczają warunki publicznego pilotażu. B14–B15 służą ocenie biznesowej,
nie rozszerzają automatycznie zakresu implementacji.

## Rejestr wykonania

Na razie nie przypisano właścicieli ani terminów i nie utworzono issues. Przy podjęciu
zadania zapisać: ID, właściciela, termin, status, odnośnik do materiału i decyzję wynikową.
Nie oznaczać zadania jako wykonanego na podstawie samego planu lub syntetycznej opinii.
