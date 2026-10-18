---
name: om-approve-merge-pr
description: Mealio solo delivery extension for om-approve-merge-pr.
---

# Mealio extension: om-approve-merge-pr

Read the [bundled skill](../../../.agents/skills/om-approve-merge-pr/SKILL.md) and
[SDLC.md](../../../SDLC.md), especially “Solo delivery policy”.
When `delivery.mode` is `solo` and `delivery.policy` is `SDLC.md`, the
maintainer-authorized role substitutions below take precedence over the bundled
human-only/self-approval restrictions. Otherwise use the bundled behavior.
Preserve all other validation, coverage, safety, claim and tracker rules.
All tracker-facing prose is Polish per [AGENTS.md](../../../AGENTS.md);
keep exact technical markers unchanged.

## Solo merge gates

- Recognize a current solo review assessment under SDLC.md; if author and
  reviewer identities match, do not attempt self-approval or ask for a second
  account. Continue only when the actual review gate is satisfied.
- Read the latest qualifying review and QA records. Missing verdict/head,
  stale evidence, later failed assessments, unresolved blocking reviews or
  conflicting QA labels block merge. No legacy missing-head exception applies.
- Missing/stale required QA routes to local QA, including backend/high-risk;
  genuine QA failure routes back to authorized fixes. Neither permits merging
  before the new PASS. Respect hard-block labels, active claims and human QA.
- Read required check configuration and exact-head workflow runs as well as
  job results; pending, missing expected or failed checks remain blockers.
- Squash only a ready, conflict-free PR after releasing this chain's claim and
  rechecking head and gates. Use `--match-head-commit <verified SHA>` with the
  tracker merge operation; never `--admin`. Another actor's claim blocks merge.
- A real platform requirement for another account's approving review is an
  explicit blocker. Never change protection or invent a formal approval.
