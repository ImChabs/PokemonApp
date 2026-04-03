Next block name
BLOCK 002 - Sandbox-Safe Validation Setup

Objective
Make the repository's validation path runnable in the current sandboxed environment by updating the validation scripts or Gradle environment setup so targeted compile validation can reach the actual app task instead of failing during Gradle or Android home initialization.

Relevant files
- AGENTS.md
- handoff/validation-report.md
- scripts/validate-compile.ps1
- scripts/validate-unit-tests.ps1
- gradle.properties
- local.properties

Constraints
- Keep the block operational and narrowly focused on validation reliability
- Prefer script or environment setup changes over app architecture changes
- Preserve the existing minimal app behavior and test scope
- Reuse the smallest meaningful validation loop while iterating

What not to change
- Do not add product features
- Do not broaden the app UI beyond the existing neutral greeting surface
- Do not refactor unrelated build logic unless required to unblock targeted validation
- Do not edit workflow skills unless script-level fixes prove insufficient

Done criteria
- Targeted compile validation reaches and executes `:app:compileDebugKotlin`
- If compile passes, targeted unit-test validation is attempted for `:app:testDebugUnitTest`
- `handoff/validation-report.md` reflects the actual reruns and final status
- Any environment redirection added is explicit, local, and reviewable

## Execution Recommendation
- Recommended reasoning effort: medium
- Rationale: The next block is still bounded, but it requires build-environment diagnosis across the validation scripts and Android Gradle setup.
