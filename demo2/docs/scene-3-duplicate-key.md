# Scene 3 · Duplicate key

**Remember:** the JDK refuses to guess.

| | |
|---|---|
| Open | `orders/duplicate-key.json`, `jdk28/Scene03_DuplicateKey.java` |
| Compare with | `java25/Jackson03_DuplicateKey.java` |
| Run | `3. Duplicate key`, then `3. Duplicate key (Jackson)` |
| Slide | 13 |
| Time | 3 min |

## 1. Set the scene

Open `duplicate-key.json` and point at the item:

```json
{
  "sku": "PRAL-36",
  "product": "Praline assortment, 36 pcs",
  "quantity": 2,
  "unitPrice": 24.90,
  "quantity": 2000
}
```

Ask the room:

> "Two boxes or two thousand? Hands up for 2. Hands up for 2000."

(Nobody can answer, and that is the point.) IntelliJ already underlines the second `"quantity"`.

## 2. Explain why this is dangerous

- RFC 8259, the JSON standard, only says names **SHOULD** be unique. It doesn't forbid duplicates.
- So libraries disagree: some keep the first value, some the last, some fail.
- In a real system the same bytes go through several programs: one validates the order with `2`, another invoices `2000`. Different readers see different orders. The JEP calls this out as a source of hard-to-find bugs and security problems.

## 3. Run the JDK 28 version

```java
try {
    Json.parse(Orders.read("duplicate-key.json"));
} catch (JsonParseException e) {
    IO.println("✗ Order rejected");
    IO.println(e.getMessage());
}
```

Expected: `✗ Order rejected`, then a `JsonParseException` message. **Read the real message from the console**: it names the duplicate member `"quantity"`, the path and the line/position.

> "JEP 540 turns SHOULD into MUST. Duplicate names are always an error. There is no option to switch it off. The order is rejected before anyone is invoiced."

## 4. Run the Jackson version

Expected (real output):

```
Invoiced: 2000 boxes = EUR 49800.0
```

> "No error, no warning. Jackson keeps the last value. EUR 49,800 of pralines."

Then be fair:

> "You can make Jackson strict: `DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY`. But you have to know that flag exists. It's commented out in the file."

## 5. Line to land

> "Strict by design: when the JSON is ambiguous, the JDK stops instead of guessing."

## 6. Transition

> "Duplicates are rare. Wrong values are not. Let's look at quantities."

## Questions you may get

- **"Can I allow duplicates?"** No. One parser, one behaviour, no configuration.
- **"Does it detect duplicates in nested objects?"** Yes, in any object of the document. Here it's inside `items[0]`.
- **"What about when I build JSON myself?"** `JsonObject.of(map)` also refuses duplicate names (`IllegalArgumentException`), for example with an `IdentityHashMap`.
- **"Can I get the line number as a number?"** Yes: `getErrorLine()` and `getErrorPosition()` on `JsonParseException`. Both are zero-based.
