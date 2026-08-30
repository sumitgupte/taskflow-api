# Exercise 2 — Vibe code a feature without instructions

**Time:** ~20 minutes · **Branch:** `exercise-2`

## Learning objectives

- Learn the weaknesses of vibe coding
- Understand the difference in capabilities between models
- Improve prompts by activating latent knowledge

## The ticket

> **Add reminders for tasks**
>
> Users keep missing deadlines. Can we add reminders so they get notified
> before a task is due? Should work for all tasks. Let's ship this soon.

Full ticket: [docs/tickets/reminders.md](../tickets/reminders.md).

**You will build this same feature three times today** — here by vibe coding it,
in Exercise 3 with instructions in place, and in Exercise 4 through a
refine → plan → implement cycle. Same ticket, same models, three processes. That
makes the comparison honest, so keep notes as you go; you'll be referring back
to this run for the rest of the day.

This branch has **no instructions file**. Whatever Copilot produces, it produces
from the raw code alone.

> **Pace:** three runs of your own, roughly 6 minutes each including the review.
> Don't wait for a run to finish perfectly — if it is still going at 4 minutes
> you have seen enough to review what it has already written.

---

## Steps

### 1. Branch

```bash
git switch exercise-2
```

### 2. Agent — GPT-5 mini

Set the model picker to **GPT-5 mini**. In Agent mode, prompt exactly:

```
Add reminders for tasks so users get notified before a task is due.
```

Let it run to completion. Accept what it proposes. Don't help it, don't clarify,
don't correct it mid-flight — you are deliberately reproducing the lazy path.

Then check whether the app still works:

```bash
./mvnw test
./mvnw spring-boot:run
```

**Save this attempt** — you'll want it side by side with Exercises 3 and 4:

```bash
git add -A && git commit -m "ex2: gpt-5-mini, no instructions"
```

### 3. Code review — go over what was generated

Read every line, as if it were a PR from someone you'd never met:

- [ ] **Does it compile? Do the tests pass?**
- [ ] **What is a "reminder" in this implementation?** A log line, a field on
      `Task`, a new endpoint, a scheduled job? Did the model tell you it made
      that choice, or just make it?
- [ ] **How does anything actually fire?** There is no scheduler in this repo.
      Did it add one, fake one, or quietly implement nothing that runs?
- [ ] **Tasks with no `dueDate`.** `InMemoryDatabase` seeds some. NPE, skipped,
      or reminded immediately?
- [ ] **Tasks already `done`.** `t3` is done. Does it still get reminded?
- [ ] **Did it invent infrastructure that doesn't exist** — an email sender, a
      notification queue, a persistence layer?
- [ ] **If there's a read endpoint, can Alice see Bob's reminders?**
- [ ] **Did it add tests?** Unprompted?
- [ ] **Did it touch things it shouldn't have?** Run `git diff --stat`.

The `NotificationService` placeholder is a good tell. Did the model find it and
build there, or invent a parallel structure somewhere else?

Reset to the branch start before the next attempt:

```bash
git switch exercise-2 && git checkout . && git clean -fd
```

### 4. Agent — try a premium model

Switch to **Claude Opus 5** (or the strongest model available to you). Same
prompt, word for word. Run the same review checklist.

Note the differences *specifically*, not just "better": did it search the
codebase before writing? Did it ask a clarifying question? Did it notice the
null `dueDate` values? Did it follow the existing constructor-injection and
`ErrorResponse` patterns?

```bash
git add -A && git commit -m "ex2: premium model, no instructions"
git switch exercise-2 && git checkout . && git clean -fd
```

### 5. Watch the same prompt run a third time

**Your facilitator runs this one on the projector** — same prompt, same model as
Step 4, a third time. You don't need to run it yourself.

**Is the result the same?** It won't be. Two runs of the same prompt will
disagree about what a reminder even is.

Non-determinism isn't a bug you can prompt away, and it's why "it worked when I
tried it" is not evidence. Reset your own tree while you watch.

### 6. Agent — activate latent knowledge

Now change one thing:

```
Add reminders for tasks using TDD.
```

Compare to Steps 2, 4 and 5.

Three words restructured the output. You didn't explain TDD, or say what to
test, or in what order. The model already knew — the prompt just reached for
knowledge that was sitting there unused. That's **activating latent knowledge**,
and it's the cheapest prompt improvement available to you.

If you have time in hand, try one more and see what else is latent:

- `Add reminders for tasks. Follow the existing patterns in this codebase.`
- `Add reminders for tasks. Think about what happens to tasks with no due date.`
- `Before writing any code, list what's ambiguous about this reminders ticket.`

That last one is a preview of Exercise 4.

---

## Debrief questions

*Five minutes. Your facilitator will pick two — the second one is the one that
matters most.*

1. Across your attempts, how many produced a reminder feature you'd merge?
2. Did any two runs agree on what "reminder" meant? If not, whose fault is that?
3. Which was the bigger lever — changing the model, or changing the prompt?
4. You reviewed generated code four times. How long did that take, and how does
   it compare to the time the generation saved?

## Takeaway

Vibe coding is fast at producing *something* and unreliable at producing the
*right* thing. A better model narrows the variance; it doesn't remove it.

The gap neither a better model nor a better prompt closed is the one Exercises 3
and 4 attack: **the ticket never said what it wanted.** Keep your commits — in
Exercise 3 you'll build the same feature again with instructions in place, and
in Exercise 4 with the requirements actually settled first.
