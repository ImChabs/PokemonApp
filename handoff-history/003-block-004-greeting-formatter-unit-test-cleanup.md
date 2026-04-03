Next block name
BLOCK 004 - Greeting Formatter Unit Test Cleanup

Objective
Replace the remaining boilerplate unit-test wrapper with a formatter-focused test file and class while keeping the existing greeting behavior unchanged.

Relevant files
- AGENTS.md
- handoff/validation-report.md
- app/src/main/java/com/example/baseaiproject/GreetingFormatter.kt
- app/src/test/java/com/example/baseaiproject/ExampleUnitTest.kt
- scripts/validate-unit-tests.ps1

Constraints
- Keep the greeting output and production code behavior unchanged
- Limit the block to the existing pure Kotlin formatter unit-test surface
- Prefer focused naming and assertions over expanding test scope broadly
- Keep verification limited to the smallest meaningful unit-test target for the changed scope

What not to change
- Do not add new product features or navigation
- Do not expand into instrumentation coverage again
- Do not refactor app architecture or validation scripts unless the unit-test block exposes a concrete issue
- Do not broaden the formatter into a more complex formatting API

Done criteria
- The remaining boilerplate unit test file and class are replaced with formatter-focused naming
- The unit test still verifies the current neutral greeting behavior
- `handoff/validation-report.md` records the actual unit-test-target validation that was run for the block
- The repository remains on the same minimal single-screen app surface after the change

## Execution Recommendation
- Recommended reasoning effort: low
- Rationale: The next block is small and mechanical, with narrow unit-test cleanup and straightforward verification.
