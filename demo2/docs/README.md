# Presenter notes: demo2 (Chocolate Factory)

One page per scene: what to open, what to say, what to run, what the audience should remember,
and the questions you may get.

| # | Scene | The one thing they must remember | Time |
|---|---|---|---|
| 1 | [Read one field](scene-1-read-one-field.md) | No library, no cast | 2 min |
| 2 | [Order to invoice](scene-2-order-to-invoice.md) | Read and write with plain Java collections; money stays exact | 4 min |
| 3 | [Duplicate key](scene-3-duplicate-key.md) | The JDK refuses to guess | 3 min |
| 4 | [Bad quantity](scene-4-bad-quantity.md) | Fail fast vs. silent coercion | 3 min |
| 5 | [Missing and null](scene-5-missing-and-null.md) | Absent is not the same as null | 2 min |
| 6 | [Shape changes](scene-6-shape-changes.md) | Sealed types: the compiler checks you covered every case | 3 min |
| 7 | [Strict parser](scene-7-strict-parser.md) | RFC 8259 only, no options | 2 min |
| – | [The catch](the-catch.md) | It is an incubator: one flag, twice | 2 min |

About 20 minutes of demo. Slides 8 to 17 of the deck follow the same order, and slides 19–20 are "the catch".

The whole talk, slide by slide, with plain-language explanations for the business side: [speaker script](speaker-script.md).

## The thread to repeat in every scene

> **Small by design, strict by design.**

Every scene is an example of one of those two words. Say it at the end of scenes 3, 4 and 7.

## Checklist before you go on stage

- [ ] Project SDK is the JDK 28 early-access build. `java --list-modules | grep json` shows `jdk.incubator.json`.
- [ ] You ran every run configuration once and read the real exception messages (the slides only summarise them).
- [ ] Big fonts: *View → Appearance → Enter Presentation Mode*, or raise the editor and console font size.
- [ ] Only these tabs open: `valid-order.json` and `Scene01_ReadOneField.java`.
- [ ] You know how to split the editor (right-click a tab → *Split Right*) for the Jackson comparisons.
- [ ] `pom.xml` has the `--add-modules` lines **un**commented (you comment them out live in "the catch").

## What is real, what is expected

- Jackson results (2000 boxes, `-1294967296`, `0` …) are real output of Jackson 2.x.
- JDK 28 behaviour (which calls throw, which succeed) follows JEP 540 and the JDK 28 API docs.
  The **exact wording** of the JDK messages comes from your EA build: read it from the console, don't recite it.
