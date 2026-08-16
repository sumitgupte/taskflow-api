# Coding guidelines

Referenced from `.github/copilot-instructions.md`, so these apply to every
change made in this repository — by a person or by an agent.

## Process

- **Use TDD.** Write a failing test first, then the code that makes it pass.
  State which test you're writing before you write the implementation.
- **Never leave the build red.** Run `./scripts/fitness.sh` and get it green
  before claiming a change is done. Running it is not optional and not
  something to assert without doing.
- **Update `CHANGELOG.md`** under `## [Unreleased]` for every user-visible
  change.
- **Stay in scope.** Change what the ticket asks for. If you spot an unrelated
  problem, mention it; don't fix it in the same change.

## Code

- **Don't write trivial comments.** Comment *why*, never *what*. Code that
  needs a comment to explain what it does should be rewritten instead.
  `// increment the counter` above `counter++` is noise.
- **Use constructor injection.** No field `@Autowired` — nothing in this
  codebase uses it.
- **Scope every task read by the authenticated user.** The `userId` comes from
  `AuthInterceptor.USER_ID_ATTRIBUTE` on the request. A read path that returns
  another user's data is a security defect, not a style nit.
- **Ownership checks live in the service layer**, not the controller.
- **Return `ErrorResponse`** for error bodies, never a bare string and never a
  raw exception message from an unexpected failure.
- **Format with Google Java Format** — `./mvnw spotless:apply`.

## Tests

- New behavior needs a test. Bug fixes need a regression test that fails
  before the fix.
- Prefer plain JUnit 5 unit tests with no Spring context, matching
  `TaskServiceTest`. Only boot a context when the thing under test needs one.
- Cover the edge cases this codebase actually has: null `dueDate`, tasks owned
  by a different user, blank title, empty tag list.
- Test names describe the behavior, not the method:
  `getTasksForUserExcludesTasksOwnedByOtherUsers`, not `testGetTasks`.
