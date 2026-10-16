# ADR-0001: Modularny monolit z modułami domenowymi

## Status

Accepted — 2026-10-16

## Kontekst

Mealio jest na etapie MVP i rozwija kilka powiązanych obszarów biznesowych. Globalny podział na `controller`, `service`, `domain` i `repository` utrudnia wskazanie właściciela danych oraz późniejsze wydzielenie funkcjonalności. Mikroserwisy zwiększyłyby koszt wdrażania i integracji bez obecnej potrzeby skalowania organizacyjnego.

## Decyzja

Backend jest modularnym monolitem. Bezpośrednie moduły reprezentują obszary biznesowe. Wewnątrz modułu stosujemy, jeśli potrzebne, pakiety `api`, `application`, `domain` i `infrastructure` oraz kierunek zależności architektury heksagonalnej.

Inne moduły korzystają wyłącznie z publicznego API właściciela. Bezpośredni dostęp do jego infrastruktury, encji, repository i tabel jest zabroniony.

Początkowo granice są logicznymi pakietami w `backend`. Osobny artefakt Maven powstaje dopiero przy potrzebie niezależnej publikacji, ponownego użycia, kompilacyjnej izolacji lub wdrożenia.

## Konsekwencje

- jeden prosty deployment dla MVP,
- czytelna własność kodu i danych,
- możliwość automatycznej weryfikacji zależności,
- komunikacja między modułami wymaga jawnych kontraktów,
- późniejsze wydzielenie jest możliwe, ale nie jest celem samym w sobie.
