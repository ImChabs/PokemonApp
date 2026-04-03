---
name: android-project-identity-customization
description: Safely customize a derived Android project's shipped identity from a reusable base, including visible app name, package name, namespace, applicationId, package-path moves, tests, and leftover-reference audits. Use when renaming or rebranding an Android project without damaging the reusable operating layer.
---

# Android Project Identity Customization

## Purpose

- Adapt a project created from this reusable Android base to a new product identity.
- Update shipped identity conservatively and reviewably instead of doing blind global replacement.
- Preserve the reusable operating layer unless the user explicitly expands scope.

## Inputs

- Read `AGENTS.md` first and follow it as the repository source of truth for structure, boundaries, and validation.
- Read the current user request.
- Require these caller inputs before making rename edits:
  - current visible app name, if known
  - current package name, if known
  - target visible app name
  - target package name
  - explicit scope:
    - shipped app identity only, or broader identity cleanup
    - whether to rename theme/style/symbol names that expose the old identity
    - whether to include tests
    - whether to update project/root naming such as `settings.gradle.kts`
  - protected paths or files that must stay untouched
  - validation expectations:
    - minimum compile validation
    - whether unit tests should run
- Helpful optional inputs:
  - known template names or legacy identity strings to audit
  - allowed modules
  - whether `applicationId` should change with the package
  - whether package-path relocation is expected

## Scope Boundaries

- In scope when requested:
  - app visible name
  - package declarations
  - imports
  - Gradle `namespace`
  - Gradle `applicationId`
  - manifest references affected by the rename
  - project-specific resource, theme, or style names that are clearly part of shipped identity
  - main, `test`, and `androidTest` source paths and package directories
  - targeted audits for leftover old-identity references
- Out of scope unless explicitly requested:
  - feature work
  - architecture changes
  - dependency changes
  - broad cleanup unrelated to identity
  - redesigns
  - edits to archival, handoff, or history material

## Protected Areas

- Preserve the reusable operating layer by default.
- Do not update these areas unless the user explicitly expands scope:
  - `.agents/`
  - `.codex/`
  - `handoff/`
  - `handoff-history/`
  - reusable scripts such as `scripts/`
  - archival or history material
  - unrelated IDE metadata
- If a protected file contains a project-looking string, treat it as manual judgment unless the user explicitly wants it changed.

## Workflow

1. Confirm the rename inputs and explicit scope before editing. If key identity values are missing or ambiguous, stop and ask.
2. Search for all known identity strings before editing:
   - old visible app name
   - old package name
   - known template names or variants
   - target package name, to detect a partial earlier rename
3. Classify every relevant match into:
   - update
   - intentionally unchanged
   - manual judgment
4. Read the files that define shipped identity for the requested scope:
   - app module Gradle file(s)
   - `AndroidManifest.xml`
   - `res/values/strings.xml`
   - source and test files under the affected packages
   - theme/style/resource files if identity-bearing names are in scope
   - project/root files such as `settings.gradle.kts` only when the caller requested that scope
5. Check for transitional mismatches before editing:
   - package declarations vs directory paths
   - `namespace` vs `applicationId`
   - main sources vs test sources
   - manifest references vs actual style/resource names
6. Apply only clearly correct identity updates.
7. When the package changes, move source, `test`, and `androidTest` files into matching package directories instead of changing only the `package` declarations.
8. Update imports and manifest/style/resource references required by the rename.
9. Remove empty old package directories only when they are truly left behind by the move and are otherwise unused.
10. Re-run focused searches after editing to confirm:
    - the old package is gone from the intended editable scope
    - accidental intermediate package names are gone
    - renamed symbols, themes, and styles are referenced consistently
11. Report what changed, what stayed intentionally unchanged, any ambiguous references, and validation results.

## Android Rename Checklist

- Visible app name:
  - update user-facing string resources that define the shipped app name
  - update launcher label references only when they depend on renamed resources
- Package name:
  - update package declarations
  - update imports
  - move Kotlin source and test files into matching directories when needed
- `namespace`:
  - update the module Gradle namespace to the intended package when requested
- `applicationId`:
  - update only when the requested target identity requires it
  - keep it intentionally unchanged when the caller wants only code/package cleanup
- Styles and themes:
  - rename only project-specific identity-bearing symbols that the caller included in scope
  - update manifest and resource references together
- Tests:
  - include `test` and `androidTest` when requested or when package consistency requires it

## Search And Classification Rules

- Prefer targeted searches over repo-wide blind replacement.
- Treat a match as `update` only when all of these are true:
  - it affects shipped identity or directly supports it
  - it is not clearly tooling, archival, or reusable base-layer material
  - the intended replacement is unambiguous
- Treat a match as `intentionally unchanged` when it belongs to:
  - reusable automation
  - agent or tooling infrastructure
  - archival or historical records
  - unrelated environment-specific files
- Treat a match as `manual judgment` when:
  - it appears in a protected file
  - it could change operating-layer behavior
  - the correct replacement is unclear

## Validation

- Always run the smallest meaningful validation for the affected scope.
- Minimum validation after a rename/customization flow:
  - targeted compile validation for the affected Android module
  - focused search confirming no relevant old package references remain in the editable app scope
- Also run targeted unit tests when package changes, imports, business logic files, or test files changed.
- Prefer the repository validation defaults from `AGENTS.md`.
- Do not broaden to full rebuilds unless the requested scope or a concrete failure requires it.
- If Gradle emits cache or daemon warnings after package moves but the requested validation still passes, report the warning without treating it as a rename failure.

## Output Expectations

- Summarize the result using sections like:
  - findings summary
  - updated references
  - intentionally unchanged references
  - ambiguous or manual-review references
  - validation performed
  - final result
- If helpful, start from `rename-report-template.md` in this skill directory and fill it in for the current rename task.

## Boundaries

- Do not use this skill to justify unrelated refactors.
- Do not rewrite protected paths just to remove every old string in the repository.
- Do not perform a rename when the target identity is underspecified.
- Favor a smaller, correct, reviewable set of edits over exhaustive but risky search-and-replace behavior.
