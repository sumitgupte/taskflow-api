# Ticket: `getTasksForUser` re-sorts on every call

**Used by:** Exercise 4, step 5 — the fully-scoped alternative ticket

## As filed

> **Title:** `getTasksForUser` re-sorts on every call
>
> `TaskService.java` re-filters and re-sorts the full task list on every single
> call, with no pagination. Fine at today's scale, a real problem as the
> dataset grows. Fix it so listing is efficient, and add test coverage — there
> is currently none for tag filtering, and the sort behavior isn't tested at
> all.

## Fully scoped

Nothing here is ambiguous, which is the point of picking it: run it through the
same refine → plan → implement cycle and notice how little the Refine stage
finds. The cycle's value is proportional to the ticket's vagueness.

## Constraints

1. Keep the public signature `getTasksForUser(String userId, String tag)`
   unchanged — `TaskController` depends on it.
2. Tests go in `src/test/java/com/bootcamp/taskflow/service/TaskServiceTest.java`
   and must cover:
   - filtering by tag
   - correct sort order with mixed and missing `dueDate` values
3. `./scripts/fitness.sh` green when you're done.

## Worth noticing while you read the code

`dueDate` is a nullable `String`, and the current sort compares it as a string
with `null` coerced to `""`. Ask what that does to ordering before you ask how
to make it faster — "efficient" and "correct" are not the same ticket, and this
one quietly contains both.
