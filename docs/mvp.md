# MVP

> Aktualizacja kierunku 2026-10-20: właściciel zatwierdził
> [osobiste MVP](../.ai/specs/2026-10-20-personal-mvp.md) i
> [odświeżony brief](../.ai/specs/product-brief.md). Ten dokument zachowuje wcześniejszy
> zakres bazowy. Nowe ustalenia mają pierwszeństwo dla docelowego zakresu: przepisy,
> własne sekcje, opcjonalna masa porcji, cel kalorii i brak AI w MVP. Nie jest to
> deklaracja wdrożenia; odpowiednie ADR i kontrakty wymagają uzgodnienia w SDD przed kodem.

Mealio jest dziennikiem żywieniowym. Umożliwia użytkownikowi aplikacji iOS szybkie zapisanie
zjedzonych produktów w wybranym dniu oraz otrzymanie powtarzalnego podsumowania kalorii i
makroskładników.

## Główne przepływy

Użytkownik może dodać pozycję posiłku przez:

1. ręczne wyszukanie i wybranie produktu,
2. zeskanowanie kodu kreskowego,
3. opis tekstowy, np. „zjadłem trzy jajka i dwie kromki chleba”.

Każdy przepływ prowadzi do ekranu potwierdzenia. System nie zapisuje niepewnych propozycji AI bez wiedzy użytkownika.

Produkt pobrany pierwszy raz z zewnętrznego źródła jest kandydatem i wymaga potwierdzenia.
Po potwierdzeniu jego lokalna wersja może być używana bez ponownego pytania, dopóki użytkownik
nie wybierze odświeżenia albo system nie wykryje wymagającej uwagi zmiany źródła.

Skanowanie sprawdza kolejno lokalny katalog, Open Food Facts i opcjonalnie FatSecret Basic.
Jeśli żaden dostawca nie zwróci produktu, użytkownik uzupełnia dane z etykiety. FatSecret
Basic obejmuje katalog USA, dlatego jest dodatkową próbą, a nie gwarantowanym źródłem dla
polskich produktów.

## Dziennik dnia

Widok dnia zawiera sekcje:

- śniadanie,
- drugie śniadanie,
- obiad,
- podwieczorek,
- kolacja,
- przekąski lub inne.

Użytkownik może przechodzić między dniami, dodawać pozycje do sekcji, zmieniać ich ilość,
przenosić je między sekcjami i usuwać. Dzień pokazuje sumę energii, białka, tłuszczu oraz
węglowodanów. Kopiowanie dnia, cele żywieniowe i konfigurowalne sekcje nie należą do pierwszej
iteracji.

## Zakres

- katalog produktów z kodem kreskowym, porcjami i wartościami odżywczymi,
- wyszukiwanie i ręczne dodawanie produktów,
- odczyt produktu po kodzie kreskowym,
- dziennik według dnia oraz sekcji posiłku,
- wpisy spożycia z produktem, ilością i jednostką,
- obliczenia dla bazowych jednostek `g` i `ml`,
- deterministyczne obliczanie kalorii i makroskładników,
- parser tekstu zwracający produkty, ilości, jednostki i poziom niepewności,
- potwierdzenie lub poprawienie propozycji przez użytkownika,
- API przygotowane dla klienta iOS,
- obserwowalność, walidacja oraz spójna obsługa błędów.

ASR może zamieniać audio na tekst, ale nie jest wymagany do pierwszego działającego przepływu tekstowego.

Pierwsza iteracja przyjmuje ilości w gramach albo mililitrach zgodnie z podstawą wartości
odżywczych produktu. Sztuki, porcje, opakowania, łyżeczki i łyżki wymagają jawnego
przelicznika produktu i mogą zostać dodane później bez zmiany modelu wpisu spożycia.

Produkt z niepełnymi danymi może zostać zapisany i użyty w dzienniku ze statusem
`INCOMPLETE`. Brak wartości nie jest zerem. Podsumowanie dnia wskazuje liczbę niekompletnych
wpisów i nie przedstawia niepełnego wyniku jako ostatecznej wartości całego dnia.

Pierwsza wersja backendu działa bez kont użytkowników, uwierzytelniania i autoryzacji. Jest
przeznaczona do lokalnego developmentu i kontrolowanego demo, a nie do publicznego
przechowywania danych wielu osób. Użytkownicy zostaną dodani po ustabilizowaniu podstawowego
przepływu przez Google login lub email i hasło.

## Źródła prawdy

Model AI może interpretować tekst i proponować dopasowania. Nie jest źródłem kalorii, makroskładników ani kodów kreskowych. Dane żywieniowe pochodzą z katalogu Mealio lub jawnie wskazanego, zweryfikowanego dostawcy. Wynik obliczeń musi dać się odtworzyć z zapisanych danych wejściowych.

## Poza zakresem

- diagnozy i rekomendacje medyczne,
- automatyczne plany dietetyczne,
- rozpoznawanie jedzenia ze zdjęć,
- funkcje społecznościowe,
- listy zakupów,
- płatności i subskrypcje,
- osobne wdrażanie każdego modułu jako mikroserwisu.

Istniejące kategorie i przepisy mogą pozostać w systemie, ale nie wyznaczają krytycznej ścieżki nowego MVP.
