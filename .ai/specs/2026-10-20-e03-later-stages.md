# E03 — późniejsze etapy

Data: 2026-10-20. Status: kompletna mapa parkowania; bez SDD wykonawczej i implementacji.
Source issue: [#12](https://github.com/kathelan/mealio/issues/12).
Źródło decyzji: [brief D08/D12, N01/N03 i Q02](product-brief.md),
[zatwierdzony zakres](2026-10-20-personal-mvp.md).

## 📝 TLDR

#12 pozostaje zaparkowanym kontenerem odrębnych kierunków, zgodnie z decyzją właściciela.
Konta/współdzielenie i Garmin są późniejsze, AI jest poza MVP bez terminu powrotu.
Kalkulator celu pozostaje częścią docelowego MVP (D12), ale Q02 blokuje jego projekt
i implementację; umieszczenie w kontenerze nie wyłącza go z MVP ani nie blokuje E01/E02.

## ❓ Open Questions

- **Q1 — closed:** #12 pozostaje zaparkowany; bieżące zlecenie obejmuje review i uporządkowanie
  dokumentacji, nie wybór nowej funkcji. Źródło: brief/backlog oraz issue #12.
- **Q02 briefu — open:** wzór, dane wejściowe i model aktywności kalkulatora. Maintainer
  przygotuje propozycję ze źródłami, właściciel zatwierdzi. Brak terminu decyzji; blokada
  dotyczy wyłącznie kalkulatora, nie katalogu ani obliczenia kcal produktu #14.

## 📝 Problem Statement

Połączenie kont, integracji i kalkulatora w jedną SDD tworzyłoby pozorną gotowość mimo
różnych celów i nierozstrzygniętych zależności. Dokument zachowuje rozróżnienie
„później”, „w MVP, ale zablokowane” oraz „poza MVP”. Nie stanowi dowodu popytu.

## 📝 Proposed Solution — warunki podjęcia

| Kierunek | Status produktu | Warunek osobnej SDD | Właściciel decyzji |
|---|---|---|---|
| Konta i współdzielenie | później, N01 | cel, własność/prywatność danych, zaproszenia, migracja danych bez właściciela; zaakceptowane zastąpienie ADR-0003 | właściciel; maintainer projektuje bezpieczeństwo |
| Garmin | później, N03 | przypadek importu/eksportu, dostęp do integracji, zgody, tokeny, retencja i awarie | właściciel; maintainer sprawdza wykonalność |
| Cel kalorii | docelowe MVP, D12 | zamknięte Q02; wzór i dane wejściowe ze źródłami oraz ręczna korekta celu | właściciel zatwierdza propozycję maintainera |
| AI/głos | poza MVP, N03 | nowa zatwierdzona decyzja zakresu; nie automatyczna kontynuacja istniejącego ASR | właściciel |

## 📝 Granice, zgodność i ryzyka

Nie dodajemy technicznego użytkownika, userId, atrap auth ani migracji przy porządkowaniu
dokumentacji. Przyszłe konta wymagają jawnej migracji pojedynczej biblioteki/dziennika,
testów izolacji i ochrony danych zgodnie z BACKWARD_COMPATIBILITY.md. Garmin nie jest
warunkiem obliczeń produktu ani uruchomienia dziennika. Istniejącego ASR nie usuwamy.
Nie projektujemy wspólnej infrastruktury tych kierunków zawczasu.

## 📋 Phasing i kryteria ukończenia dokumentu

1. Zachować powyższe statusy i otwarte Q02, bez wyznaczania harmonogramu.
2. Po osobnym zleceniu właściciela zaktualizować brief, wybrać dokładny zakres i jego issue;
   dopiero wtedy przygotować oddzielną SDD z kontraktami, testami i migracją/cofnięciem.
3. Dokument jest kompletny, gdy kierunki mają status, warunek wejścia i właściciela,
   Q02 nie blokuje E01/E02, a parkowanie nie jest przedstawiane jako wykonana funkcja.

Weryfikacja tego PR: spójność briefu/issues, odnośniki i diff. `mvn clean verify` oraz QA
aplikacji nie mają zastosowania do tej dokumentacji; nie deklarujemy testów przyszłych funkcji.
