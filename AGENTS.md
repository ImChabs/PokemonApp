# AGENTS.md

## Stack
- Native Android app
- Kotlin
- Jetpack Compose
- Material 3
- Gradle Kotlin DSL

Update this section if the repository stack changes in a durable way.

## Instruction Ownership
- `AGENTS.md` defines durable repository rules: structure, naming, state handling, dependency/design constraints, and verification expectations.
- `docs/blueprint.md` defines the current project's goals, scope, roadmap, and out-of-scope boundaries.
- `handoff/next-block.md` defines the immediate next implementation block when the block workflow is in use.
- `handoff/validation-report.md` is the live validation artifact for the current block when the validation workflow is in use.
- `handoff-history/` is archival only and should not be treated as the live source of truth unless historical lookup is actually needed.
- `docs/official-docs.md` is a selective reference index for official framework and library guidance when behavior is uncertain.
- `.agents/skills/` contains repository-local workflow skills. Treat them as operational guidance, not as a replacement for `AGENTS.md`. Some stack-specific skills define preferred template defaults for derived projects and apply when that stack or concern is actually in scope.
- `.codex/` contains optional repository-local agent configuration.

## Planning Inputs
- Read only the files needed for the current task.
- For product goals, feature scope, roadmap context, and overall app direction, consult `docs/blueprint.md` if present.
- For the immediate next implementation step, consult `handoff/next-block.md` if present.
- For the current block validation state, consult `handoff/validation-report.md` if present.
- When framework or library behavior is uncertain, prefer official documentation and consult `docs/official-docs.md` if present.
- Use `handoff-history/` only for archival or historical lookup when it is actually relevant.

## Project Structure
- Organize code primarily by feature once the project grows beyond a very small scaffold.
- A feature may contain `presentation`, `domain`, `data`, and `navigation` when that separation adds real clarity.
- Put truly shared or cross-cutting code in `core`.
- Do not move code into `core` unless it is used across multiple features or is clearly cross-cutting.
- Very small projects may keep a simpler structure until the second feature or similar complexity appears.

## Layer Rules
- `presentation`: screens, roots, viewmodels, UI state, UI actions, UI events.
- `domain`: domain models, use cases, repository contracts, business rules that should stay independent of UI and storage details.
- `data`: repository implementations, local/remote data sources, DTOs, entities, mappers, persistence integrations.
- `navigation`: route definitions and navigation wiring when navigation complexity justifies a dedicated area.
- `domain` must not depend on `data` or `presentation`.
- `presentation` consumes `domain`.
- `data` implements `domain` contracts.

## Screen Conventions
- If a screen needs a ViewModel or orchestration layer, prefer paired `<Screen>Root` and `<Screen>Screen` composables.
- Root naming should follow `<Screen>Root`.
- Keep `Screen` and `Root` in the same screen file unless the file becomes hard to maintain.
- Typical structure for a complex screen:
  - `FeatureScreen.kt`
  - `FeatureViewModel.kt`
  - `FeatureState.kt`
  - `FeatureAction.kt`
  - `FeatureEvent.kt`
- `State` should usually be a `data class`.
- `Action` and `Event` should usually be `sealed interface` types when multiple variants are needed.
- Previews belong to the presentational `Screen`.
- `Root` should not have a preview.
- Very simple screens may omit the root when no ViewModel or orchestration layer is needed.

## State Management
- Prefer immutable UI state.
- Use `StateFlow` for long-lived UI state when a ViewModel is present.
- Use `SharedFlow` or an equivalent one-off event mechanism for transient events.
- Prefer state hoisting in Compose.
- Presentational screens should receive state plus explicit callbacks or actions.
- Keep meaningful business logic out of composables.

## Use Cases
- Use cases are optional, not mandatory.
- Introduce them when they add real value through business logic, orchestration, reuse, clarity, or testability.
- Do not add trivial pass-through use cases that only delegate to a repository.

## Dependency And Design Rules
- Prefer official, stable Android libraries and the standard library first.
- Do not add dependencies without a clear need.
- Avoid unnecessary wrappers and abstractions.
- Prefer the right solution over the superficially smallest diff.
- Do not weaken architecture just to minimize diff size.
- Avoid overengineering.
- Do not touch unrelated files unless necessary for the task.

## Block Workflow Expectations
- When the repository is operating in block mode, complete exactly one coherent block per chat or execution unless the user explicitly asks for something else.
- Keep the live handoff concise, specific, and actionable.
- Treat `handoff/next-block.md` as the live next step and `handoff-history/` as append-only history.
- Next-block handoffs must include an `Execution Recommendation` section with both `Recommended reasoning effort` and `Recommended execution mode` guidance.
- Keep `handoff/validation-report.md` aligned with what was actually validated.

## Verification And Testing
- Always try to verify changes before considering the task complete.
- Use the smallest meaningful verification for the affected scope.
- Prefer focused module-level or target-level verification over full project builds.
- Repo defaults for this single-module Android base:
  - PowerShell/Windows compile: `.\scripts\validate-compile.ps1`
  - Bash/WSL compile: `bash scripts/validate-compile.sh`
  - Default targeted compile task: `:app:compileDebugKotlin`
  - For `androidTest`-only changes, use the same compile validation script with `:app:compileDebugAndroidTestKotlin`
  - PowerShell/Windows unit tests: `.\scripts\validate-unit-tests.ps1`
  - Bash/WSL unit tests: `bash scripts/validate-unit-tests.sh`
  - Default targeted unit-test task: `:app:testDebugUnitTest`
- If the build graph changes later, update these defaults rather than silently assuming they still fit.
- Use targeted instrumentation or Compose UI tests only when UI behavior changes warrant them.
- Avoid `clean` and full rebuilds unless truly necessary.
- Do not leave compile errors caused by the change.
- If verification could not be completed, state it explicitly.
- Add tests when they provide real value.
- Do not add ceremonial tests.
- Choose the test type that best matches the actual risk.
