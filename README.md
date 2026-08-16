# TaskFlow API — GitHub Copilot Bootcamp

A small Spring Boot task-management API used as the practice codebase for a
four-exercise GitHub Copilot bootcamp. It is deliberately small enough to read
in one sitting and deliberately imperfect enough to be worth improving.

## Setup (do this before the session starts)

Requirements: **JDK 25** and **Git**. Maven comes with the repo via `./mvnw`.

```bash
git clone https://github.com/sumitgupte/taskflow-api.git
cd taskflow-api
./mvnw test          # warms the Maven cache — do this on the venue wifi, not at 9:01am
./mvnw spring-boot:run
```

Open <http://localhost:3000> and log in as `alice@example.com` or
`bob@example.com` — the password field is ignored, see `AuthController`.
The UI is a single static page served from `src/main/resources/static/index.html`
and talks to the same REST API you'll be modifying.

> On Windows use `mvnw.cmd` instead of `./mvnw`.

## The exercises

Each exercise lives on its own branch that already contains everything that
exercise needs. **You do not need to have finished the previous exercise to
start the next one** — switching branches resets you to a known-good state.

| # | Branch | Title | Handout |
|---|--------|-------|---------|
| 1 | `exercise-1` | Exploring a code base with and without instructions | [docs/exercises/exercise-1.md](docs/exercises/exercise-1.md) |
| 2 | `exercise-2` | Vibe code a feature without instructions | [docs/exercises/exercise-2.md](docs/exercises/exercise-2.md) |
| 3 | `exercise-3` | Implementing a feature ad-hoc with instructions | [docs/exercises/exercise-3.md](docs/exercises/exercise-3.md) |
| 4 | `exercise-4` | Using a refine → plan → implement cycle | [docs/exercises/exercise-4.md](docs/exercises/exercise-4.md) |

```bash
git switch exercise-1     # start here
```

Your work is throwaway. When an exercise ends, don't try to merge or preserve
it — just `git switch` to the next branch. If you want to keep something,
commit it on a branch of your own first:

```bash
git switch -c my-exercise-2 && git add -A && git commit -m "my attempt"
```

## What's in here

```
src/main/java/com/bootcamp/taskflow/
  controller/    AuthController, TaskController, HealthController
  service/       TaskService, NotificationService (a placeholder)
  security/      JwtService (hand-rolled HS256), AuthInterceptor
  model/         Task, User
  dto/           request/response shapes
  db/            InMemoryDatabase — seeded users and tasks, no real persistence
src/main/resources/static/index.html   the web UI
docs/exercises/  the four exercise handouts
docs/tickets/    feature tickets used by the exercises
docs/copilot-prompt-cheatsheet.md      prompt templates — keep this open all day
```

## Useful commands

```bash
./mvnw test                 # run the test suite
./mvnw spring-boot:run      # start the API on :3000
./mvnw spotless:apply       # auto-format (Google Java Format)
./scripts/fitness.sh        # run every quality gate at once (see Exercise 3)
```

## Notes

- There is no database. `InMemoryDatabase` is a seeded `ArrayList` and state
  resets on every restart. This is on purpose — zero setup, zero Docker.
- JWT signing is hand-rolled in `security/JwtService.java`. Fine for a lab,
  not a pattern to copy. See [docs/notes-spring-boot-4-java-25.md](docs/notes-spring-boot-4-java-25.md)
  for why, plus the Spring Boot 4 / Jackson 3 gotchas worth knowing.
