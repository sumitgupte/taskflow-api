#!/usr/bin/env bash
#
# Facilitator tool. Force-recreates exercise-1..4 from the current main.
#
# Each exercise branch is an independent snapshot of main with the material
# that exercise's participants are supposed to produce themselves removed.
# Nothing is ever merged back; main is the single source of truth for shared
# content (source code, README, handouts, tickets).
#
# Usage:  ./scripts/rebuild-exercise-branches.sh [--push]
#
# WARNING: this rewrites exercise-1..4 and, with --push, force-pushes them.
# Do not run mid-session — participants' branches will diverge.

set -euo pipefail

# This script deletes itself on every branch it builds, so run from a copy
# rather than relying on an fd surviving the unlink.
if [[ "${REBUILD_REPO_DIR:-}" == "" ]]; then
  copy="$(mktemp -t rebuild-exercise-branches)"
  cp "$0" "$copy"
  REBUILD_REPO_DIR="$(cd "$(dirname "$0")/.." && pwd)" exec bash "$copy" "$@"
fi
cd "$REBUILD_REPO_DIR"

PUSH=false
[[ "${1:-}" == "--push" ]] && PUSH=true

if [[ -n "$(git status --porcelain)" ]]; then
  echo "Working tree is dirty. Commit or stash first." >&2
  exit 1
fi

BASE=main
git switch "$BASE"

# Material that must never reach a participant branch: the answer keys, and
# the tool that generates the branches.
strip_facilitator_only() {
  rm -rf docs/facilitator
  rm -f scripts/rebuild-exercise-branches.sh
}

# Everything Exercise 1 teaches participants to write, and Exercise 2 must not
# benefit from.
strip_instructions() {
  rm -rf .github
  rm -f PROJECT_OVERVIEW.md CHANGELOG.md docs/coding-guidelines.md
}

# Exercise 3 ships the instructions and the overview (its participants already
# "did" Exercise 1) but writes its own coding guidelines in step 5, and doesn't
# meet the refine/plan/implement prompt files until Exercise 4.
strip_exercise_3() {
  rm -rf .github/prompts
  rm -f docs/coding-guidelines.md
  cat > .github/copilot-instructions.md <<'INSTRUCTIONS'
# Copilot instructions

## Behavior

Always respond concisely. Lead with the answer; add background only if asked.

## Project context

See [PROJECT_OVERVIEW.md](../PROJECT_OVERVIEW.md) for the architecture, request
flow, conventions and known limitations of this project. Consult it before
answering questions about how the system fits together, and before proposing a
change that spans more than one class.
INSTRUCTIONS
}

build_branch() {
  local branch="$1"
  local message="$2"
  shift 2

  echo "── $branch"
  git switch -C "$branch" "$BASE" --quiet
  strip_facilitator_only
  "$@"
  git add -A
  git commit -q -m "$message"
}

build_branch exercise-1 \
  "Exercise 1 start state: no instructions, no project overview" \
  strip_instructions

build_branch exercise-2 \
  "Exercise 2 start state: no instructions (vibe coding)" \
  strip_instructions

build_branch exercise-3 \
  "Exercise 3 start state: instructions and overview, no coding guidelines" \
  strip_exercise_3

build_branch exercise-4 \
  "Exercise 4 start state: full instruction set plus refine/plan/implement prompts" \
  :

git switch "$BASE"

if $PUSH; then
  git push --force-with-lease origin exercise-1 exercise-2 exercise-3 exercise-4
  echo "Pushed."
else
  echo "Built locally. Re-run with --push to publish."
fi
