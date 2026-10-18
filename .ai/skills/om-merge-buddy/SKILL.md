---
name: om-merge-buddy
description: Mealio solo delivery extension for om-merge-buddy.
---

# Mealio extension: om-merge-buddy

Read the [bundled skill](../../../.agents/skills/om-merge-buddy/SKILL.md) and
[SDLC.md](../../../SDLC.md), especially “Solo delivery policy”.
When `delivery.mode` is `solo` and `delivery.policy` is `SDLC.md`, the
maintainer-authorized role substitutions below take precedence over the bundled
human-only/self-approval restrictions. Otherwise use the bundled behavior.
Preserve all other validation, coverage, safety, claim and tracker rules.
All tracker-facing prose is Polish per [AGENTS.md](../../../AGENTS.md);
keep exact technical markers unchanged.

## Read-only solo classification

- Accept a current approving solo assessment as the process review gate when
  formal self-approval is impossible. Do not require a second person/account.
- Missing or stale required QA verdict/head, or a later failed review/QA record,
  cannot be classified ready. Read exact-head CI runs as well as check jobs.
- Report backend-only/high-risk PRs needing QA as candidates for agent QA,
  naming `om-auto-qa-pr <PR> --self-qa-signoff --apply-failure` as the next action.
- Actual required platform reviews still block. This skill remains read-only;
  do not run QA, apply labels, change protection or merge during a queue scan.
