# Project Identity Rename Skill Spec

## Purpose
Create a reusable agent skill that safely adapts a project created from a reusable base/template into a new project identity without damaging the reusable operating layer.

The future skill should handle project-identity customization work such as:
- visible app/product name changes
- package or namespace changes
- project-specific symbol/style/theme renames that are clearly part of the shipped identity
- targeted cleanup of leftover template references after the main rename is done

It should produce a correct, reviewable result rather than a broad mechanical replacement.

## Problems The Skill Should Solve
- A repository may still contain template identity values after initial scaffolding.
- Project identity is usually spread across multiple layers:
  - source package declarations
  - directory paths
  - imports
  - Gradle `namespace`
  - Gradle `applicationId`
  - manifest references
  - visible strings such as app name
  - tests
  - theme/style names and other app-facing symbols
- Some old references must be updated, but others must remain unchanged because they belong to:
  - reusable scripts
  - agent/tooling config
  - handoff/history/archive material
  - git history
  - generic base-layer wording that is intentionally reusable
- A prior partial rename can leave the repository in a transitional state where:
  - package declarations and folder paths disagree
  - `namespace` and `applicationId` disagree
  - source code uses one package while tests use another
  - app code still references old theme/style names
- Blind global replace creates avoidable risk, especially in reusable repos with historical and tooling material.

## Required Inputs
The future skill should require the caller to provide:
- current project identity, if known:
  - current visible app name
  - current package name
  - current project/root name if relevant
- target project identity:
  - target visible app name
  - target package name
  - target project/root name if desired
- explicit scope:
  - whether to update only shipped app identity
  - whether to include theme/style/symbol names
  - whether to include tests
  - whether to include project/root naming such as `settings.gradle`
- protected paths that must not be changed unless clearly required
- validation expectations:
  - minimum compile validation
  - whether unit tests should run

Optional but helpful inputs:
- known template name(s) or old identity strings to audit for
- allowed modules
- whether package-path relocation is expected

## Scope Boundaries
The skill should stay within project-identity customization and cleanup.

In scope:
- app-visible naming
- package declarations and matching directory paths
- imports
- `namespace`
- `applicationId`
- manifest references that depend on renamed symbols/styles/packages
- project-specific resource names that are clearly part of app identity
- tests that depend on renamed packages or symbols
- targeted audits for leftover old-identity references

Out of scope unless explicitly requested:
- feature work
- architecture changes
- refactors unrelated to identity
- dependency changes
- redesigns
- broad cleanup unrelated to the rename
- editing archived handoff/history material
- editing tooling/config/script assets that are intentionally reusable

## Recommended Execution Steps
1. Read the repository rules first.
2. Identify protected paths and do not plan changes there by default.
3. Search for all known identity strings before editing:
   - old visible name
   - old package name
   - old template name variants
   - target package name, to detect partial transitional renames
4. Classify every match into:
   - should update
   - should remain unchanged
   - needs manual judgment
5. Read the concrete files that control shipped identity:
   - Gradle module build file(s)
   - `AndroidManifest.xml` or equivalent app manifest/config
   - user-facing string resources
   - main source files
   - theme/style resources if they carry project identity
   - test source files
6. Check for transitional mismatches before editing:
   - package declarations vs folder paths
   - `namespace` vs `applicationId`
   - main sources vs test sources
   - manifest/style references vs actual style names
7. Apply only clearly correct project-identity updates.
8. If package names change, move files to matching directory paths instead of only changing `package` lines.
9. Re-run targeted searches after editing to confirm:
   - old project package is gone from the relevant scope
   - accidental intermediate package names are gone
   - renamed symbols/styles are consistently referenced
10. Run the smallest meaningful validation for the affected scope.
11. Report:
   - what changed
   - what was intentionally left unchanged
   - any ambiguous references that still need judgment
   - validation results

## Validation Steps
Minimum recommended validation for an Android app rename/customization flow:
- run targeted compile validation for the affected app/module
- run targeted unit tests if source packages, imports, or tests changed
- run a focused search to confirm no relevant old package references remain in the app/module

Recommended checks:
- `namespace` matches the intended package
- `applicationId` matches the intended package when requested
- source directory paths align with package declarations
- main and test packages are consistent
- manifest references resolve to existing styles/resources/symbols

Prefer targeted validation over broad rebuilds.

## Edge Cases And Pitfalls Discovered
- A repository may look renamed at first glance while still containing a transitional package in source code or Gradle config.
- `applicationId` can be correct while `namespace` is still wrong.
- Package paths can drift from package declarations.
- Tests are easy to miss and may still use the old package.
- App-specific theme/style names may continue to expose the template identity even after package and app-name renames.
- Manifest references may still point at old style names after those styles are renamed.
- Search results can include:
  - git logs
  - archived handoff/history files
  - reusable scripts
  - agent/tooling config
  These should not be edited automatically.
- An empty old package directory can remain after file moves and may need explicit cleanup.
- Validation after package moves can trigger Kotlin incremental-cache issues or daemon/cache warnings. These do not automatically mean the rename is wrong. If Gradle falls back and the build/tests still succeed, report the warning but do not treat it as a code failure unless validation actually fails.
- IDE metadata files may change independently during the work. Treat unrelated worktree changes as out of scope unless the user explicitly wants them included.

## What Must Stay Untouched To Preserve The Reusable Operating Layer
Unless the user explicitly requests otherwise, the future skill should avoid changing:
- repository-local agent infrastructure
- codex/agent configuration
- live handoff files
- archived handoff/history files
- reusable validation scripts
- reusable automation scripts
- git history/logs
- unrelated IDE metadata

More generally, leave alone anything that is:
- archival rather than live product code
- tooling/internal infrastructure rather than shipped app identity
- generic base-layer material intended to stay reusable across projects

If a protected file contains a reference that looks project-specific, treat it as manual-judgment territory unless the user explicitly expands scope.

## Decision Rules For Updates
Update a reference when all of the following are true:
- it affects shipped project identity or directly supports it
- it is not clearly archival/tooling/base-layer material
- the replacement is unambiguous
- the change improves consistency across package/config/resource boundaries

Do not update a reference when any of the following are true:
- it is in history/archive material
- it belongs to reusable scripts or internal tooling
- it is only historical provenance
- the correct replacement is unclear

Escalate for manual judgment when:
- a reusable script or tool contains a project-name string that might be either branding or an intentional base identifier
- a reference appears in generated or environment-specific files
- changing it could alter the operating layer rather than shipped identity

## Suggested Skill Output Structure
The future skill should report results using sections like:
- Findings summary
- Updated references
- Intentionally unchanged references
- Ambiguous/manual-review references
- Validation performed
- Final result

## Ready-To-Use Prompt For Another Repository
Paste this into the base/template repository when asking another agent to create the reusable skill:

```text
Create a reusable agent skill for this repository that safely performs project-identity rename/customization work for projects created from this base.

Use the markdown spec at `docs/project-identity-rename-skill-spec.md` as the source of truth for the skill behavior.

What I want:
1. Create the skill in the appropriate repository-local skill location.
2. Make the skill generic and reusable across future derived projects.
3. The skill must support:
   - visible app/product name changes
   - package/namespace/applicationId changes
   - package path relocation
   - imports/tests/manifest/resource updates required by the rename
   - targeted leftover-reference audits after the main rename
4. The skill must avoid blind global replacement and must classify matches into:
   - update
   - intentionally unchanged
   - manual judgment
5. The skill must preserve the reusable operating layer and avoid protected paths unless explicitly required.
6. The skill must include validation guidance and reporting guidance.

Constraints:
- Do not implement product features.
- Do not redesign architecture.
- Do not weaken or bypass the reusable operating layer.
- Keep the skill reviewable and conservative.

Deliver:
- the finished skill files
- a short explanation of how to invoke the skill
- any assumptions or repo-specific adjustments you had to make
```
