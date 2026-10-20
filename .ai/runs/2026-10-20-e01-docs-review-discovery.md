# Review dokumentacji E01 oraz odświeżenie discovery

Data: 2026-10-20. Cel: sprawdzić trzy PR-y względem zatwierdzonych założeń, poprawić je,
scalić i ocenić wpływ na discovery/brief/backlog. Bez implementacji aplikacji.

## Etapy i wynik

1. Porównano issues #10–29, brief, relacje/zaakceptowane decyzje, ADR-y i stan kodu.
   Zachowano lokalne, wcześniejsze dwa nieśledzone raporty użytkownika.
2. #32: uzgodniono ADR-0004 (produkt/przepis, historia, sekcje, AI poza MVP), poprawiono
   faktyczne relacje diagramu i granice mapy względem przyszłych story-SDD.
3. #33: zamknięto powtórne pytanie o parkowanie, oddzielono cel w docelowym MVP z Q02
   od kont/Garmina później i AI poza MVP. Bez zmiany decyzji produktu.
4. #34: uzupełniono kontrakty stores/pochodzenia i niezmiennego snapshotu, idempotencję
   potwierdzenia; nowa revision-003 naprawia braki, sumy częściowe, korektę i pierwsze
   potwierdzenie. Dwa minory niezależnego review poprawiono i ponownie sprawdzono.
5. Po niezależnych review wykonano squash merge z --match-head-commit i zielonymi
   workflowami dokładnego SHA. Bazy aktualizowano bez force/admin, zachowując oba
   wpisy lessons podczas konfliktu.
6. om-discover --refresh: sprawdzono źródła i potwierdzenia E01-C1–C4 oraz diary-home,
   zredagowano brief i poddano go niezależnemu sceptycznemu czytaniu. Brak CRITICAL/WARNING.
   To synchronizacja już potwierdzonych wyborów; nie dodano badań ani akceptacji makiety.
7. Oceniono istniejący backlog. Przygotowano pełne drzewo i dodatkowe kryteria bez nowych
   issues; zgodnie z om-backlog zapis do trackera wymaga potwierdzenia tej propozycji.

## Scaleni PR-y

| PR | Zweryfikowany head | Squash commit |
|---|---|---|
| #32 | 893b7ba01589cf0f365632e7e352733467623f84 | 1f36d84ee8748b3ffc436a92de4c9850c585c5cb |
| #33 | c8d56a9d83f522aa15782b577cd40e774bb95ec4 | b200e013fda81755987cf3a043f6da506dba0cb4 |
| #34 | 6f4484f0dc714f7d2b37a740f23cbf8c9c9c6516 | e2165c97d485303e261d1eec8a9bc4cd4cc948f0 |

## Zmienione pliki tego etapu

- .ai/specs/product-brief.md — R09–R11, D13–D17, przepływ i readiness oraz przeliczony Coverage.
- .ai/specs/research/2026-10-20-e01-review-backlog-sync.md — całe istniejące drzewo i propozycja AC.
- .ai/runs/2026-10-20-e01-docs-review-discovery.md — ten raport.

Szczegółowe pliki/poprawki wcześniejszych etapów są w raportach pr32/pr33/pr34-documentation-review.

## Weryfikacja

- Pełny diff, granice modułów i zgodność z briefem/issues/ADR — PASS.
- Lokalne odnośniki, git diff --check, składnia JS i manifesty SHA-256 — PASS.
- Nowa makieta: 18 scenariuszy oraz 2 dodatkowe sprawdzenia pochodzenia w realnym
  agent-browser — PASS; rzeczywiste screenshoty w revision-003/evidence.
- Diagram E02: relacje względem węzłów, podświetlenie i klawiatura — PASS.
- Niezależne końcowe review E01/E02/E03 i briefu — PASS.
- Coverage briefu: 39 claims = 38 sourced (interview 2, document 32, product 4) + 1 assumed;
  brak nowych respondentów/pomiarów i brak synthetic.
- Lokalne mvn clean verify i build/testy iOS pominięto jako nieadekwatne do spec-only.
  Nie zmieniono kodu aplikacji. CI wymagane dla scalonych PR-ów zakończone sukcesem;
  pominięcia jobów dotyczyły wyłącznie udokumentowanego filtra Markdown-only.

## Otwarte kwestie

- Propozycja synchronizacji issues oczekuje na potwierdzenie; nie przedstawiamy jej jako zastosowanej.
- Q02 nadal blokuje kalkulator celu; A01 pozostaje untested. Usuwanie zajętych sekcji,
  późniejsza edycja spożycia i cel/pozostało wymagają właściwych SDD, nie tego review.
- Techniczny PASS makiety nie dowodzi ograniczenia codziennej pracy ani działania
  natywnego klienta, aparatu i backendowych funkcji, których jeszcze nie zaimplementowano.
