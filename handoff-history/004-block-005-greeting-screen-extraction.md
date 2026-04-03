Next block name
BLOCK 005 - Greeting Screen Extraction

Objective
Move the greeting composable and preview out of `MainActivity.kt` into a dedicated screen file so the minimal app surface follows the repository's screen organization guidance without changing visible behavior.

Relevant files
- AGENTS.md
- handoff/validation-report.md
- app/src/main/java/com/example/baseaiproject/MainActivity.kt
- app/src/main/java/com/example/baseaiproject/GreetingScreen.kt
- scripts/validate-compile.ps1

Constraints
- Keep the current greeting text, preview output, and app launch behavior unchanged
- Limit the block to presentation-file organization for the existing single-screen surface
- Follow the existing Compose patterns already present in the app
- Keep verification limited to the smallest meaningful compile target for the changed production Kotlin source

What not to change
- Do not introduce a ViewModel, navigation, or new UI behavior
- Do not expand the formatter into a broader API
- Do not change app theme files or Gradle configuration unless the extraction requires a small in-scope fix
- Do not add new tests unless the extraction exposes a concrete need

Done criteria
- `MainActivity.kt` only owns activity setup and screen invocation
- The greeting composable and preview live in a dedicated `GreetingScreen.kt` file
- The app still compiles with the same single-screen behavior
- `handoff/validation-report.md` records the actual compile validation run for the block

## Execution Recommendation
- Recommended reasoning effort: low
- Rationale: The next block is a small presentation-file extraction with straightforward compile-only verification.
