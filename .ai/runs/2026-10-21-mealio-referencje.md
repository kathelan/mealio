# Referencje wizualne Mealio

Data: 2026-10-21. Cel: przełożyć referencję Fitatu i potwierdzone wypowiedzi
właściciela na dokument decyzji, bez implementowania UI ani tokenów.

## Wykonane działania

- Przeczytano brief i dwie relacje tego samego właściciela.
- Sprawdzono publiczną dokumentację Fitatu oraz dwa lokalne zdjęcia referencyjne.
- Właściciel zastąpił GitHub/Slack referencją Fitatu, potwierdził charakter,
  antycharakter, role koloru i kierunek nawigacji iOS.
- Utworzono `lekcja-06/referencje.md` z identyfikatorami cech, pochodzeniem,
  antyreferencjami, konfliktami i lukami. Nie zmieniono briefu ani zachowania produktu.
- Zdjęcia usunięto z indeksu i wyłączono lokalnie przez `.git/info/exclude`;
  pozostają na dysku. Nie są częścią dostawy ani dowodami publikowanymi w trackerze.

## Zmienione pliki

- `lekcja-06/referencje.md` — dokument źródłowy dla przyszłych zasad i tokenów.
- Ten raport — przebieg zadania dokumentacyjnego.

## Weryfikacja

- Zakres wyłącznie dokumentacyjny: kontrola diffu, struktury tabel, odnośników
  lokalnych i wykluczenia zdjęć z indeksu; wyniki odnotowane przy dostawie.
- Nie uruchamiano Maven ani testów iOS: dokument nie zmienia kodu lub aplikacji;
  właściwa bramka to spójność dokumentu i odnośników.

## Otwarte kwestie

- Model klasyfikacji przepisów: przykład hierarchii a płaskie etykiety w briefie.
- Paleta, typografia, szczegóły komponentów, ruch, język i dostępność pozostają
  do osobnych decyzji. Dokument nie jest gotową specyfikacją implementacyjną.
- Współdzielenie jest poza MVP. Nie oceniono jego faktycznej dostępności w Fitatu.
