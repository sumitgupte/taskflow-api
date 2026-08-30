# Exercise 4 — Using a refine → plan → implement cycle

**Time:** ~35 minutes · **Branch:** `exercise-4`

## Learning objectives

- Understand the value — and the difference in quality — of refine → plan → implement
- Get ideas for how to add even more prompts

## Why this exercise exists

**Third time building the same feature.** Exercises 2 and 3 both failed the same
way: the ticket was one line, so the model guessed, and every run guessed
differently. Better models narrowed the variance. Instructions constrained the
style. Neither supplied the missing requirements, because nobody had them.

This is the exercise where you stop trying to fix that with a better prompt and
fix it with a better *process*. Three prompts, in order, each one reviewed
before the next runs:

| Stage | Question it answers | Output |
|-------|--------------------|--------|
| **Refine** | What are we actually building? | A spec |
| **Plan** | How, in what order, verified how? | Numbered steps |
| **Implement** | Do it. | Code + tests |

This branch ships all three as reusable prompt files in `.github/prompts/`, so
you can invoke them with `/refine`, `/plan`, `/implement` in Copilot Chat.

> **Pace:** Refine 13, Plan 9, Implement 13. **You will not finish the feature,
> and that is the intended outcome** — the exercise is the cycle, not the
> reminder system. The failure mode is rushing Refine to "get to the real work",
> which is the exact habit this exercise exists to break.

> **IntelliJ:** prompt files live under *Settings → Languages & Frameworks →
> GitHub Copilot → Customizations → Prompt Files*, and slash-command invocation
> isn't at VS Code parity yet — on some organisation plans the preview features
> they rely on are switched off entirely. If `/refine` doesn't resolve, open
> `.github/prompts/refine.prompt.md` and paste its body into chat. Every step
> below also gives the prompt in full under "…or by hand", so nothing here
> depends on the slash command working.

---

## Steps

### 1. Branch

```bash
git switch exercise-4
```

This branch has everything from Exercise 3 — instructions, project overview,
coding guidelines, changelog, fitness script — plus the prompt files.

### 2. Refine

The same ticket you've been handed twice already:
[docs/tickets/reminders.md](../tickets/reminders.md).

> **Add reminders for tasks**
>
> Users keep missing deadlines. Can we add reminders so they get notified
> before a task is due? Should work for all tasks. Let's ship this soon.

This time nobody types it straight into an agent. **Do not write code.** Prompt:

```
/refine docs/tickets/reminders.md
```

…or by hand (and in IntelliJ, if `/` doesn't resolve — `#file:` is VS Code
syntax, so attach the file via *Add context* instead, or just paste the path):

```
Read #file:docs/tickets/reminders.md. Identify gaps or ambiguities in the
requirements. Focus on user-facing behavior. Check the actual codebase for
constraints the ticket ignores. Do not propose an implementation yet.
```

Then force the decisions rather than letting them stay implicit:

```
For each ambiguity, propose the most reasonable default and one sentence
of justification. I'll override the ones I disagree with.
```

**Review it yourself.** At minimum you should end up with a written answer to:

- What *is* a reminder here? There's no email or push infrastructure — check
  `service/NotificationService.java` before you promise anything.
- How far before the due date does it fire? Fixed rule or per-task?
- What about tasks with no `dueDate`? (Check `db/InMemoryDatabase.java`.)
- What about tasks already marked `done`?
- One-shot, or repeating until done?
- Who can see or manage another user's reminders?

**Output:** a spec of 5–10 bullets, saved to `docs/tickets/reminders-spec.md`.
Where your pair disagreed, write down the decision anyway. An assumption you
made on purpose is fine; one you didn't notice you were making is the bug.

### 3. Plan

```
/plan docs/tickets/reminders-spec.md
```

…or by hand (and in IntelliJ, if `/` doesn't resolve — `#file:` is VS Code
syntax, so attach the file via *Add context* instead, or just paste the path):

```
Based on #file:docs/tickets/reminders-spec.md, create detailed implementation
instructions for this requirement. For each step give: the files affected,
new or modified, and how I verify that step works before starting the next.
Follow the existing patterns in this codebase. Do not write code yet.
```

Then pressure-test it before you trust it:

```
Review this plan for steps too large to verify independently, missing test
coverage, and steps that depend on an assumption we never confirmed.
```

**Swap plans with another pair and review theirs.** A step you can't tell "done"
for is not a step. `Implement the reminder feature` is not a step — it's the
ticket with different punctuation.

**Output:** a numbered plan in `docs/tickets/reminders-plan.md`.

### 4. Implement

```
/implement docs/tickets/reminders-plan.md
```

…or by hand (and in IntelliJ, if `/` doesn't resolve — `#file:` is VS Code
syntax, so attach the file via *Add context* instead, or just paste the path):

```
Implement the plan in #file:docs/tickets/reminders-plan.md. Follow all
coding guidelines in #file:docs/coding-guidelines.md. You are only done
once all functional requirements are fulfilled and all quality checks pass:
./scripts/fitness.sh must be green.
```

Turn auto-approval on and let it work. Then check, against the plan, not
against your vibes:

- Which numbered steps did it actually do? Which did it skip or merge?
- Did it stop at the plan's boundary, or keep going into things you didn't ask for?
- Is `./scripts/fitness.sh` green? Did it run it, or did it just say it did?
- Did it update `CHANGELOG.md`?

You will probably not finish the whole feature. **That's expected and fine** —
the exercise is the cycle, not the reminder system.

Now put all three attempts side by side. Same ticket, same models, three
processes:

```bash
git diff exercise-2 -- src/    # vibe coded, no instructions
git diff exercise-3 -- src/    # instructions and guidelines, still one-line prompt
```

Score each on the six questions from Step 2 — what a reminder is, when it fires,
no-`dueDate` tasks, already-`done` tasks, one-shot vs repeating, cross-user
visibility. **Which questions did each version answer, and did it answer them on
purpose?**

The Exercise 2 code probably answers all six too. The difference isn't how many
got answered — it's that yours are written down in a spec somebody reviewed, so
you can tell whether they're right.

### 5. When is this cycle *not* worth it?

Open the other two tickets — don't run the cycle on them, just read them:

- [docs/tickets/rate-limiting.md](../tickets/rate-limiting.md) — already
  half-refined.
- [docs/tickets/task-listing-performance.md](../tickets/task-listing-performance.md)
  — fully scoped, no ambiguity at all.

**For each, ask: what would Refine actually find here?** On the performance
ticket, close to nothing — you'd go straight to Plan.

That's the real lesson, and it's the one people get wrong when they take this
home: **the value of the cycle scales with how vague the ticket was to begin
with.** On a crisp ticket, Refine is nearly free and nearly pointless. On a
one-liner, it's the only thing that makes the rest work. It is not a ritual to
perform on every change.

> Finished the implement stage early? Run the cycle properly on
> `rate-limiting.md` rather than just reading it.

### 6. Go further

Discuss as a group, then try one:

**What other prompts could you imagine in this cycle?** Some starting points:

- `/review` — review this diff against the plan and the coding guidelines, as a PR
- `/critique` — argue against this plan; what's the strongest case that it's wrong?
- `/decompose` — split step 4 into steps small enough to verify in under 10 minutes
- `/regress` — what existing behavior could this change break? Write a test for each

**How can we improve plan adherence?** The recurring failure is a model that
drifts off the plan halfway through. Things that help:

- Number the steps and have it state which step it's on before each edit
- Have it check off completed steps in the plan file as it goes, so the plan is state
- Implement one step per chat turn instead of handing over the whole plan
- Put the plan in the repo and reference it by `#file` rather than pasting it — it
  stays in context and stays reviewable
- Make the fitness functions strict enough that drifting *fails* rather than
  merely being wrong

---

## Takeaway

Refine decides what's true. Plan decides what order. Implement is the part
everyone thinks is the work, and it's the part that goes wrong when the other
two were skipped. The model is the same in all three stages — you're the thing
that changed.

---

See [docs/copilot-prompt-cheatsheet.md](../copilot-prompt-cheatsheet.md) for
more prompt templates for each stage. Keep it after the bootcamp.
