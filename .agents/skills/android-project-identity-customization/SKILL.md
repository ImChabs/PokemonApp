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
  - current package name, if known; otherwise discover it from the editable app files before asking
  - target visible app name
  - target package name, if explicitly provided
  - explicit scope overrides, if any:
    - shipped app identity only instead of the normal full identity rename flow
    - whether to keep `namespace` or `applicationId` intentionally different from the target package
    - whether to skip test-source moves
    - whether to skip live operational doc/config cleanup
    - whether to keep project/root naming such as `settings.gradle.kts` intentionally unchanged
  - protected paths or files that must stay untouched
  - validation expectations:
    - minimum compile validation
    - whether unit tests should run
- Helpful optional inputs:
  - known template names or legacy identity strings to audit
  - allowed modules
  - whether theme/style/symbol renames should differ from the default identity cleanup behavior
  - whether package-path relocation is expected

### Default package derivation

- If the caller provides a new project/app name but does not provide a target package name, derive the target package by default.
- Derive it by:
  - reading the current package name
  - preserving the existing package prefix
  - replacing the trailing app-identity portion with a normalized suffix derived from the target visible app name
- Normalize the target visible app name into a package suffix by:
  - lowercasing it
  - removing spaces, hyphens, underscores, and other punctuation
  - prefixing with `app` if the result does not start with a letter
- If the old identity clearly appears in the trailing package portion, replace that trailing identity portion.
- If the old identity is not clearly represented in multiple trailing segments, replace only the last package segment.
- Example: `com.example.baseaiproject` + `PokemonApp` -> `com.example.pokemonapp`
- If the current prefix is still generic, such as `com.example`, keep that prefix by default, apply the derived suffix, and report that the prefix remains generic and can be customized later.

## Scope Boundaries

- In scope by default for a normal identity rename:
  - app visible name
  - package declarations
  - imports
  - Gradle `namespace`
  - Gradle `applicationId`
  - manifest references affected by the rename
  - project-specific resource, theme, or style names that are clearly part of shipped identity
  - Kotlin symbol names that clearly expose the shipped identity, such as app-branded theme or composable names
  - main, `test`, and `androidTest` source paths and package directories
  - live operational docs or config references that expose the shipped identity
  - targeted audits for leftover old-identity references
- Out of scope unless explicitly requested:
  - feature work
  - architecture changes
  - dependency changes
  - broad cleanup unrelated to identity
  - redesigns
  - edits to archival or history material

## Protected Areas

- Preserve the reusable operating layer by default.
- During a normal identity rename, update clearly identity-bearing references in these live operational/config areas only when the replacement is unambiguous and directly supports the shipped rename:
  - `handoff/`
  - `.codex/`
  - `README.md`
  - reusable scripts such as `scripts/`
  - project/root files such as `settings.gradle.kts`
- Treat these areas as manual judgment unless the caller explicitly expands scope further:
  - `.agents/`
  - unrelated IDE metadata
- Treat these archival/history areas as intentionally unchanged by default, even during broader cleanup:
  - `handoff-history/`
  - archival or history material
- If a protected file contains a project-looking string, treat it as manual judgment unless the user explicitly wants it changed.

## Workflow

1. Confirm the rename inputs and any explicit scope overrides before editing. If key identity values are missing or ambiguous, stop and ask.
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
   - project/root files such as `settings.gradle.kts` when they participate in the shipped identity
   - live docs/config files such as `README.md`, `handoff/`, or `.codex/` when they expose the shipped identity and were not explicitly excluded
5. If live operational cleanup includes repo-description docs, read `docs/blueprint.md` before editing them so product wording stays factual.
6. If the caller did not provide a target package name, derive it using the default package-derivation rule.
7. Treat the derived or explicit target package as the default for:
   - package declarations
   - source directory moves
   - `test` and `androidTest` directory moves
   - Gradle `namespace`
   - Gradle `applicationId`
8. Only keep `namespace` or `applicationId` different from the target package when the caller explicitly requested that difference.
9. If the current package prefix is generic, such as `com.example`, still apply the derived package by default and report the generic prefix as an intentional follow-up consideration, not a blocker.
10. Check for transitional mismatches before editing:
   - package declarations vs directory paths
   - `namespace` vs `applicationId`
   - main sources vs test sources
   - manifest references vs actual style/resource names
11. Apply only clearly correct identity updates.
12. When the package changes, move source, `test`, and `androidTest` files into matching package directories instead of changing only the `package` declarations, unless the caller explicitly excluded those moves.
13. Update imports and manifest/style/resource references required by the rename.
14. During the normal full identity rename flow, remove stale old-identity mentions from live prose such as `handoff/validation-report.md` unless the old name is intentionally being documented for comparison or transition context.
15. Remove empty old package directories only when they are truly left behind by the move and are otherwise unused.
16. Re-run focused searches after editing to confirm:
    - the old package is gone from the intended editable scope
    - accidental intermediate package names are gone
    - renamed symbols, themes, and styles are referenced consistently
    - stale old-identity mentions are gone from live editable prose when live cleanup was in scope
17. Report what changed, what stayed intentionally unchanged, any ambiguous references, and validation results.

## Android Rename Checklist

- Visible app name:
  - update user-facing string resources that define the shipped app name
  - update launcher label references only when they depend on renamed resources
- Package name:
  - update package declarations
  - update imports
  - move main Kotlin source files into matching directories
  - move `test` and `androidTest` Kotlin source files into matching directories by default when the package changes
  - skip test-source moves only when the caller explicitly excludes them
- `namespace`:
  - update the module Gradle namespace to the target package by default
  - keep it intentionally different only when the caller explicitly requested that difference
- `applicationId`:
  - update the module `applicationId` to the target package by default
  - keep it intentionally different only when the caller explicitly requested that difference
- Styles and themes:
  - rename project-specific identity-bearing symbols by default as part of the shipped identity update
  - update manifest and resource references together
- Branded Kotlin symbols:
  - rename Kotlin symbols that clearly surface the old shipped identity by default unless the caller explicitly excluded that cleanup
  - update usages consistently together with related package, theme, or resource renames
- Live operational cleanup:
  - update live docs/config files that still expose the old shipped identity unless the caller explicitly excluded that cleanup
  - keep repo-description updates factual and aligned with `docs/blueprint.md`
- Tests:
  - include `test` and `androidTest` package declarations, imports, and directory moves by default when the package changes
  - validate the moved tests when the repository validation defaults make that practical

## Search And Classification Rules

- Prefer targeted searches over repo-wide blind replacement.
- Treat a match as `update` only when all of these are true:
  - it affects shipped identity or directly supports it
  - it is not clearly archival material or reusable base-layer material that the caller did not include
  - the intended replacement is unambiguous
- Treat package, `namespace`, `applicationId`, and main/test/androidTest package-path updates as part of the normal identity rename unless the caller explicitly opted out.
- Treat stale old-identity mentions in live validation or handoff prose as `update` when they are incidental leftovers from the rename target, not intentional historical context.
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
- Do not rewrite archival history by default, even when the user requests broader cleanup.
- Do not perform a rename when the target identity is underspecified.
- Do not ask the caller to separately approve a package rename, `namespace` update, `applicationId` update, or main/test/androidTest package-path move when those values can be derived unambiguously from the default package-derivation rule.
- Do not derive a new organization/domain prefix unless the caller explicitly requested one; preserve the existing prefix by default.
- Do not turn broader cleanup into speculative product rewriting; keep live doc cleanup aligned with `docs/blueprint.md` and other explicit project sources.
- Favor a smaller, correct, reviewable set of edits over exhaustive but risky search-and-replace behavior.
