# E01 — review zgodności przepływu produktów, revision-003

Data: 2026-10-20. Poprzednia rewizja: [revision-002](../revision-002/README.md).
Brief: [product-brief](../../../../specs/product-brief.md).
Źródła: [R02/R03 i zakres](../../../../specs/2026-10-20-personal-mvp.md),
[E01-C1–C4](../../../../specs/research/decisions/2026-10-20-e01-product-corrections.md),
[katalog](../../../../specs/2026-10-20-e01-product-catalog.md),
[skaner](../../../../specs/2026-10-20-e01-barcode-resolution.md).

## Granica i zmiany

Uczestnik dodaje produkt do wybranej sekcji dziennika: wyszukuje lub skanuje, tworzy
własny produkt albo potwierdza kandydata, wybiera ilość i wraca z sumami. Dziennik pozostaje
kontekstowym hostem E02 #17/#27. Makieta nie rozszerza #24/#25 o implementację dziennika.
Poprzednie rewizje zachowano jako historię; zawierają rozbieżności znalezione w review,
więc aktualnym wzorcem prezentowania braków, korekty i potwierdzenia jest revision-003.

- Niepełny produkt można zapisać; puste pole to unknown, jawne zero pozostaje zerem.
- Sumy pokazują znane wartości i PARTIAL. Gdy wszystkie wartości pola są nieznane,
  pokazujemy „—”; pusty zbiór wpisów ma rzeczywiste zero.
- Produkt ma niezmienne wersje z pochodzeniem per pole. Korekta marki zachowuje źródła
  niezmienionych faktów. Dziennik utrwala kopię użytej wersji, a historia pokazuje v1/v2.
- Pierwsze potwierdzenie kandydata tworzy v1, korekta lokalnego produktu tworzy v2.
- Sklepy edytuje się osobno; wiele nazw nie tworzy nowej wersji żywieniowej.
- Nieznany kod, awaria i limit mają osobne stany. Ręczny formularz zachowuje powód wejścia.
- Lokalny skan przechodzi od razu do wyboru ilości bez popupu; korekta jest osobną akcją.
- Formularz pozwala wybrać 100 g/100 ml i jawny przelicznik sztuki. Konflikt zajętego
  GTIN pozwala użyć istniejącego produktu, rozpocząć korektę albo anulować.

## Założenia i wyłączenia

[ASSUMPTION] Fikcyjne dane i obliczenia JavaScript służą wyłącznie symulacji; właściwe
obliczenia, walidacja precyzji, GTIN/checksum, transakcje, idempotencja i źródła są backendowe.
Referencja wpisana w makiecie dotyczy wszystkich zmienionych pól; API pozwoli przypisać ją
oddzielnie. Symulacja nie implementuje requestów, race conditions, kamery, trwałego zapisu,
tygodnia, ustawień sekcji, finalnego stylu ani autoryzacji. Personalizacja sekcji była
osobnym kontekstem revision-002; jej reguły pozostają do SDD #16/#27. Ładowanie/brak sieci
API nie występują w lokalnej synchronicznej symulacji. Nie ma panelu syntetycznego ani
nowego badania użytkownika; browser PASS nie jest potwierdzeniem skuteczności produktu.

## Odtwarzanie i weryfikacja

Otwórz index.html lokalnie. Reset przywraca trzy produkty, trzy wpisy i 895 kcal.
Sprawdzono w izolowanym `agent-browser` v0.35.2 / Chrome na file://, bez instalacji zależności.
Snapshoty accessibility dostarczały referencje akcji; asercje sprawdzały tekst i stan pamięci.
Poniższe scenariusze rzeczywiście przeszły; dowody PNG są w evidence/:

- start: 895 kcal — PASS.
- niepełny produkt: null i jawne zero — PASS.
- suma znanych 895 kcal + PARTIAL bez null jako zero — PASS.
- ilość 50 g daje 76 kcal i snapshot — PASS.
- korekta v2: zmieniona marka, pochodzenie makr i historia zachowane — PASS.
- historia v1 niezmienna — PASS.
- dwa sklepy bez nowej wersji — PASS.
- pierwsze potwierdzenie: v1 i serwerowa semantyka pochodzenia — PASS.
- ręczny wpis zachowuje informację o awarii — PASS.
- ręczny wpis zachowuje limit — PASS.
- lokalny skan bez potwierdzania — PASS.
- konflikt GTIN bez dodatkowego produktu — PASS.
- 3 sztuki po 55 g: 165 g i 255,75 kcal — PASS.
- viewport 390 bez poziomego przewijania — PASS.
- nawigacja klawiaturą — PASS.
- reset danych — PASS.
- nieznany hash — PASS.
- wstecz/dalej — PASS.

Dowody: diary.png, incomplete-product.png, partial-diary.png, correction.png, history.png,
candidate-confirmed.png, provider-fallback.png, gtin-conflict.png, pieces.png, mobile.png.

Kontrola statyczna: samodzielny HTML bez zasobów sieciowych, składnia JavaScript,
odnośniki kontekstu oraz zgodność hashy manifestu. Build/testów Java i iOS nie wykonywano:
artefakt nie zmienia aplikacji i nie jest dowodem działania aparatu lub HTTP/PostgreSQL.
