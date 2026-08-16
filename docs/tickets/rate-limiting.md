# Ticket: Protect the API from abuse (rate limiting)

**Used by:** Exercise 4, step 5 — the "run the cycle again" ticket

## As filed

> **Title:** Protect the API from abuse
>
> Someone could hammer our endpoints. Add rate limiting before we're in
> production.

## Already refined

This ticket has been through a Refine pass. Treat the following as settled —
your Refine stage should be short, which is the thing to notice:

- The limit applies **per authenticated user** (the `userId` set by
  `security/AuthInterceptor.java`), not per IP.
- Scope: `/tasks` routes only. Not `/auth/login`, not `/health`.
- Limit: 100 requests per user per 15-minute window. These numbers will change
  later — don't bake an assumption into the design that can't survive that.
- On exceeded: HTTP 429 with a JSON body matching the existing `ErrorResponse`
  shape. No `Retry-After` header required for v1.

## Open for the Plan stage

- Another `HandlerInterceptor` alongside `AuthInterceptor`, or a Servlet
  `Filter`? What's the ordering relative to auth, and why does it matter?
- Where does the counter state live? There is no real database here — see
  `db/InMemoryDatabase.java`.
- Thread safety: the API is multi-threaded and the current store is a plain
  `ArrayList`.
- How do you test a 15-minute window without waiting 15 minutes?

**Output:** a numbered plan, reviewed by another pair. Flag any step that
depends on an assumption not listed above.
