# Exercise 1 — Exploring a code base with and without instructions

**Time:** ~25 minutes · **Branch:** `exercise-1`

## Learning objectives

- Intro to instructions
- The difference between **behavioral** and **structural** instructions

## Why this exercise exists

Copilot's answers about a codebase are only as good as the context it starts
with. There are two different ways to improve that context, and people
routinely conflate them:

- **Behavioral instructions** change *how* Copilot answers — tone, length,
  format, what it should or shouldn't do. They add no new facts.
- **Structural instructions** change *what Copilot knows* — they point it at
  the architecture, the conventions, the files that matter.

You're going to feel the difference by asking the same question three times.

---

## Steps

### 1. Branch

```bash
git switch exercise-1
```

This branch has **no** `.github/copilot-instructions.md` and no project
overview. Blank slate, on purpose.

### 2. Agent — ask what the code base does

Open Copilot Chat in **Agent mode** — the mode dropdown in VS Code's chat view,
or the *Agent* tab of IntelliJ's Copilot tool window — and ask, with no files
attached and no setup:

```
What does this code base do?
```

**Record the answer.** Copy it into a scratch file — you'll be comparing
against it twice. Pay attention to:

- How long is it? Is that length useful to you?
- Did it find the auth model, or just list the folders?
- Did it notice `NotificationService` is an empty placeholder?
- Did it invent anything that isn't true?

### 3. Behavioral — add a style instruction

Create `.github/copilot-instructions.md` with **one** behavioral rule. Pick
whichever end of the spectrum you find more interesting:

```markdown
# Copilot instructions

Always respond concisely.
```

…or the opposite:

```markdown
# Copilot instructions

Make sure to elaborate extensively on every answer.
```

> Save the file. In **both VS Code and IntelliJ**, `.github/copilot-instructions.md`
> is picked up automatically for every chat request in the open project — you
> don't attach it. Start a **new chat** so you're not comparing against a primed
> conversation. If the old behavior persists, restart the IDE.

### 4. Agent — ask again

```
What does this code base do?
```

Same question, new chat. Compare to your Step 2 answer.

**The point:** the shape of the answer changed. Did the *accuracy* change? Did
it learn a single new fact about TaskFlow? Almost certainly not — you changed
the delivery, not the knowledge.

### 5. Structural — build real context

Ask Copilot to produce a project overview, then wire it into the instructions.

```
Create a PROJECT_OVERVIEW.md describing this codebase: its purpose, the
request flow from HTTP through to the in-memory store, each package's
responsibility, the auth model, and the known limitations. Be specific
and reference real file paths. Do not invent features that don't exist.
```

Review what it generates — **do not accept it blind.** Check at least:

- Does the described request flow match `TaskController` → `TaskService` → `InMemoryDatabase`?
- Did it correctly describe how `AuthInterceptor` passes `userId` downstream?
- Did it claim any persistence, email, or notification capability that doesn't exist?

Fix what's wrong, then reference it from your instructions file:

```markdown
# Copilot instructions

Always respond concisely.

## Project context

See [PROJECT_OVERVIEW.md](../PROJECT_OVERVIEW.md) for the architecture,
request flow, and known limitations of this project. Consult it before
answering questions about how the system fits together.
```

### 6. Agent — ask again

```
What does this code base do?
```

Third time, new chat. Now compare all three answers side by side.

---

## Debrief questions

1. Which step changed the answer more — the behavioral rule or the structural one?
2. Your Step 5 overview was written *by* Copilot, from the same code it already
   had access to. Why does feeding it back as an instruction help at all?
3. What did you have to correct in the generated overview? What does that tell
   you about trusting a `PROJECT_OVERVIEW.md` nobody reviewed?
4. Behavioral instructions cost tokens on every single request. Which of the two
   rules you tried in Step 3 would you actually keep, and why?

## Takeaway

Behavioral instructions are cheap and change the packaging. Structural
instructions are the ones that change the answers — and they are only as
trustworthy as the review you gave them.
