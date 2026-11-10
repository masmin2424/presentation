# Scene 2 · Order to invoice

**Remember:** read and write with plain Java collections; money stays exact.

| | |
|---|---|
| Open | `jdk28/Scene02_OrderToInvoice.java` |
| Compare with | `java25/Jackson02_OrderToInvoice.java` |
| Run | `2. Order to invoice`, then `2. Order to invoice (Jackson)` |
| Slides | 11 (reading) and 12 (writing) |
| Time | 4 min |

## 1. Set the scene

> "An order comes in as JSON, an invoice goes out as JSON. No library, and no classes to map to."

This is the only scene that does the full round trip. Explain it in two halves.

## 2. First half: reading (slide 11)

```java
JsonValue order = Json.parse(Orders.read("valid-order.json"));

for (JsonValue item : order.get("items").asList()) {
    int quantity = item.get("quantity").asInt();
    BigDecimal unitPrice = new BigDecimal(item.get("unitPrice").toString());
    ...
}
```

1. **`asList()`** turns the JSON array into a normal, read-only `java.util.List<JsonValue>`. "Loop it, stream it, everything you know."
2. **`asInt()`** is strict: it only works for a JSON number that is exactly an int. "Keep that in mind, we'll break it in scene 4."
3. **The money line, the most important point of the scene:**
   - "Why not `asDouble()`? A double can round. Fine for temperatures (the JEP's own example), not for invoices."
   - "`toString()` on a JSON number gives back the number exactly as written in the file: `"24.90"`. So `new BigDecimal(value.toString())` loses nothing."
   - "There is no `asBigDecimal()` on purpose. The JEP itself tells you to write this one line."

## 3. Second half: writing (slide 12)

```java
var line = new LinkedHashMap<String, JsonValue>();   // keeps our member order
line.put("product", item.get("product"));
line.put("quantity", JsonNumber.of(quantity));
line.put("lineTotal", JsonNumber.of(lineTotal.toPlainString()));
lines.add(JsonObject.of(line));
...
IO.println(json);                               // compact
IO.println(Json.toDisplayString(json, "  "));   // pretty
```

1. **Reuse what you read.** `item.get("product")` is already a `JsonValue`: it goes in as it is, no conversion.
2. **Create new values with `.of(...)`:** `JsonNumber.of(quantity)` for the int, `JsonNumber.of(lineTotal.toPlainString())` for the money. `toPlainString()` because `toString()` can switch to scientific notation (`2.9E+2`).
3. **Build with plain Java collections:** a `Map` goes into `JsonObject.of(...)`, a `List` into `JsonArray.of(...)`.
   - "Why `LinkedHashMap`? `Map.of` has no order: the invoice fields could come out shuffled."
4. **Two ways to print:**
   - `toString()`: compact, one line, no spaces. For machines.
   - `Json.toDisplayString(json, "  ")`: pretty-printed with the indent you choose. For humans and logs.
   - Both can be parsed back with `Json.parse`.
   - Warn: `asString()` is **not** a way to print JSON. It only works on a JSON string value.

## 4. Run it

Expected (shortened):

```
{"invoiceFor":"CHOC-2027-0042","customer":"Café Praline","lines":[...],"total":294.60,"currency":"EUR"}
{
  "invoiceFor": "CHOC-2027-0042",
  ...
  "total": 294.60,
  "currency": "EUR"
}
```

Point at `294.60`: "The trailing zero is still there, because we never went through `double`."

## 5. Compare with Java 25 + Jackson

Run `Jackson02_OrderToInvoice`. Say it plainly:

> "To be fair: Jackson's builder (`put`, `putArray`, `addObject`) is nicer for writing JSON."

- Jackson reads `24.90` as a `double`, so its total prints as `294.6`.
- The JDK API is deliberately minimal for writing. The JEP says generating and transforming JSON is an area where they want feedback during incubation.

## 6. Transition

> "That was the happy path. Now let's see what happens when the partner sends us something wrong."

## Questions you may get

- **"Can I parse straight into an `Order` record?"** No data binding, on purpose. You'll cover it in "what it refuses to do".
- **"Why not `Map.of`?"** Member order (point 3).
- **"What if a value is null?"** `JsonObject.of` rejects Java `null` values; use `JsonNull.of()` for a JSON null.
- **"Can I give it a file or a stream?"** No: `Json.parse` takes a `String` or a `char[]`. The whole document must fit in memory.

## 30-second version

> "`parse`, navigate with `get`, convert with `asList` and `asInt`. Money: `new BigDecimal(value.toString())`. To write, you build normal Java maps and lists, wrap them with `.of()`, and print with `toString()` or `toDisplayString()`."
