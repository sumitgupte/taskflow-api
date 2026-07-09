# GitHub Copilot Prompt Cheat-Sheet: Refine → Plan → Implement

Keep this open during the labs, and after the bootcamp. The templates
are starting points — the actual skill is knowing *which questions your
prompt needs to answer* before you send it.

---

## Phase 1: Refine

**Goal:** turn a vague ticket into a spec Copilot can act on reliably.
Everything wrong with AI-generated code usually traces back to a skipped
step here.

### Interrogate the ticket
```
Here's a ticket as filed: "<paste raw ticket text>"

Before I plan or write any code, list the ambiguities and missing
details in this ticket that would affect implementation. Group them
into: functional gaps, edge cases, and non-functional concerns
(performance, security, error handling).
```

### Check for hidden edge cases against real code
```
Given this codebase [reference the relevant file(s) with #file],
what edge cases in the existing data would this new feature need to
handle? Look specifically at [null/missing fields, existing data
variations, auth boundaries].
```

### Force a decision, don't leave it implicit
```
For each ambiguity you listed, propose the most reasonable default
assumption and a one-sentence justification. I'll override any I
disagree with.
```

**Red flag to watch for:** Copilot (or you) quietly picking an assumption
and moving straight to code. If you didn't consciously decide it, it's
not refined — it's guessed.

---

## Phase 2: Plan

**Goal:** get a step-by-step implementation plan you review *before*
any code exists. This is the single highest-leverage checkpoint in the
whole workflow and the one people are most tempted to skip.

### Generate a plan from the refined spec
```
Based on this spec: <paste your refined spec from Phase 1>

Produce a step-by-step implementation plan for this codebase. For each
step, list: the file(s) affected, whether it's new or modified, and how
I'd verify that step works before moving to the next. Do not write code
yet.
```

### Pressure-test the plan
```
Review this plan for: steps that are too large to verify independently,
missing test coverage, and any step that depends on an assumption we
haven't confirmed. Suggest where to split anything too big.
```

### Sanity-check against existing patterns
```
Does this plan follow the existing patterns in this codebase (see
[#file references to similar existing code])? Flag anywhere it
introduces a new pattern instead of reusing one that already exists.
```

**Red flag to watch for:** a plan step like "implement the feature" with
no sub-steps. If you can't tell what "done" looks like for a step, it's
not a plan yet, it's a restatement of the ticket.

---

## Phase 3: Implement

**Goal:** execute the plan in small, independently verifiable chunks —
and review Copilot's output the way you'd review a junior engineer's PR,
not the way you'd skim autocomplete.

### Implement one step at a time
```
Implement step 3 of this plan: <paste just that step>.
Keep this change scoped to what's described — don't touch unrelated
code. Match the existing style and error-handling patterns in
[#file reference].
```

### Ask for tests alongside the code, not after
```
Write tests for the function/change above, covering: the happy path,
the edge cases we identified in the Refine phase, and at least one
failure case. Use the existing test patterns in [#file reference].
```

### Review generated code like a PR
```
Explain what this code does, line by line, and call out anything that
deviates from the plan or makes an assumption we didn't discuss.
```

### When something looks off
```
This doesn't look right: <describe what's wrong or paste the
unexpected behavior>. Don't just patch it - explain why it happened
first.
```

**Red flag to watch for:** accepting a large multi-file suggestion in
one go because it "looks reasonable." Confident-sounding code and
correct code are not the same thing — Copilot produces the former
regardless of the latter.

---

## General habits, all phases

- **Reference real files**, don't describe them from memory — use
  `#file` / open-tabs context so Copilot works from the actual code,
  not a guess at what it might contain.
- **One phase at a time.** Asking for refine+plan+code in a single
  prompt collapses the checkpoints that make this workflow work.
- **Treat plan review as non-optional**, even under deadline pressure —
  it's the cheapest place to catch a wrong assumption, and the most
  expensive place to catch it is in code review or production.
- **When Copilot is confidently wrong**, it's usually because the
  prompt skipped Refine, not because the model is having a bad day.
