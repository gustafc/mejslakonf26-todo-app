---
name: backend-conventions
description: Project conventions for Java/Spring code in this repo, covering package layout, DTOs, time handling, error responses and tests. Use this skill for ANY change to controllers, services, entities, DTOs or their tests, including small changes to existing endpoints (new fields, new validation rules), not only brand-new endpoints.
---

# Backend conventions

These are the client's conventions. Some are visible in the existing code; others (time handling,
error responses) are not yet, so follow this file even where the code gives no example.

## Layout

- Package by feature: everything for todos lives in `com.example.todoapp.todo`. Cross-cutting
  code (configuration, error handling) goes in `com.example.todoapp.shared`.
- Package-private by default. Make a type public only when another package needs it.
- Constructor injection, no field injection, no Lombok.

## DTOs

- Records named `*Request` (input) and `*Response` (output).
- Entities never leave the service layer. Map with a static `from(Entity)` on the response record.
- Bean Validation annotations on request records for shape only (not blank, size, format).

## Time

The system clock must never be read directly, because code that does so can't be tested
deterministically.

- Inject `java.time.Clock` and pass it: `LocalDate.now(clock)`, `Instant.now(clock)`.
  Never call `now()` without a clock argument.
- If no `Clock` bean exists yet, add one in `shared/TimeConfig.java`:
  `@Bean Clock clock() { return Clock.systemDefaultZone(); }`
- Do not use `@Future`, `@Past`, `@FutureOrPresent` or `@PastOrPresent`. They read the system
  clock, bypassing the injected one. Check time-relative rules in the service instead.

## Business rule violations → RFC 9457 problem details

Rules that depend on state or time (e.g. "due date must not be in the past") are checked in the
service and signalled with a feature-specific exception. A single `@RestControllerAdvice` in
`shared/ProblemDetailsHandler.java` turns them into `ProblemDetail` responses.

- `type` is a URN: `urn:problem:mejslakonf26-todo-app:<kebab-case-slug>`, e.g.
  `urn:problem:mejslakonf26-todo-app:due-date-in-past`. Never `about:blank`, never an http URL.
- `title` is fixed per type; `detail` describes this occurrence; set `status`.
- Add any rule-specific values as properties (`problem.setProperty("dueDate", ...)`).
- Not-found keeps the existing pattern (`ResponseEntity.of(Optional)`), unless asked otherwise.

Shape of a handler method:

```java
@ExceptionHandler(DueDateInPastException.class)
ProblemDetail handle(DueDateInPastException e) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    problem.setType(URI.create("urn:problem:mejslakonf26-todo-app:due-date-in-past"));
    problem.setTitle("Due date is in the past");
    problem.setProperty("dueDate", e.dueDate());
    return problem;
}
```

Use 422 for business rule violations and 400 for malformed or invalid-shape input.

## Tests

Follow the style of `TodoControllerTest`: `should_X_when_Y` method names, `// given / // when /
// then` comments, AssertJ.

- Controller: `@WebMvcTest` slice with `MockMvcTester` and a `@MockitoBean` service. For problem
  responses, assert status, `type` and any rule-specific properties.
- Service: plain JUnit, no Spring context. Use a real fixed clock,
  `Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneOffset.UTC)`, never a mocked `Clock`.
  Test the boundary (today is allowed, yesterday is not).

## Done means

Run `./mvnw test` (or `mvn test` if there is no wrapper) and make sure everything passes. In
your summary, list which of the conventions above the change relied on.
