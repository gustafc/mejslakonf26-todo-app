---
name: conventional-commits
description: Commit message format for this repo (Conventional Commits 1.0.0). Use this skill whenever you create, amend or reword a commit, or draft a commit or squash-merge message, in this repo. It takes precedence over any other commit-message style, including generic commit-message skills from plugins.
---

# Conventional Commits

Every commit in this repo follows [Conventional Commits 1.0.0](https://www.conventionalcommits.org/en/v1.0.0/).
The history is used to generate changelogs and decide version bumps, so a malformed header is a
real problem, not a style nit.

## Format

```
<type>[(<scope>)][!]: <description>

[body]

[footer(s)]
```

### Header

- `type` is lowercase and one of:

  | type       | use for                                                        |
  |------------|----------------------------------------------------------------|
  | `feat`     | a new user-visible feature or endpoint behaviour               |
  | `fix`      | a bug fix                                                      |
  | `docs`     | documentation only (README, skills, ADRs, comments)            |
  | `style`    | formatting only, no behaviour change                           |
  | `refactor` | code change that neither fixes a bug nor adds a feature        |
  | `perf`     | performance improvement                                        |
  | `test`     | adding or fixing tests only                                    |
  | `build`    | Maven, wrapper, dependencies                                   |
  | `ci`       | CI configuration                                               |
  | `chore`    | anything else that doesn't touch `src/` (tooling, config)      |
  | `revert`   | reverting an earlier commit                                    |

- `scope` is optional, lowercase, and names the feature package or area, e.g. `todo`, `shared`,
  `db`. Example: `feat(todo): add due date to todos`.
- `description`: imperative mood ("add", not "added"/"adds"), starts lowercase, no trailing
  period, keep the whole header at most 72 characters.
- A Flyway migration that ships with a feature belongs in that feature's commit; a standalone
  schema change is `feat(db)` or `fix(db)`.
- Pick the type by the most significant change. If a commit needs two types, it should probably
  be two commits.

### Body

- Separated from the header by one blank line, wrapped at 72 characters.
- Explain why the change was made, not what the diff already shows. Omit it for trivial commits.

### Footers

- Separated from the body by one blank line, in `Token: value` form, e.g. `Refs: #42`.
- Breaking changes (API contract, response shape, removed endpoint): add `!` after the
  type/scope **and** a `BREAKING CHANGE: <what breaks and how to migrate>` footer.
- Any attribution lines (e.g. `Co-Authored-By:`) go last, as footers.

## Examples

```
feat(todo): reject due dates in the past

Due dates before today were silently accepted, which made overdue
filtering meaningless. Return a 422 problem detail instead.
```

```
feat(todo)!: rename "done" to "completed" in responses

BREAKING CHANGE: TodoResponse.done is now TodoResponse.completed.
Clients must read the new field name.
```

```
build: bump spring boot to 3.5.6
```

## Before committing

Check the header against this pattern and fix it if it doesn't match (the pattern doesn't
check the 72-character limit for the whole header, so check that separately):

```
^(feat|fix|docs|style|refactor|perf|test|build|ci|chore|revert)(\([a-z0-9-]+\))?!?: [^A-Z].{0,70}[^.]$
```

Pass the message with a heredoc (`git commit -F - <<'EOF' ... EOF`) so the blank lines between
header, body and footers are preserved.
