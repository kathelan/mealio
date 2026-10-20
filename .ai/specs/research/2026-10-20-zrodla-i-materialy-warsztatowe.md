# Źródła i materiały warsztatowe

Data przeglądu: 2026-10-20.

## Dostępne źródła

| Źródło | Co można z niego odtworzyć |
|---|---|
| [docs/mvp.md](../../../docs/mvp.md) | Cel produktu, trzy wejścia, dziennik, zakres i wyłączenia. |
| [docs/domain.md](../../../docs/domain.md) | Produkt, wpis spożycia, kompletność, jednostki i rozdzielenie przepisów od dziennika. |
| [ADR-0003](../../../docs/decisions/0003-defer-identity.md) | Odroczenie kont i warunki późniejszego publicznego użycia. |
| [ADR-0004](../../../docs/decisions/0004-daily-food-diary.md) | Dziennik dnia i atomowe potwierdzanie propozycji tekstowych. |
| [ADR-0005](../../../docs/decisions/0005-quantities-and-units.md) | g/ml w MVP, przyszłe jednostki i jawne przeliczniki. |
| [ADR-0006](../../../docs/decisions/0006-incomplete-nutrition.md) | Niepełne dane, częściowe sumy i zachowanie historii. |
| [ADR-0007](../../../docs/decisions/0007-external-product-confirmation.md) | Kandydat, korekta, potwierdzenie i brak produktu. |
| [ADR-0008](../../../docs/decisions/0008-product-provider-fallback.md) | Kolejność źródeł i opcjonalność FatSecret. |
| [Strategia danych](../../../docs/product-data-strategy.md) | Dostawcy, licencje do sprawdzenia, produkty generyczne i dalsze kroki. |
| [Eksperyment danych](../../../docs/product-data-experiment.md) | Pierwsza próbka Sondey i metoda dalszego pomiaru. |
| [Archiwum katalogu](../archive/2026-10-17-product-catalog.md) | Historyczny projekt; nie aktywne zlecenie implementacji. |
| [Korekta zakresu](../../runs/2026-10-17-migration-scope-correction.md) i [pamięć projektu](../../lessons.md) | Funkcje produktowe wymagają osobnego zlecenia; nie są częścią migracji monorepo. |

## Co rzeczywiście zbadano według zapisu

Eksperyment opisuje jeden produkt: Sondey, GTIN `4056489623182`, opakowanie 70 g.
Zapisano zgodność pięciu pól z etykietą: energii, tłuszczu, węglowodanów, błonnika i białka.
Nie ustalono kompletnej zgodności etykiety; nazwa wymaga normalizacji, a liczba sztuk
uzupełnienia. Wpis na stronie FatSecret nie potwierdza odczytu tego GTIN przez API.
To pojedyncza obserwacja, nie wynik pokrycia całego polskiego rynku ani dowód popytu.
Nie wykonano ponownego odczytu dostawców w ramach tego porządkowania dokumentacji.

## Brakujące materiały warsztatowe

W przeszukanych dokumentach `docs/` i `.ai/` nie znaleziono osobnych protokołów,
eksportów tablic ani transkrypcji warsztatów. W dostępnej rozmowie również ich nie ma.
Nie można więc potwierdzić kompletności wszystkich pomysłów z warsztatów.

Do uzupełnienia, jeśli istnieją:

- oryginalne notatki, eksporty tablic i odnośniki do materiałów,
- daty i tematy spotkań oraz kontekst poszczególnych decyzji,
- segmenty odbiorców, obserwacje i potrzeby poparte rzeczywistymi badaniami,
- rozważane alternatywy, odrzucone pomysły i uzasadnienia,
- decyzje biznesowe, priorytety, właściciele i terminy,
- ustalenia dotyczące dystrybucji, kosztów, monetyzacji i miar sukcesu.

Po otrzymaniu źródeł zachować ich pochodzenie, połączyć powtarzające się tematy,
uzupełnić rejestr pomysłów i TODO oraz jawnie opisać sprzeczności. Nie zastępować
oryginałów samym podsumowaniem i nie publikować w repozytorium danych wrażliwych.
