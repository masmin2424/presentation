# Demo cheat sheet: two terminals, before vs after

## Setup (once, before the meeting)

- [ ] Java 21 installed (already done: it's your default `java`)
- [ ] JDK 28 unzipped to `C:\jdk-28` (from https://jdk.java.net/28/)
- [ ] Jackson jars in `lib\` (already included in the demo folder)
- [ ] `config.json` contains `"port": 9090`

## Open two terminals side by side

In VS Code: open the folder `Downloads\jep540-demo`, then **Terminal › New Terminal**, then the **split** button (⊞) to get two terminals.

### LEFT terminal: BEFORE (Java 21 + Jackson)

Nothing to set up. Plain `java` is already Java 21.

```
java -version
java -cp "lib/*" ConfigDemoBefore.java
java -cp "lib/*" BuildJsonDemoBefore.java
java -cp "lib/*" WeatherDemoBefore.java
java -cp "lib/*" ThreadDumpDemoBefore.java
```

> `-cp "lib/*"` = "also load the Jackson library from the lib folder". Without it: compile errors.

### RIGHT terminal: AFTER (JDK 28, nothing added)

Run this line first, once per terminal. It only affects this terminal:

```
$env:Path = "C:\jdk-28\bin;$env:Path"
```

```
java -version
java --add-modules jdk.incubator.json ConfigDemo.java
java --add-modules jdk.incubator.json BuildJsonDemo.java
java --add-modules jdk.incubator.json WeatherDemo.java
java --add-modules jdk.incubator.json ThreadDumpDemo.java
```

> `--add-modules jdk.incubator.json` = "switch on the JSON API built into Java 28". No library needed.

## The key moment (do it live)

1. In `config.json`, change `9090` to `"abc"` and save.
2. LEFT: `java -cp "lib/*" ConfigDemoBefore.java` prints `port = 0` (silently wrong).
3. RIGHT: `java --add-modules jdk.incubator.json ConfigDemo.java` prints a clear error.
4. Put `9090` back.

## Belgian edition 🇧🇪 (fun examples)

### Comics: Tintin, Lucky Luke, the Smurfs…

```
LEFT:   java -cp "lib/*" ComicsDemoBefore.java
RIGHT:  java --add-modules jdk.incubator.json ComicsDemo.java
```

Add `dog` at the end to keep only heroes with a dog (Milou, Bill).
Point to make: some heroes have no companion (field missing) and Blake & Mortimer has `"companion": null`.
The new API handles both in one line: `tryGet("companion").flatMap(JsonValue::tryValue)`.

### Chocolate: the "free pralines" bug

```
LEFT:   java -cp "lib/*" ChocolateDemoBefore.java
RIGHT:  java --add-modules jdk.incubator.json ChocolateDemo.java
```

Both print the bill: TOTAL 67,70 EUR.
Then in `chocolate-order.json` change `"quantity": 12` to `"quantity": "12 pcs"` (as if a partner changed their export script) and rerun both:
- LEFT (Jackson): `0 x Manon cafe`, TOTAL 48,50 EUR. **12 pralines given away for free!**
- RIGHT (JDK 28): `ORDER REJECTED` with a clear error.

Put `12` back afterwards.

## What to say

| | Before | After |
|---|---|---|
| Java version | 21 (today) | 28 (March 2027) |
| Extra library | Jackson (3 jars, or Maven) | None |
| Wrong value | Hidden (`port = 0`) | Clear error |
| Production ready | Yes | Not yet (incubator) |

Tips: press ↑ to rerun the last command. The `WARNING: Using incubator modules` line in the right terminal is normal.
