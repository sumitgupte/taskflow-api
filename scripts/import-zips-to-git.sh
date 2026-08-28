#!/usr/bin/env bash
#
# Rebuild this repo, all branches, from per-branch ZIPs downloaded from GitHub —
# then optionally push the exercise branches to a new remote such as an internal
# GitLab.
#
# For the case where you can DOWNLOAD from GitHub but not clone or reach it from
# the machine that needs the material. GitHub's "Download ZIP" only ever gives
# you one branch, so you download all five and this stitches them back together.
#
# Every branch is committed LOCALLY. Only the exercise branches are pushed —
# `main` stays on your machine, so anything you keep on it (notes, local
# tweaks, material you don't want on the org's server) never leaves. Override
# with PUSH_BRANCHES if you want a different set:
#
#   PUSH_BRANCHES="main exercise-1" ./import-zips-to-git.sh ...
#
# History is NOT preserved: each branch becomes a single fresh commit. That is
# fine here — the exercise branches are generated snapshots, not a story.
#
# Usage:
#   ./import-zips-to-git.sh <zip-dir> <target-dir> [remote-url]
#
# Example:
#   ./import-zips-to-git.sh ~/Downloads ~/taskflow-api \
#       https://gitlab.company.com/team/taskflow-api.git
#
# Omit the remote URL to build the repo locally and push it yourself.

set -euo pipefail

BRANCHES=(main exercise-1 exercise-2 exercise-3 exercise-4)

# What actually gets pushed. Everything above is still committed locally.
# shellcheck disable=SC2206
PUSH_BRANCHES=(${PUSH_BRANCHES:-exercise-1 exercise-2 exercise-3 exercise-4})

ZIP_DIR="${1:?usage: $0 <zip-dir> <target-dir> [remote-url]}"
TARGET="${2:?usage: $0 <zip-dir> <target-dir> [remote-url]}"
REMOTE="${3:-}"

command -v unzip >/dev/null || { echo "unzip is required" >&2; exit 1; }

ZIP_DIR="$(cd "$ZIP_DIR" && pwd)"

for p in "${PUSH_BRANCHES[@]}"; do
  found=no
  for b in "${BRANCHES[@]}"; do [[ "$b" == "$p" ]] && found=yes; done
  [[ "$found" == yes ]] || { echo "PUSH_BRANCHES names '$p', which is not a branch this script builds." >&2; exit 1; }
done

# Fail before doing any work if a download is missing, rather than producing a
# repo that is quietly short a branch.
declare -a ZIPS=()
missing=()
for b in "${BRANCHES[@]}"; do
  # shellcheck disable=SC2206
  found=($ZIP_DIR/*"$b".zip)
  if [[ ${#found[@]} -ne 1 || ! -f "${found[0]}" ]]; then
    missing+=("$b")
  else
    ZIPS+=("${found[0]}")
  fi
done
if [[ ${#missing[@]} -gt 0 ]]; then
  echo "No ZIP found in $ZIP_DIR for: ${missing[*]}" >&2
  echo "Expected files named like taskflow-api-<branch>.zip" >&2
  exit 1
fi

if [[ -e "$TARGET" ]]; then
  echo "$TARGET already exists — remove it or pick another path." >&2
  exit 1
fi

mkdir -p "$TARGET"
TARGET="$(cd "$TARGET" && pwd)"
git -C "$TARGET" init -q

staging="$(mktemp -d)"
trap 'rm -rf "$staging"' EXIT

for i in "${!BRANCHES[@]}"; do
  b="${BRANCHES[$i]}"
  zip="${ZIPS[$i]}"
  echo "── $b  ($(basename "$zip"))"

  rm -rf "${staging:?}/x" && mkdir -p "$staging/x"
  unzip -q "$zip" -d "$staging/x"

  # A GitHub source archive contains exactly one top-level directory.
  src="$(find "$staging/x" -mindepth 1 -maxdepth 1 -type d | head -1)"
  [[ -d "$src" ]] || { echo "  unexpected archive layout in $zip" >&2; exit 1; }

  git -C "$TARGET" checkout -q --orphan "$b"
  git -C "$TARGET" rm -rqf --cached . 2>/dev/null || true
  find "$TARGET" -mindepth 1 -maxdepth 1 ! -name .git -exec rm -rf {} +

  cp -R "$src"/. "$TARGET"/

  # ZIP round-trips lose the executable bit on some extractors, which breaks
  # ./mvnw and ./scripts/*.sh in ways that look like a broken repo.
  chmod +x "$TARGET"/mvnw 2>/dev/null || true
  chmod +x "$TARGET"/scripts/*.sh 2>/dev/null || true

  git -C "$TARGET" add -A
  git -C "$TARGET" commit -q -m "$b start state (imported from GitHub ZIP)"
done

git -C "$TARGET" checkout -q main

echo
echo "Built $TARGET with branches: $(git -C "$TARGET" branch --format='%(refname:short)' | tr '\n' ' ')"

local_only=()
for b in "${BRANCHES[@]}"; do
  pushed=no
  for p in "${PUSH_BRANCHES[@]}"; do [[ "$b" == "$p" ]] && pushed=yes; done
  [[ "$pushed" == no ]] && local_only+=("$b")
done
[[ ${#local_only[@]} -gt 0 ]] && echo "Staying local only: ${local_only[*]}"

if [[ -n "$REMOTE" ]]; then
  echo "── pushing to $REMOTE: ${PUSH_BRANCHES[*]}"
  git -C "$TARGET" remote add origin "$REMOTE"
  git -C "$TARGET" push -u origin "${PUSH_BRANCHES[@]}"
  echo "Done. Set the default branch on the remote to ${PUSH_BRANCHES[0]} if it isn't already."
else
  cat <<EOF

Next, point it at your GitLab project and push the exercise branches:

  cd "$TARGET"
  git remote add origin <your-gitlab-url>
  git push -u origin ${PUSH_BRANCHES[*]}

${local_only[*]:+Not pushed, by design: ${local_only[*]}}
EOF
fi
