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

<!-- EXERCISE-NAV-START -->
Each exercise lives on its own branch that already contains everything that
exercise needs — and nothing it doesn't.

| # | Branch | Title | Handout |
|---|--------|-------|---------|
| 1 | `exercise-1` | Exploring a code base with and without instructions | [docs/exercises/exercise-1.md](docs/exercises/exercise-1.md) |
| 2 | `exercise-2` | Vibe code a feature without instructions | [docs/exercises/exercise-2.md](docs/exercises/exercise-2.md) |
| 3 | `exercise-3` | Implementing a feature ad-hoc with instructions | [docs/exercises/exercise-3.md](docs/exercises/exercise-3.md) |
| 4 | `exercise-4` | Using a refine → plan → implement cycle | [docs/exercises/exercise-4.md](docs/exercises/exercise-4.md) |

```bash
git switch exercise-1     # start here
```
<!-- EXERCISE-NAV-END -->

**Exercises 2, 3 and 4 all build the same feature** — task reminders — by three
different processes: vibe coding, ad-hoc with instructions, and a
refine → plan → implement cycle. Holding the feature constant is what makes the
comparison mean anything, so **commit your work at the end of each exercise.**
You'll diff the three against each other in Exercise 4.

```bash
git add -A && git commit -m "ex2: my attempt"
```

Each branch starts from the same clean baseline rather than from the previous
exercise's code, so you can begin any exercise without having finished the last
one. Committing on the branch keeps your version available to compare against.

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
docs/exercises/  the exercise handout(s) for this branch
docs/tickets/    the feature ticket(s) this branch's exercise works from
```

## Calling the API with curl

Start the app first (`./mvnw spring-boot:run`). Every response below is real
output from a fresh start — the seeded data is the same on every run, so you
can paste these and compare.

### Health — no auth

```bash
curl -s http://localhost:3000/health
```
```json
{"status":"ok"}
```

### Log in and grab a token

The password field is ignored (see `AuthController`), so email alone is enough.

```bash
curl -s -X POST http://localhost:3000/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"alice@example.com"}'
```
```json
{"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1MSIsImV4cCI6MTc4Njg5NDU4NDk4NH0.mw3pvSAsd5eIjkoT4IrxpW5etKzrRcxBgLG-v-5U6zM","user":{"id":"u1","name":"Alice Johnson","email":"alice@example.com"}}
```

Stash it in a shell variable — every `/tasks` call below needs it. The token is
valid for 120 minutes; re-run this if you start getting 401s.

```bash
TOKEN=$(curl -s -X POST http://localhost:3000/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"alice@example.com"}' | jq -r .token)
```

> No `jq`? Use `bob@example.com` to see the other user's tasks, and pull the
> token out by hand:
> ```bash
> TOKEN=$(curl -s -X POST http://localhost:3000/auth/login \
>   -H 'Content-Type: application/json' \
>   -d '{"email":"alice@example.com"}' | sed 's/.*"token":"\([^"]*\)".*/\1/')
> ```

### List tasks

Results are scoped to the token's owner and sorted by `dueDate`.

```bash
curl -s http://localhost:3000/tasks -H "Authorization: Bearer $TOKEN"
```
```json
[{"id":"t2","ownerId":"u1","title":"Book dentist appointment","done":false,"dueDate":"2026-07-10","tags":["personal"]},
 {"id":"t1","ownerId":"u1","title":"Write Q3 report","done":false,"dueDate":"2026-07-15","tags":["work"]}]
```

Filter by tag (exact match, case-sensitive — `Work` returns nothing):

```bash
curl -s "http://localhost:3000/tasks?tag=work" -H "Authorization: Bearer $TOKEN"
```
```json
[{"id":"t1","ownerId":"u1","title":"Write Q3 report","done":false,"dueDate":"2026-07-15","tags":["work"]}]
```

Without a token you get a 401:

```bash
curl -s http://localhost:3000/tasks
```
```json
{"error":"Missing or malformed Authorization header"}
```

### Get one task

```bash
curl -s http://localhost:3000/tasks/t1 -H "Authorization: Bearer $TOKEN"
```
```json
{"id":"t1","ownerId":"u1","title":"Write Q3 report","done":false,"dueDate":"2026-07-15","tags":["work"]}
```

`t3` belongs to Bob, so as Alice it's a **404, not a 403** — the API doesn't
confirm that someone else's task exists:

```bash
curl -s http://localhost:3000/tasks/t3 -H "Authorization: Bearer $TOKEN"
```
```json
{"error":"Task not found"}
```

### Create a task — `201`

```bash
curl -s -X POST http://localhost:3000/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"title":"Prepare bootcamp demo","dueDate":"2026-09-01","tags":["work","urgent"]}'
```
```json
{"id":"9eb29d05-a1b5-400d-a1b7-7daac3777a24","ownerId":"u1","title":"Prepare bootcamp demo","done":false,"dueDate":"2026-09-01","tags":["work","urgent"]}
```

`title` is the only validated field:

```bash
curl -s -X POST http://localhost:3000/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' -d '{"title":""}'
```
```json
{"error":"title is required"}
```

### Update a task — partial `PATCH`

Fields you omit are left untouched; only what you send is applied.

```bash
curl -s -X PATCH http://localhost:3000/tasks/t1 \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"done":true}'
```
```json
{"id":"t1","ownerId":"u1","title":"Write Q3 report","done":true,"dueDate":"2026-07-15","tags":["work"]}
```

### Delete a task — `204`, empty body

```bash
curl -s -o /dev/null -w '%{http_code}\n' -X DELETE http://localhost:3000/tasks/t2 \
  -H "Authorization: Bearer $TOKEN"
```
```
204
```

### Endpoint summary

| Method | Path | Auth | Success |
|--------|------|------|---------|
| `POST` | `/auth/login` | no | `200` + token |
| `GET` | `/health` | no | `200` |
| `GET` | `/tasks` | yes | `200`, optional `?tag=` |
| `GET` | `/tasks/{id}` | yes | `200`, or `404` if missing *or* not yours |
| `POST` | `/tasks` | yes | `201`, `400` if title blank |
| `PATCH` | `/tasks/{id}` | yes | `200`, `404` if not yours |
| `DELETE` | `/tasks/{id}` | yes | `204`, `404` if not yours |

State resets on every restart, so `git checkout .` and a restart puts the
seeded data back exactly as above.

## Useful commands

<!-- COMMANDS-START -->
```bash
./mvnw test                 # run the test suite
./mvnw spring-boot:run      # start the API on :3000
./mvnw spotless:apply       # auto-format (Google Java Format)
./scripts/fitness.sh        # run every quality gate at once
```
<!-- COMMANDS-END -->

## Notes

- There is no database. `InMemoryDatabase` is a seeded `ArrayList` and state
  resets on every restart. This is on purpose — zero setup, zero Docker.
- JWT signing is hand-rolled in `security/JwtService.java` — a minimal HS256
  encode/verify with no dependency beyond the JDK. Fine for a lab, not a
  pattern to copy.
