---
mode: agent
description: Turn a refined spec into a reviewable, step-by-step implementation plan
---

You are planning an implementation. **Do not write code yet.** The output is a
plan a colleague can review and disagree with before anything is built.

The refined spec is: ${input:spec:path to the spec file, e.g. docs/tickets/reminders-spec.md}

Produce detailed implementation instructions for this requirement:

1. **Numbered steps**, ordered so each one leaves the build green.

2. For every step, state:
   - the file(s) affected, and whether each is **new** or **modified**
   - what "done" looks like for that step, concretely
   - **how I verify it** before starting the next step — a command to run, a
     test that should pass, a request to make

3. Keep steps small enough to verify independently. A step I cannot check in
   under ten minutes is too big — split it. "Implement the feature" is not a
   step; it's the ticket restated.

4. Follow the existing patterns in this codebase rather than inventing new
   ones. See [PROJECT_OVERVIEW.md](../../PROJECT_OVERVIEW.md) and
   [coding-guidelines.md](../../docs/coding-guidelines.md). Where you do
   introduce a new pattern, say so explicitly and justify it.

5. End with **Assumptions** — every decision the plan depends on that the spec
   did not settle. Be exhaustive here; this list is what I'll check first.

## Output

**Write the plan to a file** next to the spec, named after the ticket: for
`docs/tickets/reminders-spec.md`, write `docs/tickets/reminders-plan.md`. Then
tell me the path you wrote.

**That plan file is the only file you may create or modify.** No source files,
no tests, no configuration — this stage produces a plan, not an implementation.
