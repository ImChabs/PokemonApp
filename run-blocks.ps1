[CmdletBinding()]
param(
    [switch]$SaveLogs,
    [Parameter(Mandatory = $true, Position = 0)]
    [int]$BlockCount
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$PromptText = 'Use the existing repository skill: implement block and produce handoff. Continue from the current handoff/next-block.md, implement exactly one coherent block and verify the smallest meaningful affected scope.'

function Get-ArchiveFiles {
    param(
        [Parameter(Mandatory = $true)]
        [string]$ArchiveDir
    )

    if (-not (Test-Path $ArchiveDir)) {
            return @()
    }

        return @(Get-ChildItem -Path $ArchiveDir -File | Sort-Object Name)
}

function Get-ArchiveFileCount {
    param(
        [Parameter(Mandatory = $true)]
        [string]$ArchiveDir
    )

        return @(Get-ArchiveFiles -ArchiveDir $ArchiveDir).Count
}

function Get-HighestArchivePrefix {
    param(
        [Parameter(Mandatory = $true)]
        [string]$ArchiveDir
    )

    $highestPrefix = 0
    foreach ($file in Get-ArchiveFiles -ArchiveDir $ArchiveDir) {
        if ($file.Name -match '^([0-9]{3})-') {
            $prefixValue = [int]$Matches[1]
            if ($prefixValue -gt $highestPrefix) {
                $highestPrefix = $prefixValue
            }
        }
    }

    return $highestPrefix
}

function Test-ArchivePrefixUniqueness {
    param(
        [Parameter(Mandatory = $true)]
        [string]$ArchiveDir
    )

    if (-not (Test-Path $ArchiveDir)) {
        return $true
    }

    $duplicatePrefixes = @(
        Get-ArchiveFiles -ArchiveDir $ArchiveDir |
            ForEach-Object {
                if ($_.Name -match '^([0-9]{3})-') {
                    $Matches[1]
                }
            } |
            Group-Object |
            Where-Object { $_.Count -gt 1 } |
            ForEach-Object { $_.Name }
    )

    if ($duplicatePrefixes.Count -gt 0) {
        Write-Error ("Stopping safely: duplicate handoff-history archive prefixes detected: {0}" -f ($duplicatePrefixes -join ', '))
        return $false
    }

    return $true
}

function Test-ArchiveProgress {
    param(
        [Parameter(Mandatory = $true)]
        [string]$ArchiveDir,
        [Parameter(Mandatory = $true)]
        [int]$PreviousArchiveCount,
        [Parameter(Mandatory = $true)]
        [int]$PreviousHighestPrefix
    )

    if (-not (Test-ArchivePrefixUniqueness -ArchiveDir $ArchiveDir)) {
        return $false
    }

    $currentArchiveCount = Get-ArchiveFileCount -ArchiveDir $ArchiveDir
    $currentHighestPrefix = Get-HighestArchivePrefix -ArchiveDir $ArchiveDir

    if ($currentArchiveCount -ne ($PreviousArchiveCount + 1)) {
        Write-Error ("Stopping safely: expected exactly one new handoff-history archive file, but file count changed from {0} to {1}." -f $PreviousArchiveCount, $currentArchiveCount)
        return $false
    }

    if ($currentHighestPrefix -ne ($PreviousHighestPrefix + 1)) {
        Write-Error ("Stopping safely: expected the next handoff-history archive prefix to advance from {0:D3} to {1:D3}, but found {2:D3}." -f $PreviousHighestPrefix, ($PreviousHighestPrefix + 1), $currentHighestPrefix)
        return $false
    }

    return $true
}

function Format-Elapsed {
    param(
        [Parameter(Mandatory = $true)]
        [int]$TotalSeconds
    )

    return [TimeSpan]::FromSeconds($TotalSeconds).ToString('hh\:mm\:ss')
}

function Show-LiveStatus {
    param(
        [Parameter(Mandatory = $true)]
        [string]$SpinnerFrame,
        [Parameter(Mandatory = $true)]
        [int]$BlockIndex,
        [Parameter(Mandatory = $true)]
        [int]$RequestedBlockCount,
        [Parameter(Mandatory = $true)]
        [string]$DetectedReasoningEffort,
        [Parameter(Mandatory = $true)]
        [int]$ElapsedSeconds
    )

    Write-Host -NoNewline ("`r[{0}] Block {1}/{2} | elapsed {3} | effort {4}" -f $SpinnerFrame, $BlockIndex, $RequestedBlockCount, (Format-Elapsed -TotalSeconds $ElapsedSeconds), $DetectedReasoningEffort)
}

function Clear-LiveStatusLine {
    $padding = ' ' * 120
    Write-Host -NoNewline "`r$padding`r"
}

function Invoke-CodexWithLiveStatus {
    param(
        [Parameter(Mandatory = $true)]
        [string]$RepoRoot,
        [Parameter(Mandatory = $true)]
        [string]$ReasoningConfigOverride,
        [Parameter(Mandatory = $true)]
        [bool]$PersistLogs,
        [Parameter(Mandatory = $true)]
        [string]$LastMessageFile,
        [Parameter(Mandatory = $true)]
        [string]$LogFile,
        [Parameter(Mandatory = $true)]
        [int]$BlockIndex,
        [Parameter(Mandatory = $true)]
        [int]$RequestedBlockCount,
        [Parameter(Mandatory = $true)]
        [string]$DetectedReasoningEffort
    )

    $streamTarget = $LogFile
    $tempLogFile = $null
    if (-not $PersistLogs) {
        $tempLogFile = Join-Path ([System.IO.Path]::GetTempPath()) ("baseaiproject-run-blocks-{0}.log" -f ([guid]::NewGuid().ToString('N')))
        $streamTarget = $tempLogFile
    }

    $job = Start-Job -ScriptBlock {
        param(
            [string]$RepoRootArg,
            [string]$ReasoningConfigOverrideArg,
            [bool]$PersistLogsArg,
            [string]$LastMessageFileArg,
            [string]$PromptTextArg,
            [string]$StreamTargetArg
        )

        Set-Location $RepoRootArg

        if ($PersistLogsArg) {
                    & codex exec --json --color never --output-last-message $LastMessageFileArg --cd $RepoRootArg --skip-git-repo-check --sandbox workspace-write -c $ReasoningConfigOverrideArg $PromptTextArg *> $StreamTargetArg
        }
        else {
                    & codex exec --color never --cd $RepoRootArg --skip-git-repo-check --sandbox workspace-write -c $ReasoningConfigOverrideArg $PromptTextArg *> $StreamTargetArg
        }

        return $LASTEXITCODE
    } -ArgumentList $RepoRoot, $ReasoningConfigOverride, $PersistLogs, $LastMessageFile, $PromptText, $streamTarget

    $startTime = Get-Date
    $spinnerFrames = @('/', '-', '\', '|')
    $spinnerIndex = 0

    while ($job.State -eq 'Running' -or $job.State -eq 'NotStarted') {
        $elapsedSeconds = [int]((Get-Date) - $startTime).TotalSeconds
        Show-LiveStatus -SpinnerFrame $spinnerFrames[$spinnerIndex] -BlockIndex $BlockIndex -RequestedBlockCount $RequestedBlockCount -DetectedReasoningEffort $DetectedReasoningEffort -ElapsedSeconds $elapsedSeconds
        $spinnerIndex = ($spinnerIndex + 1) % $spinnerFrames.Count
        Start-Sleep -Seconds 1
        $job = Get-Job -Id $job.Id
    }

    Wait-Job -Job $job | Out-Null
    $jobOutput = @(Receive-Job -Job $job)
    $jobState = $job.State
    Remove-Job -Job $job
    Clear-LiveStatusLine

    if ($tempLogFile -and (Test-Path $tempLogFile)) {
        Remove-Item $tempLogFile -Force
    }

    if ($jobState -eq 'Failed') {
        return 1
    }

    if ($jobOutput.Count -eq 0) {
        return 0
    }

    return [int]$jobOutput[-1]
}

function Write-BlockManifest {
    param(
        [Parameter(Mandatory = $true)]
        [string]$BlockManifestFile,
        [Parameter(Mandatory = $true)]
        [int]$BlockIndex,
        [Parameter(Mandatory = $true)]
        [string]$DetectedReasoningEffort,
        [Parameter(Mandatory = $true)]
        [string]$ReasoningConfigOverride,
        [Parameter(Mandatory = $true)]
        [string]$Result,
        [Parameter(Mandatory = $true)]
        [string]$RunnerResult,
        [Parameter(Mandatory = $true)]
        [string]$ValidationStatus,
        [Parameter(Mandatory = $true)]
        [int]$ExitCode,
        [Parameter(Mandatory = $true)]
        [string]$LogFile,
        [Parameter(Mandatory = $true)]
        [string]$LastMessageFile,
        [Parameter(Mandatory = $true)]
        [string]$ValidationReportPath,
        [Parameter(Mandatory = $true)]
        [string]$TimestampUtc
    )

    $manifest = [ordered]@{
        block_number                   = $BlockIndex
        timestamp_utc                  = $TimestampUtc
        detected_reasoning_effort      = $DetectedReasoningEffort
        codex_config_override_requested = $ReasoningConfigOverride
        result                         = $Result
        runner_result                  = $RunnerResult
        validation_status              = $ValidationStatus
        exit_code                      = $ExitCode
        jsonl_log_path                 = $LogFile
        last_message_path              = $LastMessageFile
        validation_report_path         = $ValidationReportPath
    }

    $manifest | ConvertTo-Json -Depth 4 | Set-Content -Path $BlockManifestFile -Encoding UTF8
}

function Write-RunManifest {
    param(
        [Parameter(Mandatory = $true)]
        [string]$ManifestFile,
        [Parameter(Mandatory = $true)]
        [string]$RunId,
        [Parameter(Mandatory = $true)]
        [int]$RequestedBlockCount,
        [Parameter(Mandatory = $true)]
        [string]$CompletionStatus,
        [Parameter(Mandatory = $true)]
        [string[]]$ValidationStatuses,
        [Parameter(Mandatory = $true)]
        [string[]]$SummaryFiles
    )

    $manifest = [ordered]@{
        run_timestamp_utc       = $RunId
        requested_block_count   = $RequestedBlockCount
        executed_block_count    = $SummaryFiles.Count
        completion_status       = $CompletionStatus
        runner_completion_status = $CompletionStatus
        block_validation_statuses = $ValidationStatuses
        summary_artifact_paths  = $SummaryFiles
    }

    $manifest | ConvertTo-Json -Depth 4 | Set-Content -Path $ManifestFile -Encoding UTF8
}

function Get-DetectedReasoningEffort {
    param(
        [Parameter(Mandatory = $true)]
        [string]$HandoffPath
    )

    $match = Select-String -Path $HandoffPath -Pattern '^- Recommended reasoning effort:\s*(.+?)\s*$' | Select-Object -First 1
    if ($null -eq $match) {
        return 'medium'
    }

    $detectedEffort = $match.Matches[0].Groups[1].Value.ToLowerInvariant().Replace(' ', '')
    switch ($detectedEffort) {
        'low' { return 'low' }
        'medium' { return 'medium' }
        'high' { return 'high' }
        'xhigh' { return 'xhigh' }
        default { return 'medium' }
    }
}

function Resolve-CliReasoningEffort {
    param(
        [Parameter(Mandatory = $true)]
        [string]$DetectedEffort
    )

    if ($DetectedEffort -eq 'xhigh') {
        return 'high'
    }

    return $DetectedEffort
}

function Get-ValidationLoopStatuses {
    param(
        [Parameter(Mandatory = $true)]
        [string]$ValidationReportPath
    )

    if (-not (Test-Path $ValidationReportPath)) {
        return @()
    }

    return @(
        Select-String -Path $ValidationReportPath -Pattern '^- Final status:\s*(.+?)\s*$' |
            ForEach-Object { $_.Matches[0].Groups[1].Value.Trim().ToLowerInvariant() }
    )
}

function Get-ValidationStatusSummary {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$LoopStatuses
    )

    $normalized = @(
        $LoopStatuses |
            Where-Object { -not [string]::IsNullOrWhiteSpace($_) } |
            ForEach-Object { $_.Trim().ToLowerInvariant() }
    )

    if ($normalized.Count -eq 0) {
        return 'not_recorded'
    }

    if ($normalized -contains 'failed_unresolved') {
        return 'failed_unresolved'
    }

    if ($normalized -contains 'passed_after_fix') {
        return 'passed_after_fix'
    }

    if ($normalized -contains 'passed') {
        return 'passed'
    }

    if (($normalized | Where-Object { $_ -eq 'not_run' }).Count -eq $normalized.Count) {
        return 'not_run'
    }

    return 'mixed'
}

function Test-ValidationStatusAccepted {
    param(
        [Parameter(Mandatory = $true)]
        [string]$ValidationStatus
    )

    return $ValidationStatus -in @('passed', 'passed_after_fix')
}

function Get-RelativePathCompat {
    param(
        [Parameter(Mandatory = $true)]
        [string]$BaseDirectory,
        [Parameter(Mandatory = $true)]
        [string]$TargetPath
    )

    $normalizedBaseDirectory = [System.IO.Path]::GetFullPath($BaseDirectory)
    if (-not $normalizedBaseDirectory.EndsWith([System.IO.Path]::DirectorySeparatorChar) -and -not $normalizedBaseDirectory.EndsWith([System.IO.Path]::AltDirectorySeparatorChar)) {
        $normalizedBaseDirectory = "$normalizedBaseDirectory$([System.IO.Path]::DirectorySeparatorChar)"
    }

    $baseUri = [Uri]::new($normalizedBaseDirectory)
    $targetUri = [Uri]::new([System.IO.Path]::GetFullPath($TargetPath))
    return [Uri]::UnescapeDataString($baseUri.MakeRelativeUri($targetUri).ToString()).Replace('\', '/')
}

function Sanitize-LastMessageLinks {
    param(
        [Parameter(Mandatory = $true)]
        [string]$LastMessageFile,
        [Parameter(Mandatory = $true)]
        [string]$RepoRoot
    )

    if (-not (Test-Path $LastMessageFile)) {
        return
    }

    $content = Get-Content -Path $LastMessageFile -Raw
    $repoRootFullPath = [System.IO.Path]::GetFullPath($RepoRoot)
    $lastMessageDir = Split-Path -Parent (Resolve-Path $LastMessageFile)

    $updated = [System.Text.RegularExpressions.Regex]::Replace(
        $content,
        '\]\(([^)]+)\)',
        {
            param($match)

            $target = $match.Groups[1].Value
            if ($target -match '^[a-zA-Z][a-zA-Z0-9+\-.]*://') {
                return $match.Value
            }

            $candidatePath = $null
            if ($target -match '^/[A-Za-z]:/') {
                $candidatePath = $target.TrimStart('/')
            }
            elseif ($target -match '^[A-Za-z]:[\\/]') {
                $candidatePath = $target
            }
            else {
                $uri = $null
                if ([Uri]::TryCreate($target, [UriKind]::Absolute, [ref]$uri) -and $uri.IsFile) {
                    $candidatePath = $uri.LocalPath
                }
            }

            if ([string]::IsNullOrWhiteSpace($candidatePath)) {
                return $match.Value
            }

            try {
                $candidateFullPath = [System.IO.Path]::GetFullPath([Uri]::UnescapeDataString($candidatePath))
            }
            catch {
                return $match.Value
            }

            if (-not $candidateFullPath.StartsWith($repoRootFullPath, [System.StringComparison]::OrdinalIgnoreCase)) {
                return $match.Value
            }

            $relativePath = Get-RelativePathCompat -BaseDirectory $lastMessageDir -TargetPath $candidateFullPath
            return "]($relativePath)"
        }
    )

    if ($updated -ne $content) {
        Set-Content -Path $LastMessageFile -Value $updated -Encoding UTF8
    }
}

function Write-BlockResult {
    param(
        [Parameter(Mandatory = $true)]
        [int]$BlockIndex,
        [Parameter(Mandatory = $true)]
        [int]$RequestedBlockCount,
        [Parameter(Mandatory = $true)]
        [string]$Result,
        [Parameter(Mandatory = $true)]
        [string]$RunnerResult,
        [Parameter(Mandatory = $true)]
        [int]$ExitCode,
        [Parameter(Mandatory = $true)]
        [int]$DurationSeconds,
        [Parameter(Mandatory = $true)]
        [string]$ValidationStatus,
        [Parameter(Mandatory = $true)]
        [string]$DetectedReasoningEffort
    )

    if ($Result -eq 'success') {
        Write-Host ("[ok] Block {0}/{1} | runner {2} | validation {3} | duration {4} | effort {5}" -f $BlockIndex, $RequestedBlockCount, $RunnerResult, $ValidationStatus, (Format-Elapsed -TotalSeconds $DurationSeconds), $DetectedReasoningEffort)
    }
    else {
        Write-Host ("[x] Block {0}/{1} | runner {2} | validation {3} | duration {4} | effort {5} | error code {6}" -f $BlockIndex, $RequestedBlockCount, $RunnerResult, $ValidationStatus, (Format-Elapsed -TotalSeconds $DurationSeconds), $DetectedReasoningEffort, $ExitCode)
    }
}

function Write-FinalSummary {
    param(
        [Parameter(Mandatory = $true)]
        [int]$RequestedBlockCount,
        [Parameter(Mandatory = $true)]
        [int]$ExecutedBlockCount,
        [Parameter(Mandatory = $true)]
        [string]$CompletionStatus,
        [Parameter(Mandatory = $true)]
        [string[]]$ValidationStatuses,
        [Parameter(Mandatory = $false)]
        [string]$RunManifestFile = ''
    )

    $summary = "Run summary | requested $RequestedBlockCount | executed $ExecutedBlockCount | runner status $CompletionStatus"
    if ($ValidationStatuses.Count -gt 0) {
        $summary += " | validation statuses " + ($ValidationStatuses -join ',')
    }
    if (-not [string]::IsNullOrWhiteSpace($RunManifestFile)) {
        $summary += " | run manifest $RunManifestFile"
    }

    Write-Host $summary
}

if ($BlockCount -lt 1) {
    throw 'Block count must be a positive integer.'
}

$repoRoot = Split-Path -Parent $PSCommandPath
Push-Location $repoRoot
try {
    $runId = (Get-Date).ToUniversalTime().ToString('yyyyMMddTHHmmssZ')
    $runManifestFile = Join-Path 'automation-logs' "$runId-manifest.json"

    $runSummaryFiles = New-Object System.Collections.Generic.List[string]
    $runValidationStatuses = New-Object System.Collections.Generic.List[string]
    $completionStatus = 'stopped_early'
    $executedBlockCount = 0
    $archiveDir = 'handoff-history'
    $handoffPath = 'handoff/next-block.md'
    $validationReportPath = 'handoff/validation-report.md'

    New-Item -ItemType Directory -Force -Path 'automation-logs', 'automation-logs/summaries' | Out-Null
    if ($SaveLogs) {
        New-Item -ItemType Directory -Force -Path 'automation-logs/last-messages' | Out-Null
    }

    if (-not (Test-Path $handoffPath)) {
        Write-Error "Stopping safely: $handoffPath does not exist."
        Write-RunManifest -ManifestFile $runManifestFile -RunId $runId -RequestedBlockCount $BlockCount -CompletionStatus $completionStatus -ValidationStatuses $runValidationStatuses.ToArray() -SummaryFiles $runSummaryFiles.ToArray()
        Write-FinalSummary -RequestedBlockCount $BlockCount -ExecutedBlockCount $executedBlockCount -CompletionStatus $completionStatus -ValidationStatuses $runValidationStatuses.ToArray() -RunManifestFile $runManifestFile
        exit 1
    }

    if (-not (Test-ArchivePrefixUniqueness -ArchiveDir $archiveDir)) {
        exit 1
    }

    for ($blockIndex = 1; $blockIndex -le $BlockCount; $blockIndex++) {
        if (-not (Test-Path $handoffPath)) {
            Write-Error "Stopping safely before block ${blockIndex}: $handoffPath does not exist."
            Write-RunManifest -ManifestFile $runManifestFile -RunId $runId -RequestedBlockCount $BlockCount -CompletionStatus $completionStatus -ValidationStatuses $runValidationStatuses.ToArray() -SummaryFiles $runSummaryFiles.ToArray()
            Write-FinalSummary -RequestedBlockCount $BlockCount -ExecutedBlockCount $executedBlockCount -CompletionStatus $completionStatus -ValidationStatuses $runValidationStatuses.ToArray() -RunManifestFile $runManifestFile
            exit 1
        }

        $logFile = Join-Path 'automation-logs' "$runId-block-$blockIndex.jsonl"
        $lastMessageFile = Join-Path 'automation-logs/last-messages' "$runId-block-$blockIndex.md"
        $summaryFile = Join-Path 'automation-logs/summaries' "$runId-block-$blockIndex.json"
        $detectedReasoningEffort = Get-DetectedReasoningEffort -HandoffPath $handoffPath
        $requestedReasoningEffort = Resolve-CliReasoningEffort -DetectedEffort $detectedReasoningEffort
        $reasoningConfigOverride = "model_reasoning_effort=""$requestedReasoningEffort"""
        $blockStart = Get-Date
        $blockDurationSeconds = 0
        $exitCode = 0
        $result = 'success'
        $runnerResult = 'success'
        $validationStatus = 'not_recorded'
        $previousArchiveCount = Get-ArchiveFileCount -ArchiveDir $archiveDir
        $previousHighestPrefix = Get-HighestArchivePrefix -ArchiveDir $archiveDir
        $manifestLogFile = if ($SaveLogs) { $logFile } else { '' }
        $manifestLastMessageFile = if ($SaveLogs) { $lastMessageFile } else { '' }

        $exitCode = Invoke-CodexWithLiveStatus -RepoRoot $repoRoot -ReasoningConfigOverride $reasoningConfigOverride -PersistLogs $SaveLogs.IsPresent -LastMessageFile $lastMessageFile -LogFile $logFile -BlockIndex $blockIndex -RequestedBlockCount $BlockCount -DetectedReasoningEffort $detectedReasoningEffort
        if ($SaveLogs) {
            Sanitize-LastMessageLinks -LastMessageFile $lastMessageFile -RepoRoot $repoRoot
        }
        $validationStatus = Get-ValidationStatusSummary -LoopStatuses (Get-ValidationLoopStatuses -ValidationReportPath $validationReportPath)

        if ($exitCode -ne 0) {
            $result = 'failure'
            $runnerResult = 'failure'
            $blockDurationSeconds = [int]((Get-Date) - $blockStart).TotalSeconds

            $timestampUtc = (Get-Date).ToUniversalTime().ToString('yyyy-MM-ddTHH:mm:ssZ')
            Write-BlockManifest -BlockManifestFile $summaryFile -BlockIndex $blockIndex -DetectedReasoningEffort $detectedReasoningEffort -ReasoningConfigOverride $reasoningConfigOverride -Result $result -RunnerResult $runnerResult -ValidationStatus $validationStatus -ExitCode $exitCode -LogFile $manifestLogFile -LastMessageFile $manifestLastMessageFile -ValidationReportPath $validationReportPath -TimestampUtc $timestampUtc
            $runSummaryFiles.Add($summaryFile)
            $runValidationStatuses.Add($validationStatus)
            $executedBlockCount = $runSummaryFiles.Count
            Write-RunManifest -ManifestFile $runManifestFile -RunId $runId -RequestedBlockCount $BlockCount -CompletionStatus $completionStatus -ValidationStatuses $runValidationStatuses.ToArray() -SummaryFiles $runSummaryFiles.ToArray()
            if ($SaveLogs) {
                Write-Error "Block $blockIndex failed. Inspect $logFile and $lastMessageFile for details."
            }

            Write-BlockResult -BlockIndex $blockIndex -RequestedBlockCount $BlockCount -Result $result -RunnerResult $runnerResult -ExitCode $exitCode -DurationSeconds $blockDurationSeconds -ValidationStatus $validationStatus -DetectedReasoningEffort $detectedReasoningEffort
            Write-FinalSummary -RequestedBlockCount $BlockCount -ExecutedBlockCount $executedBlockCount -CompletionStatus $completionStatus -ValidationStatuses $runValidationStatuses.ToArray() -RunManifestFile $runManifestFile
            exit 1
        }

        $blockDurationSeconds = [int]((Get-Date) - $blockStart).TotalSeconds
        if (-not (Test-ArchiveProgress -ArchiveDir $archiveDir -PreviousArchiveCount $previousArchiveCount -PreviousHighestPrefix $previousHighestPrefix)) {
            $result = 'failure'
            $exitCode = 1

            $timestampUtc = (Get-Date).ToUniversalTime().ToString('yyyy-MM-ddTHH:mm:ssZ')
            Write-BlockManifest -BlockManifestFile $summaryFile -BlockIndex $blockIndex -DetectedReasoningEffort $detectedReasoningEffort -ReasoningConfigOverride $reasoningConfigOverride -Result $result -RunnerResult $runnerResult -ValidationStatus $validationStatus -ExitCode $exitCode -LogFile $manifestLogFile -LastMessageFile $manifestLastMessageFile -ValidationReportPath $validationReportPath -TimestampUtc $timestampUtc
            $runSummaryFiles.Add($summaryFile)
            $runValidationStatuses.Add($validationStatus)
            $executedBlockCount = $runSummaryFiles.Count
            Write-RunManifest -ManifestFile $runManifestFile -RunId $runId -RequestedBlockCount $BlockCount -CompletionStatus $completionStatus -ValidationStatuses $runValidationStatuses.ToArray() -SummaryFiles $runSummaryFiles.ToArray()

            Write-BlockResult -BlockIndex $blockIndex -RequestedBlockCount $BlockCount -Result $result -RunnerResult $runnerResult -ExitCode $exitCode -DurationSeconds $blockDurationSeconds -ValidationStatus $validationStatus -DetectedReasoningEffort $detectedReasoningEffort
            Write-FinalSummary -RequestedBlockCount $BlockCount -ExecutedBlockCount $executedBlockCount -CompletionStatus $completionStatus -ValidationStatuses $runValidationStatuses.ToArray() -RunManifestFile $runManifestFile
            exit 1
        }

        if (-not (Test-ValidationStatusAccepted -ValidationStatus $validationStatus)) {
            $result = 'failure'
            $exitCode = 1
            Write-Error "Block $blockIndex failed validation gating: recorded validation status '$validationStatus' is not accepted."
            $timestampUtc = (Get-Date).ToUniversalTime().ToString('yyyy-MM-ddTHH:mm:ssZ')
            Write-BlockManifest -BlockManifestFile $summaryFile -BlockIndex $blockIndex -DetectedReasoningEffort $detectedReasoningEffort -ReasoningConfigOverride $reasoningConfigOverride -Result $result -RunnerResult $runnerResult -ValidationStatus $validationStatus -ExitCode $exitCode -LogFile $manifestLogFile -LastMessageFile $manifestLastMessageFile -ValidationReportPath $validationReportPath -TimestampUtc $timestampUtc
            $runSummaryFiles.Add($summaryFile)
            $runValidationStatuses.Add($validationStatus)
            $executedBlockCount = $runSummaryFiles.Count
            Write-RunManifest -ManifestFile $runManifestFile -RunId $runId -RequestedBlockCount $BlockCount -CompletionStatus $completionStatus -ValidationStatuses $runValidationStatuses.ToArray() -SummaryFiles $runSummaryFiles.ToArray()
            Write-BlockResult -BlockIndex $blockIndex -RequestedBlockCount $BlockCount -Result $result -RunnerResult $runnerResult -ExitCode $exitCode -DurationSeconds $blockDurationSeconds -ValidationStatus $validationStatus -DetectedReasoningEffort $detectedReasoningEffort
            Write-FinalSummary -RequestedBlockCount $BlockCount -ExecutedBlockCount $executedBlockCount -CompletionStatus $completionStatus -ValidationStatuses $runValidationStatuses.ToArray() -RunManifestFile $runManifestFile
            exit 1
        }

        $timestampUtc = (Get-Date).ToUniversalTime().ToString('yyyy-MM-ddTHH:mm:ssZ')
        Write-BlockManifest -BlockManifestFile $summaryFile -BlockIndex $blockIndex -DetectedReasoningEffort $detectedReasoningEffort -ReasoningConfigOverride $reasoningConfigOverride -Result $result -RunnerResult $runnerResult -ValidationStatus $validationStatus -ExitCode $exitCode -LogFile $manifestLogFile -LastMessageFile $manifestLastMessageFile -ValidationReportPath $validationReportPath -TimestampUtc $timestampUtc
        $runSummaryFiles.Add($summaryFile)
        $runValidationStatuses.Add($validationStatus)
        $executedBlockCount = $runSummaryFiles.Count
        Write-BlockResult -BlockIndex $blockIndex -RequestedBlockCount $BlockCount -Result $result -RunnerResult $runnerResult -ExitCode $exitCode -DurationSeconds $blockDurationSeconds -ValidationStatus $validationStatus -DetectedReasoningEffort $detectedReasoningEffort
    }

    $completionStatus = 'completed'
    Write-RunManifest -ManifestFile $runManifestFile -RunId $runId -RequestedBlockCount $BlockCount -CompletionStatus $completionStatus -ValidationStatuses $runValidationStatuses.ToArray() -SummaryFiles $runSummaryFiles.ToArray()

    Write-FinalSummary -RequestedBlockCount $BlockCount -ExecutedBlockCount $executedBlockCount -CompletionStatus $completionStatus -ValidationStatuses $runValidationStatuses.ToArray() -RunManifestFile $runManifestFile
    exit 0
}
finally {
    Pop-Location
}
