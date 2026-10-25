# JEP 540 – The chocolate demo 🍫

One demo, one story: a nightly batch reads a partner's order file. The partner changes their export script and writes `"quantity": "12 pcs"` instead of `12`.

- **Before** (Java 21 + Jackson): the quantity silently becomes `0`. The batch stays green, and 12 pralines are invoiced for free.
- **After** (JDK 28, built-in JSON API): the order is **rejected** with a clear error.

The full story, the step-by-step script and the honest "can this happen to us?" answer are in [CHOCOLATE-DEMO.md](CHOCOLATE-DEMO.md).

## Files

| File | What it is |
|---|---|
| `chocolate-order.json` | The partner's order file (4 kinds of pralines, optional gift wrap) |
| `ChocolateDemoBefore.java` | Reads the order with **Jackson** (Java 21) |
| `ChocolateDemo.java` | Reads the order with the **built-in JSON API** (JDK 28) |
| `CHOCOLATE-DEMO.md` | The story and the presentation script |
| `DEMO-CHEATSHEET.md` | The commands for two terminals side by side |
| `FAQ.md` | Answers to sceptical questions |

## Setup

- **Before:** your normal Java 21, plus the Jackson library in `lib/`. If `lib/` is missing, download it once (PowerShell, in this folder, ~2.3 MB from Maven Central):

  ```
  mkdir lib; $m = "https://repo1.maven.org/maven2/com/fasterxml/jackson/core"; foreach ($a in "jackson-databind","jackson-core","jackson-annotations") { Invoke-WebRequest "$m/$a/2.18.2/$a-2.18.2.jar" -OutFile "lib\$a-2.18.2.jar" }
  ```

- **After:** JDK 28 (https://jdk.java.net/28/), unzipped to `C:\jdk-28`. In the terminal you use for it, run once:

  ```
  $env:Path = "C:\jdk-28\bin;$env:Path"
  ```

  This only affects that terminal; your Java 21 stays the default everywhere else.

## Run

```
java -cp "lib/*" ChocolateDemoBefore.java
java --add-modules jdk.incubator.json ChocolateDemo.java
```

Both print the bill: **TOTAL 67,70 EUR**. Then change `"quantity": 12` to `"quantity": "12 pcs"` in `chocolate-order.json` and run both again.

## Notes

- `WARNING: Using incubator modules: jdk.incubator.json` is normal on the JDK 28 side.
- JEP 540 is an incubator API: great for scripts, batch tools and tests, not for production yet.
- The shop, the partner and the prices are fictional.
- Feedback to the OpenJDK team: core-libs-dev@openjdk.org
