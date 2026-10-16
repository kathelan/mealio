# ADR-0005: Ilości, jednostki i przeliczniki porcji

## Status

Accepted — 2026-10-16

## Kontekst

Dostawcy danych żywieniowych najczęściej podają wartości na 100 g lub 100 ml. Użytkownik
docelowo chce wpisywać również sztuki, porcje, opakowania, łyżeczki i łyżki. Takie jednostki
nie mają uniwersalnego przelicznika na masę, a objętość nie jest równoważna masie bez gęstości.

## Decyzja

MVP przyjmuje ilości w `GRAM` i `MILLILITER`. Produkt określa podstawę swoich wartości jako
`PER_100_G` albo `PER_100_ML`. Obliczenia nie mieszają tych wymiarów.

Model przechowuje ilość wejściową oraz ilość bazową wykorzystaną w obliczeniu. Przyszłe
jednostki `PIECE`, `SERVING`, `PACKAGE`, `TEASPOON` i `TABLESPOON` będą obsługiwane przez
jawny `ServingConversion` należący do produktu lub jego opakowania.

Przykładowo produkt Sondey może definiować `1 PIECE = 17.5 GRAM` i
`1 PACKAGE = 70 GRAM`. Łyżeczka i łyżka mogą być mapowane na objętość, lecz przejście z ml
do g wymaga gęstości produktu. System nie zgaduje brakującego przelicznika.

Odpowiedź zewnętrznego API jest kandydatem. Adapter waliduje jednostkę, podstawę wartości,
masę opakowania i rozmiar porcji przed udostępnieniem przelicznika aplikacji.

## Konsekwencje

- pierwsza wersja ma prostą i jednoznaczną matematykę,
- późniejsze jednostki nie wymagają zmiany modelu dziennika,
- wpis zachowuje sposób podania ilości i wynik normalizacji,
- brak przelicznika powoduje prośbę o gramy lub mililitry zamiast cichego oszacowania,
- potrzebne są testy konwersji, precyzji i zaokrąglania.
