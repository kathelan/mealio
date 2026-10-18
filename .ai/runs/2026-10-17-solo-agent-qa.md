# Solo delivery with agent QA

## Goal

Deliver reviewed, locally verified PRs without requiring another person or GitHub
account for review or QA. Preserve CI, unit coverage, compatibility and data safety.

## Scope

Tracker titles, bodies and comments are Polish; exact technical markers stay unchanged.

Repository delivery policy, opt-in configuration and repo-local skill extensions.
The downloaded skill collection and application code remain outside this task.

## Implementation plan

1. Define the solo policy, evidence requirements, risk handling and identity fallback.
2. Connect review, QA and merge skills through persistent local extensions.
3. Check policy consistency and links, review the PR, and merge only on green CI.

## Risks

- Local extensions must explicitly supersede the bundled human-only approval rules;
  a QA label or an implementation run is not sufficient evidence.
- Native iOS QA requires a working simulator/test runner; browser evidence cannot
  stand in for an untested native flow.
- GitHub branch protection remains authoritative. No extra reviewer account is
  provisioned and no protection is disabled automatically.

## Verification

- Structural check: valid JSON; original validation commands, QA gate and labels
  preserved; six local skill extensions resolve their bundled skills and policy;
  20 relative Markdown links resolve; CLAUDE inherits AGENTS; Polish tracker rule
  is present in the common policy and tracker descriptor. PASS.
- `git diff --check`: PASS.
- `mvn clean verify`: first attempt failed because Docker Desktop was stopped
  (`Could not find a valid Docker environment` in ASR PostgreSQL integration).
  Started Docker Desktop and re-ran the same command: BUILD SUCCESS in all four
  reactor entries, with unit coverage gates and PostgreSQL integration passing.
  Log: `/private/tmp/mealio-solo-agent-qa-verify-docker.log` (local only).
- Read actual main ruleset: four required CI checks, zero required approving
  reviews. No branch protection or ruleset was changed.
- Examined the transition scenarios below against policy and each extension.
  This is a manual instruction review, not a claim that an LLM execution engine
  has been tested end to end.

| Scenario | Required result checked in instructions |
|---|---|
| Same-account review | Current assessment comment; no forbidden self-approval |
| Backend-only QA | Real HTTP/integration assertions; no browser prerequisite |
| High-risk QA | Agent sign-off after complete PASS and risk-specific evidence |
| Unit tests alone | Insufficient for behavioral QA |
| Missing UI screenshots or simulator | No UI sign-off; BLOCKED when required |
| QA failure then clean review | Failure preserved until successful QA re-test |
| Push after approval | Fresh review/QA or explicit scope reconfirmation required |
| Older PASS, newer FAIL/BLOCKED | Newer failure blocks progression |
| Another actor or unclear chronology | No approval inferred from the record |
| Active claim, human QA, hard block or pending CI | No merge |
| Missing solo configuration | Original bundled behavior |
| Polish tracker output | Polish prose; technical markers preserved |

## Changed files and outcome

AGENTS.md, SDLC.md, pipeline config, tracker descriptor, six local skill
extensions, the shared lesson index and this plan/report. Application source,
unit tests, migrations and the downloaded skill collection were not modified.
The implementation is published on PR #8; final review and CI evidence live on
that PR. Squash merge remains conditional on the exact final head passing all
required checks.

## Limits

No web/native screen changed, so no browser screenshots or simulator run was
performed. The new high-risk agent-signoff path has been inspected against the
scenario matrix; its first application PR will provide live end-to-end evidence.
A future skill upgrade must preserve local extensions and re-check precedence.

## Progress

PR: #8

> Convention: `- [ ]` pending, `- [x]` done. Append ` — <commit sha>` when a step lands.

### Phase 1: Policy and integration

- [x] 1.1 Define solo delivery policy and configure the repository. — f2bc7fb
- [x] 1.2 Connect review, local QA and merge through local extensions. — f2bc7fb

### Phase 2: Verification and delivery

- [x] 2.1 Verify consistency, publish review evidence and deliver the PR.
