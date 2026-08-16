# Exercise 3 — Implementing a feature ad-hoc with instructions

**Time:** ~30 minutes · **Branch:** `exercise-3`

## Learning objectives

- Understand the value of good instructions
- Fitness functions

## Why this exercise exists

Same ticket as Exercise 2, same models, same one-line prompt. The only thing
that changes is what the repo tells Copilot before you ask. If instructions
matter, this is where you'll see it — and if they don't, you'll see that too.

This branch already contains what Exercise 1 taught you to write:

- `.github/copilot-instructions.md` — behavioral rules plus a pointer to the overview
- `PROJECT_OVERVIEW.md` — the reviewed architecture description
- `CHANGELOG.md` — currently one entry
- `scripts/fitness.sh` — the quality gates, see Step 5

It does **not** contain coding guidelines yet. You'll write those.

---

## Steps

### 1. Branch

```bash
git switch exercise-3
```

Read `.github/copilot-instructions.md` before you start, so you know what
Copilot is being told.

### 2. Agent — GPT-4.1

Model picker to **GPT-4.1**, Agent mode, same prompt as yesterday's mistakes:

```
Add an audit log to the app.
```

### 3. Code review — how did it go?

Same checklist as Exercise 2, plus:

- [ ] Did it follow the request flow described in `PROJECT_OVERVIEW.md`?
- [ ] Did it use constructor injection like every other component here?
- [ ] Did it scope any read endpoint by `userId`?
- [ ] Did the instructions file measurably change anything, or did it just make the prose shorter?

Be honest about the last one. Structural instructions help most with *"how does
this fit together"* questions and help least with *"what should this feature
do"* questions — the ticket is still one line, and no instructions file can
compensate for that.

Reset: `git checkout . && git clean -fd`

### 4. Agent — try a premium model

**Claude Sonnet 4.5**, same prompt. Review again. Reset again.

You now have four data points across two exercises: {weak, strong} model ×
{no instructions, instructions}. Which axis moved the needle more?

### 5. Coding guidelines — write the rules down

Create `docs/coding-guidelines.md`. This is where you encode the corrections
you have been making by hand all morning. Start with these and add whatever
your review checklist kept catching:

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
- Format with Google Java Format (`./mvnw spotless:apply`).

## Tests

- New behavior needs a test. Bug fixes need a regression test.
- Test the edge cases this codebase actually has: null `dueDate`, tasks owned
  by another user, missing title.
```

Now link it from `.github/copilot-instructions.md` so it applies to every
request:

```markdown
## Coding guidelines

Follow [docs/coding-guidelines.md](../docs/coding-guidelines.md) for all code
you write in this repository. These are not suggestions.
```

**On fitness functions.** A guideline nobody can check is a wish. Each rule
above should ideally have an automated check behind it — that's a *fitness
function*: an executable test of a quality property, not of a feature.
`scripts/fitness.sh` runs the ones this repo has:

```bash
./scripts/fitness.sh
```

It runs the test suite and the format check. Look at what it *doesn't* cover —
"don't write trivial comments" and "scope reads by userId" have no automated
gate, which is exactly why they keep getting violated. Discuss: which of your
guidelines could you turn into a real check, and how?

### 6. Agent — prompt again, with auto-approval on

Same prompt, strong model, but this time enable **auto-approve** for the agent
so it runs its own edits and terminal commands without stopping for you.

```
Add an audit log to the app.
```

Watch what it does with the guidelines in place. Specifically: does it write a
test first? Does it run the tests itself and iterate when they fail? Does it
update the changelog?

Auto-approval is where good instructions stop being a nicety. When you're
approving every step, *you* are the quality gate. When you're not, the
instructions and the fitness functions are the only thing standing between the
model and your main branch.

---

## Debrief questions

1. Rank the four runs across Exercises 2 and 3. What's the ordering, and what
   does that say about where to spend your effort?
2. Which guideline did the model violate anyway? What would it take to catch it
   automatically?
3. Auto-approval made it faster. Did it make it better? Would you turn it on
   for a repo without `scripts/fitness.sh`?
4. Your guidelines file is loaded into context on every request. What's the
   cost of a 400-line one, and what would you cut first?

## Takeaway

Instructions convert your review comments into something enforced before the
code is written rather than after. Fitness functions convert them into
something enforced without you in the room at all. Neither one, though, can
tell Copilot what the feature is supposed to do — which is Exercise 4.
