# JEP 540 · Chocolate Factory demo

Live demo for a talk on **JEP 540: Simple JSON API (Incubator)**, integrated in **JDK 28** (GA planned 9 March 2027).

Story: chocolate orders arrive as JSON, we turn them into invoices.
Each scene is one small class you run on stage. Four of the JDK 28 scenes have a Jackson
version on the Java 25 side, so you can show the two side by side in IntelliJ.

Slides: [`JEP540-Chocolate-Factory.pptx`](JEP540-Chocolate-Factory.pptx) (21 slides, speaker notes on each one).
Presenter notes, one page per scene: [`docs/`](docs/README.md).
The code on the slides is the code in this folder.

## Setup (do this the day before)

1. Get a **JDK 28 early-access build** that contains the `jdk.incubator.json` module:
   https://jdk.java.net/28/ (macOS / AArch64 for an Apple Silicon Mac), or the build in [`../jdk/`](../jdk/).
   Check with `java --list-modules | grep json`.
2. Open **this `demo2` folder** (not the repository root) in IntelliJ as a Maven project,
   so the `pom.xml` and the run configurations in `.run/` are picked up.
3. *File → Project Structure → Project*: SDK = the JDK 28 EA build.
   Language level: `28` if IntelliJ lists it, otherwise `25`. The code only uses Java 25 language features; JDK 28 is needed for the module.
4. Reload Maven. The run configurations from `.run/` appear in the Run menu in two folders:
   **JDK 28 (built-in JSON)** and **Java 25 + Jackson**. They already pass `--add-modules jdk.incubator.json`.
5. Run every configuration once, so you have seen the real messages before the audience does.

Terminal alternative: `mvn -q compile exec:exec -Dscene=be.chocolatefactory.jdk28.Scene04_BadQuantity`

## The JSON files (`src/main/resources/orders/`)

| File | What is in it |
|---|---|
| `valid-order.json` | A clean order: nested customer/address, 3 items, a `null`, a boolean, an optional `giftWrap` |
| `duplicate-key.json` | `"quantity": 2` … and later in the same item `"quantity": 2000` |
| `bad-quantity.json` | Quantities `"2"` (string), `2.5`, `3000000000`, `null`, and one item with no quantity |
| `legacy-order.json` | Old till system: `"orderId": 42` is a number, not a string |
| `trailing-comma.json` | A trailing comma after the last item |
| `with-comments.json` | `//` comment lines (hand-edited file) |

IntelliJ will underline the last two (and the duplicate key) in red. That is the point.

## The scenes

| # | JDK 28 class | Java 25 + Jackson | What you show | Expected result |
|---|---|---|---|---|
| 1 | `Scene01_ReadOneField` | `Jackson01_ReadOneField` | One chain of `get(...)`, no cast, no library | `Ship to: Bruges` on both |
| 2 | `Scene02_OrderToInvoice` | `Jackson02_OrderToInvoice` | `asList()`, `asInt()`, money as `BigDecimal`, build with `JsonObject.of`, print with `toString()` and `Json.toDisplayString` | Total `294.60` (Jackson prints `294.6`) |
| 3 | `Scene03_DuplicateKey` | `Jackson03_DuplicateKey` | Duplicate names are always an error | JDK: `JsonParseException` naming `"quantity"`. Jackson: `2000 boxes = EUR 49800.0` |
| 4 | `Scene04_BadQuantity` | `Jackson04_BadQuantity` | `asInt()` only accepts an exact int | JDK: 5 × `JsonValueException` with a path. Jackson: `2`, `2`, `-1294967296`, `0`, `0` |
| 5 | `Scene05_MissingAndNull` | – | `tryGet` (absent), `tryValue` (null), `get` fails fast | `gift wrap: true/false/false`, `Discount code: none`, then one error |
| 6 | `Scene06_ShapeChanges` | – | Sealed `JsonValue` + pattern `switch`; 6 cases = no `default` | `CHOC-2027-0042`, `LEGACY-42`, then the type of each member |
| 7 | `Scene07_StrictParser` | – | No trailing commas, no comments; clean the text yourself | 2 × `JsonParseException`, then `after cleanup → CHOC-2027-0046` |

The exact wording of the JDK exception messages comes from the EA build. The JEP shows the
format: `<what went wrong>. Path: "{items[0{quantity". Location: line …, position ….`

## "The catch", live (30 seconds)

1. In `pom.xml`, comment out the two `<arg>` lines under `maven-compiler-plugin`.
2. Reload Maven, build: compilation fails because the `jdk.incubator.json` module is not resolved.
3. Put them back. Run any scene: the console starts with `WARNING: Using incubator modules: jdk.incubator.json`.

## Optional: the Java 25 side on a real JDK 25

`mvn -Pjava25 compile` with a JDK 25 builds only the `java25` package (the `jdk28` package is excluded,
and the `--add-modules` flag is removed). In IntelliJ: tick the `java25` profile in the Maven tool window
and switch the SDK to 25.

## Troubleshooting

* `?` instead of ✓ ✗ → in the console: keep `-Dstdout.encoding=UTF-8` in the VM options (already in `.run/`).
* `package jdk.incubator.json is not visible` in IntelliJ → *Settings → Build → Compiler → Java Compiler*,
  check that `--add-modules jdk.incubator.json` is in the additional parameters for this module (Maven reload normally does it).
* `module not found: jdk.incubator.json` → the project SDK is not JDK 28.
* Running a class with the green gutter arrow instead of a `.run` configuration → add
  `--add-modules jdk.incubator.json` to that configuration's VM options.

Versions: Jackson `2.21.8` (2.21 is a Jackson LTS branch), maven-compiler-plugin `3.15.0`, exec-maven-plugin `3.6.3`.
