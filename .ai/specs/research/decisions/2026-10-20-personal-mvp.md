# Decyzja — osobiste MVP Mealio po sesji grill-me

- Data: 2026-10-20.
- Właściciel i potwierdzający: właściciel produktu prowadzący rozmowę.
- Źródło: doprecyzowania Q21–Q34 i potwierdzenie podsumowania Q35 słowem „tak”.
- Status: active.
- Przegląd: przed specyfikacją techniczną lub po teście codziennego używania.
- Zmiana wymaga potwierdzenia właściciela i nowego zapisu zachowującego historię.

Zatwierdzony zakres: [specyfikacja produktu](../../2026-10-20-personal-mvp.md).
Sesja zastępuje wcześniejsze wymaganie jawnej masy każdej porcji: liczba porcji jest
obowiązkowa, końcowa masa opcjonalna, kcal i makro pochodzą ze składników.
Zastępuje obowiązkowe zagnieżdżone kategorie opcjonalnymi płaskimi etykietami niezależnymi
od własnych sekcji dziennika. Odkłada AI, użytkowników, współdzielenie i Garmin poza MVP.
Kody kreskowe i cele są częścią docelowego MVP; nie ustalono nowego harmonogramu dostawy.
Wzór kalkulatora pozostaje otwarty, więc jego implementacja nie jest gotowa do rozpoczęcia.

Domyślny zestaw sekcji dziennika to śniadanie, drugie śniadanie, obiad, lunch, kolacja
i przekąski. Użytkownik może zmienić ich nazwy i liczbę, także ograniczyć dzień do trzech
własnych posiłków.

Nowe identyfikatory briefu: D08 (zakres osobisty), D09 (porcje i parametry),
D10 (etykiety i dziennik), D11 (bazy przepisów), D12 (cel ze wzoru i ręczna korekta).
D08 zastępuje D03–D06 jako dotychczasowy podział „później” i harmonogram; nie usuwa
istniejącego kodu ani nie ustala kolejności technologicznej. D09 zastępuje D07 i R05;
D10 zastępuje część D02 dotyczącą hierarchii, zachowując pozostały cel ponownego użycia potraw.

Odrzucone podczas rozmowy założenia agenta: odrębne „Gotuję” jako obowiązkowy etap,
rozbijanie przepisu na osobne wpisy dziennika, opcjonalna liczba porcji oraz brak
checkboxa parametryzacji. Nie należy przywracać ich jako domyślnych rozwiązań.
