---
mode: agent
description: Execute a reviewed plan, step by step, until all quality gates pass
---

Implement the plan in: ${input:plan:path to the plan file, e.g. docs/tickets/reminders-plan.md}

Rules for this stage:

1. **Follow all coding guidelines** in
   [coding-guidelines.md](../../docs/coding-guidelines.md). TDD: the failing
   test comes first.

2. **Work one numbered step at a time.** Before each step, state which step
   number you are starting. After it, run its verification from the plan and
   report the actual result — not what you expect the result to be.

3. **Stay on the plan.** If a step turns out to be wrong or impossible, stop
   and tell me rather than improvising a different design. If you find
   something broken that the plan doesn't cover, mention it and keep going.

4. **You are only done once all functional requirements are fulfilled and all
   quality checks pass.** Run `./scripts/fitness.sh` and get it green. Do not
   report success without having run it.

5. Update `CHANGELOG.md` under `## [Unreleased]`.

Finish with a short summary: which plan steps you completed, which you didn't
and why, and anything you had to deviate from.
