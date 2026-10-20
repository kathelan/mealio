# Wspólna pamięć projektu

Trwałe ustalenia i usprawnienia przydatne agentom AI. Wpisy powinny mieć datę, krótki kontekst
oraz odnośnik do rozwiniętej notatki, jeśli istnieje. Nie zapisuj sekretów ani danych wrażliwych.

## Wnioski

- 2026-10-17 — Korekta zakresu przez użytkownika: migracja monorepo obejmuje
  porządkowanie struktury, builda, testów i standardów przy zachowaniu zachowania.
  Nie planuj nowych funkcji jako faz migracji. Product/nutrition/diary/mealparser
  wymagają oddzielnego zlecenia; katalog dodany w tym przebiegu został wycofany.

- 2026-10-17 — Preferencja użytkownika przy fazie 5: opcjonalne dane w domenie
  i kontraktach Java reprezentujemy przez Optional, a złożone obiekty tworzymy
  builderami z zachowaną walidacją. Nullable JSON/JPA mapujemy w adapterach.
  Szczegóły są w .ai/docs/engineering-standards.md i wspólnych instrukcjach AGENTS.md.
- 2026-10-17 — Migracja pakietów JPA: po przeniesieniu encji uruchamiaj
  `mvn clean verify`, aby pozostałe pliki `.class` nie wprowadzały starych encji
  do skanowania Hibernate. Dług architektoniczny zapisuj jako konkretne pary klas;
  ArchUnit widzi właściciela odziedziczonej metody, co może różnić się od typu w imporcie.
- 2026-10-17 — Opcjonalne kategorie recipe: `Set.of().contains(null)` rzuca NPE.
  Przed walidacją obecności null normalizuj opcjonalną kolekcję do zbioru,
  który obsługuje takie sprawdzenie; testuj oddzielnie pominięte pole i pustą kolekcję.

- 2026-10-17 — Bramka pokrycia jednostkowego korzysta wyłącznie z danych Surefire.
  Failsafe zapisuje oddzielny plik JaCoCo; wspólny plik pozwoliłby integracjom
  ukryć brakujące testy jednostkowe. Sprawdzaj bramki próbą negatywną i uruchamiaj
  polecenia workflow lokalnie przed uznaniem konfiguracji CI za zweryfikowaną.

- 2026-10-17 — Czas w testach HTTP/persystencji: Clock.systemUTC może mieć inną
  precyzję na macOS i Linuksie. Stosuj Clock.fixed z celowo niezerowymi nanosekundami
  i sprawdzaj oddzielnie czas HTTP oraz zapis zgodny z precyzją kolumny TIMESTAMP.
  Nie zakładaj, że odczyt z bazy zachowa wszystkie nanosekundy obiektu Instant.

- 2026-10-18 — Proces solo: review i QA to odrębne przebiegi agenta z dowodami
  przypiętymi do SHA; formalny self-approval GitHuba nie jest możliwy. Polityka
  w SDLC.md i rozszerzenia w .ai/skills/ zastępują wymaganie drugiej osoby,
  zachowując testy i CI. QA backendu wymaga dowodów HTTP/persystencji, a UI
  rzeczywistego przejścia i screenshotów. Treści trackera publikujemy po polsku,
  zachowując dokładne znaczniki techniczne. Przy aktualizacji skilli zachowuj
  lokalne rozszerzenia i sprawdzaj ich zgodność z nowymi bramkami.

- 2026-10-20 — Po sesji grill-me nie utożsamiaj sekcji dziennika z etykietą przepisu
  ani liczby porcji z końcową masą. Porcje są obowiązkowe, masa opcjonalna; checkbox
  pyta o ilość składnika na jedną porcję. Przepis pozostaje pojedynczym wpisem,
  a jego późniejsze zmiany nie przeliczają historii. Źródło:
  [zatwierdzony zakres](specs/2026-10-20-personal-mvp.md).

- 2026-10-20 — Projektując E01 właściciel potwierdził heksagonalność ze Springiem.
  Nowe porty i core application/domain pozostają niezależne od Spring/JPA/HTTP;
  Spring składa zależności i obsługuje transakcje przez adaptery infrastructure.
  Transakcja obejmuje cały przypadek użycia, nie pojedyncze repository; HTTP dostawcy
  pozostaje poza transakcją. Legacy nie refaktoryzujemy przy okazji.
  [Mapa fundamentów](specs/2026-10-20-e01-reusable-foundations.md).
- 2026-10-20 — Katalog produktów nie utożsamia nazwy z tożsamością: podobne nazwy
  mogą oznaczać różne marki lub własne pozycje z odrębnym przelicznikiem (np. jajko).
  Wyniki wyszukiwania rozróżniają je przez markę, pochodzenie, kompletność i konwersję;
  GTIN pozostaje unikalny. [SDD katalogu](specs/2026-10-20-e01-product-catalog.md).
- 2026-10-20 — E01: sklep jest ręcznie wpisywaną, wielowartościową metadaną produktu,
  odrębną od marki i pochodzenia danych. GTIN pozostaje unikalny; korekta tworzy
  niezmienną wersję z pochodzeniem per pole, a błąd dostawcy nie blokuje ręcznego wpisu.
  [Decyzje E01](specs/research/decisions/2026-10-20-e01-product-corrections.md).
