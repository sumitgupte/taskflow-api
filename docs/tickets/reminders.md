# Ticket: Add reminders for tasks

**Used by:** Exercise 4, steps 2–4 (refine → plan → implement)

## As filed by the product owner

> **Title:** Add reminders for tasks
>
> Users keep missing deadlines. Can we add reminders so they get notified
> before a task is due? Should work for all tasks. Let's ship this soon.

That's the whole ticket. This is realistic — most tickets arrive
under-specified, and closing the gaps before generating code is the job.

## Constraints that exist whether the ticket mentions them or not

You'll find these by reading the code, which is the point. Listed here only so
a stuck pair can get unstuck:

- `service/NotificationService.java` is an empty placeholder. There is no
  email transport, no push infrastructure, and no scheduler in this project.
- `db/InMemoryDatabase.java` holds everything in an `ArrayList`. State does not
  survive a restart.
- `dueDate` on `model/Task.java` is a plain `String`, not a date type, and it is
  nullable.
- Every task-scoped read goes through `security/AuthInterceptor.java`, which
  puts the authenticated `userId` on the request.

## Definition of done for the exercise

Not a shipped reminder system — you won't get there in 45 minutes and you
aren't meant to. Done is:

1. `docs/tickets/reminders-spec.md` — 5–10 bullets, every ambiguity decided
2. `docs/tickets/reminders-plan.md` — numbered, each step independently verifiable
3. One or two plan steps actually implemented, with `./scripts/fitness.sh` green
