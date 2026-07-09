# Lab Ticket 3 (Alternate — Implement phase): Fix the Task Listing Performance Issue

**Use this if your group finishes the main ticket's Plan step early, or
if the facilitator assigns it as a second breakout track for the
Implement lab. This ticket is already scoped — go straight to
Implement, no Refine/Plan needed.**

## Ticket, as filed

> **Title:** `getTasksForUser` re-sorts on every call
>
> `TaskService.java` re-filters and re-sorts the full task list on every
> single call, with no pagination. Fine at today's scale, a real problem
> as the dataset grows. Fix it so listing is efficient, and add test
> coverage — there currently is none for tag filtering, and the sort
> behavior isn't tested at all.

## Your task

1. Open `src/main/java/com/bootcamp/taskflow/service/TaskService.java`
   and read the facilitator note at the top of the file describing the
   known issues.
2. Prompt Copilot for a fix. Keep the public method signature
   (`getTasksForUser(String userId, String tag)`) unchanged — other code
   depends on it.
3. Ask Copilot to generate tests in
   `src/test/java/com/bootcamp/taskflow/service/TaskServiceTest.java`
   covering:
   - filtering by tag
   - correct sort order with mixed/missing `dueDate` values
4. Run `mvn test` and confirm everything passes.

**Output:** working code + passing tests, plus a one-line note on
anything Copilot suggested that you had to correct or reject.
