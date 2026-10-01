---
name: db-migration
description: Rules for Flyway database migrations in this repo. Use this skill whenever a change touches the database schema, such as adding or changing a column or table, adding a field to a JPA entity, or writing any file under src/main/resources/db/migration, even if the user doesn't mention migrations.
---

# Database migrations

## Never edit an applied migration

A migration that is committed has been, or will be, applied somewhere: a colleague's database,
CI, the client's test and production environments. Flyway checksums every applied migration and
refuses to start when one changes. Editing one breaks every environment where it already ran.

To change the schema, always add a new migration, even to fix a mistake in the previous one.
(The team-toolkit plugin also blocks such edits. Don't try to work around it.)

## Naming

`V<yyyyMMddHHmm>__<snake_case_description>.sql`, UTC timestamp, e.g.
`V202609261430__add_due_date_to_todo.sql`.

- Get the timestamp by running `date -u +%Y%m%d%H%M`. Do not make one up.
- `V1__init.sql` predates this rule and stays as it is. Timestamps sort after it.
- Timestamps avoid version collisions between parallel branches, which sequence numbers cause.

## Content

- One logical change per migration. Forward-only; no undo scripts.
- Keep the JPA entity in sync. `spring.jpa.hibernate.ddl-auto=validate` makes startup fail on any
  mismatch, and `TodoApplicationTests` catches that.
- Before writing SQL, read [reference.md](reference.md) for SQL style, type mappings and how to
  add columns to tables that already contain data.
