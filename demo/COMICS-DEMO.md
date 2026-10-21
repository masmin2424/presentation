# 🗯️ The comics demo: what it shows and why

## The story (for everyone)

`comics.json` is a small list of famous Belgian comics: Tintin, Spirou, Suske en Wiske, Lucky Luke, Blake et Mortimer, Gaston Lagaffe, the Smurfs, Boule et Bill and Le Chat.

For each series we know the creator, the year it started, the hero, and **sometimes** the hero's animal companion: Milou, Jolly Jumper, Bill…

The program answers simple questions:
- Which series are the oldest?
- Which heroes have an animal, and which animal?
- Which heroes have a **dog**?
- What is the average creation year?

## The goal of the demo

Show that **real JSON is messy, and the new API handles the mess simply and safely.**

In real data, not every record has every field. Here the "companion" is:

| Case | Example | In the JSON |
|---|---|---|
| Present | Tintin | `"companion": { "name": "Milou", "species": "dog" }` |
| Missing | The Smurfs | no `"companion"` line at all |
| Explicitly empty | Blake et Mortimer | `"companion": null` |

That's three different situations. A program must handle all three without crashing and without mixing them up.

| | BEFORE: Java 21 + Jackson | AFTER: JDK 28 built-in JSON API |
|---|---|---|
| Missing field | `get()` returns Java `null`: you must check it | `tryGet()` returns an `Optional` |
| JSON `null` | Returns a special `NullNode`: you must check it **too** | `tryValue()` returns an empty `Optional` |
| Code | `(c == null \|\| c.isNull()) ? ... : ...` | `tryGet("companion").flatMap(JsonValue::tryValue)` |
| Forget a check | `NullPointerException` somewhere later | The `Optional` reminds you there may be nothing |

## How to run it (two terminals side by side)

**Left terminal: BEFORE** (your normal Java 21)

```
java -cp "lib/*" ComicsDemoBefore.java
java -cp "lib/*" ComicsDemoBefore.java dog
```

**Right terminal: AFTER** (JDK 28; first run `$env:Path = "C:\jdk-28\bin;$env:Path"` once)

```
java --add-modules jdk.incubator.json ComicsDemo.java
java --add-modules jdk.incubator.json ComicsDemo.java dog
```

Expected result (both sides):

```
1929  Tintin               by Herge              with Milou (dog)
1938  Spirou et Fantasio   by Rob-Vel            with Spip (squirrel)
1945  Suske en Wiske       by Willy Vandersteen
1946  Lucky Luke           by Morris             with Jolly Jumper (horse)
...
5 of 9 heroes have an animal companion
Average creation year: 1951
```

With `dog` at the end, only Tintin (Milou) and Boule et Bill (Bill) remain.

## Step by step during the presentation

1. **Run both.** Same list, same result. "Same job, both work."
2. **Run both with `dog`.** "Two Belgian heroes have a dog: Milou and Bill."
3. **Open `comics.json`** and point at the three cases: Tintin (companion), the Smurfs (no line), Blake et Mortimer (`null`).
4. **Show the code side by side**, the `companion(...)` method at the bottom of each file:
   - Before: `(c == null || c.isNull()) ? Optional.empty() : Optional.of(c)`, two checks to remember.
   - After: `comic.tryGet("companion").flatMap(JsonValue::tryValue)`, one line, nothing to forget.
5. **Optional bonus, a typo bug.** In `comics.json`, change Spirou's `"firstYear"` to `"firstyear"` (small y). Run both:
   - **Jackson:** `NullPointerException ... because the return value of "JsonNode.get(String)" is null`. It doesn't say **which** field or **which** comic.
   - **JDK 28:** an error naming the missing member `firstYear`. The JEP's errors also give the path and position in the file.

   Put `"firstYear"` back afterwards.

Press ↑ in a terminal to rerun the last command.

## What else the demo shows (for developers)

- **Reading a JSON array** at the top level: `Json.parse(...).asList()`.
- **Nested objects:** `companion.get("species").asString()`.
- **Works with streams:** sort, filter, count and average with normal Java code. No special query language.
- **A command-line argument** (`dog`) to show how quickly you can build a small useful tool.

## Honest notes

- Both versions are about the same length. The difference is how easy it is to handle missing and `null` values **correctly**.
- The comics facts (creators, start years) are well known. Accents were removed (Herge, Andre Franquin) to avoid garbled characters in Windows terminals.
- I tested the Jackson side, including the typo's `NullPointerException`. The JDK 28 error text may differ slightly: run it once before the meeting.
- The new API is an incubator in JDK 28: great for tools, tests and demos, not for production yet.

## One-line summary

> "Real data is messy: some heroes have a dog, some don't, some have `null`. The new API makes handling that the natural way to write code, instead of something you have to remember."
