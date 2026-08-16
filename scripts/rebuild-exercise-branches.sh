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

  # Drop the "Coding guidelines" section from main's instructions rather than
  # rewriting the file, so edits to the behavior and project-context sections on
  # main propagate here instead of silently drifting.
  local tmp
  tmp="$(mktemp)"
  awk '/^## Coding guidelines/ { exit } { print }' .github/copilot-instructions.md > "$tmp"
  # awk leaves the blank line that separated the sections; trim trailing blanks.
  printf '%s\n' "$(cat "$tmp")" > .github/copilot-instructions.md
  rm -f "$tmp"
}

# Keep only this exercise's handout. Reading ahead spoils the arc: the whole
# design depends on Exercise 2 failing before Exercise 3 shows why, and on
# nobody meeting refine -> plan -> implement until Exercise 4.
keep_only_handout() {
  local keep="docs/exercises/exercise-$1.md"
  find docs/exercises -name '*.md' ! -path "$keep" -delete
}

# Keep only the ticket(s) this exercise works from. Exercise 1 has no ticket at
# all, so its docs/tickets/ goes away entirely.
keep_only_tickets() {
  if [[ $# -eq 0 ]]; then
    rm -rf docs/tickets
    return
  fi
  local keep=("$@") f base
  for f in docs/tickets/*.md; do
    base="$(basename "$f")"
    [[ " ${keep[*]} " == *" $base "* ]] || rm -f "$f"
  done
}

# Fitness functions are introduced in Exercise 3 step 5, as the payoff of having
# just written coding guidelines. Shipping the script earlier pre-answers the
# "what would actually enforce this?" discussion that step exists to have —
# Exercises 1 and 2 only ever run ./mvnw test.
strip_fitness() {
  rm -f scripts/fitness.sh
  local tmp
  tmp="$(mktemp)"
  awk '
    /<!-- COMMANDS-START -->/ { skip = 1 }
    /<!-- COMMANDS-END -->/   { skip = 0
      print "```bash"
      print "./mvnw test                 # run the test suite"
      print "./mvnw spring-boot:run      # start the API on :3000"
      print "./mvnw spotless:apply       # auto-format (Google Java Format)"
      print "```"
      next
    }
    !skip { print }
  ' README.md > "$tmp"
  mv "$tmp" README.md
}

# The README's branch table links to all four handouts, which don't exist on a
# scoped branch. Swap it for a "you are here" block naming only this exercise.
set_readme_nav() {
  local n="$1" title="$2"
  local tmp
  tmp="$(mktemp)"
  awk -v n="$n" -v title="$title" '
    /<!-- EXERCISE-NAV-START -->/ {
      print "**You are on branch `exercise-" n "` — Exercise " n ": " title "**"
      print ""
      print "Your handout: [docs/exercises/exercise-" n ".md](docs/exercises/exercise-" n ".md)"
      print ""
      print "This branch contains only what Exercise " n " needs. The handouts and"
      print "tickets for the others live on their own branches (`exercise-1` … `exercise-4`)."
      print "You do not need to have finished this one to switch to the next; each branch"
      print "is a known-good starting point."
      skip = 1
      next
    }
    /<!-- EXERCISE-NAV-END -->/ { skip = 0; next }
    !skip { print }
  ' README.md > "$tmp"
  mv "$tmp" README.md
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

exercise_1() {
  strip_instructions
  strip_fitness
  keep_only_handout 1
  keep_only_tickets
  rm -f docs/copilot-prompt-cheatsheet.md
  set_readme_nav 1 "Exploring a code base with and without instructions"
}

exercise_2() {
  strip_instructions
  strip_fitness
  keep_only_handout 2
  keep_only_tickets reminders.md
  rm -f docs/copilot-prompt-cheatsheet.md
  set_readme_nav 2 "Vibe code a feature without instructions"
}

exercise_3() {
  strip_exercise_3
  keep_only_handout 3
  keep_only_tickets reminders.md
  rm -f docs/copilot-prompt-cheatsheet.md
  set_readme_nav 3 "Implementing a feature ad-hoc with instructions"
}

# Exercise 4 keeps the prompt cheat-sheet: it's the refine -> plan -> implement
# reference its handout links to, and the takeaway participants leave with.
exercise_4() {
  keep_only_handout 4
  keep_only_tickets reminders.md rate-limiting.md task-listing-performance.md
  set_readme_nav 4 "Using a refine → plan → implement cycle"
}

build_branch exercise-1 \
  "Exercise 1 start state: no instructions, no project overview" \
  exercise_1

build_branch exercise-2 \
  "Exercise 2 start state: no instructions (vibe coding)" \
  exercise_2

build_branch exercise-3 \
  "Exercise 3 start state: instructions and overview, no coding guidelines" \
  exercise_3

build_branch exercise-4 \
  "Exercise 4 start state: full instruction set plus refine/plan/implement prompts" \
  exercise_4

git switch "$BASE"

if $PUSH; then
  git push --force-with-lease origin exercise-1 exercise-2 exercise-3 exercise-4
  echo "Pushed."
else
  echo "Built locally. Re-run with --push to publish."
fi
