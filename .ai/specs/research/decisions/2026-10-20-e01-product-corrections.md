# Decyzje E01 — korekty produktu i sklepy

- Data: 2026-10-20.
- Właściciel i potwierdzający: właściciel produktu w rozmowie grill-me.
- Zakres: E01/#10, z wpływem na SDD #13, #20 i #24.
- Status: zaakceptowane do projektu; nie są wdrożeniem.
- Zmiana: wymaga ponownego potwierdzenia właściciela i zachowania historii wersji.

## Ustalenia

### E01-C1 — Ręczny wpis po błędzie dostawcy

Niedostępność lub limit zewnętrznego dostawcy nie oznacza, że produktu nie znaleziono.
Użytkownik może mimo to przejść do ręcznego wpisu. UI zachowuje informację o błędzie
lub limicie i nie przedstawia wyniku jako `NOT_FOUND`. FatSecret jest opcjonalny i nie
blokuje MVP.

### E01-C2 — Konflikt GTIN i korekta

GTIN pozostaje unikalny. Gdy zapisane dane dla zajętego GTIN kolidują z nową propozycją,
UI pyta użytkownika, czy użyć istniejącego produktu, skorygować jego dane, czy anulować.
Korekta wymaga świadomej akcji użytkownika i tworzy nową, niezmienną ProductVersion;
wcześniejsza wersja pozostaje dostępna. Każde pole danych ma własne pochodzenie, tak aby
ręczna zmiana marki nie zmieniała źródła wartości odżywczych, które pozostały bez zmian.
Pochodzenie może wskazywać dostawcę, etykietę lub ręczne wprowadzenie wraz z opcjonalną
referencją podaną przez użytkownika. E01 nie dodaje automatycznego odpytania kolejnych
baz podczas korekty.

Skan produktu już znanego lokalnie zwraca go od razu. Użytkownik ma oddzielną akcję
rozpoczęcia korekty; nie dostaje popupu przy każdym skanie i Mealio nie odpytuje dostawców
w tle.

### E01-C3 — Sklepy przy produkcie

Użytkownik może wpisać własne nazwy sklepów, z którymi produkt jest powiązany. Jeden produkt
może mieć wiele sklepów; nazwy pomagają rozróżnić wyniki, ale nie definiują marki ani
identyfikatora produktu. Sklepy należą do metadanych Product i można je zmieniać bez tworzenia
nowej wersji żywieniowej. Nie wprowadzamy centralnego słownika sieci.

### E01-C4 — Low-fi przed implementacją UI

Przed implementacją ekranów iOS w #24 i #25 powstanie klikalny low-fi dla wyszukiwania,
wyboru i tworzenia produktu oraz skanu, potwierdzenia i korekty. Backendowe #13/#14 mogą
postępować bez makiety. Akceptacja low-fi nie zatwierdza finalnego stylu wizualnego.

## Ślad decyzji

Właściciel potwierdził: ręczny wpis po błędzie dostawcy; pytanie przy konflikcie GTIN;
nowa wersja i pochodzenie per pole; opcjonalność FatSecret; ręczne nazwy sklepów,
wiele sklepów przy jednym produkcie; korektę lokalnego wyniku wyłącznie na żądanie;
low-fi dla #24/#25.
