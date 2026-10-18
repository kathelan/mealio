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

Pending. This is a documentation/configuration change; validate JSON, relative
links, skill routing, positive/negative gate scenarios and the diff. Record CI
separately from checks actually performed locally.

## Progress

PR: #8

> Convention: `- [ ]` pending, `- [x]` done. Append ` — <commit sha>` when a step lands.

### Phase 1: Policy and integration

- [x] 1.1 Define solo delivery policy and configure the repository.
- [x] 1.2 Connect review, local QA and merge through local extensions.

### Phase 2: Verification and delivery

- [ ] 2.1 Verify consistency, publish review evidence and deliver the PR.
