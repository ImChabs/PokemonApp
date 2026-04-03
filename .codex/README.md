# Repo-local Codex Level 2 Reviewer

This repository includes one optional project-local subagent:

- `validator_reviewer`
  - Use it only after a normal implementation block is complete and the Level 1 validation loop has already updated `handoff/validation-report.md`.
  - It is review-only and focuses on correctness, regressions, validation target choice, missing verification, and validation-report consistency.
  - It does not replace `AGENTS.md`.
  - It does not replace the existing project skills.

## How to invoke it

Start a new Codex session in this repository so the repo-local `.codex/config.toml` is loaded, then ask explicitly for the subagent.

Example request:

```text
Use the `validator_reviewer` subagent to review this completed block after Level 1 validation. Check correctness, regressions, whether the chosen validation target made sense, and whether `handoff/validation-report.md` matches what actually happened. Return concise findings only.
```

## Why this stays minimal

- There is exactly one project-local subagent.
- The Level 1 implementation and validation skills stay unchanged.
- The integration is explicit and optional.
- The subagent is configured as read-only with `approval_policy = "never"` and `sandbox_mode = "read-only"`.
