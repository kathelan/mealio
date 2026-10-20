# Mealio MVP — ustalenia i pomysły

Data zebrania: 2026-10-20. Zestawienie dokumentów, nie transkrypcja warsztatów.

## Problem i wartość produktu — ustalone

Mealio jest dziennikiem żywieniowym dla iOS. Umożliwia zapisanie zjedzonych produktów
w wybranym dniu oraz uzyskanie powtarzalnej sumy kalorii, białka, tłuszczu i węglowodanów.
Szybkość dodania oraz wiarygodność danych to kierunki wynikające z opisu produktu;
nie ma tu jeszcze badań potwierdzających przewagę nad innymi aplikacjami.

Źródło: [MVP](../../../docs/mvp.md).

## Główne przepływy — ustalone

| Przepływ | Zachowanie i wartość |
|---|---|
| Ręczne dodanie | Wyszukanie produktu, wybór ilości i potwierdzenie wpisu. |
| Kod kreskowy | Lokalny katalog, następnie Open Food Facts, opcjonalnie FatSecret; przy braku produktu uzupełnienie etykiety. |
| Opis tekstowy | Parser proponuje produkty, ilości i jednostki; użytkownik poprawia i potwierdza wynik. |
| Dziennik dnia | Przegląd dni, dodanie, zmiana ilości, przeniesienie i usunięcie wpisu; sumy dnia. |

Dziennik ma sekcje: śniadanie, drugie śniadanie, obiad, podwieczorek, kolacja oraz
przekąski/inne. Potwierdzenie wpisu posiłku i zatwierdzenie nowego produktu zewnętrznego
są odrębnymi pojęciami: zatwierdzona lokalna wersja produktu nie wymaga ponownej
weryfikacji przy każdym użyciu. Propozycja tekstowa obejmująca wiele wpisów jest
zapisywana atomowo po potwierdzeniu.

Źródła: [MVP](../../../docs/mvp.md), [dziennik — ADR-0004](../../../docs/decisions/0004-daily-food-diary.md),
[potwierdzanie — ADR-0007](../../../docs/decisions/0007-external-product-confirmation.md).

## Reguły produktu — ustalone

- AI interpretuje opis, ale nie ustala kalorii ani makro. Wartości pochodzą z katalogu
  lub zweryfikowanego dostawcy; obliczenia są deterministyczne.
- Pierwsza iteracja przyjmuje gramy i mililitry. Sztuki i porcje wymagają jawnego
  przelicznika; brak przelicznika prowadzi do prośby o g/ml. Nie zakładamy `1 ml = 1 g`.
- Produkt bez kompletu makro może być użyty jako `INCOMPLETE`. Brak wartości nie oznacza
  zera; bilans `PARTIAL` nie może wyglądać jak pełny wynik dnia.
- Pierwszy produkt od dostawcy jest kandydatem do potwierdzenia i korekty. Pochodzenie
  i wersja danych są zachowane; zmiana katalogu nie nadpisuje historycznych wyliczeń.
- Brak produktu różni się od awarii i limitu dostawcy. Ręczne dodanie jest pełnoprawną ścieżką.
- Pierwsza iteracja działa bez kont i auth, lokalnie albo jako kontrolowane demo.
  Publiczne użycie przez wiele osób wymaga wcześniejszego wdrożenia tożsamości i izolacji.

Źródła: [MVP](../../../docs/mvp.md), [jednostki](../../../docs/decisions/0005-quantities-and-units.md),
[niepełne dane](../../../docs/decisions/0006-incomplete-nutrition.md),
[potwierdzanie](../../../docs/decisions/0007-external-product-confirmation.md),
[kolejność źródeł](../../../docs/decisions/0008-product-provider-fallback.md),
[odroczenie kont](../../../docs/decisions/0003-defer-identity.md).

## Pomysły na później i granice MVP

| Temat | Obecny status |
|---|---|
| Głos / ASR | Opcjonalne audio → tekst; nie interpretuje posiłków i nie jest wymagane dla przepływu tekstowego. |
| Sztuki, porcje, opakowania, łyżeczki i łyżki | Rozszerzenie po MVP, z przelicznikiem właściwym dla produktu. |
| OCR etykiety | Kierunek ułatwiający uzupełnianie produktu; ręczny formularz pozostaje wystarczającą ścieżką. |
| Kopiowanie dnia, cele żywieniowe, własne sekcje | Odłożone poza pierwszą iterację. |
| Konta: Google albo email i hasło | Po ustabilizowaniu podstawowego przepływu, przed publicznym użyciem przez wiele osób. |
| Przepisy i kategorie | Istnieją w systemie, lecz nie wyznaczają krytycznej ścieżki nowego MVP. |
| Płatny polski katalog / FatSecret Premier / GS1 | Opcjonalne kierunki zależne od pokrycia, kosztów i warunków wykorzystania danych. |
| Produkty generyczne bez kodu | Potrzebują odpowiedniego źródła danych; licencjonowana baza NIZP PZH jest kierunkiem do sprawdzenia. |
| Ceny i obserwacje sklepowe | Ceny to osobny model zależny od sklepu, lokalizacji i czasu, poza MVP. GTIN nie identyfikuje sklepu. |
| Rozpoznawanie jedzenia ze zdjęć | Poza zakresem; odrębne od OCR etykiety. |
| Plany dietetyczne, diagnozy i rekomendacje medyczne | Poza zakresem. |
| Społeczność i listy zakupów | Poza zakresem. |
| Płatności i subskrypcje | Poza MVP; model zarabiania nie został wybrany. |

Źródła: [MVP](../../../docs/mvp.md), [strategia danych](../../../docs/product-data-strategy.md),
[model domenowy](../../../docs/domain.md). Informacje o ofertach dostawców odtwarzają
stan zapisany w źródłach; nie stanowią aktualnej weryfikacji cen, licencji ani limitów.
