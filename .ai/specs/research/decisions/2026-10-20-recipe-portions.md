# Decyzja — porcje przepisu

- Data: 2026-10-20.
- Właściciel i potwierdzający: użytkownik sesji, właściciel produktu Mealio.
- Źródło: doprecyzowanie w rozmowie backlogu: „przy przepisach dzielisz dania na porcje
  i porcja ma x gram i na podstawie tego wylicza mealio kcal”.
- Status: active.

Przepis dzieli się na porcje z określoną masą w gramach. Mealio oblicza kcal i makro
porcji na podstawie składników oraz tego podziału. Nie przyjęto propozycji agenta,
by jedynym sposobem zapisu spożycia było wpisywanie gramów z całego dania.
Przelicznik porcji jest właściwością konkretnego przepisu, nie uniwersalną stałą.

Historia zachowuje wartości użyte w chwili zapisu zgodnie z istniejącym R02;
użytkownik nie wprowadził odstępstwa od tej reguły. Szczegóły modelu wersji i walidacji
spójności masy, liczby porcji i składników należą do specyfikacji implementacyjnej.

Przegląd: przed specyfikacją potraw lub po wykryciu niespójnego przeliczenia.
Zmiana wymaga nowej decyzji zatwierdzonej przez właściciela z zachowaniem historii.
