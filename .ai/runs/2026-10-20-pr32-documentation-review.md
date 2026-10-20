# Review i poprawki dokumentacji PR #32

Cel: review zgodności z założeniami właściciela i issues, poprawki oraz doprowadzenie do squash merge.

Wykonano pełny przegląd ryzyk, zgodności, braków, klarowności i prostoty w izolowanym worktree.
Uzgodniono ADR-0004 z pojedynczym wpisem przepisu i wyłączeniem AI z MVP. Poprawiono końce relacji diagramu HTML, dodano klawiaturę i kryteria ukończenia mapy; rozróżniono mapę od przyszłych story-SDD.

Weryfikacja: git diff --check, lokalne odnośniki Markdown, składnia JS i zgodność z decyzjami.
Model HTML sprawdzony w przeglądarce: kierunki relacji, podświetlenie i klawiatura.
`mvn clean verify` pominięto jako nieadekwatne do spec-only; nie zmieniono kodu aplikacji.
Nie wykonano testów przyszłych funkcji Java/iOS. Końcowy SHA, review, CI i wynik scalenia
są publikowane na PR.

Otwarte: SDD pozostają projektami, nie wdrożeniem. Q02 nadal blokuje kalkulator;
E02 wymaga osobnych story-SDD. Discovery/brief/backlog będą odświeżone po przeglądzie trzech PR-ów.
