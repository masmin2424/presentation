# Scene 1 · Read one field

**Remember:** no library, no cast.

| | |
|---|---|
| Open | `orders/valid-order.json`, `jdk28/Scene01_ReadOneField.java` |
| Compare with | `java25/Jackson01_ReadOneField.java` |
| Run | `1. Read one field`, then `1. Read one field (Jackson)` |
| Slides | 9, then 12 for the comparison |
| Time | 2 min |

## 1. Set the scene (before any code)

Show `valid-order.json` for five seconds.

> "This is a chocolate order from our web shop. A customer, an address, three items. Our first job is tiny: where do we ship it? We only need the city."

## 2. Walk through the code

```java
String city = Json.parse(Orders.read("valid-order.json"))
                  .get("customer").get("address").get("city")
                  .asString();
```

Point at each part, in this order:

1. **`import jdk.incubator.json.Json;`**: "This import comes from the JDK. Nothing in the pom."
2. **`Json.parse(...)`** returns a `JsonValue`: the root of the tree.
3. **`.get("customer").get("address").get("city")`**: "Notice what is missing: no cast. `get` is declared on `JsonValue` itself, so I never have to turn the value into a `JsonObject` first. That is a deliberate design decision in the JEP."
4. **`.asString()`**: "I convert only at the end, once I have reached the value."
5. **`void main()` and `IO.println`**: "This is just Java 25 style, nothing to do with JSON. No `public static void main(String[] args)`, no `System.out`."

## 3. Run it

Expected output:

```
Ship to: Bruges
```

## 4. Compare with Java 25 + Jackson

Split the editor: `Jackson01_ReadOneField` on the left, `Scene01_ReadOneField` on the right. Run the Jackson one.

Be honest, it builds trust:

> "Same length. Jackson is not verbose here. The difference is not the code, it's what comes with it."

- **A dependency**: `jackson-databind` brings `jackson-core` and `jackson-annotations`, versions to align, security advisories to follow.
- **A checked exception**: `readTree` throws `JsonProcessingException`, hence `throws Exception` on `main`.
- **The real difference shows up when the JSON is bad.** "Hold that thought: scenes 3 and 4."

## 5. Optional live trick (30 seconds)

Change `"city"` to `"town"` in the Java code and run again.

> "A typo. Instead of a `NullPointerException` three lines later, I get a `JsonValueException` that says which member is missing and the path it was following."

Undo the change.

## 6. Transition

> "Reading one field is the easy part. Let's do real work: turn this order into an invoice."

## Questions you may get

- **"Does it work on Java 25?"** No. The module only exists from JDK 28 (early access today, GA planned 9 March 2027).
- **"What does `get` do on an array?"** `get(int)` reads an array element. `get(String)` on an array, or `get(int)` on an object, throws `JsonValueException`.
- **"Is `asString()` like `toString()`?"** No. `asString()` only works on a JSON string and returns its text. `toString()` prints any value as JSON.
