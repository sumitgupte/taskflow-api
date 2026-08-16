#!/usr/bin/env bash
#
# Fitness functions: executable checks of quality properties, not of features.
# A coding guideline nobody can run is a wish. Everything enforced here is a
# guideline that graduated.
#
# Usage:  ./scripts/fitness.sh
# Exit 0 = all gates green.

set -uo pipefail
cd "$(dirname "$0")/.."

MVNW="./mvnw"
[[ "$(uname -s)" == MINGW* || "$(uname -s)" == MSYS* ]] && MVNW="./mvnw.cmd"

failed=()

run_gate() {
  local name="$1"
  shift
  printf '\n\033[1m▶ %s\033[0m\n' "$name"
  if "$@"; then
    printf '\033[32m✔ %s\033[0m\n' "$name"
  else
    printf '\033[31m✘ %s\033[0m\n' "$name"
    failed+=("$name")
  fi
}

run_gate "Tests pass" $MVNW -q test
run_gate "Code is formatted (Google Java Format)" $MVNW -q spotless:check
run_gate "Application context loads" $MVNW -q compile

printf '\n────────────────────────────────\n'
if [[ ${#failed[@]} -eq 0 ]]; then
  printf '\033[32mAll fitness functions green.\033[0m\n'
  exit 0
fi

printf '\033[31m%d gate(s) failed:\033[0m\n' "${#failed[@]}"
printf '  - %s\n' "${failed[@]}"
printf '\nFormatting failures are auto-fixable: %s spotless:apply\n' "$MVNW"
exit 1
