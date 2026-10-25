# Demo cheat sheet: the chocolate batch, two terminals 🍫

## Setup (once, before the meeting)

- [ ] Java 21 installed (your default `java`)
- [ ] JDK 28 unzipped to `C:\jdk-28` (from https://jdk.java.net/28/)
- [ ] Jackson jars in `lib\`
- [ ] The `scenarios\` folder has 5 files

## Open two terminals side by side

In VS Code: open this folder, then **Terminal › New Terminal**, then the **split** button (⊞).

- **LEFT = BEFORE** (Java 21 + Jackson): nothing to set up.
- **RIGHT = AFTER** (JDK 28): run once: `$env:Path = "C:\jdk-28\bin;$env:Path"`

## The 5 scenario files

Run the same file on both sides. Press ↑ and change the file name for the next one.

```
LEFT:   java -cp "lib/*" ChocolateDemoBefore.java scenarios/1-normal.json
RIGHT:  java --add-modules jdk.incubator.json ChocolateDemo.java scenarios/1-normal.json
```

| File | LEFT (Jackson) shows | RIGHT (JDK 28) shows | Say |
|---|---|---|---|
| `1-normal.json` | 67,70 EUR | 67,70 EUR | "Good data: both work." |
| `2-units-in-quantity.json` | `0 x Hazelnut praline`, 48,50 EUR | ORDER REJECTED | "No silent conversion." |
| `3-duplicate-key.json` | `1 x Hazelnut praline`, 50,10 EUR | FILE REJECTED | "Strict JSON: no guessing which key wins." |
| `4-giftwrap-in-french.json` | no gift wrap, 65,20 EUR | ORDER REJECTED | "`"oui"` is not a boolean." |
| `5-new-order-id-format.json` | Invoice for order **#0** | Invoice for order **A-1042** | "Sealed types: the new format is handled on purpose." |

Short on time? Scenarios 1, 2 and 5.

## What to say at the end

| | Before | After |
|---|---|---|
| Java version | 21 (today) | 28 (March 2027) |
| Extra library | Jackson (3 jars, or Maven) | None |
| 4 partner changes | 4 wrong invoices, 0 errors | 3 rejected, 1 handled on purpose |
| Production ready | Yes | Not yet (incubator) |

> "In our apps, entities and validation catch most of this. The demo is about batch jobs, scripts and glue code, where those layers often don't exist. That's where strict-by-default helps."

The `WARNING: Using incubator modules` line in the right terminal is normal.
