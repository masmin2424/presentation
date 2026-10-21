# Everyday JSON in the JDK: JEP 540

A presentation and runnable demos about **JEP 540: Simple JSON API (Incubator)**, the JSON API built into Java starting with JDK 28.

> Python, Go, JavaScript, even Zig ship JSON out of the box. Java finally does too.

## What's in this repository

| Folder | Content |
|---|---|
| [`slides/`](slides/) | The presentation, as [PDF](slides/everyday-json-in-the-jdk.pdf) and [PowerPoint](slides/everyday-json-in-the-jdk.pptx) |
| [`video/`](video/) | The presentation video and an AI-generated intro clip |
| [`images/`](images/) | The AI-generated presenter image |
| [`demo/`](demo/) | Runnable Java demos, each with a **before** (Java 21 + Jackson) and **after** (JDK 28 built-in API) version |

## The demos in 30 seconds

You need **JDK 28** ([early access builds](https://jdk.java.net/28/)). No Maven, no libraries:

```
cd demo
java --add-modules jdk.incubator.json ConfigDemo.java
```

| Demo | What it shows |
|---|---|
| `ConfigDemo` | Read a config file, with defaults and clear errors |
| `BuildJsonDemo` | Build JSON safely instead of gluing strings together |
| `WeatherDemo` | The JEP's own example: average temperature from a forecast |
| `ThreadDumpDemo` | Analyse a real JDK thread dump in a few lines |
| `ComicsDemo` 🇧🇪 | Belgian comics: handle missing and `null` fields simply |
| `ChocolateDemo` 🇧🇪 | A batch reads a partner's order file: Jackson silently turns `"12 pcs"` into 0, the new API rejects it |

See [`demo/README.md`](demo/README.md) for all commands, [`demo/DEMO-CHEATSHEET.md`](demo/DEMO-CHEATSHEET.md) for a side-by-side before/after run, and [`demo/FAQ.md`](demo/FAQ.md) for answers to common questions.

## Important to know

- JEP 540 is an **incubator** API in JDK 28: great for scripts, tools, tests and demos, **not for production yet**. Names may change before it becomes final.
- It does **not** replace Jackson: no object mapping, no streaming. It's for the simple cases.
- The "after" demos were written against the JEP 540 API as documented; run them on a JDK 28 build to confirm the exact output.

## Credits and sources

- [JEP 540: Simple JSON API (Incubator)](https://openjdk.org/jeps/540) on openjdk.org
- Inspired by the Devoxx talk **"Everyday JSON"** by Viktor Klang and Alan Bateman (Oracle Java Platform Group)
- Feedback on the API goes to the OpenJDK team: core-libs-dev@openjdk.org
- Comic series names and creators are used for illustration only. The chocolate shop and prices are fictional.
- The presenter image and intro clip are AI-generated (Grok).
