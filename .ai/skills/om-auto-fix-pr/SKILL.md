---
name: om-auto-fix-pr
description: Mealio solo delivery extension for om-auto-fix-pr.
---

# Mealio extension: om-auto-fix-pr

Read the [bundled skill](../../../.agents/skills/om-auto-fix-pr/SKILL.md) and
[SDLC.md](../../../SDLC.md), especially “Solo delivery policy”.
When `delivery.mode` is `solo` and `delivery.policy` is `SDLC.md`, the
maintainer-authorized role substitutions below take precedence over the bundled
human-only/self-approval restrictions. Otherwise use the bundled behavior.
Preserve all other validation, coverage, safety, claim and tracker rules.
All tracker-facing prose is Polish per [AGENTS.md](../../../AGENTS.md);
keep exact technical markers unchanged.

## Solo stabilization

- Recognize current solo review assessments, including when GitHub has no formal
  approval because the reviewer uses the author's account.
- Full mode runs local QA for every `needs-qa` PR lacking current PASS evidence,
  not just UI diffs: `om-auto-qa-pr <PR> --self-qa-signoff --apply-failure`.
  Do not duplicate QA already completed by the review skill on this head.
- `--no-ui` suppresses browser checks only; it cannot satisfy missing required
  UI QA or skip backend QA. `--ci-only` retains its narrow scope and never
  claims a full review/QA completion.
- In an explicitly authorized repair chain, `qa-failed` routes to fixes,
  re-review and QA re-test. Only QA can clear failure after a complete PASS;
  other hard blocks and active human QA are preserved. Respect iteration limits.
- QA owns approval labels; this orchestrator never grants them itself and never
  merges. Leave the outer caller a current, evidenced merge-ready result.
