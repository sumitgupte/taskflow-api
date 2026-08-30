# Exercise 3 — Implementing a feature ad-hoc with instructions

**Time:** ~30 minutes · **Branch:** `exercise-3`

## Learning objectives

- Understand the value of good instructions
- Fitness functions

## Why this exercise exists

**Same ticket as Exercise 2.** Same models, same one-line prompt. The only thing
that changes is what the repo tells Copilot before you ask.

That's the experiment: hold the feature constant and vary one input at a time.
If instructions matter, this is where you'll see it — and if they matter less
than you expected, you'll see that too.

This branch already contains what Exercise 1 taught you to write:

- `.github/copilot-instructions.md` — a behavioral rule plus a pointer to the overview
- `PROJECT_OVERVIEW.md` — the reviewed architecture description
- `CHANGELOG.md` — currently one entry
- `scripts/fitness.sh` — the quality gates, see Step 5

It does **not** contain coding guidelines yet. You'll write those.

> Starting fresh here is expected — you don't carry Exercise 2's code forward.
> The comparison is between *runs*, and each run starts from the same clean
> baseline. Keep Exercise 2's branch around so you can diff against it.

---

## Steps

### 1. Branch

```bash
git switch exercise-3
```

Read `.github/copilot-instructions.md` before you start, so you know what
Copilot is being told that it wasn't told yesterday.

### 2. Agent — GPT-5 mini

Model picker to **GPT-5 mini**, Agent mode, the same prompt as Exercise 2:

```
Add reminders for tasks so users get notified before a task is due.
```

### 3. Code review — how did it go?

Same checklist as Exercise 2 — what is a reminder, what fires it, null
`dueDate`, already-`done` tasks, invented infrastructure, cross-user reads —
plus:

- [ ] Did it follow the request flow described in `PROJECT_OVERVIEW.md`?
- [ ] Did it use constructor injection like every other component here?
- [ ] Did it still invent a scheduler or an email transport, even though the
      overview says neither exists?
- [ ] Did the instructions measurably change the *code*, or just the prose
      around it?

Diff it directly against your Exercise 2 attempt:

```bash
git diff exercise-2 -- src/
```

Be honest about the last checkbox. Structural instructions help most with
*"how does this fit together"* and least with *"what should this feature do"* —
the ticket is still one line, and no instructions file compensates for that.

Reset: `git checkout . && git clean -fd`

### 4. Agent — try a premium model

**Claude Opus 5**, same prompt. Review again. Reset again.

You now have four data points across two exercises: {weak, strong} model ×
{no instructions, instructions}, all building the same feature. Which axis moved
the needle more?

### 5. Coding guidelines — write the rules down

Create `docs/coding-guidelines.md`. This is where you encode the corrections
you've been making by hand all morning. Start here and add whatever your review
checklist kept catching:

```markdown
# Coding guidelines

## Process

- Use TDD. Write a failing test first, then the code that makes it pass.
- Never leave the build red. Run `./scripts/fitness.sh` before you claim done.
- Update `CHANGELOG.md` under `## [Unreleased]` for every user-visible change.

## Code

- Don't write trivial comments. Comment *why*, never *what* — code that needs a
  comment to explain what it does should be rewritten instead.
- Use constructor injection. No field `@Autowired`.
- Every read path that returns task data must be scoped by the authenticated
  `userId` from `AuthInterceptor.USER_ID_ATTRIBUTE`.
- Match the existing error shape: return `ErrorResponse`, not a bare string.
- Don't invent infrastructure. There is no scheduler, no email transport and no
  database — if a feature needs one, say so instead of faking it.
- Format with Google Java Format (`./mvnw spotless:apply`).

## Tests

- New behavior needs a test. Bug fixes need a regression test.
- Test the edge cases this codebase actually has: null `dueDate`, tasks owned
  by another user, tasks already marked done, missing title.
```

That "don't invent infrastructure" rule exists because of what you watched
happen twice. Guidelines earn their place by being the write-up of a mistake
you've already seen.

Link it from `.github/copilot-instructions.md` so it applies to every request:

```markdown
## Coding guidelines

Follow [docs/coding-guidelines.md](../docs/coding-guidelines.md) for all code
you write in this repository. These are not suggestions.
```

**On fitness functions.** A guideline nobody can check is a wish. Each rule
should ideally have an automated check behind it — that's a *fitness function*:
an executable test of a quality property rather than of a feature.

```bash
./scripts/fitness.sh
```

It runs the test suite, the format check and a compile. Look at what it
*doesn't* cover — "don't write trivial comments" and "scope reads by userId"
have no automated gate, which is exactly why they keep getting violated.
Discuss: which of your guidelines could become a real check, and how?

### 6. Agent — prompt again, with auto-approval on

Same prompt, strong model, but enable **auto-approve** so the agent runs its own
edits and terminal commands without stopping for you — the auto-approve /
*Continue* toggles in VS Code's chat, or *Settings → GitHub Copilot → Chat* in
IntelliJ.

```
Add reminders for tasks so users get notified before a task is due.
```

Watch what the guidelines change. Does it write a test first? Does it run the
tests itself and iterate when they fail? Does it update the changelog? Does it
tell you it can't schedule anything instead of pretending it can?

Auto-approval is where good instructions stop being a nicety. When you approve
every step, *you* are the quality gate. When you don't, the instructions and the
fitness functions are the only thing between the model and your main branch.

Commit your best attempt — Exercise 4 builds this feature one more time, and
you'll want all three to compare:

```bash
git add -A && git commit -m "ex3: with instructions and guidelines"
```

---

## Takeaway

Instructions turn your review comments into something enforced before the code
is written rather than after. Fitness functions turn them into something
enforced without you in the room at all.

Neither can tell Copilot what the feature is supposed to *do*. Six runs in,
nobody has yet decided what a reminder is — which is Exercise 4.
