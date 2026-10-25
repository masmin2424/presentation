# 🍫 The chocolate demo: what JEP 540 changes, and why developers should care

## The story

A Belgian chocolate shop sells through a partner webshop. Every night, the partner's **export script** drops a JSON order file. Our **nightly batch** reads it and produces the invoice.

There is no web form, no person in the loop. Machine to machine: their script writes, our batch reads.

One day the partner **changes their export script**. Nobody tells us. The file is still valid-looking JSON, and our batch runs at 2 a.m. as usual.

What does the batch do? It depends on **how it reads JSON**.

## 4 partner changes, 4 JEP 540 rules

Scenario 1 is the normal file; scenarios 2 to 5 are each one realistic change in the partner's script. The base order is 4 kinds of pralines + gift wrap, **TOTAL 67,70 EUR**.

| # | Scenario file | What the partner's script now writes | BEFORE: Java 21 + Jackson (real output) | AFTER: JDK 28, JEP 540 | JEP 540 rule |
|---|---|---|---|---|---|
| 1 | `1-normal.json` | Nothing changed | Order #1042, 67,70 EUR | Order #1042, 67,70 EUR | Same result when the data is right |
| 2 | `2-units-in-quantity.json` | `"quantity": "12 pcs"` | `0 x Hazelnut praline`, **48,50 EUR**. 12 pralines for free | **ORDER REJECTED** | **No silent conversion.** `asInt()` returns a number or fails |
| 3 | `3-duplicate-key.json` | `"quantity"` written twice: 12, then 1 | Last one wins: `1 x Hazelnut praline`, **50,10 EUR** | **FILE REJECTED** | **Strict JSON (RFC 8259).** Duplicate keys are an error, not a guess |
| 4 | `4-giftwrap-in-french.json` | `"giftWrap": "oui"` | Read as `false`: gift wrap **silently dropped**, 65,20 EUR | **ORDER REJECTED** | **No silent conversion.** `asBoolean()` accepts only `true`/`false` |
| 5 | `5-new-order-id-format.json` | `"orderId": "A-1042"` instead of `1042` | **Invoice for order #0** | Invoice for order **A-1042** | **Sealed types + pattern matching.** The code handles number *and* text ids on purpose |

The BEFORE column is real output (Jackson 2.18.2, default settings, Java 21). **None of these cases raised an error: the batch finishes green every time.**
The AFTER column is JEP 540's designed behaviour. Run it once on JDK 28 to see the exact error messages.

## Why this matters for developers

### 1. Wrong data is loud, not silent
With Jackson's tree API, `asInt()`, `asBoolean()` and `asLong()` **guess**: anything they can't read becomes `0` or `false`. The program keeps running with wrong numbers.
With JEP 540, `asInt()` on text **throws**. A loud error at 2 a.m. is fixed the next morning. A silent one shows up weeks later in the accounts.

> Developer benefit: you don't have to remember to add checks like `isInt()` everywhere. The safe behaviour is the default.

### 2. The same file means the same thing everywhere
JSON with a duplicate key is ambiguous: is the quantity 12 or 1? Different parsers pick different answers (first, last, error). JEP 540 follows the JSON standard (RFC 8259) strictly and **refuses** the file.

> Developer benefit: no "works on my parser" bugs between systems. And no hidden security trick where two systems read the same file differently.

### 3. Missing, `null` and "wrong" are three different things
For `giftWrap`, the new API makes each case explicit in one readable line:

```java
boolean giftWrap = order.tryGet("giftWrap")      // missing   -> empty Optional
        .flatMap(JsonValue::tryValue)             // null      -> empty Optional
        .map(JsonValue::asBoolean)                // "oui"     -> error
        .orElse(false);                           // default: no gift wrap
```

> Developer benefit: optional fields are handled with `Optional`, the tool Java developers already know, instead of `has()` / `isNull()` / `null` checks scattered around.

### 4. Format changes are handled on purpose
The partner switched order ids from `1042` to `"A-1042"`. Jackson's `asLong()` quietly produces order **#0**. With JEP 540, a JSON value is one of six **sealed** types, so the code can say exactly what it accepts:

```java
static String orderId(JsonValue id) {
    return switch (id) {
        case JsonNumber n -> "#" + n.asLong();      // old format
        case JsonString s -> s.asString();          // new format
        default -> throw new IllegalArgumentException("orderId must be a number or a text");
    };
}
```

> Developer benefit: modern Java (records, sealed types, pattern matching) works naturally with JSON. Legacy and new formats live side by side, and anything else is rejected.

### 5. Zero dependency
The BEFORE version needs Jackson (3 jar files, or Maven). The AFTER version is one `.java` file and the JDK.

> Developer benefit: batch tools, import scripts and test helpers without a build setup, and less third-party code to patch.

## "But we have checks everywhere. Can this happen to us?"

An honest answer:

- **In our main applications, mostly no.** Entity classes, validation (`@Min`, `@NotNull`) and database types catch most of this.
- **Where it does happen:** code that reads JSON **field by field**, with no entity class: batch jobs, import scripts, glue code for a partner's API, quick tools, tests. That code often has none of those layers, and it's exactly what JEP 540 is designed for.
- **Even with entities, Jackson's defaults are lenient.** Tested with Jackson 2.18.2 on a class with `int quantity`: a missing, `null` or empty quantity becomes `0`; `12.7` becomes `12`. Caught only if someone added validation.
- **Jackson isn't "broken".** These are documented, configurable defaults. A careful developer can make Jackson strict. The point: with Jackson the silent path is the easy one; **with JEP 540 the safe path is the easy one.**

## How to run it (two terminals side by side)

**Left: BEFORE** (Java 21 + Jackson jars in `lib/`)

```
java -cp "lib/*" ChocolateDemoBefore.java scenarios/2-units-in-quantity.json
```

**Right: AFTER** (JDK 28; first run `$env:Path = "C:\jdk-28\bin;$env:Path"` once)

```
java --add-modules jdk.incubator.json ChocolateDemo.java scenarios/2-units-in-quantity.json
```

Change the file name to run each scenario: `1-normal`, `2-units-in-quantity`, `3-duplicate-key`, `4-giftwrap-in-french`, `5-new-order-id-format`. Press ↑ in the terminal to edit the previous command.

## Presentation script (about 5 minutes)

1. **Tell the story.** "Every night a partner sends us an order file. Our batch turns it into an invoice."
2. **Scenario 1.** Run both: same invoice, 67,70 EUR. "Good data: both work."
3. **Scenario 2, "12 pcs".** Left: 48,50 EUR. "12 pralines for free, batch green." Right: rejected. **Rule: no silent conversion.**
4. **Scenario 3, duplicate key.** Left: 50,10 EUR. "Which quantity is right? Jackson picked one." Right: file rejected. **Rule: strict JSON.**
5. **Scenario 4, "oui".** Left: gift wrap gone. "The customer asked for it." Right: rejected. **Rule: booleans are booleans.**
6. **Scenario 5, "A-1042".** Left: "invoice for order #0". Right: "A-1042". Show the `switch` in the code. **Rule: sealed types let you handle the change on purpose.**
7. **Close.** "Four changes in a file, four silent errors with the old default, zero with the new one. Strict by default means you find out tonight, not at the end of the month."

Short on time? Do scenarios 1, 2 and 5 only.

## Honest notes

- The shop, the partner and the prices are fictional.
- JEP 540 is an incubator API in JDK 28: great for batch tools, scripts and tests, **not for production yet**. Method names may change before it's final.
- The AFTER outputs are the designed behaviour of JEP 540; the exact error texts may differ. Run each scenario once on JDK 28 before presenting.

## One-line summary

> "With the old default, four changes in a partner's file gave four wrong invoices and zero errors. With JEP 540, the wrong ones are refused and the new format is handled on purpose."
