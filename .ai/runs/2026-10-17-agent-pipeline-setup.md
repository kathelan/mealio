# Agent pipeline setup

## Goal

Configure the Mealio repository for the `om-*` agent pipeline using the detected GitHub, Maven, and browser setup.

## Actions

- Confirmed the GitHub origin, `main` default branch, authenticated `gh` CLI, Maven multi-module layout, and the repository CI commands.
- Added `.ai/agentic.config.json` with GitHub tracking, `agent-browser`, `mvn clean verify`, full labels, and the QA merge gate enabled.
- Installed the GitHub tracker and `agent-browser` descriptors.
- Created 25 missing labels from the 27-label taxonomy; existing `bug` and `documentation` labels were left unchanged.
- Generated `SDLC.md`, `CODE_REVIEW.md`, and `BACKWARD_COMPATIBILITY.md`. Existing `AGENTS.md` and `CLAUDE.md` were preserved.
- Added ignore rules for generated QA artifacts and environment state, and added `.gitkeep` files for the new analysis, script, and QA paths. Existing run and spec markers were preserved.
- Preserved the pre-existing untracked `.ai/runs/2026-10-17-create-skills-directory.md`.

## Changed files

- `.ai/agentic.config.json`
- `.ai/trackers/github.md`
- `.ai/browsers/agent-browser.md`
- `.ai/analysis/.gitkeep`, `.ai/scripts/.gitkeep`, `.ai/qa/.gitkeep`
- `SDLC.md`, `CODE_REVIEW.md`, `BACKWARD_COMPATIBILITY.md`, `.gitignore`

## Verification

- `jq -e . .ai/agentic.config.json` — passed.
- Template marker and placeholder scan of `SDLC.md` — passed.
- Required `.gitkeep` paths — passed.
- Cross-skill coverage — passed: 42 installed skills, no missing skill names or reference files.
- GitHub label inventory — all 27 taxonomy labels present; the two pre-existing labels were not modified.
- `git diff --check` — passed.
- Application tests were not run; this setup changed configuration and documentation only.

## Open items

- The setup is not committed or opened as a pull request. The repository instructions require changes on a task branch and delivery through a PR.
- Optional product discovery configuration remains available through `om-setup-discovery-pipeline`.
