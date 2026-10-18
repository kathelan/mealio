---
name: om-auto-review-pr
description: Mealio solo delivery extension for om-auto-review-pr.
---

# Mealio extension: om-auto-review-pr

Read the [bundled skill](../../../.agents/skills/om-auto-review-pr/SKILL.md) and
[SDLC.md](../../../SDLC.md), especially “Solo delivery policy”.
When `delivery.mode` is `solo` and `delivery.policy` is `SDLC.md`, the
maintainer-authorized role substitutions below take precedence over the bundled
human-only/self-approval restrictions. Otherwise use the bundled behavior.
Preserve all other validation, coverage, safety, claim and tracker rules.
All tracker-facing prose is Polish per [AGENTS.md](../../../AGENTS.md);
keep exact technical markers unchanged.

## Review and automatic QA

- Apply the risk classification in SDLC.md, including compatible additive API changes.
- Perform the full review and validation; a `review` label is never a verdict.
- When GitHub identity equals PR author, publish the current-head assessment
  specified in SDLC.md instead of submitting a forbidden formal self-review.
  Use the idempotent marker `🤖 om-auto-review-pr — solo assessment`.
  A failed assessment still sets `changes-requested` and runs the normal autofix
  loop only when authorized; do not turn platform rejection into an approval.
- After a clean final review, derive and publish concrete QA scenarios and run
  `om-auto-qa-pr <PR> --self-qa-signoff --apply-failure` if QA is required and
  current evidence is missing. Backend-only and `risk-high` are included.
  Re-enter the same claim and retain it for the outer caller. Do not recursively
  call review when the QA stage recognizes the just-published assessment.
- QA owns its sign-off labels. A direct review command never merges; return QA
  failures to the authorized fix loop and stop after at most three cycles.
