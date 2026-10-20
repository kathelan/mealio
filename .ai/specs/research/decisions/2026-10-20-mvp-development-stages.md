# Decyzja — etapy rozwoju Mealio

- Data: 2026-10-20.
- Właściciel i potwierdzający: użytkownik prowadzący sesję, właściciel produktu Mealio.
- Źródło: rozmowa discovery; podsumowanie etapów agenta potwierdzone słowami „wyglada ok”.
- Status: active.

## Zatwierdzona kolejność

1. Produkty i deterministyczne obliczenia wartości dla ilości.
2. Potrawy wielokrotnego użytku, zagnieżdżone kategorie, konfigurowalne sekcje dnia,
   dziennik i dzienne sumy kcal oraz białka, tłuszczów i węglowodanów.
3. Kody kreskowe: odczyt, potwierdzenie danych i ręczne uzupełnianie braków.
4. Opis tekstowy i głosowy: interpretacja, dopasowanie katalogu, korekta i potwierdzenie wpisu.
5. Cele: oszacowanie utrzymania i deficytu, ręczne cele makro i edytowalne propozycje
   uwzględniające aktywność, bilans zjedzone/pozostało.
6. Docelowy klient iOS w SwiftUI i integracja UI/UX na końcu.

Konta oraz współdzielenie przepisów pozostają później; nie ustalono ich miejsca
względem etapu klienta iOS. Obecna wersja jest lokalna lub stanowi kontrolowane demo.
Dodatkowe składniki odżywcze są opcjonalne.

## Uzasadnienie i konsekwencje

Kolejność zaproponował agent na podstawie relacji o powtarzaniu potraw; właściciel ją
zatwierdził. Pierwszy kompletny rezultat to zapis potrawy, ponowne dodanie do dziennika,
przeliczona porcja i bilans. Sam katalog jest fundamentem, nie rozwiązaniem całej potrzeby.
Współdzielenie z żoną wymaga późniejszego etapu.

To świadoma zmiana priorytetu względem docs/mvp.md, które odkłada przepisy,
konfigurowalne sekcje i cele. Dokumenty źródłowe i odpowiednie ADR trzeba uzgodnić
w specyfikacji przed implementacją tych zmian; discovery ich nie nadpisuje.

## Nierozstrzygnięte

Dobór wzorów i modelu aktywności, granice rekomendacji, cele jakościowe oraz limity
i retencja tekstu/audio. Nie zatwierdzono stałego przelicznika deficytu na tempo utraty masy.
Przegląd decyzji: przed specyfikacją danego etapu albo po wyniku testu przeczącym założeniom.
Zmiana: nowy zapis zatwierdzony przez właściciela, zachowujący historię tej decyzji.
