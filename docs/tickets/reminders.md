# Ticket: Add reminders for tasks

**Used by:** Exercises 2, 3 and 4 — the same ticket, built three different ways.

## As filed by the product owner

> **Title:** Add reminders for tasks
>
> Users keep missing deadlines. Can we add reminders so they get notified
> before a task is due? Should work for all tasks. Let's ship this soon.

That's it. That's the whole ticket.

This is realistic — most tickets arrive under-specified, and closing the gaps
before generating code is the job. Everything that goes wrong in Exercise 2
traces back to something this ticket doesn't say.

## What it doesn't tell you

Not a checklist to work through now — just proof that the gaps are real, and
worth re-reading after you've seen what a model does with the ticket as filed:

- What *is* a reminder here? In-app, email, push?
- How long before the due date does it fire? Fixed, or per task?
- What happens to tasks that have no due date?
- What happens to tasks already marked done?
- Does it fire once, or keep reminding until the task is done?
- Who can see or manage another user's reminders?

Every implementation answers all six of these. The only question is whether a
human decided the answers or a model guessed them.
