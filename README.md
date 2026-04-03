# BaseAiProject

BaseAiProject is a reusable Android base repository for future AI-assisted Android development. It is intentionally small: a minimal Jetpack Compose app scaffold plus a repo workflow layer for durable instructions, narrow validation, block handoffs, and reusable agent skills.

This repository is not a finished product and it is not trying to be a full app architecture out of the box. Its current purpose is to be a practical starting point that stays easy to verify, easy to hand off, and easy to specialize into a real Android project later.

## What This Repository Is

This repo combines two concerns:

- A minimal Android template built with Kotlin, Jetpack Compose, Material 3, and Gradle Kotlin DSL.
- An operational workflow base for agent-assisted development, including durable repo rules, validation scripts, handoff artifacts, and repo-local skills.

The Android side gives you a clean shell to start from. The workflow side gives you structure for making small, reviewable changes without losing continuity between sessions.

## Why It Exists

The repository exists to validate and reuse a disciplined development workflow in a real Android codebase without carrying product-specific assumptions.

Its main goals are:

- Keep a neutral Android starting point that can be reused for future projects.
- Preserve durable repository rules in one place.
- Encourage small implementation blocks with explicit handoff continuity.
- Prefer targeted validation over broad rebuilds.
- Provide repo-local operational skills that can guide future project growth.

## Current Stack And Scaffold State

Current stack in the repo:

- Native Android app
- Kotlin
- Jetpack Compose
- Material 3
- Gradle Kotlin DSL
- Android Gradle Plugin 9.1.0
- Kotlin 2.2.10
- minSdk 24, compileSdk 36, targetSdk 36

Current scaffold state:

- Single-module project with one `:app` module.
- Neutral template identity is still present: project name `BaseAiProject`, package `com.example.baseaiproject`.
- Minimal app surface: `MainActivity` renders a single greeting using a tiny pure Kotlin formatter.
- One unit test covers the formatter.
- One instrumented Compose test checks that the greeting is displayed.
- No navigation, persistence, networking, authentication, background work, or multi-module split yet.

That small surface is deliberate. The repo is currently optimized for operational reliability and future reuse, not product depth.

## Workflow Philosophy

The repo is designed around a few durable rules:

- Make the smallest correct change.
- Preserve existing behavior unless the task explicitly changes it.
- Read the relevant files first and follow local patterns.
- Validate the smallest meaningful affected scope.
- Keep handoff artifacts accurate and current.
- Avoid broad speculative architecture until the project actually needs it.

`AGENTS.md` is the main durable source of truth for these repo rules. It defines structure, naming, state handling, dependency expectations, and verification defaults.

`docs/blueprint.md` is different: it describes the current direction of this repository instance. Right now that direction is intentionally operational and minimal.

## Validation Workflow

Validation in this repo is intentionally narrow and script-driven.

Default validation commands:

```powershell
.\scripts\validate-compile.ps1
.\scripts\validate-unit-tests.ps1
```

```bash
bash scripts/validate-compile.sh
bash scripts/validate-unit-tests.sh
```

Defaults used by those scripts:

- Compile target: `:app:compileDebugKotlin`
- Unit test target: `:app:testDebugUnitTest`
- For `androidTest`-only changes, use the compile script with `:app:compileDebugAndroidTestKotlin`

The expected practice is:

- Choose the smallest meaningful validation target.
- Run that target first.
- Fix only in-scope failures.
- Record what actually happened in `handoff/validation-report.md`.

Windows note: in some locked-down environments, PowerShell script execution policy may block direct `.ps1` execution. In that case, run the same script with process-level bypass, for example:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\validate-compile.ps1
```

## Handoff And Block Workflow

This repository supports small block-based execution with live handoff files.

Key rules:

- `handoff/next-block.md` is the live source of truth for the immediate next block.
- The handoff includes an `Execution Recommendation` section with both reasoning-effort guidance and execution-mode guidance for the next block.
- `handoff/validation-report.md` is the live validation artifact for the current block.
- `handoff-history/` is append-only archive history.
- Blocks should stay narrow, coherent, and reviewable.

The repo also includes optional wrappers for running a fixed number of sequential Codex blocks:

```powershell
.\run-blocks.ps1 3
```

```bash
./run-blocks.sh 3
```

Those wrappers are documented in `docs/automation-harness.md`. They rely on the same handoff files and validation discipline already used in the repository.

## Key Files And Directories

- `AGENTS.md`: durable repository rules and verification defaults.
- `docs/blueprint.md`: current repo direction, scope, and roadmap.
- `docs/automation-harness.md`: how the block runners work.
- `docs/official-docs.md`: curated official Android references for uncertain framework behavior.
- `app/`: the current Android app scaffold.
- `scripts/`: targeted compile and unit-test validation scripts.
- `handoff/`: live next-block and validation artifacts.
- `handoff-history/`: archived block handoffs.
- `.agents/skills/`: repo-local workflow and stack guidance.
- `.codex/`: optional repo-local Codex configuration, including the read-only `validator_reviewer` subagent.
- `run-blocks.ps1` / `run-blocks.sh`: optional fixed-count block runners.

## Skills And Template Guidance

The skills under `.agents/skills/` are operational guidance, not proof that every described technology is already installed in the app.

The exact generated next-block handoff structure is defined by `.agents/skills/implement-block-and-produce-handoff/SKILL.md`.

Some skills describe preferred defaults for future derived projects, including:

- Compose UI patterns
- presentation/MVI structure
- testing guidance
- navigation
- data-layer patterns
- Koin DI
- error handling
- module structure
- project identity customization
- block implementation and validation loops

Use them as repo-local guidance when that concern becomes relevant. Do not read them as a claim that the current scaffold already includes Koin, Room, Ktor, multi-module architecture, or a full feature stack.

## Using This Repo As A Starting Point

A practical way to reuse this repository for a new Android project is:

1. Copy or template the repository.
2. Rename the shipped project identity conservatively: app name, package, namespace, `applicationId`, and related paths. The repo includes guidance for this in `.agents/skills/android-project-identity-customization/`.
3. Update `docs/blueprint.md` to reflect the real product direction for the derived project.
4. Keep `AGENTS.md` if its durable rules still fit, or revise it deliberately if your project needs different conventions.
5. Replace the tiny app shell incrementally instead of doing a broad rewrite all at once.
6. Continue using the repo validation scripts and handoff artifacts for each coherent block of work.

If you only want the Android scaffold, you can ignore most of the agent-specific layer and still use the app as a clean starting point. If you want continuity across AI-assisted sessions, keep the workflow files in place and treat them as part of the template.

## Important Notes

- This repo currently favors neutrality over product opinion.
- The current app code is intentionally minimal so workflow changes stay easy to validate.
- `AGENTS.md` has higher authority for durable repo behavior than any skill file.
- `handoff-history/` is archival, not the live source of truth.
- The optional `.codex/` setup adds local Codex behavior, but the repository structure and validation scripts remain useful even without it.
