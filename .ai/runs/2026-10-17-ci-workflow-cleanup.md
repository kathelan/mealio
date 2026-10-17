# Porządek w nazewnictwie workflow CI

## Cel
Ujednolicić nazwę workflow GitHub Actions i usunąć nieaktualny, lokalny duplikat.

## Działania
- Zmieniono `.github/workflows/verify.yml` na `.github/workflows/mealio-ci.yml`.
- Usunięto nieśledzony plik `.github/workflows/verify 2.yml`, który zawierał starszą konfigurację i mogły z niego powstać zduplikowane workflow.
- Zachowano nazwę workflow `Mealio CI` oraz cztery nazwy jobów wymagane przez ruleset `Protect main`.
- Ujednolicono prefiks grupy concurrency z nazwą workflow.
- Zaktualizowano ścieżkę workflow w `docs/development.md`.
- Dołączono istniejące lokalne zmiany w `AGENTS.md` i `README.md` wskazane przez użytkownika.

## Weryfikacja
- YAML workflow parsuje się przez Ruby Psych.
- `git diff --check` przechodzi.
- `actionlint` nie jest zainstalowany lokalnie.
- Pełne testy Maven nie są wymagane dla zmiany nazwy workflow i dokumentacji.
- Weryfikacja GitHub Actions nastąpi na PR.

## Otwarte kwestie
Brak.
