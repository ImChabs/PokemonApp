# AGENTS.md

## Stack
- Native Android app
- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Ktor Client
- Kotlinx Serialization
- Gradle Kotlin DSL
- Single-module app

Update this section if the repository stack changes in a durable way.

## Project Identity
- This repository is a native Android practice app focused on learning and validating HTTP networking with Ktor against a real public Pokémon API.
- Prioritize networking quality, explicit UI state handling, and relevant tests over feature breadth or visual polish.
- Keep the project intentionally small, clear, and technically honest.

## Instruction Ownership
- `AGENTS.md` defines durable repository rules: structure, naming, layering, state handling, dependency/design constraints, and verification expectations.
- `docs/blueprint.md` defines the current project's goals, scope, roadmap, and out-of-scope boundaries.
- `handoff/next-block.md` defines the immediate next implementation block when the block workflow is in use.
- `handoff/validation-report.md` is the live validation artifact for the current block when the validation workflow is in use.
- `handoff-history/` is archival only and should not be treated as the live source of truth unless historical lookup is actually needed.
- `docs/official-docs.md` is a selective reference index for official framework and library guidance when behavior is uncertain.
- `.agents/skills/` contains repository-local workflow skills. Treat them as operational guidance, not as a replacement for `AGENTS.md`.
- `.codex/` contains optional repository-local agent configuration.

## Planning Inputs
- Read only the files needed for the current task.
- For product goals, scope, roadmap context, and app direction, consult `docs/blueprint.md`.
- For the immediate next implementation step, consult `handoff/next-block.md`.
- For the current block validation state, consult `handoff/validation-report.md`.
- When framework or library behavior is uncertain, prefer official documentation and consult `docs/official-docs.md`.
- Use `handoff-history/` only for archival or historical lookup when actually relevant.

## Implementation Priorities
1. Correct and understandable networking flow
2. Clear loading, success, error, and empty UI states
3. Strong timeout and error handling
4. Small, maintainable architecture
5. Relevant tests only
6. Minimal overengineering

## Scope Guardrails
- In scope:
  - paginated Pokémon list
  - search by Pokémon name
  - Pokémon detail screen
  - loading, error, empty, and success UI states
  - manual retry from the UI
  - timeout configuration
  - targeted unit tests
  - targeted Compose UI tests
- Out of scope unless the user explicitly changes direction:
  - authentication
  - POST/PUT/DELETE flows
  - local persistence
  - offline-first behavior
  - background sync
  - push notifications
  - multi-module architecture
  - aggressive caching
  - complex search/filter combinations beyond name search
  - broad or ceremonial test coverage
  - advanced design/polish work

## Project Structure
- Keep the project single-module for now.
- Organize code primarily by feature once the scaffold grows beyond a very small setup.
- A feature may contain `presentation`, `data`, `domain`, and `navigation` only when that separation adds real clarity.
- Put truly shared or cross-cutting code in `core`.
- Do not move code into `core` unless it is used across multiple features or is clearly cross-cutting.
- Search behavior should usually live with the Pokémon list feature unless it becomes complex enough to justify its own separation.

## Layering Approach
- Prefer a simple layered structure:
  - `presentation`: screens, roots, viewmodels, UI state, UI actions, UI events
  - `data`: repositories, remote data sources, DTOs, mappers, error translation
  - optional `domain`: repository contracts, app-facing models, use cases, business rules
  - optional `navigation`: routes, args, navigation wiring
- Do not force a `domain` layer for ceremony alone.
- Add repository contracts, app-facing models, or use cases only when they reduce coupling, improve clarity, or improve testability.
- DTOs and raw remote models must not leak directly into composables or public screen state when that creates unnecessary coupling.

## Networking Rules
- Use Ktor for real HTTP requests.
- Configure timeouts explicitly in the HTTP client.
- Model network responses with DTOs.
- Introduce mappers only when they meaningfully improve separation between API models and app-facing models.
- Keep error handling explicit at the repository/data boundary.
- Expose app-friendly results or states upward instead of raw transport details where possible.
- Keep retry user-driven in the UI.
- Prefer a single clear place for shared HTTP client configuration, serialization, and timeout setup.
- Avoid unnecessary wrappers around Ktor that do not add real value.

## Screen Conventions
- If a screen needs a ViewModel or orchestration layer, prefer paired `<Screen>Root` and `<Screen>Screen` composables.
- Root naming should follow `<Screen>Root`.
- Root handles collection, orchestration, and side effects.
- Screen stays presentational and receives state plus callbacks/actions.
- Keep `Screen` and `Root` in the same file unless the file becomes hard to maintain.
- Typical structure for a complex screen:
  - `PokemonListScreen.kt`
  - `PokemonListViewModel.kt`
  - `PokemonListState.kt`
  - `PokemonListAction.kt`
  - `PokemonListEvent.kt` only when one-off events are actually needed
- `State` should usually be a `data class`.
- `Action` and `Event` should usually be `sealed interface` types when multiple variants are needed.
- Previews belong to the presentational `Screen`.
- `Root` should not have a preview.
- Very simple screens may omit the root when no orchestration layer is needed.

## State Management
- Represent loading, success, error, and empty states explicitly when relevant.
- Prefer immutable UI state.
- Use `StateFlow` for long-lived UI state when a ViewModel is present.
- Use `SharedFlow` or an equivalent one-off event mechanism only for transient events that should not live in state.
- Prefer state hoisting in Compose.
- Keep meaningful business logic out of composables.
- Avoid hidden or ambiguous state transitions.

## Model And Mapping Rules
- DTOs exist to model the API.
- UI/domain-facing models should be introduced when they improve clarity or decouple screens from remote response structure.
- Do not duplicate models without a clear reason.
- Keep pagination and response transformation easy to trace.
- Prefer explicit naming that reflects whether a model is remote, domain-like, or UI-facing.

## Use Cases
- Use cases are optional, not mandatory.
- Introduce them when they add real value through orchestration, reuse, clarity, or testability.
- Do not add trivial pass-through use cases that only delegate to a repository.

## Dependency And Design Rules
- Prefer official, stable Android libraries and Ktor-supported libraries first.
- Do not add dependencies without a clear need tied to the current scope.
- Avoid unnecessary DI frameworks, caching layers, wrappers, or abstractions.
- Prefer the right solution over the superficially smallest diff.
- Do not weaken architecture just to minimize diff size.
- Avoid overengineering.
- Do not touch unrelated files unless necessary for the task.

## Testing Rules
- Add tests when they provide real value.
- Prioritize targeted unit tests for:
  - repository behavior
  - mapping logic
  - state transformation
  - error handling
  - timeout-related behavior
- Prioritize targeted Compose UI tests for:
  - loading state
  - error state
  - empty state
  - retry interactions
  - important success rendering
- Prefer deterministic tests.
- Use fakes or Ktor `MockEngine` when real network dependency would reduce reliability.
- Avoid real network calls in automated tests.
- Do not add broad coverage for trivial code.

## Block Workflow Expectations
- When the repository is operating in block mode, complete exactly one coherent block per chat or execution unless the user explicitly asks for something else.
- Keep the live handoff concise, specific, and actionable.
- Treat `handoff/next-block.md` as the live next step and `handoff-history/` as append-only history.
- Prefer blocks that map cleanly to the roadmap, such as:
  - Ktor/client foundation
  - list loading
  - search by name
  - detail screen
  - hardening and relevant tests
- Next-block handoffs must include an `Execution Recommendation` section with both `Recommended reasoning effort` and `Recommended execution mode` guidance.
- Keep `handoff/validation-report.md` aligned with what was actually validated.

## Verification And Testing
- Always try to verify changes before considering the task complete.
- Use the smallest meaningful verification for the affected scope.
- Prefer focused Gradle tasks over full project builds.
- Prefer repo-local validation scripts if they exist; otherwise use direct Gradle tasks.
- Default validation targets for this project:
  - compile app: `./gradlew :app:compileDebugKotlin`
  - compile android tests when needed: `./gradlew :app:compileDebugAndroidTestKotlin`
  - unit tests: `./gradlew :app:testDebugUnitTest`
  - connected tests only when UI changes justify them: `./gradlew :app:connectedDebugAndroidTest`
- Avoid `clean` and full rebuilds unless truly necessary.
- Do not leave compile errors caused by the change.
- If verification could not be completed, state it explicitly.