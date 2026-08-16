---
mode: ask
description: Interrogate a ticket for gaps and ambiguities before any code exists
---

You are refining a ticket. **Do not propose an implementation and do not write
any code.** The only output of this stage is a shared understanding of what is
actually being built.

The ticket is: ${input:ticket:path to the ticket file, e.g. docs/tickets/reminders.md}

1. Read the ticket, then read the parts of this codebase it would touch. Use
   [PROJECT_OVERVIEW.md](../../PROJECT_OVERVIEW.md) for orientation.

2. List the gaps and ambiguities in the requirements, grouped as:
   - **Functional gaps** — behavior a user would notice that the ticket doesn't specify
   - **Edge cases** — what the existing seeded data and nullable fields do to this feature
   - **Non-functional** — performance, security, error handling, persistence, concurrency

   Focus on **user-facing behavior** first. Prioritise ambiguities where two
   reasonable readings lead to materially different implementations.

3. Call out anything the ticket assumes exists that **does not exist in this
   repository**. Check before you assert either way.

4. For each ambiguity, propose the most reasonable default and one sentence of
   justification. Mark anything you cannot reasonably default as needing a
   human decision.

Ask me about anything genuinely blocking. Do not silently pick an assumption
and move on — an assumption made on purpose is fine, one nobody noticed is a
bug waiting to be shipped.
