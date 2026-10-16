# ADR-0004: Dziennik żywieniowy jako główny model MVP

## Status

Accepted — 2026-10-16

## Kontekst

Mealio ma umożliwiać rejestrowanie jedzenia w układzie dziennym podobnym funkcjonalnie do
popularnych dzienników kalorii. Jednorazowy kalkulator nie zapewnia historii ani sum dnia.
Model `Meal` jako kontener może niepotrzebnie komplikować dodawanie pojedynczych produktów.

## Decyzja

Podstawowym zapisywanym obiektem jest `FoodEntry`: produkt spożyty w określonej ilości,
dacie i sekcji posiłku. `DailyDiary` jest modelem odczytu grupującym wpisy i obliczającym
sumy dnia; nie wymaga osobnej tabeli.

Pierwsze sekcje to śniadanie, drugie śniadanie, obiad, podwieczorek, kolacja i inne.
Wpis można dodać, zmienić, przenieść oraz usunąć. Data dziennika jest przechowywana jawnie
jako `LocalDate`, dzięki czemu nie zależy od strefy czasowej serwera.

Parser tekstowy może zaproponować wiele wpisów. Zostają one zapisane jako jedna atomowa
operacja dopiero po potwierdzeniu użytkownika.

## Konsekwencje

- prosty model odpowiadający interfejsowi dziennika,
- łatwe sumowanie dnia i sekcji,
- niezależne poprawianie pojedynczych pozycji,
- przepisy pozostają szablonami i nie są utożsamiane ze spożyciem,
- kopiowanie dni, cele kaloryczne oraz konfigurowalne sekcje można dodać później.
