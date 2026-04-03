# Automation Harness

This repository includes small wrappers for running a fixed number of sequential Codex implementation blocks.

Available runners:
- Bash or WSL: `./run-blocks.sh`
- PowerShell or Windows: `.\run-blocks.ps1`

## Usage

From the repository root:

```bash
./run-blocks.sh 3
```

```powershell
.\run-blocks.ps1 3
```

Optional artifact mode:

```bash
./run-blocks.sh --save-logs 3
```

```powershell
.\run-blocks.ps1 -SaveLogs 3
```

The harness:
- requires `handoff/next-block.md` to exist before starting and before each new block
- starts a fresh `codex exec` session for every block
- uses the repository handoff workflow already in place
- resolves the reasoning effort for each block from the current `handoff/next-block.md`
- leaves any `Recommended execution mode` value in the handoff as advisory metadata unless the runners are extended to consume it
- expects the per-block validation workflow inside those sessions to use the shell-native validation scripts in `scripts/`
- treats a block as successful only when the recorded validation status is acceptable by default (`passed` or `passed_after_fix`)
- relies on the installed Codex configuration for approval policy while keeping the compatible `--sandbox workspace-write` override
- passes `--skip-git-repo-check` so the harness can still be used while the base is not yet inside a Git repository
- keeps terminal output compact during normal runs
- always writes lightweight summary artifacts under `automation-logs/`
- enables the verbose JSONL and last-message artifacts only when `--save-logs` or `-SaveLogs` is passed explicitly

## Validation Environment Alignment

Choose the runner that matches the shell you want the block sessions to use.

If you run the Bash wrapper, the Level 1 validation workflow should use the bash-native repo scripts:
- `bash scripts/validate-compile.sh`
- `bash scripts/validate-unit-tests.sh`

If you run the PowerShell wrapper, the Level 1 validation workflow should use the PowerShell repo scripts:
- `.\scripts\validate-compile.ps1`
- `.\scripts\validate-unit-tests.ps1`

The compile validation script defaults to `:app:compileDebugKotlin` and also accepts an explicit Gradle task override such as `:app:compileDebugAndroidTestKotlin` when the smallest meaningful verification is an `androidTest` or instrumentation compile target.

## Validation Gating

- Acceptable recorded validation statuses are `passed` and `passed_after_fix`.
- `failed_unresolved`, `not_recorded`, `mixed`, and `not_run` are treated as harness failures by default.
- The final block result keeps both the runner outcome and the validation outcome visible in terminal output and summary artifacts.

## Archive History Guardrail

The harness checks `handoff-history/` before the run starts and after every successful block.

- It stops safely if duplicate numeric archive prefixes already exist.
- It expects each completed block to add exactly one new archive file.
- It expects that new archive file to use the next numeric prefix after the current maximum.

## Reasoning Effort Resolution

For each block, the script reads the live handoff source of truth at `handoff/next-block.md`.

The current runners only consume this repository convention:

`- Recommended reasoning effort: <value>`

If the handoff also includes:

`- Recommended execution mode: <plan_first|direct>`

that value is preserved as handoff guidance for humans or future workflow extensions, but the current runners do not parse or enforce it.

Resolution behavior:
- `low`, `medium`, and `high` are passed through directly to Codex CLI as `model_reasoning_effort`
- `xhigh` is mapped down to `high` for CLI execution
- missing or invalid values fall back conservatively to `medium`

The value is resolved again before every block, so if one block updates the handoff recommendation for the next block, the following `codex exec` run uses the new recommendation automatically.

Important wording:
- the printed and recorded detected reasoning effort is the value parsed from `handoff/next-block.md`
- the printed and recorded Codex config override is the exact `-c` request the harness passes to `codex exec`
- this is a trace of what the harness detected and requested, not a stronger guarantee about internal runtime behavior than the current Codex CLI surface exposes

## CLI Compatibility

This harness is aligned to the currently installed `codex exec` surface in this workspace.

- It does not pass a separate approval flag
- Approval policy is inherited from the existing Codex configuration
- The harness still passes `--sandbox workspace-write`, which keeps workspace-write execution explicit

## Runtime Artifacts

By default, the harness writes only lightweight summary artifacts under `automation-logs/`:

- `automation-logs/<utc-timestamp>-manifest.json` stores a run-level manifest for the full harness invocation
- `automation-logs/summaries/<utc-timestamp>-block-<n>.json` stores a small per-block run manifest artifact

When log saving is enabled, the harness also writes:
- `automation-logs/<utc-timestamp>-block-<n>.jsonl` stores the machine-readable Codex event stream for each block when using the Bash runner and the redirected runner log when using the PowerShell runner
- `automation-logs/last-messages/<utc-timestamp>-block-<n>.md` stores the last assistant message for each block, with repo-local absolute file links relativized when possible

Each run manifest JSON records:
- `run_timestamp_utc`
- `requested_block_count`
- `executed_block_count`
- `completion_status`
- `runner_completion_status`
- `block_validation_statuses`
- `summary_artifact_paths`

Each per-block run manifest JSON records:
- `block_number`
- `timestamp_utc`
- `detected_reasoning_effort`
- `codex_config_override_requested`
- `result`
- `runner_result`
- `validation_status`
- `exit_code`
- `jsonl_log_path`
- `last_message_path`
- `validation_report_path`

Compatibility note:
- `result` and `completion_status` are preserved for compatibility with earlier runs
- `result` reflects the overall per-block harness outcome after archive checks and validation gating
- `runner_result` preserves whether the underlying `codex exec` invocation itself succeeded
- `runner_completion_status` remains the run-level compatibility field for the harness completion state
- `validation_status` and `block_validation_statuses` summarize the statuses recorded in the live `handoff/validation-report.md` when available

## Terminal Output

During execution, the harness prints:
- one live heartbeat line per running block:
  - `[|] Block X/Y | elapsed HH:MM:SS | effort <value>`
- one final line per finished block:
  - success: `[ok] Block X/Y | runner success | validation <value> | duration HH:MM:SS | effort <value>`
  - failure: `[x] Block X/Y | runner <success|fail> | validation <value> | duration HH:MM:SS | effort <value> | error code <n>`
- one compact run summary line at exit that reports runner status separately from collected validation statuses

The heartbeat is intentionally indeterminate and does not claim percentage completion.

## Current Limitations

This first version is intentionally minimal and easy to audit.

- Only fixed-count execution is supported
- Run-until-complete is not supported
- The scripts assume `codex` is already installed, authenticated, and available in the selected shell PATH
- Validation target selection and in-block fixes remain the responsibility of the per-block Codex execution and the existing repository workflow; the harness only gates on the recorded final validation status
- Execution-mode recommendations in the handoff are currently advisory only and do not change runner behavior
- PowerShell-saved `.jsonl` logs can still show mojibake for some Unicode punctuation; this remains a known limitation because a safe fix would require a riskier change to process-output capture
