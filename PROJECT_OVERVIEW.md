# TaskFlow API — project overview

Structural context for anyone (human or AI) working in this repository.

## What this is

A single-module Spring Boot 4 / Java 25 REST API for personal task management,
plus a static single-page UI that consumes it. Every user sees only their own
tasks. There is no persistence layer, no external service, and no build step
for the frontend — the whole system runs from `./mvnw spring-boot:run` on port
3000 with nothing else installed.

## Request flow

```
Browser (static/index.html)
   │  fetch() with Authorization: Bearer <jwt>
   ▼
TaskController            @RestController, /tasks
   │  reads userId via request.getAttribute("userId")
   │
   │  ← AuthInterceptor ran first (registered in WebConfig for /tasks/**):
   │      parses the Bearer header, calls JwtService.verifyAndGetUserId,
   │      sets the "userId" request attribute, or writes 401 and stops.
   ▼
TaskService               @Service, all business logic and ownership filtering
   ▼
InMemoryDatabase          @Component, two ArrayLists seeded in its constructor
```

`AuthController` (`/auth/login`) and `HealthController` (`/health`) sit outside
the interceptor and need no token.

## Packages

| Package | Responsibility |
|---------|----------------|
| `controller` | HTTP surface. Maps requests, chooses status codes, returns `ErrorResponse` on failure. No business logic. |
| `service` | `TaskService` holds all task logic and ownership checks. `NotificationService` is an empty placeholder — no scheduler, no transport, nothing wired. |
| `security` | `JwtService` (hand-rolled HS256 encode/verify, JDK crypto only) and `AuthInterceptor` (`HandlerInterceptor`). |
| `model` | `Task`, `User` — plain mutable POJOs, no JPA, no annotations. |
| `dto` | Request/response shapes: `TaskRequest`, `LoginRequest`, `LoginResponse`, `ErrorResponse`. |
| `db` | `InMemoryDatabase` — the entire persistence story. |
| `config` | `WebConfig` registers the auth interceptor. |

## Conventions in force

- **Constructor injection everywhere.** No field `@Autowired` appears in this
  codebase; don't introduce it.
- **Ownership is enforced in the service layer**, not the controller. Every
  `TaskService` method takes `userId` as its first parameter and filters on it.
  A read path that skips this is a data leak, not a style issue.
- **Errors return `ErrorResponse`**, never a bare string or a stack trace.
- **Google Java Format**, enforced by Spotless (`./mvnw spotless:apply`).
- **Tests are plain JUnit 5 unit tests** with no Spring context — `TaskService`
  only needs an `InMemoryDatabase`, so nothing boots the application.

## Auth model

`POST /auth/login` takes an email, finds the matching seeded user, and issues a
120-minute HS256 token carrying that `userId`. **The password is not checked at
all** (see the comment in `AuthController`) — a deliberate lab simplification.
The signing secret comes from `taskflow.jwt.secret`, overridable via the
`TASKFLOW_JWT_SECRET` environment variable.

## Known limitations

These are real and load-bearing for the exercises. Don't silently "fix" them
unless a ticket asks you to:

- **No persistence.** `InMemoryDatabase` is two `ArrayList`s. All state is lost
  on restart, and the lists are not thread-safe despite a multi-threaded server.
- **`Task.dueDate` is a nullable `String`**, not a date type. Sorting compares
  it as a string with `null` coerced to `""`, which puts undated tasks first.
- **`getTasksForUser` re-filters and re-sorts the whole list on every call**,
  with no pagination.
- **No input validation** on title length or `dueDate` format.
- **Tag filtering is case-sensitive exact match.**
- **No email, push, or scheduling infrastructure exists.**
- **JWT is hand-rolled** in `security/JwtService.java` — a minimal HS256
  encode/verify on the JDK's own crypto, with no library. Fine for a lab, not a
  pattern to copy.

## Endpoints

| Method | Path | Auth | Notes |
|--------|------|------|-------|
| `POST` | `/auth/login` | no | Body `{email}`. Password ignored. |
| `GET` | `/health` | no | `{"status":"ok"}` |
| `GET` | `/tasks` | yes | Optional `?tag=` filter |
| `GET` | `/tasks/{id}` | yes | 404 if not found *or* not yours |
| `POST` | `/tasks` | yes | 400 if title blank |
| `PATCH` | `/tasks/{id}` | yes | Null fields in the body are left untouched |
| `DELETE` | `/tasks/{id}` | yes | 204 on success |
