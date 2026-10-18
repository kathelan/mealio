---
name: om-pr-autopilot
description: Mealio solo delivery extension for om-pr-autopilot.
---

# Mealio extension: om-pr-autopilot

Read the [bundled skill](../../../.agents/skills/om-pr-autopilot/SKILL.md) and
[SDLC.md](../../../SDLC.md), especially “Solo delivery policy”.
When `delivery.mode` is `solo` and `delivery.policy` is `SDLC.md`, the
maintainer-authorized role substitutions below take precedence over the bundled
human-only/self-approval restrictions. Otherwise use the bundled behavior.
Preserve all other validation, coverage, safety, claim and tracker rules.
All tracker-facing prose is Polish per [AGENTS.md](../../../AGENTS.md);
keep exact technical markers unchanged.

## Solo chain adjustments

- Recognize current solo review assessments in diagnosis and matrix rows 5/9.
- Replace UI-only row 7 and human-signoff stop row 10 with local QA whenever
  `needs-qa` lacks current complete PASS evidence, including backend/high-risk:
  `om-auto-qa-pr <PR> --self-qa-signoff --apply-failure`.
- Re-diagnose after review: if it already completed QA for this head, skip the
  duplicate QA step. On an authorized repair run, route `qa-failed` through
  implementation/fix, current review and QA re-test; do not erase it to merge.
  Respect `blocked`, `do-not-merge`, foreign claims and active human QA.
- Preserve `--dry-run` as fully read-only and `--confirm` behavior. A direct
  invocation still needs `--allow-merge` to merge. Implementation producers
  pass that flag under AGENTS.md; intake/spec-only work cannot infer it.
- Release the outer claim immediately before the merge skill rechecks all gates.
  Report current-head results and exact platform blockers, never ask for a
  second person solely because the same account cannot self-approve.
