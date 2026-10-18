---
name: om-auto-qa-pr
description: Mealio solo delivery extension for om-auto-qa-pr.
---

# Mealio extension: om-auto-qa-pr

Read the [bundled skill](../../../.agents/skills/om-auto-qa-pr/SKILL.md) and
[SDLC.md](../../../SDLC.md), especially “Solo delivery policy”.
When `delivery.mode` is `solo` and `delivery.policy` is `SDLC.md`, the
maintainer-authorized role substitutions below take precedence over the bundled
human-only/self-approval restrictions. Otherwise use the bundled behavior.
Preserve all other validation, coverage, safety, claim and tracker rules.
All tracker-facing prose is Polish per [AGENTS.md](../../../AGENTS.md);
keep exact technical markers unchanged.

## Local QA for API, web and native changes

- Replace bundled UI-only scoping, browser-only setup and screenshot-only sign-off
  conditions with the surface-specific checks and evidence in SDLC.md.
  Read acceptance criteria and diff before selecting any environment/provider.
- Recognize a current approving solo assessment or formal review. A `review`
  label is insufficient; never sign off over blocking findings. If review is
  missing, run review first; when that review already produced current QA, reuse
  it instead of nesting another QA run.
- For backend-only changes, use existing Spring HTTP/integration runners and
  disposable PostgreSQL/Testcontainers as needed. Skip browser provisioning,
  browser credentials and screenshot requirements. Publish actual API/data
  assertions and results. Unit tests alone cannot satisfy QA.
- Web UI retains the bundled browser/environment workflow and screenshots.
  Native UI requires simulator/native checks and screenshots; unavailable native
  tooling is BLOCKED, never replaced by web or API evidence.
- `--self-qa-signoff` may sign off `risk-high` in solo mode only after the
  complete required scenario set and area-specific evidence pass. Keep explicit
  `--evidence-only` read-only on approval labels. Follow SDLC.md for report files,
  exact verdict/head fields, current-head recheck and failure invalidation.
- Publish image-free API reports via comment-pr; upload UI evidence through
  attach-image-evidence. QA never modifies source or merges. Retain inherited
  locks and tear down only environments/worktrees created by this run.
