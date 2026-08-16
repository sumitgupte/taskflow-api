# Facilitator guide

---

## Run of show

| | Block | Mins | Branch |
|-|-------|------|--------|
| | Intro: what Copilot is doing under the hood | 20 | — |
| **1** | Exploring a code base with and without instructions | 25 | `exercise-1` |
| | Debrief 1 | 10 | |
| **2** | Vibe code a feature without instructions | 30 | `exercise-2` |
| | Debrief 2 | 10 | |
| | *Break* | 15 | |
| **3** | Implementing a feature ad-hoc with instructions | 30 | `exercise-3` |
| | Debrief 3 | 10 | |
| **4** | Refine → plan → implement cycle | 45 | `exercise-4` |
| | Debrief 4 + close | 15 | |

Roughly 3h30 including breaks. Exercise 4 is the one to protect if you're
running late — cut Exercise 2's step 5 (the repeat run) before you cut anything
in Exercise 4.

## The arc

Each exercise is engineered to fail in a way the next one fixes, and Exercises
2–4 build the *same feature* so the failures are directly comparable. Say this
out loud at the debriefs or the sequence reads as four disconnected labs:

1. **Ex 1** — instructions change what Copilot *knows*, not just how it talks.
2. **Ex 2** — with no instructions and a one-line ticket, output is a lottery. Better model = narrower lottery, still a lottery.
3. **Ex 3** — same ticket, now with instructions + fitness functions. The floor rises. The ceiling doesn't, because the ticket is *still* one line.
4. **Ex 4** — same ticket again, refined first. The missing ingredient was never the prompt or the model. It was that nobody had decided what to build.

The pivot at the end of Ex 3 is the whole day. If people leave thinking the
lesson is "write a good instructions file", the arc didn't land.

## Before the session

- [ ] Everyone has Copilot licensed with **agent mode** and access to at least
      one premium model (Claude Sonnet 4.5 or equivalent). Ex 2 and 3 compare
      two models; without a second model those steps collapse.
- [ ] Everyone has **JDK 25**. `java -version` must say 25. Spring Boot 4.1
      won't build on 17.
- [ ] Everyone ran `./mvnw test` **on their own machine, before the day**.
      First run pulls ~60MB from Maven Central. Doing this on venue wifi at
      09:01 with 20 people costs you the first exercise.
- [ ] All four branches exist on the remote and you've verified
      `git switch exercise-3` works from a fresh clone.

## Branch model

Each exercise branch is an **independent snapshot off `main`**, containing
everything that exercise presupposes. Nobody needs to have finished Ex 1 to
start Ex 2.

| Branch | Instructions | PROJECT_OVERVIEW | Guidelines | Prompt files |
|--------|--------------|------------------|------------|--------------|
| `exercise-1` | ✗ | ✗ | ✗ | ✗ |
| `exercise-2` | ✗ | ✗ | ✗ | ✗ |
| `exercise-3` | ✓ | ✓ | ✗ *(they write it)* | ✗ |
| `exercise-4` | ✓ | ✓ | ✓ | ✓ |
| `main` | ✓ | ✓ | ✓ | ✓ + this guide |

Each branch also carries **only its own handout and its own ticket(s)**. This is
deliberate: someone who reads the Exercise 4 handout while sitting in Exercise 2
already knows the punchline, and the day's arc depends on Exercise 2 failing
before Exercise 3 explains it.

| Branch | Handout | Tickets | Cheat-sheet |
|--------|---------|---------|-------------|
| `exercise-1` | `exercise-1.md` | *none — no ticket in this exercise* | ✗ |
| `exercise-2` | `exercise-2.md` | `reminders.md` | ✗ |
| `exercise-3` | `exercise-3.md` | `reminders.md` | ✗ |
| `exercise-4` | `exercise-4.md` | `reminders.md`, `rate-limiting.md`, `task-listing-performance.md` | ✓ |
| `main` | all four | all four | ✓ |

`docs/copilot-prompt-cheatsheet.md` is scoped to `exercise-4` for the same
reason — it lays out refine → plan → implement in full, which is Exercise 4's
content. Hand it out at the end of the day as the thing people keep.

The README's branch table is rewritten on each exercise branch into a "you are
on `exercise-N`" block, since the links to the other three handouts would
otherwise 404.

## One feature, three processes

**Exercises 2, 3 and 4 all build the reminders feature.** Same ticket, same
models, three processes. This is the spine of the day and the reason the
comparison is worth anything — vary one input at a time and hold the feature
constant.

Say this explicitly at the start of Exercise 2, or people experience it as
repetition rather than as an experiment. The line that works: *"you're going to
build this three times, and the third one will be the only one you'd merge."*

**Push people to commit at the end of each exercise.** Exercise 4 step 4 has
them run `git diff exercise-2 -- src/` and `git diff exercise-3 -- src/` to put
all three side by side. That comparison is the payoff of the whole day, and it
doesn't work if nobody committed. Remind them at each debrief:

```bash
git add -A && git commit -m "ex2: gpt-4.1, no instructions"
```

Branches still start from a clean baseline rather than from the previous
exercise's code, so a participant who falls behind or wrecks their tree can
switch and be caught up instantly:

```bash
git checkout . && git clean -fd     # reset within an exercise
```

---

## Exercise 1 — answer key

**Expected shape of the three answers.**

- **Step 2 (no instructions):** a competent file-tree walk. It will find the
  controllers and correctly say "task management REST API". Typically misses or
  garbles: that there is no real database, that the password is never checked,
  that `NotificationService` is an empty shell.
- **Step 4 (behavioral):** noticeably shorter or noticeably longer. Same facts,
  same gaps. **This is the point** — if a table reports their accuracy improved,
  ask them to point at the specific new fact.
- **Step 6 (structural):** now cites the request flow and the limitations,
  because they're written down.

**Things Copilot commonly gets wrong in the generated `PROJECT_OVERVIEW.md`**
— push tables to find at least one:

- Claims the login validates the password hash. It does not; see the comment in
  `AuthController`. The `User` records do carry a bcrypt-looking hash field,
  which is what leads it astray.
- Describes `InMemoryDatabase` as "a database layer that could be swapped for
  JPA", implying an abstraction that isn't there.
- Invents notification or email capability from the *name* `NotificationService`.
- Misses that `AuthInterceptor` only guards `/tasks/**` (`WebConfig`), so
  `/health` and `/auth/login` are open.

That last category — plausible claims derived from names rather than code — is
the most useful thing to surface all day.

**If a table finishes early:** have them add a *wrong* fact to
`PROJECT_OVERVIEW.md` on purpose and re-ask. Copilot will repeat it
confidently. Instructions are trusted input, not verified input.

## Exercise 2 — answer key

**Known rough edges in the codebase** (these were deliberately left in; do not
"clean them up" before a session):

1. `TaskService.getTasksForUser()` re-filters and re-sorts the whole list on
   every call, no pagination — fine at 4 tasks, a problem at 40,000.
2. No validation on title length or `dueDate` format.
3. Tag filtering is case-sensitive exact match only.
4. `InMemoryDatabase`'s `ArrayList`s are not thread-safe.
5. `dueDate` is a nullable `String`; sorting coerces `null` to `""`.
6. `AuthController` never checks the password.

**What the reminders runs typically produce**, in rough order of frequency:

- A `boolean reminderSent` / `reminderAt` field bolted onto `Task`, with nothing
  that ever actually fires. The feature "exists" and does nothing.
- `@Scheduled` + `@EnableScheduling` added out of nowhere, sometimes with a
  fabricated `spring-boot-starter-quartz` dependency. This repo has no
  scheduler — watch for a model inventing one and never mentioning that it did.
- An email or push sender hallucinated behind `NotificationService`, complete
  with an SMTP config block for a server that doesn't exist. The class name
  alone is enough to trigger this.
- **NPE on the seeded null `dueDate` values.** Very common, and it's the cheapest
  possible demonstration that the model didn't read `InMemoryDatabase`.
- Reminders fired for `t3`, which is already `done`. Nobody decided that should
  or shouldn't happen, so it just falls out of the implementation.
- A `GET /reminders` endpoint **unscoped by user**, so Alice reads Bob's. Steer
  at least one table to find this; it's the highest-value moment in the exercise
  and the one that maps most directly onto a real production incident.
- A hardcoded lead time (24h is the favourite) with no configuration and no
  mention that a choice was made.
- Rarely, unprompted tests. When the TDD prompt lands in step 6, tests appear.

The six ambiguities from the ticket are the scoring rubric — what a reminder is,
when it fires, no-`dueDate`, already-`done`, one-shot vs repeating, and who can
see them. **Every implementation answers all six.** The point to land: the model
didn't skip those decisions, it made them silently and differently every run.

**The step 5 point (same prompt, same model, different result)** is easy to
rush past. Make people actually diff it. "It worked when I tried it" dies here.

**The step 6 point (latent knowledge).** Three words — "using TDD" — restructure
the output. Nobody explained TDD to the model. Emphasise that the leverage was
in *naming a practice the model already knows*, not in describing it.

## Exercise 3 — answer key

**Expected ranking of the four runs** (Ex2 weak → Ex2 strong → Ex3 weak →
Ex3 strong). The usual finding: **model choice moves quality more than the
instructions file does**, for this kind of vague ticket. Let that land honestly
rather than steering to "instructions are great" — it sets up Ex 4.

What instructions *do* reliably improve: adherence to existing patterns
(constructor injection, `ErrorResponse`), and — because `PROJECT_OVERVIEW.md`
says so in as many words — a sharp drop in invented schedulers and email
transports. That's the single clearest measurable win, so point at it.

What they don't fix: the feature still isn't specified. The six ambiguities are
still being answered silently, just in better-formatted code.

Have tables run `git diff exercise-2 -- src/` here rather than eyeballing it.
The Ex2-vs-Ex3 diff on the same feature is far more convincing than two
descriptions of two different features would have been.

**Fitness functions.** `scripts/fitness.sh` runs tests + format check +
compile. The discussion that matters is the comment block at the bottom of the
script — the three guidelines with *no* automated gate, which are the three
that keep getting violated. Good answers from tables:

- Scoping-by-userId → an ArchUnit rule, or a test that logs in as Bob and
  asserts every endpoint returns nothing of Alice's
- Changelog → a CI check that the diff touches `CHANGELOG.md`
- Trivial comments → not mechanically checkable; this is the honest answer, and
  "then it needs a human in review" is the right conclusion

**Auto-approval (step 6).** Some participants will be nervous. That's the
correct instinct and worth naming: auto-approve is only defensible in
proportion to the strength of your automated gates. A repo with no
`fitness.sh` has no business running an unsupervised agent.

## Exercise 4 — answer key

**Expected time split:** Refine 15, Plan 10, Implement 15, and they will not
finish the feature. Say so up front, twice. The failure mode is a table
rushing Refine to "get to the real work", which is exactly the habit the
exercise exists to break.

**A good reminders spec settles at least:** in-app only (no email/push exists);
a fixed lead time, configurable via properties not per-task; tasks with no
`dueDate` are skipped; `done` tasks are skipped; one-shot not repeating;
reminders are visible only to the task owner.

**A good plan** has a first step that is a *test*, and no step you can't verify.
Watch for "3. Implement the reminder service" — that's the tell.

**Common implement-stage failures**, all worth calling out:

- Claims `./scripts/fitness.sh` is green without running it. Have them check.
- Drifts off the plan around step 3 and starts designing something else.
- Implements steps 1–5 in one turn, so nothing was verified independently.

**Step 5 (second ticket) is the comparison that makes the point.**
`task-listing-performance.md` is fully scoped, so Refine finds almost nothing —
which shows the cycle's value is proportional to the ticket's vagueness, not a
ritual to perform on everything. If time is short, have half the room take
rate-limiting and half take performance, then compare Refine outputs across the
two.

**Step 6** is a discussion, not a lab. The plan-adherence answers you're
fishing for: one step per turn, plan lives in the repo and gets checked off as
state, fitness functions strict enough that drift fails loudly.

---

## Rebuilding the branches

If you edit shared material on `main` and need to propagate it:

```bash
./scripts/rebuild-exercise-branches.sh
```

This force-recreates `exercise-1..4` from `main` with the per-branch deletions
applied. It rewrites those branches — don't run it mid-session, and warn people
before pushing, since their local exercise branches will diverge.
