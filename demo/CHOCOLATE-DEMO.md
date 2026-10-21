# 🍫 The chocolate demo: what it shows and why

## The story (a realistic one)

A Belgian chocolate shop sells through a partner webshop. Every night, the partner's **export script** drops a JSON file with the day's orders. Our **nightly batch** reads the file and prepares the invoices.

There is no person in the loop and no web form. It's machine to machine: their script writes, our batch reads.

`chocolate-order.json` is one of those orders: 4 kinds of pralines, a quantity and a price for each, and an optional gift wrap. The normal bill is **67,70 EUR**.

### What goes wrong

One day the partner **changes their export script**. Nobody tells us. Typical changes:

| Their change | What the file now contains | Why it happens |
|---|---|---|
| They add the unit | `"quantity": "12 pcs"` | "It's clearer for our warehouse team" |
| A field is left empty | `"quantity": ""` | A bug in their new version |
| They switch to text | `"quantity": "douze"` | A translation or formatting step added by mistake |

The file is still valid JSON. Our batch runs at 2 a.m. as usual. What does it do?

## The goal of the demo

Show that **when code reads JSON by hand, a small change on the other side can silently produce wrong results**, and that the new built-in Java JSON API refuses to guess.

| File contains | BEFORE: Java 21 + Jackson (`asInt()`) | AFTER: JDK 28 built-in JSON API |
|---|---|---|
| `"quantity": 12` | 12, bill 67,70 EUR | 12, bill 67,70 EUR |
| `"quantity": "12 pcs"` | **0**, bill 48,50 EUR, no error | **Order rejected**, clear error |
| `"quantity": ""` | **0**, no error | **Order rejected**, clear error |
| `"quantity": "douze"` | **0**, no error | **Order rejected**, clear error |

The "Before" column is real output: I ran each case with Jackson 2.18.2 on Java 21.
The "After" column is the designed behaviour of JEP 540 (no silent conversion): run it once on JDK 28 to see the exact message.

The message: **the batch doesn't crash, so nobody looks.** The invoices go out with 12 pralines missing, and the problem is found weeks later in the accounts. A loud error at 2 a.m. would have been fixed the next morning.

## "But we have checks everywhere. Can this really happen to us?"

A fair question, and the honest answer has two parts.

### In our main applications: mostly no

| Layer | Does it catch `"12 pcs"`? |
|---|---|
| Front-end form validation | Yes, but a batch file never goes through a form |
| Entity / DTO (Jackson maps JSON onto a class with `int quantity`) | **Yes**, rejected with an error |
| Validation annotations (`@Min(1)`, `@NotNull`) | Yes, if someone added them |
| Database column type | Yes, a numeric column refuses text |

So in a normal layered application, `"12 pcs"` and `"douze"` are caught.

### Where it does happen

1. **Code that reads JSON field by field**, with no entity class: batch jobs, import scripts, glue code calling a partner's API, quick tools, tests, config readers. This code often has none of the layers above, and it's exactly the kind of code the new API is meant for.

2. **Even with entities, Jackson's defaults are lenient.** Real results from Jackson 2.18.2, mapping onto a class with `int quantity`:

| File contains | Entity gets | Caught? |
|---|---|---|
| `"quantity"` missing | **0** | Only if validation was added |
| `"quantity": null` | **0** | Only if validation was added |
| `"quantity": ""` | **0** | Only if validation was added |
| `"quantity": 12.7` | **12**, decimals dropped | No |
| `"quantity": "12"` (text) | 12 | Accepted on purpose |
| `"quantity": "12 pcs"` | error | Yes |

So the most realistic production bug is not "douze". It's **a missing or empty quantity that silently becomes 0** when nobody added validation.

### How to say it

> "In our apps, the entity and validation layers would catch most of this. The demo shows quick code without those layers: batch jobs, scripts, tools. That's where the new API helps, because it's strict by default. You're safe even when you forgot the checks."

## How to run it (two terminals side by side)

**Left terminal: BEFORE** (your normal Java 21)

```
java -cp "lib/*" ChocolateDemoBefore.java
```

**Right terminal: AFTER** (JDK 28; first run `$env:Path = "C:\jdk-28\bin;$env:Path"` once)

```
java --add-modules jdk.incubator.json ChocolateDemo.java
```

## Step by step during the presentation

1. **Tell the story.** "Every night a partner sends us order files. Our batch turns them into invoices."
2. **Run both.** Same bill: 67,70 EUR. "Both versions work."
3. **"Now the partner changes their script."** Open `chocolate-order.json`, change `"quantity": 12` to `"quantity": "12 pcs"`. Save.
4. **Run the left one (Jackson).** Point at `0 x Manon cafe` and the total of 48,50 EUR.
   "No error. The batch finished green. We just invoiced 12 pralines for free."
5. **Run the right one (JDK 28).** Point at `ORDER REJECTED`.
   "The new API refuses to guess. Someone gets an alert and calls the partner."
6. **For fun:** try `"douze"`. Same result on both sides.
7. **Put `12` back** and save.

Press ↑ in a terminal to rerun the last command.

## What else the demo shows (for developers)

- **Reading numbers:** `asInt()` for quantities, `asDouble()` for prices.
- **Walking a list:** `order.get("items").asList()` and a normal `for` loop.
- **Optional fields:** `"giftWrap"` may be missing. `tryGet("giftWrap")` returns an `Optional`, so the default is explicit: no gift wrap.
  Remove the `"giftWrap"` line to show it: the 2,50 EUR disappears and nothing breaks.
- **No coercion:** the new API never converts a string into a number behind your back. That's a deliberate design choice of JEP 540.

## Honest notes

- Jackson isn't "broken": `asInt()` is documented to return 0 when it can't convert. A careful developer can catch it (check `isInt()` first, or map onto a class and add validation). The point is that with Jackson's tree API the silent path is the easy one; with the new API, the safe path is the easy one.
- The shop, the partner and the prices are made up (`Chocolaterie Java (fictional)`).
- The new API is an incubator in JDK 28: great for tools, tests and demos, not for production yet.

## One-line summary

> "The partner changed their script, the batch stayed green, and we gave away the pralines. Strict by default means you find out the same night, not at the end of the month."
