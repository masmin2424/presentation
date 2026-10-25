# Everyday JSON in the JDK: JEP 540

A presentation and runnable demos about **JEP 540: Simple JSON API (Incubator)**, the JSON API built into Java starting with JDK 28.

> Python, Go, JavaScript, even Zig ship JSON out of the box. Java finally does too.

## What's in this repository

| Folder | Content |
|---|---|
| [`slides/`](slides/) | The presentation, as [PDF](slides/everyday-json-in-the-jdk.pdf) and [PowerPoint](slides/everyday-json-in-the-jdk.pptx) |
| [`video/`](video/) | The presentation video and an AI-generated intro clip |
| [`images/`](images/) | The AI-generated presenter image |
| [`demo/`](demo/) | The chocolate batch demo, in a **before** (Java 21 + Jackson) and **after** (JDK 28 built-in API) version |
| [`jdk/`](jdk/) | OpenJDK 28 early-access build 20 for **macOS (Apple Silicon)**, stored with Git LFS |

## JDK 28 for macOS (Apple Silicon)

`jdk/openjdk-28-ea+20_macos-aarch64_bin.tar.gz` is a copy of the official OpenJDK early-access build, licensed under GPLv2 with the Classpath Exception (see the `legal/` folder inside the archive).

```
tar -xzf jdk/openjdk-28-ea+20_macos-aarch64_bin.tar.gz
export JAVA_HOME="$PWD/jdk-28.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
java -version
```

- Clone with Git LFS installed (`git lfs install`), or download the file from GitHub's web page: otherwise you only get a small pointer file.
- Early-access builds are replaced regularly. For the newest build (and Windows/Linux), use the official site: https://jdk.java.net/28/

## The chocolate demo in 30 seconds 🍫

A nightly batch reads a partner's order file. One day the partner changes their export script and writes `"quantity": "12 pcs"` instead of `12`.

| | Before: Java 21 + Jackson | After: JDK 28 built-in JSON API |
|---|---|---|
| Normal order | TOTAL 67,70 EUR | TOTAL 67,70 EUR |
| `"quantity": "12 pcs"` | Silently 0: TOTAL 48,50 EUR, 12 pralines for free | Order rejected with a clear error |

```
cd demo
java -cp "lib/*" ChocolateDemoBefore.java
java --add-modules jdk.incubator.json ChocolateDemo.java
```

See [`demo/README.md`](demo/README.md) for setup, [`demo/CHOCOLATE-DEMO.md`](demo/CHOCOLATE-DEMO.md) for the full story and presentation script, [`demo/DEMO-CHEATSHEET.md`](demo/DEMO-CHEATSHEET.md) for a two-terminal run, and [`demo/FAQ.md`](demo/FAQ.md) for answers to common questions.

## Important to know

- JEP 540 is an **incubator** API in JDK 28: great for scripts, tools, tests and demos, **not for production yet**. Names may change before it becomes final.
- It does **not** replace Jackson: no object mapping, no streaming. It's for the simple cases.
- The "after" demos were written against the JEP 540 API as documented; run them on a JDK 28 build to confirm the exact output.

## Credits and sources

- [JEP 540: Simple JSON API (Incubator)](https://openjdk.org/jeps/540) on openjdk.org
- Inspired by the Devoxx talk **"Everyday JSON"** by Viktor Klang and Alan Bateman (Oracle Java Platform Group)
- Feedback on the API goes to the OpenJDK team: core-libs-dev@openjdk.org
- The chocolate shop, the partner and the prices are fictional.
- The presenter image and intro clip are AI-generated (Grok).
