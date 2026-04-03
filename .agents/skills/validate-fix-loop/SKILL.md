---
name: validate-fix-loop
description: Run the smallest meaningful validation for the current block, fix in-scope failures, rerun the same validation, and record the final result in `handoff/validation-report.md`.
---

# Validate Fix Loop

## Purpose

- Make repository verification operational and repeatable for one implementation block.
- Run only the smallest meaningful validation for the changed scope.
- Fix only failures caused by the current block or a small required adjacent correction.
- Leave a clear validation artifact at `handoff/validation-report.md`.

## Inputs

- Read `AGENTS.md` first and follow its verification expectations.
- Read the current user request and `handoff/next-block.md` when it defines the active block.
- Use this skill alongside the implementation workflow. It does not replace the block handoff process.

## Validation Targets

- Use the repository validation defaults from `AGENTS.md` for shell-specific commands and default Gradle tasks.
- Use the default compile validation target for presentation, navigation, resource, and general Kotlin compile verification.
- When the changed scope is limited to `androidTest` or instrumentation or Compose UI test sources, use the same compile validation script with the `androidTest` compile override defined by `AGENTS.md`.
- Use the default unit-test validation target from `AGENTS.md` when domain or data logic changes or when adding or updating unit tests.
- Run one validation target per loop.
- If the changed scope genuinely needs both targets, finish the compile loop first and then start a separate unit-test loop.
- Keep selection explicit. Do not introduce broader validation unless the block clearly warrants it.

## Level 1 Loop Policy

1. Choose one smallest meaningful validation target for the current loop.
2. Start from `.agents/skills/validate-fix-loop/validation-report-template.md` and overwrite `handoff/validation-report.md` with the current block name, the selected target, the underlying command, and an initial `in_progress` status before or during the first run.
3. Run the selected validation script in the shell that matches the current environment, including any explicit Gradle task override needed for `androidTest` compile validation.
4. If it passes, record the pass result for that loop and stop the loop.
5. If it fails, decide whether the failure is caused by the current block or is a small required adjacent correction.
6. If the failure is in scope, fix it and rerun the same validation target.
7. Stop after a maximum of 3 total runs for that validation target.
8. If the validation still fails after the allowed runs, stop expanding scope, leave the code in the best coherent state reached, and document the unresolved failure clearly in `handoff/validation-report.md`.
9. If a second validation target is still required after the first loop passes, start a second loop and record it separately in the same report.
10. Do not introduce multi-agent orchestration, CI, background automation, or broader cleanup from this loop.

## Report Expectations

- `handoff/validation-report.md` is the live report for the current block and should be overwritten each block.
- `.agents/skills/validate-fix-loop/validation-report-template.md` is the reusable report template.
- Record for each loop:
  - target name
  - underlying command
  - why that target was chosen
  - per-run outcome
  - in-scope fixes applied between runs
  - final status
  - outstanding issues, if any
- Before finalizing the report, replace any placeholder or in-progress wording so skipped loops read clearly as `Not used.` or `Not run because <reason>.`
- Allowed final statuses:
  - `passed`
  - `passed_after_fix`
  - `failed_unresolved`
  - `not_run`

## Boundaries

- Keep fixes scoped to the active block and small required adjacent corrections only.
- Do not use the loop to justify unrelated refactors or repo-wide cleanup.
- If validation cannot be completed because of environment limits or unrelated breakage, document that clearly and stop.
