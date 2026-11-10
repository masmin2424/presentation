# Scene 7 · Strict parser

**Remember:** RFC 8259 only, no options.

| | |
|---|---|
| Open | `orders/trailing-comma.json`, `orders/with-comments.json`, `jdk28/Scene07_StrictParser.java` |
| Run | `7. Strict parser` |
| Slide | 17 |
| Time | 2 min |

## 1. Set the scene

Show the two files. IntelliJ already marks both in red.

- `trailing-comma.json`: a comma after the last item, `{ "sku": "BAR-70", "quantity": 3 },`
- `with-comments.json`: `// typed by hand from the old spreadsheet`

> "Files that humans edit by hand. Comments, a comma too many. Very common in config files."

## 2. Run it

Expected:

```
✗ trailing-comma.json → JsonParseException ...
✗ with-comments.json → JsonParseException ...
✓ after cleanup → CHOC-2027-0046
```

Read one real message from the console: it gives the line and position.

## 3. Explain the rule

> "The parser accepts standard JSON, RFC 8259, and nothing else. No comments, no trailing commas, no JSON5. And no option to allow them."

Why, in the JEP's terms:

- The API is built for **machine-to-machine** JSON.
- Every extension makes the test matrix bigger and adds interoperability risk.
- One parser, one behaviour: the same as for duplicate keys in scene 3.

## 4. The workaround

```java
String cleaned = Orders.read("with-comments.json").replaceAll("(?m)^\\s*//.*$", "");
```

> "If you have hand-written files, clean the text yourself before parsing. The JEP suggests exactly this trick, with a regex that removes comment lines."

## 5. Be fair about Jackson

> "No difference with Jackson here: Jackson's default settings also reject comments and trailing commas. The difference is that Jackson has switches to allow them, and the JDK doesn't."

## 6. Line to land (end of the demo)

> "Small by design, strict by design. That's the whole API."

## 7. Transition (back to the slides)

> "So that's what it does. Now, what it refuses to do, and the catch."

Go to slide 18 ("Small on purpose").

## Questions you may get

- **"Why not support comments for config files?"** The JEP chooses simplicity over convenience: pre-process the file yourself, or use a library that supports JSON5.
- **"Does it accept a BOM or leading whitespace?"** Whitespace around the value is standard JSON and is fine. Anything outside RFC 8259 is rejected; test your own files on the EA build.
- **"Is the regex safe?"** It only removes lines that *start* with `//`. A `//` inside a string value (for example a URL) is left alone, because the line doesn't start with it.
