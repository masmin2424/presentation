# Scene 4 · Bad quantity

**Remember:** fail fast vs. silent coercion. This is the scene people remember.

| | |
|---|---|
| Open | `orders/bad-quantity.json`, `jdk28/Scene04_BadQuantity.java` |
| Compare with | `java25/Jackson04_BadQuantity.java` |
| Run | `4. Bad quantity`, then `4. Bad quantity (Jackson)` |
| Slide | 14 |
| Time | 3 min |

## 1. Set the scene

Open `bad-quantity.json`:

```json
{ "sku": "PRAL-36", "quantity": "2" },
{ "sku": "TRUF-12", "quantity": 2.5 },
{ "sku": "BAR-70",  "quantity": 3000000000 },
{ "sku": "COIN-01", "quantity": null },
{ "sku": "BOX-XL" }
```

> "Five ways a quantity goes wrong in real life: a string, a decimal, a number too big for an int, a null, and a field that's just not there."

## 2. Walk through the code

```java
for (JsonValue item : order.get("items").asList()) {
    String sku = item.get("sku").asString();
    try {
        IO.println("✓ " + sku + " → " + item.get("quantity").asInt());
    } catch (JsonValueException e) {
        IO.println("✗ " + sku + " → " + e.getMessage());
    }
}
```

> "Same `asInt()` as in scene 2. The try/catch is only there so we see all five results in one run."

## 3. Run the JDK 28 version

Expected: **five ✗ lines**, one `JsonValueException` per item. Read one or two real messages from the console and point at the path in them (it tells you which item).

What happens for each value, and why:

| Value | Result | Why |
|---|---|---|
| `"2"` | ✗ | It's a JSON string. `asInt()` never parses strings. |
| `2.5` | ✗ | Not a whole number. |
| `3000000000` | ✗ | Doesn't fit in an `int`. |
| `null` | ✗ | `null` is not a number. |
| missing | ✗ | `get("quantity")` fails: the member isn't there. |

## 4. Run the Jackson version

Expected (real output):

```
✓ PRAL-36 → 2
✓ TRUF-12 → 2
✓ BAR-70 → -1294967296
✓ COIN-01 → 0
✓ BOX-XL → 0
```

Go through it slowly. Pause on the third line.

- `"2"` becomes 2: it parsed the string for you.
- `2.5` becomes 2: truncated.
- `3000000000` becomes **-1294967296**: it overflowed. "Negative pralines."
- `null` and missing become 0.
- "And every line has a green tick."

Be fair:

> "Jackson isn't wrong. It's lenient by design, and that's often convenient. `path()` is the usual 'safe' Jackson idiom: with `get()` you'd get a `NullPointerException` for the missing one."

## 5. Line to land

> "Strict by design: `asInt()` gives you an exact int or an exception that tells you where. Never a guess."

## 6. Transition

> "But sometimes a value is allowed to be missing, or null. That's not an error, it's optional data."

## Questions you may get

- **"Is `2.0` accepted?"** Yes. `asInt()` accepts any JSON number with an integral value: `2.0` gives 2. Only a real fractional part (`2.5`) fails.
- **"What if my partner really sends `"2"` as a string?"** Handle it on purpose and visibly: `switch` on the type (scene 6) and call `Integer.parseInt(value.asString())` yourself.
- **"Does `asLong()` work for 3000000000?"** Yes, it fits in a `long`.
- **"And `asDouble()`?"** It rounds to the nearest double, and throws only if the number is out of `double` range.
