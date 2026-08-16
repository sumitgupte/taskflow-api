# Exercise 2 — Vibe code a feature without instructions

**Time:** ~30 minutes · **Branch:** `exercise-2`

## Learning objectives

- Learn the weaknesses of vibe coding
- Understand the difference in capabilities between models
- Improve prompts by activating latent knowledge

## The ticket

> **Add an audit log**
>
> We need to know who changed what. Add an audit log.

That's the whole ticket. It's vague on purpose — this is what "vibe coding"
actually looks like in the wild: a one-liner, no spec, straight to the model.
Full ticket text: [docs/tickets/audit-log.md](../tickets/audit-log.md).

This branch has **no instructions file**. Whatever Copilot produces, it
produces from the raw code alone.

---

## Steps

### 1. Branch

```bash
git switch exercise-2
```

### 2. Agent — GPT-4.1

Set the model picker to **GPT-4.1**. In Agent mode, prompt exactly:

```
Add an audit log to the app.
```

Let it run to completion. Accept what it proposes. Don't help it, don't
clarify, don't correct it mid-flight — you are deliberately reproducing the
lazy path.

Then run the app and see whether it still works:

```bash
./mvnw test
./mvnw spring-boot:run
```

### 3. Code review — go over what was generated

Read every line it wrote, as if it were a PR from someone you'd never met.
Work through this checklist:

- [ ] **Does it compile? Do the tests pass?**
- [ ] **What's actually audited?** Every mutation, or only the ones it happened to notice? Check `updateTask` and `deleteTask` in `TaskService` specifically.
- [ ] **Where does the log go?** stdout, a field, a new list, a file? Does it survive a restart? (Nothing in this repo does.)
- [ ] **Who did it?** Did it capture the `userId` that `AuthInterceptor` puts on the request, or did it invent a user concept?
- [ ] **Did it add tests?** Unprompted?
- [ ] **Did it touch things it shouldn't have?** Run `git diff --stat`.
- [ ] **Is there an endpoint to read the log?** If so, can Alice read Bob's entries?

That last one is worth dwelling on. This codebase scopes every read by owner.
An audit endpoint that forgets to is a real data leak, and it is a very common
generated-code mistake.

Now reset before the next attempt:

```bash
git checkout . && git clean -fd
```

### 4. Agent — try a premium model

Switch the model picker to **Claude Sonnet 4.5** (or the strongest model
available to you). Same prompt, word for word:

```
Add an audit log to the app.
```

Run the same review checklist from Step 3. Note the differences — not just
"better", but *specifically* what it did that the first model didn't. Common
things to look for: did it search the codebase before writing? Did it ask a
clarifying question? Did it follow the existing constructor-injection and
`ErrorResponse` patterns?

Reset again: `git checkout . && git clean -fd`

### 5. Agent — the same prompt, again

```
add an audit log
```

Run it a third time on the same model as Step 4. **Is the result the same as
Step 4?** It won't be. Non-determinism is not a bug you can prompt away, and
it's the reason "it worked when I tried it" is not evidence.

Reset again.

### 6. Agent — activate latent knowledge

Now change one thing about the prompt:

```
Add an audit log using TDD.
```

Compare the result to Steps 2, 4 and 5.

Three words changed the output substantially — you didn't tell the model *how*
to do TDD, or what to test, or in what order. The model already knew. The
prompt just reached for knowledge that was sitting there unused. This is what
"activating latent knowledge" means, and it is the cheapest prompt improvement
available to you.

Try one or two more of these and see what else is latent:

- `Add an audit log. Follow the existing patterns in this codebase.`
- `Add an audit log. Think about thread safety and what happens on restart.`
- `Before writing any code, list what's ambiguous about "add an audit log".`

---

## Debrief questions

1. Across your four attempts, how many produced an audit log you'd merge?
2. Which was the bigger lever — changing the model, or changing the prompt?
3. Every run interpreted "audit log" differently. Whose fault is that?
4. You reviewed generated code four times today. How long did it take, and how
   does that compare to the time the generation itself saved?

## Takeaway

Vibe coding is fast at producing *something* and unreliable at producing the
*right* thing. A better model narrows the variance; it doesn't remove it. The
gap that neither a better model nor a better prompt can close is the one you'll
attack in Exercises 3 and 4: **the ticket never said what it wanted.**
