# Demo cheat sheet: the chocolate batch, two terminals 🍫

## Setup (once, before the meeting)

- [ ] Java 21 installed (your default `java`)
- [ ] JDK 28 unzipped to `C:\jdk-28` (from https://jdk.java.net/28/)
- [ ] Jackson jars in `lib\`
- [ ] `chocolate-order.json` contains `"quantity": 12` for Manon cafe

## Open two terminals side by side

In VS Code: open this folder, then **Terminal › New Terminal**, then the **split** button (⊞).

### LEFT terminal: BEFORE (Java 21 + Jackson)

Nothing to set up:

```
java -cp "lib/*" ChocolateDemoBefore.java
```

> `-cp "lib/*"` = "also load the Jackson library from the lib folder".

### RIGHT terminal: AFTER (JDK 28, nothing added)

Once per terminal:

```
$env:Path = "C:\jdk-28\bin;$env:Path"
```

Then:

```
java --add-modules jdk.incubator.json ChocolateDemo.java
```

> `--add-modules jdk.incubator.json` = "switch on the JSON API built into Java 28".

## The script (about 3 minutes)

1. **Story:** "Every night a partner sends us an order file. Our batch turns it into invoices."
2. **Run both:** same bill, **TOTAL 67,70 EUR**.
3. **"The partner changes their script."** In `chocolate-order.json`, change `"quantity": 12` to `"quantity": "12 pcs"`. Save.
4. **LEFT (Jackson):** `0 x Manon cafe`, **TOTAL 48,50 EUR**, no error. "The batch is green. We just gave away 12 pralines."
5. **RIGHT (JDK 28):** `ORDER REJECTED`. "It refuses to guess. We find out tonight, not at the end of the month."
6. **For fun:** try `"douze"`. Same result on both sides.
7. **Bonus:** delete the `"giftWrap"` line. Both totals drop by 2,50 EUR, nothing breaks: optional fields are handled.
8. **Put `12` back** and save.

Press ↑ to rerun the last command.

## What to say

| | Before | After |
|---|---|---|
| Java version | 21 (today) | 28 (March 2027) |
| Extra library | Jackson (3 jars, or Maven) | None |
| `"12 pcs"` in the file | Silently 0, wrong invoice | Clear error |
| Production ready | Yes | Not yet (incubator) |

> "In our apps, entities and validation catch most of this. The demo is about batch jobs, scripts and glue code, where those layers often don't exist. That's where strict-by-default helps."

The `WARNING: Using incubator modules` line in the right terminal is normal.
