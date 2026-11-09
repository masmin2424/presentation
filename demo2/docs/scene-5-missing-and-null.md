# Scene 5 · Missing and null

**Remember:** absent is not the same as null.

| | |
|---|---|
| Open | `orders/valid-order.json`, `jdk28/Scene05_MissingAndNull.java` |
| Run | `5. Missing and null` |
| Slide | 14 |
| Time | 2 min |

## 1. Set the scene

In `valid-order.json`, point at two things:

- `"giftWrap": true` exists **only on the first item**. On the other two, the member is absent.
- `"discountCode": null` is **present**, but its value is null.

> "JSON documents evolve. Fields appear, fields are optional, fields are null. These are two different situations and the API has a tool for each."

## 2. The three tools (slide 14)

| Method | Use it when | You get |
|---|---|---|
| `tryGet("giftWrap")` | the member may be **absent** | `Optional`, empty if the member isn't there |
| `tryValue()` | the value may be **null** | `Optional`, empty if the value is JSON `null` |
| `get("vatNumber")` | the member **must** be there | the value, or a `JsonValueException` |

## 3. Walk through the code

```java
boolean giftWrap = item.tryGet("giftWrap").map(JsonValue::asBoolean).orElse(false);

String discount = order.get("discountCode").tryValue()
                       .map(JsonValue::asString)
                       .orElse("none");
```

- "`tryGet` gives an `Optional`: plain Java. `map`, `orElse`, you already know it."
- "`tryValue` is called on the **value**, not on the object: `get("discountCode")` works because the member exists; its value is null."
- "There's no `asNull()`. For null you use `tryValue()`, or `instanceof JsonNull`."

## 4. Run it

Expected:

```
PRAL-36 gift wrap: true
TRUF-12 gift wrap: false
BAR-70 gift wrap: false
Discount code: none
✗ ...
```

The last line is the `get("vatNumber")` call: a `JsonValueException` saying the customer object has no `vatNumber` member. Read the real message.

> "When a field must be there, keep using `get`: you get a clear error right away, not a `NullPointerException` three methods later."

## 5. Line to land

> "Optional data is handled with `Optional`. Required data fails fast. You choose, per field, in the code."

## 6. Transition

> "Absent and null are easy. Harder: the same field changes **type**."

## Questions you may get

- **"What if a field can be absent OR null?"** Chain them: `item.tryGet("x").flatMap(JsonValue::tryValue)` is empty in both cases.
- **"What does `tryGet` do on an array?"** It throws `JsonValueException`: `tryGet` is for objects only.
- **"Why not just return `null` like Jackson's `get`?"** Because `null` doesn't tell you what went wrong. An `Optional` makes "maybe missing" visible in the type.
