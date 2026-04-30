# ThinkDrop

A personal commonplace book. Add anything that catches — words, theories, concepts, quotes, ideas, observations. One a day. No pressure.

## What it is

ThinkDrop is a Duolingo-style learning path for your own mind. The path screen shows a winding sequence of nodes; completing one means dropping a new entry. Entries are typed, reviewed, and resurface via spaced repetition so things you learn actually stick.

Mascot: a Siberian husky. Sits on the path, hops on the reveal screen, lives in your profile avatar.

## Frontend

Single-file vanilla HTML/CSS/JS. No build step, no framework, no npm.

- Fonts: Fraunces (serif headings), Manrope (UI), JetBrains Mono (labels).
- Storage: `localStorage` (GitHub Pages) or `window.storage` (Claude artifacts).
- Entry types: word · concept · theory · quote · idea · observation.
- 4-step add flow: pick kind → name it → elaborate → preview → drop it.

### Running locally

```
open index.html
```

Or serve it (needed for `fetch` to the backend):

```
python3 -m http.server 5173
```

## Scala backend

The `backend/` directory contains an http4s + cats-effect service that powers spaced-repetition session generation — a Duolingo-style Session Generator written in Scala 3.

### What it does

| Endpoint | Description |
|---|---|
| `GET  /health` | Health check |
| `POST /api/session` | Generate a review session (SM-2 scheduling) |
| `POST /api/review` | Submit review results, get updated SM-2 fields back |
| `POST /api/next-due` | Next due date across all entries (streak-at-risk warning) |

The SM-2 algorithm mirrors Duolingo's approach: quality ratings 0–5, ease factor adjustments, exponential interval growth, failure resets.

### Stack

- Scala 3.3.3
- http4s 0.23 (Ember server)
- circe 0.14 (JSON)
- cats-effect 3 (IO monad)
- sbt 1.10.1

### Setup

You need Java 21 and sbt. With [SDKMAN](https://sdkman.io/):

```bash
sdk install java 21.0.3-tem
sdk install sbt 1.10.1
```

Or with [Homebrew](https://brew.sh/):

```bash
brew install openjdk@21 sbt
```

### Running the backend

```bash
cd backend
sbt run
# ThinkDrop backend listening on 0.0.0.0:8080
```

### Building a fat JAR

```bash
cd backend
sbt stage
./target/universal/stage/bin/thinkdrop-backend
```

## Screens

| Screen | What it does |
|--------|-------------|
| Path | Winding lesson path. Tap a node to add today's entry. |
| Add | 4-step flow: kind picker → title → body → preview. |
| Reveal | Celebration + husky hop after a successful entry. |
| Profile | Streak grid, stats, "on this day", recent entries with kind badges. |

## How streaks work

- Adding any entry today keeps the streak alive.
- Missing a day resets the counter.
- Longest streak is tracked separately and never resets.
- The "last 30 days" grid shows your full recent history.

## Deployed

Frontend: `https://bhagyashree-vaidya.github.io/ThinkDrop/`
