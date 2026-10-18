# Software delivery process

## Purpose

This file documents how work flows from ticket to merged PR in this repository. The agent skills configured in `.ai/agentic.config.json` enforce the process; humans read it here. PRs target `main`; issues and PRs live in github, with every tracker operation the skills run defined in `.ai/trackers/github.md` (edit that file to extend or override tracker behavior).

Work enters through two paths: a free-form task brief handed to an agent, or a filed ticket. Both converge on the same review loop, the same validation gate, and the same merge gates.

Before intake, the work is shaped: `om-brainstorm` turns a single idea or question into a routing decision and a brief, and the spec skills (`om-spec-writing`, `om-auto-write-spec`) turn a feature into a design document before anything is built. Those steps feed the table below; they are not the ticket flow itself.


## Roles

- **Author** — the human or agent who writes the change. Owns the ticket from claim to a merge-ready PR.
- **Reviewer** — reads the diff and approves or requests changes. May be a human or the `om-auto-review-pr` skill; the `om-code-review` checklist applies either way. In solo mode this is a distinct review pass after implementation, including for `risk-high`; no second person or account is required. The reviewer also signs off the spec when a feature requires one.
- **Designer** — owns the flow and its states before the code exists, and the design contract the UI review reads back. May be a human, `om-ux-shape` for the shaping, `om-ux-review-pr` for the pass over a PR's screens.
- **QA reviewer** — the agent running `om-auto-qa-pr` after review, or a person. Exercises the changed behavior locally, records observable results and signs off only a complete pass. Backend/API, browser and native iOS checks use the appropriate environment and evidence. See the solo delivery policy below.
- **Maintainer** — owns branch protection, the label taxonomy, the config, this document, and the installed skills with their repo-local overrides under `.ai/skills/`; arbitrates when gates conflict. Acts as the release manager unless the team names one.

## Ticket lifecycle

| Stage | What happens | Driven by | Done when |
|---|---|---|---|
| Discovery | An idea, question, or itch is talked through before any artifact exists: the problem is questioned, alternatives (including building nothing) are weighed, and the conversation ends in a routing decision — an answer, a filed ticket, a brief for a spec, or a direct change. | `om-brainstorm` or a human | Conversation routed; a brief written when the work continues |
| Intake | A ticket or task brief is filed in github with enough detail to act on: what is wrong or wanted, for whom, and what done looks like. `om-prepare-issue` files it with SDLC labels. | Anyone, `om-prepare-issue` | Ticket exists |
| Triage | Confirm the issue is real, still unfixed on `main`, and not already claimed or covered by an open PR. Read-only; stops the chain cleanly when there is nothing to do. | `om-verify-in-repo` or a human | Confirmed actionable, or closed as no-action |
| Claim | The author claims the ticket so concurrent agents back off. See the claim protocol below. | `om-fix` / `om-auto-create-pr`, or a human | Claim visible on the ticket |
| Design | For a user-facing change, the flow and its states are settled before the code exists: what the screen does when empty, loading, in error, and without permission, and what the change deliberately does not do. A ticket that touches no UI skips this stage. | `om-ux-shape`, or a human designer | The flow and its states are decided, or the ticket is not user-facing |
| Implement | Locate the minimal change surface (`om-root-cause`, read-only), then implement the change with regression tests and run the validation gate. Task briefs without a ticket go through `om-auto-create-pr`, which plans, implements phase by phase in an isolated worktree, and runs the same gate. | `om-root-cause` + `om-fix`, `om-auto-create-pr`, or a human author | Change complete, validation gate green |
| PR | Commit, push, and open a PR against `main` with normalized labels. On a hand-worked branch, `om-check-and-commit` runs the gate, fixes obvious drift, and pushes when green. | `om-open-pr`, `om-auto-create-pr`, or `om-check-and-commit` | Open, labeled PR |
| Review loop | The reviewer reads the diff against the `om-code-review` checklist and approves or requests changes. Requested changes are addressed (`om-auto-continue-pr` resumes agent PRs from the tracking plan, and adopts a PR that has none by reconstructing the plan from the PR's own context) and the PR is re-reviewed until approved. A user-facing change also gets a design pass: `om-ux-review-pr` walks the changed screens and reports findings ranked by user impact. That pass is advisory — it informs the review, it does not hold the merge. | `om-auto-review-pr` (single PR), `om-review-prs` (sweep), `om-ux-review-pr` (design pass), or a human | Current approving review or solo review assessment published |
| QA | A `needs-qa` PR automatically continues to `om-auto-qa-pr --self-qa-signoff --apply-failure`. Run local API/integration, browser or native scenarios as appropriate; publish evidence tied to the head commit. A failure returns to implementation and review. | Agent QA, with the appropriate local environment | Current PASS evidence and `qa-approved` + `qa-self-verified`; failure blocks merge |
| Merge | `om-merge-buddy` reports, read-only, which PRs can merge now and which are close but blocked. `om-approve-merge-pr` re-checks every gate, approves, and squash-merges. | `om-merge-buddy` + `om-approve-merge-pr`, or a human | PR squash-merged into `main` |
| Post-merge housekeeping | Close issues the merged PR fixes; comment on issues whose PRs were closed without merging; turn leftover asks or review comments into tracked follow-up issues. | `om-close-fixed-issues`, `om-followup-issue-from-pr` | Tracker reconciled, follow-ups filed |

After merge, this process stops. Deployment, smoke tests, monitoring, and rollback belong to the repository's release process, not to this document: the Maintainer — or a release manager, when the team names one — drafts the changelog with `om-auto-update-changelog` and reconciles the tracker with `om-close-fixed-issues`, and `om-pipeline-retro` reads finished runs to rank what second passes cost. Merge is where this document ends; delivering the change to users is a separate process the team owns.


## Label state machine

Pipeline labels are mutually exclusive: a PR carries at most one, and it names where the PR sits in the flow.

- A ready, non-draft PR carries `review`.
- The reviewer moves it: request changes → `changes-requested`; after fixes it returns to `review`; approval → `merge-queue`.
- `merge-queue` is routing, not proof of QA: a `needs-qa` PR legitimately sits there until QA signs off.
- Only a QA reviewer sets the `qa` pipeline label. They move a queued `needs-qa` PR from `merge-queue` to `qa` while testing, then back to `merge-queue` with `qa-approved` on pass, or to `qa-failed` on failure. In solo mode, agent QA uses its `in-progress` claim while testing and leaves `qa` for an active human QA session; it never overwrites that session.
- `blocked` and `do-not-merge` are set and cleared by humans and stop the flow wherever it is.

| Group | Labels | Exclusivity | Meaning |
|---|---|---|---|
| Pipeline | `review`, `changes-requested`, `qa`, `qa-failed`, `merge-queue`, `blocked`, `do-not-merge` | one at a time | Workflow state |
| Category | `bug`, `feature`, `refactor`, `security`, `dependencies`, `documentation` | additive | Kind of change |
| Meta | `needs-qa`, `skip-qa`, `qa-approved`, `qa-self-verified`, `in-progress`, `ci-monitoring` | additive | Process signals |
| Priority | `priority-low`, `priority-medium`, `priority-high`, `priority-extreme` | one at a time; unset = medium | Urgency of the work |
| Risk | `risk-low`, `risk-medium`, `risk-high` | one at a time; unset = medium | Blast radius of the change |

Priority is how urgent the work is; risk is how dangerous the change is to ship. A one-line fix for an outage can be `priority-extreme` and `risk-low`; a large auth refactor that can wait can be `priority-low` and `risk-high`. A PR inherits both from its source issue unless the scope clearly changed. When an automated skill adds or changes a pipeline or meta label, it leaves a short comment explaining why.

When no priority label is set, infer one:

- `priority-extreme` — production outage, data loss, or an active security incident.
- `priority-high` — security hardening or a release-blocking regression.
- `priority-medium` — ordinary bug fixes and net-new features (also the default reading of unset).
- `priority-low` — cosmetic, docs-only, dependency bumps, follow-up cleanup.

When no risk label is set, infer one:

- `risk-high` — authentication and login sessions, data scoping, money, schema migrations, incompatible shared contract changes, or broad cross-cutting edits. A compatible additive endpoint in one domain is normally medium; assess destructive effects and affected consumers explicitly rather than classifying every `/api/v1` edit as high.
- `risk-medium` — an ordinary single-area change that ships with tests (also the default reading of unset).
- `risk-low` — docs-only, test-only, typo, or isolated cosmetic changes.

When signals conflict, pick the higher label and say why in the label comment. A `risk-high` PR is not merely advised to get more scrutiny; it triggers gates:

| Area behind `risk-high` | What the PR must carry |
|---|---|
| Auth, sessions, permissions | an integration test for the denied path and the wrong-scope read; a distinct agent review pass in solo mode |
| Data scoping | an isolation test proving one scope cannot read another |
| Money | tests for the failure, retry, and idempotency paths; a distinct agent review pass in solo mode |
| Schema migrations | a migration test up and down, and a rollback plan in the PR body |
| Shared contract surfaces | the consuming side exercised, per `BACKWARD_COMPATIBILITY.md` |
| Any `risk-high` | `needs-qa` when user-facing; agent QA is permitted in solo mode with the evidence below and the area-specific evidence above. Missing required tests still blocks review. |

One label lives outside this taxonomy: `do-not-close`, applied by humans to issues that housekeeping skills must never auto-close. Skills only ever read it.

## The QA gate

The one hard rule of this process: **a PR carrying `needs-qa` must not merge until it also carries `qa-approved`, even when every other check is green.** `om-merge-buddy` classifies such a PR as blocked; `om-approve-merge-pr` refuses to merge it.

- Apply `needs-qa` to UI changes, new features, and other user-facing behavior that needs local execution beyond unit tests.
- For a UI change, QA covers more than "it works": the state matrix (default, empty, loading, error, no-permission, long content, narrow viewport) is part of the pass/fail, and so is conformance to the design contract in `.uxproof/` when the repository has one. A missing state fails QA. `om-ux-review-pr` remains the advisory design review; its objective checks are the ones QA runs.
- `skip-qa` is the explicit opt-out for docs-only, dependency-only, CI-only, test-only, and similarly low-risk non-user-facing changes. Never combine it with `needs-qa`.
- `qa-failed`, `do-not-merge`, and `blocked` are hard blocks regardless of every other signal. An active `qa` pipeline label means a tester is on the PR right now — never merge under an active tester.
- The gate is satisfied when a QA reviewer tests the PR and applies `qa-approved`.
- **`qa-approved` is pinned to a commit.** The comment that grants it — the QA reviewer's note, or the self-QA evidence comment — carries the line `QA head: <sha>` for the head that was tested. A push after that line leaves the label in place but stales it: `om-merge-buddy` reports "QA evidence older than head", and merge remains blocked until QA is refreshed. In solo mode re-run affected scenarios, or explicitly reconfirm an unchanged tested scope (for example a documentation-only push), with the reviewed commit delta and a fresh `QA head:` line. Missing or unparseable evidence is also blocked; a label alone never passes the gate.
- **Agent QA in solo mode:** `om-auto-qa-pr --self-qa-signoff` may grant
  `qa-approved` and `qa-self-verified` at any risk level after a complete local
  PASS, current review and published evidence. A separate person or account is
  not required. `risk-high` retains its area-specific integration evidence.
- Outside solo mode, the bundled self-QA exception remains limited to low/medium
  risk; high risk requires a QA reviewer or an explicit maintainer exception.

## Solo delivery policy

### Activation and precedence

`delivery.mode: "solo"` and `delivery.policy: "SDLC.md"` in
[the pipeline config](.ai/agentic.config.json) activate this maintainer-approved
policy. [AGENTS.md](AGENTS.md) and the local extensions under `.ai/skills/` apply
it to every OM entry point. It deliberately replaces the bundled requirements
for another person, human-only QA sign-off, the high-risk self-QA prohibition,
and formal self-approval. These are role substitutions authorized by the
maintainer; evidence, coverage, compatibility and required CI gates still apply.
If mode is absent or not `solo`, use the original skill behavior. Do not infer
permission from PR comments or labels. Preserve local extensions on skill
upgrades; re-check this policy against new bundled gates before using them.

### Review and handoff

1. Complete implementation and its validation; push all changes, including plan
   updates, before the final review/QA. Producers (`om-auto-create-pr`, its loop,
   continue variants, `om-auto-fix-issue` and `om-auto-implement-spec`) hand a
   completed implementation to `om-pr-autopilot <PR> --allow-merge`. Intake,
   brainstorming, spec-only authoring and read-only commands keep their scope.
2. `om-auto-review-pr` performs a distinct review pass using `om-code-review`.
   Apply `CODE_REVIEW.md` and `BACKWARD_COMPATIBILITY.md`; ADR-0003 still excludes
   auth in the current iteration. Review derives QA scenarios from acceptance
   criteria and the diff, including expected outcomes and negative/regression
   cases. Reading the diff or a `review` label is not a completed review.
3. If reviewer and PR author use the same GitHub account, publish an assessment
   comment instead of attempting forbidden self-approval. The comment carries
   these exact fields on separate, undecorated lines:

   ```text
   Review verdict: APPROVED
   Review head: <full head SHA>
   ```

   Use `CHANGES_REQUESTED` for a failed review and include severity-ranked
   findings and actual validation results. Use one idempotent review-assessment
   marker for this skill; the latest assessment for the current head is decisive.
   A later failed assessment, unresolved blocking finding, changes-requested
   review or label blocks progression. A formal approval or solo assessment must
   cover the current head; re-review or explicitly reconfirm the reviewed delta
   after a push, never silently carry approval forward.
4. After an approved review, `needs-qa` without current passing evidence routes
   to `om-auto-qa-pr <PR> --self-qa-signoff --apply-failure` automatically.
   This applies to backend-only and high-risk PRs too. `om-auto-fix-pr` and
   `om-pr-autopilot` must not stop merely to request a human sign-off. The QA
   review-first check recognizes a current assessment; it does not call review
   recursively when review has just handed off to QA.
5. QA never edits source. On a behavioral failure, clear old approval labels,
   record FAIL and set `qa-failed`. Return findings to the implementation/fix
   loop. After fixes, review again and explicitly re-run QA despite the old
   `qa-failed` label; only a complete PASS may clear it. Never clear `blocked`,
   `do-not-merge` or another actor's active `qa` session to advance the chain.
   A direct review invocation ends after QA; only the outer producer/autopilot
   continues to merge. Limit automatic repair to three cycles, then report.

### Local QA and evidence

Scope checks to actual behavior and risk. Run in an isolated PR worktree at the
reported SHA, using synthetic fixtures and disposable local backing services.
Never mutate production or external user data. Reuse an environment only after
checking it runs the tested revision. QA remains read-only on source.

| Surface | Required verification and evidence |
|---|---|
| Backend/API | Exercise the real HTTP boundary and relevant persistence/integrations. An integration test starting the actual Spring application and PostgreSQL Testcontainers qualifies; a mock-only unit test does not. Record requests or test methods, statuses, observable data effects, negative cases, commands and results. Browser screenshots are not required. |
| Web UI | Use `om-prepare-test-env` and the configured browser provider. Walk the changed scenario and relevant states; capture actual screenshots and assertions. Use integration/E2E coverage when the scenario crosses components. |
| Native iOS | Use a working iOS simulator and the repository's native UI/E2E runner. Record simulator, OS, app revision, interactions, assertions and screenshots. A browser or API check cannot establish native UI correctness. If unavailable, report BLOCKED; do not claim support was provisioned. |
| Mixed flow | Verify each affected surface and their interaction. Passing API checks cannot stand in for untested UI. |
| Docs/tooling only | Validate instructions, links, configuration and routing; no application UI screenshots are needed. Use `skip-qa` only after scope inspection establishes that execution of application behavior is unnecessary. |

For non-browser checks, skip browser installation, browser credentials and
browser-only environment preflight. Discover the existing module launch/test
commands and local test prerequisites from its README and test configuration;
use repository-native runners. Do not invent a browser dependency for API QA.
Simply rerunning unit tests, checking compilation, or attaching screenshots
without outcome assertions is insufficient. Reusing an existing integration/E2E
scenario is valid if QA runs it on the reviewed SHA and verifies it covers the
acceptance criteria; add tests only through the implementation step when needed.

Publish the report on the PR, with artifacts kept under `paths.qa` in an ignored
`artifacts_<runId>` directory. Upload UI screenshots through the tracker evidence
operation, and use an image-free comment for API evidence. Never put artifacts
on the implementation branch. Both `report.md` and `report.json` record the head,
environment, synthetic setup, scenarios, expected/observed outcomes, commands,
results and artifact references. A sign-off comment includes these exact lines:

```text
QA verdict: PASS
QA head: <full head SHA>
```

Every required scenario must pass. FAIL, BLOCKED, skipped required checks,
missing artifacts or an environment running another revision cannot earn approval.
An environment failure is BLOCKED, not proof of a behavioral defect; invalidate
old approval and report the missing prerequisite. Explicit `--evidence-only`
continues to suppress sign-off. Labels are applied through descriptor guards
only after the report is published and head freshness is rechecked.

### Merge and platform constraints

`om-merge-buddy`, `om-pr-autopilot` and `om-approve-merge-pr` recognize a current
solo assessment as the review gate when formal self-approval is impossible.
This is a local process verdict, not a GitHub approving review. Never create a
second account or claim GitHub recorded an approval. If branch protection or a
ruleset actually requires another account's approval, report that exact blocker;
do not change protection, force/admin merge, or bypass it automatically.

Merge only an open, ready, conflict-free PR with no active claim/QA session,
no unresolved blocking review findings or hard-block labels, current approving
review, current QA PASS when required, and all required CI checks genuinely green.
Check workflow runs for the exact head as well as reported jobs; pending, absent
expected or failing checks do not count as green. A final head change restarts
freshness checks. Bind the squash merge to the verified head through
`--match-head-commit <SHA>`; never use `--admin`. `skip-qa` and `needs-qa` together
are an inconsistency, and missing QA head/verdict is blocked, not legacy approval.
No extra confirmation is required solely because GitHub rejects self-approval.
After merge, `Closes`/`Fixes` links close implementing issues; housekeeping uses
`om-close-fixed-issues` when needed and never closes bare references.

## The claim protocol

Before mutating an issue or PR, an agent claims it with all three signals: it assigns itself, adds the `in-progress` label, and posts a claim comment saying what it is doing. Any agent that finds an existing claim backs off instead of colliding. A PR carrying `in-progress` is also skipped by the merge tooling.

`in-progress` means **actively working**. Once an agent's work is finished and fully reported — labels applied, review submitted, comments posted — it swaps `in-progress` for `ci-monitoring` if it still intends to report the CI outcome. `ci-monitoring` is **not** a claim and blocks nobody: it says only that the CI-result follow-up comment is still owed, so another agent or a human may act on the PR freely. That distinction matters because CI runs long: an agent that reported its work and then died while watching a run leaves an honest, self-describing state instead of a lock nobody holds. The label comes off when the follow-up lands, or when the agent gives up waiting at `ci.maxWaitMinutes` and says so.

The claim is released when the work finishes — on success and on failure alike. A stale `in-progress` with no recent activity may be cleared by the maintainer.

### Reporting is decoupled from CI

Agents apply labels, submit reviews, and post comments **as soon as their work is done**, without waiting for CI to go green. A review submitted while checks are still running says so in its body: branch protection plus the QA-approval gate hold the actual merge, and the approval covers the code, not a green run. The CI outcome arrives afterwards as a follow-up comment, which also corrects the pipeline label if the result changes the verdict.

The wait for that outcome is bounded by `ci.maxWaitMinutes` (default 40). When it expires with checks still running, the agent stops waiting, runs the local validation gate as its own evidence, posts that together with the still-pending check names and an explicit statement that no further follow-up is coming, drops `ci-monitoring`, and finishes.

A red signal does not short-circuit the review either. A failing required check or a conflicted head is collected as a **blocker finding** and reported together with the full code review, never instead of it: one review cycle gives the author the failing check, the conflict, and every code finding at once, rather than the cheapest red flag first and another cycle to discover the rest. Such a verdict is still `changes-requested` — completeness changed, the gate did not.

None of this touches the merge gates. Reporting early is safe; merging early is not — required checks still gate every merge, and the merge tooling refuses until they are genuinely green.


## The automation contract

The `om-auto-*` skills run this process unattended and are chainable: each accepts the artifact the previous one produced (an issue id, a spec path, or a PR number from the `PR: #<number> (link: <url>)` reference line every PR-producing skill emits), and each detects work already started — an open PR referencing the issue or plan — and continues on it rather than opening a duplicate. A completed autonomous run leaves a **ready** (non-draft), fully labeled PR — one pipeline label, category, QA meta, one priority, one risk — with a run-summary comment and, for user-facing changes, screenshots from the working app attached as PR evidence. Draft PRs are reserved for explicitly incomplete states: spec-only design PRs, interrupted hand-offs, or autonomous defaults flagged for human confirmation. Automation applies `qa-approved` only through the self-QA exception (`om-auto-qa-pr --self-qa-signoff`, always paired with `qa-self-verified`, never on a `risk-high` PR); no authoring, review, or merge skill ever applies it.

## Validation gate

Every PR passes the full validation gate before review sign-off, in this order:

- `mvn clean verify`

Any non-zero exit fails the gate and blocks the PR. The implementing skills run the gate before opening a PR, and `om-check-and-commit` runs it before pushing a hand-worked branch. The command list lives in `.ai/agentic.config.json`; when it changes, update it there and in this section together.

## Amending this process

This document and `.ai/agentic.config.json` describe the same process: change them together, and re-run the `om-setup-agent-pipeline` skill when the toolchain or label taxonomy changes.


The design contract the Design and Review stages read is set up once: `om-ux-setup` extracts it from the repository and is re-run when the design system changes.

Per-skill deviations — extra review rules, a different PR body template, an added gate step — belong in a repo-local skill of the same name at `.ai/skills/<skill-name>/SKILL.md`, which takes precedence over the installed skill (and can `@`-import or reference it to extend rather than replace it); local rules win, but a repo-local skill cannot grant what the installed skill's safety rules forbid.
