# Lab Ticket 2 (Alternate — Plan phase): Rate Limiting

**Use this if your group finishes the main ticket's Refine step early, or
if the facilitator assigns it as a second breakout track for the Plan lab.**

## Ticket, as filed

> **Title:** Protect the API from abuse
>
> Someone could hammer our endpoints. Add rate limiting before we're
> in production.

## Your task

This ticket has already been through a quick Refine pass — treat these
as settled:

- Limit applies per authenticated user (the userId set by
  `AuthInterceptor` in `security/AuthInterceptor.java`), not per IP.
- Scope: `/tasks` routes only for now, not `/auth/login`.
- Limit: 100 requests per user per 15-minute window (numbers can change
  later — don't hardcode assumptions your plan can't survive changing).
- On limit exceeded: HTTP 429 with a JSON error body, no retry-after
  logic required for v1.

## Plan phase

Prompt Copilot to produce an implementation plan: whether this fits as
another `HandlerInterceptor` alongside `AuthInterceptor`, or as a Servlet
`Filter`, where the state needs to live (this repo has no real database —
see `InMemoryDatabase.java`), and how you'd test it without waiting
15 real minutes.

**Output:** a numbered plan, reviewed by a partner. Flag any step that
depends on an assumption not listed above.
