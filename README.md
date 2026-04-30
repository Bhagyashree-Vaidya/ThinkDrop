# ThinkDrop

A vocabulary and idea tracker with a Duolingo-style daily streak. One word a day. No pressure.

## What it is

ThinkDrop is a personal commonplace book for words. You add one word each day, write what it means to you, and keep a streak going. Think of it as a private dictionary built from the words that actually caught your attention.

The path screen works like Duolingo: a winding sequence of nodes, each one a lesson. Complete a node by adding your word. Locked nodes open as you keep the streak.

## Stack

- Plain HTML, CSS, and vanilla JavaScript. No build step, no framework, no npm.
- Fonts: Fraunces (serif headings), Manrope (UI), JetBrains Mono (labels).
- Storage: `localStorage` on GitHub Pages, `window.storage` inside Claude artifacts.

## How streaks work

A streak is counted as consecutive days with at least one entry.

- Adding a word today keeps your streak going.
- Missing a day resets the streak counter.
- Longest streak is tracked separately and never resets.
- The "last 30 days" grid on the profile screen shows your full recent history.

## Screens

| Screen | What it does |
|--------|-------------|
| Path | Winding lesson path. Tap a node to add today's word. |
| Add | Three-step flow: word, definition, preview. |
| Reveal | Celebration after a successful entry. |
| Profile | Streak grid, stats, "on this day" resurfacing, recent entries. |

## Running locally

Open `index.html` in a browser. That's it.

## Deployed

Live at: `https://bhagyashree-vaidya.github.io/ThinkDrop/`
