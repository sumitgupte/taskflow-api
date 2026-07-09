# Lab Ticket 1 (Main): Task Reminders

**This is the ticket used across all three lab phases (Refine → Plan → Implement). Keep your work from each phase — you'll build on it in the next.**

---

## Ticket, as filed by the product owner

> **Title:** Add reminders for tasks
>
> Users keep missing deadlines. Can we add reminders so they get notified
> before a task is due? Should work for all tasks. Let's ship this soon.

That's it. That's the whole ticket. This is realistic — most tickets
arrive under-specified, and it's on you to close the gaps before you
start generating code.

---

## Phase 1: Refine (in session)

Do **not** start coding. Use Copilot Chat against the `taskflow-api`
codebase to interrogate this ticket. At minimum, resolve:

- What counts as a "reminder"? In-app? Email? Push? (There's no email/push
  infrastructure in this repo — check
  `src/main/java/com/bootcamp/taskflow/service/NotificationService.java`
  and decide what's realistic to build in a lab.)
- How far before the due date does a reminder fire? Configurable per task,
  or one fixed rule?
- What happens to tasks with no `dueDate` (check `InMemoryDatabase.java` —
  some exist)?
- What happens to already-`done` tasks — do they still get reminded?
- Is this a one-time reminder or recurring until the task is done?
- Who can see/manage a user's reminders — any auth implications beyond
  what `AuthInterceptor` already covers?

**Output of this phase:** a short written spec (5-10 bullet points) with
these questions answered. Assume reasonable defaults where the group
disagrees, but write the decision down — don't leave it implicit.

## Phase 2: Plan (in session)

Using your refined spec, prompt Copilot to produce a step-by-step
implementation plan against the real repo structure — which files change,
which are new, and in what order you'd build and verify each piece.

**Output of this phase:** a numbered plan, reviewed by a partner, with
any step that seems too large broken down further.

## Phase 3: Implement (in session)

Pick 1-2 steps from your plan (not the whole feature — you won't finish
it and that's fine) and implement them with Copilot, one step at a time.
Have your partner review the generated code as if it were a PR.

**Output of this phase:** working code for your chosen step(s), plus a
one-line note on anything Copilot got wrong or you had to correct.
