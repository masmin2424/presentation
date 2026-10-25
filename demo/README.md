# JEP 540 – The chocolate demo 🍫

A nightly batch turns a partner's JSON order file into an invoice. The partner changes their export script in **4 realistic ways**. Each change shows one rule of JEP 540, the JSON API built into JDK 28.

| Partner writes | Java 21 + Jackson (default) | JDK 28, JEP 540 |
|---|---|---|
| `"quantity": "12 pcs"` | 0 pralines billed, no error | Rejected: no silent conversion |
| `"quantity"` twice | Last one wins, no error | Rejected: strict JSON |
| `"giftWrap": "oui"` | Gift wrap dropped, no error | Rejected: booleans are booleans |
| `"orderId": "A-1042"` | Invoice for order #0 | Handled on purpose with a `switch` |

The full story, why it matters for developers, and the presentation script: [CHOCOLATE-DEMO.md](CHOCOLATE-DEMO.md).

## Files

| File | What it is |
|---|---|
| `scenarios/1-normal.json` … `5-new-order-id-format.json` | The partner's order file, before and after each script change |
| `chocolate-order.json` | A copy of the normal order, to edit live |
| `ChocolateDemoBefore.java` | The batch with **Jackson** (Java 21) |
| `ChocolateDemo.java` | The batch with the **built-in JSON API** (JDK 28) |
| `CHOCOLATE-DEMO.md` | The story, the 5 rules, and the presentation script |
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
java -cp "lib/*" ChocolateDemoBefore.java scenarios/2-units-in-quantity.json
java --add-modules jdk.incubator.json ChocolateDemo.java scenarios/2-units-in-quantity.json
```

Without a file name, both read `chocolate-order.json`.

## Notes

- `WARNING: Using incubator modules: jdk.incubator.json` is normal on the JDK 28 side.
- JEP 540 is an incubator API: great for scripts, batch tools and tests, not for production yet.
- The Jackson results are real output (Jackson 2.18.2, Java 21). The JDK 28 results are JEP 540's designed behaviour; confirm the exact messages on a JDK 28 build.
- The shop, the partner and the prices are fictional.
- Feedback to the OpenJDK team: core-libs-dev@openjdk.org
