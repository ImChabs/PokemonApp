#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

repo_root=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
gradle_wrapper="$repo_root/gradlew"

if [[ ! -f "$gradle_wrapper" ]]; then
  printf 'Could not find gradle wrapper at %s\n' "$gradle_wrapper" >&2
  exit 1
fi

printf 'Running targeted unit-test validation: :app:testDebugUnitTest\n'

cd "$repo_root"
"$gradle_wrapper" ':app:testDebugUnitTest'
