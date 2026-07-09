# Notes: Spring Boot 4 / Java 25 specifics

Context for whoever runs `mvn clean compile` first (see README) and for
anyone customizing this repo before the bootcamp.

## Versions used

- Spring Boot **4.1.0** (latest stable as of this writing; released
  June 10, 2026) — Spring Boot 4.0 also supports Java 25 if you'd rather
  pin to that instead.
- Java **25**
- Jakarta EE 11 / Servlet 6.1 baseline (comes from the Spring Boot 4
  parent — no action needed, just don't be surprised by it)

## Things that changed from a Spring Boot 3 / Java 17 mental model

- **Jackson 3 by default.** Spring Boot 4 moved most Jackson classes from
  `com.fasterxml.jackson` to a new `tools.jackson` package (annotations
  like `@JsonProperty` are the exception and stay under
  `com.fasterxml.jackson.annotation`). This repo avoids the issue
  entirely by never touching `ObjectMapper`/`JsonMapper` directly —
  controllers return plain objects and Spring's auto-configured message
  converter handles serialization. If you add code that needs Jackson
  directly, use `tools.jackson.databind.json.JsonMapper`, not
  `com.fasterxml.jackson.databind.ObjectMapper`.
- **Undertow support removed** (not Servlet 6.1 compatible yet). Not used
  in this repo, but worth knowing if a lab exercise reaches for it.
- **`@MockBean`/`@SpyBean` removed** in favor of `@MockitoBean`/
  `@MockitoSpyBean`, and `@SpringBootTest` no longer auto-configures
  MockMvc (`@AutoConfigureMockMvc` is now explicit). Not relevant to the
  current test file since `TaskServiceTest` is a plain unit test with no
  Spring context, but relevant if labs add web-layer tests.
- **Java 17 remains the floor** even though this repo targets 25 — Spring
  Boot 4 didn't raise the minimum, it added first-class support for
  newer JDKs on top of it.

## Why this repo hand-rolls its own JWT instead of using a library

The environment that generated this repo has no network access to Maven
Central, so a JWT library's exact compatibility with Jackson 3 /
Spring Boot 4 couldn't be verified before shipping this. Rather than
guess, `security/JwtService.java` implements a minimal HS256
encode/verify with no dependency beyond the JDK's own `javax.crypto`.
It's intentionally simple and fine for a lab; swap in
`io.jsonwebtoken:jjwt` (verify Jackson 3 compatibility first) or Spring
Security's resource-server support for anything beyond bootcamp use.
