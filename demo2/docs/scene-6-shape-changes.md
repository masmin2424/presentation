# Scene 6 · Shape changes

**Remember:** sealed types, so the compiler checks you covered every case.

| | |
|---|---|
| Open | `orders/valid-order.json`, `orders/legacy-order.json`, `jdk28/Scene06_ShapeChanges.java` |
| Run | `6. Shape changes (sealed switch)` |
| Slide | 16 |
| Time | 3 min |

## 1. Set the scene

Show the two `orderId` values:

- `valid-order.json` (new web shop): `"orderId": "CHOC-2027-0042"`, a **string**
- `legacy-order.json` (old till system): `"orderId": 42`, a **number**

> "Same field, two types, depending on which system wrote the file. This really happens, even inside the JDK: in thread dumps, the thread id is a string up to JDK 26 and a number from JDK 27. That's the JEP's own example."

## 2. First switch: handle both types on purpose

```java
String id = switch (orderId) {
    case JsonString s -> s.asString();
    case JsonNumber n -> "LEGACY-" + n.asLong();
    default -> throw new JsonValueException("Unexpected orderId: " + orderId);
};
```

- "A pattern-matching `switch` on the value: if it's a string, take it; if it's a number, build a legacy id."
- "This one needs a `default`, because I only handle 2 of the 6 JSON types."

## 3. Second switch: all six types, no default

Explain the key idea first:

> "`JsonValue` is **sealed**. It can only ever be one of six types: string, number, boolean, null, object, array. The compiler knows that list."

```java
return switch (value) {
    case JsonObject o  -> "object " + o.asMap().keySet();
    case JsonArray a   -> "array of " + a.asList().size();
    case JsonString s  -> "string \"" + s.asString() + "\"";
    case JsonNumber n  -> "number " + n;
    case JsonBoolean b -> "boolean " + b.asBoolean();
    case JsonNull _    -> "null";
};
```

> "Six cases, and **no default**. The compiler checks that every case is covered."

## 4. Run it

Expected:

```
valid-order.json → CHOC-2027-0042
legacy-order.json → LEGACY-42

orderId : string "CHOC-2027-0042"
express : boolean true
loyaltyPoints : number 120
customer : object [name, address]
currency : string "EUR"
discountCode : null
items : array of 3
```

> "The order file contains every JSON type on purpose."

## 5. Live trick (the best moment of this scene)

Delete the `case JsonNull _ -> "null";` line. IntelliJ goes red: the switch no longer covers all possible values. Undo.

> "That's the compiler protecting you. If a new case is ever missed, you know at compile time, not in production."

## 6. Line to land (callback to the history slide)

> "This is why it took 12 years. In 2014 Java had no sealed types and no pattern matching. These language features are what make a **small** JSON API pleasant to use."

## 7. Transition

> "Last scene: what happens with JSON that humans typed by hand."

## Questions you may get

- **"What's `_`?"** An unnamed variable (Java 22): I match `JsonNull` but don't need the variable.
- **"Why does `number 120` print exactly `120`?"** `toString()` on a parsed number keeps its original text.
- **"Could a library add a seventh type?"** No. That's what sealed means: the six types are fixed by the JDK.
