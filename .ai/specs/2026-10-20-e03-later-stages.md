# E03 — późniejsze etapy

## 📝 TLDR

Issue #12 jest kontenerem na odroczone prace, a nie opisem jednej wdrażalnej funkcji.
Wymienia konta i współdzielenie, Garmin oraz kalkulator celu kalorii; ostatni z tych zakresów
pozostaje zablokowany przez Q02. Nie należy projektować ich łącznie bez wskazania konkretnego
celu i zależności.

## ❓ Open Questions

- **Q1:** Który kierunek ma dostać osobną SDD jako następny, czy #12 pozostaje zaparkowane (rekomendowane)?
  - **Pozostawić zaparkowane:** zero nowych zobowiązań teraz; wracamy po ustabilizowaniu E01/E02 albo gdy pojawi się konkretny przypadek użycia.
  - **Konta i współdzielenie:** trzeba zdecydować, kto jest właścicielem produktów i przepisów, jak zapraszać drugą osobę, co jest prywatne oraz jak przypisać dane z MVP. To superseduje ADR-0003 i wymaga osobnej pracy o auth, prywatności i migracji.
  - **Garmin:** trzeba określić, co Mealio importuje/eksportuje i po co (np. aktywność czy dane zdrowotne), jak działa zgoda, tokeny, synchronizacja i awarie zewnętrznego API. Samo hasło „integracja z Garminem” nie definiuje użytecznej funkcji.
  - **Kalkulator celu kalorii:** ten przepływ jest częścią docelowego MVP, ale Q02 pozostaje otwarte: wzór, dane wejściowe i model aktywności wymagają researchu oraz zatwierdzenia właściciela. Można go specyfikować niezależnie od #11 po zamknięciu Q02.
  - **AI:** brief jawnie wyłącza AI z MVP i nie wyznacza terminu później; wymagałoby to ponownego otwarcia decyzji, więc nie rekomenduję wybierać go teraz.

## 📝 Problem Statement

Issue #12 zachowuje przyszłe kierunki poza bieżącym osobistym MVP. Brief oznacza konta,
współdzielenie i Garmin jako późniejsze, AI jako poza MVP, a wzór celu kalorii pozostawia
otwartym w Q02.

## 📝 Proposed Solution

Każdy wybrany później zakres otrzyma własną specyfikację po wskazaniu celu, zależności i
właściciela decyzji. Ta propozycja nie przesądza kolejności uruchomienia E03.
