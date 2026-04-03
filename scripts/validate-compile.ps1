[CmdletBinding()]
param(
    [string]$GradleTask = ':app:compileDebugKotlin'
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$repoRoot = Split-Path -Parent $PSScriptRoot
$gradleWrapper = Join-Path $repoRoot 'gradlew.bat'

if ([string]::IsNullOrWhiteSpace($env:GRADLE_USER_HOME)) {
    $env:GRADLE_USER_HOME = Join-Path $repoRoot '.gradle-user-home'
}

if ([string]::IsNullOrWhiteSpace($env:ANDROID_USER_HOME)) {
    $env:ANDROID_USER_HOME = Join-Path $repoRoot '.android-user-home'
}

# AGP treats ANDROID_SDK_HOME as a deprecated preferences location and can fail if both are set.
if (-not [string]::IsNullOrWhiteSpace($env:ANDROID_SDK_HOME)) {
    Remove-Item Env:ANDROID_SDK_HOME
}

New-Item -ItemType Directory -Force -Path $env:GRADLE_USER_HOME, $env:ANDROID_USER_HOME | Out-Null

if (-not (Test-Path $gradleWrapper)) {
    throw "Could not find gradle wrapper at $gradleWrapper"
}

Push-Location $repoRoot
try {
    Write-Host "Running targeted compile validation: $GradleTask"
    & $gradleWrapper $GradleTask
    exit $LASTEXITCODE
}
finally {
    Pop-Location
}
