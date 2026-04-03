Next block name
BLOCK 003 - Neutral Greeting UI Test Coverage

Objective
Replace the remaining boilerplate instrumentation test with a focused Android UI test that exercises the existing neutral greeting surface without expanding app behavior.

Relevant files
- AGENTS.md
- handoff/validation-report.md
- app/src/main/java/com/example/baseaiproject/MainActivity.kt
- app/src/androidTest/java/com/example/baseaiproject/ExampleInstrumentedTest.kt
- scripts/validate-compile.ps1

Constraints
- Keep the app behavior and displayed copy unchanged
- Prefer a single focused UI assertion over broader instrumentation coverage
- Reuse the existing minimal Compose surface instead of introducing new architecture
- Keep verification limited to the smallest meaningful androidTest compile target for the changed scope

What not to change
- Do not add product features or navigation
- Do not refactor the validation scripts unless the UI-test block exposes a concrete script issue
- Do not broaden unit-test scope beyond the existing formatter coverage
- Do not replace the neutral greeting surface with a more complex layout

Done criteria
- The boilerplate package-name instrumentation test is replaced with a focused test of the neutral greeting UI
- The new test remains aligned with the current minimal app surface
- `handoff/validation-report.md` records the actual androidTest-target validation that was run for the block
- The repository remains on the minimal single-screen app surface after the change

## Execution Recommendation
- Recommended reasoning effort: medium
- Rationale: The next block is still small, but it needs careful Android test selection and narrow verification against the existing Compose surface.
