# FAQ: JEP 540 (Simple JSON API), questions to expect

Honest, short answers for sceptical or senior developers. Several of these were asked live at the Devoxx talk.

## "Why should we care?"

**We already have Jackson. Why would we need this?**
For our applications, we don't: Jackson stays. This is for everything around them: scripts, tests, diagnostic tools and prototypes, where adding a library and a Maven project is overkill. The JEP itself says replacing existing JSON libraries is explicitly not a goal.

**We can't use it in production for a year or more. Why talk about it now?**
Because now is when it can still change. In incubator, real feedback shapes the final API. And we can already use it at no risk in tools that never go to production, like a batch-file checker or a small import script.

**Will Jackson or Spring stop working, or need migrating?**
No. Nothing changes for existing code. It's a new, separate API, and the frameworks keep using their own libraries.

## "Is it any good?"

**Is it faster than Jackson?**
I don't have benchmarks, and the talk didn't claim it is. It's designed to be simple, not to be the fastest. For big volumes or performance-critical paths, Jackson stays the choice.

**Can it map JSON to my classes or records, like Jackson's `ObjectMapper.readValue`?**
No. Object mapping is deliberately out of scope. In the talk's Q&A, the OpenJDK team said mapping to and from records might come later through other language work, but it isn't in this JEP.

**Can it stream a huge file?**
No. It reads the whole document into memory. Streaming is out of scope; use Jackson for that.

**Building nested JSON looks verbose: a `Map` per level?**
True. The team said so themselves: the parsing side is more mature than the building side. That's exactly the kind of feedback they want while it's an incubator.

**What about very large numbers, like `BigDecimal`?**
The JEP has a section on JSON numbers explaining how to get exact values like `BigDecimal`. It was asked at the talk, and they pointed to the JEP. Check the JEP for the details before answering precisely.

## "Is it safe?"

**Why so strict? No comments, no trailing commas?**
On purpose. It follows the official JSON standard, RFC 8259, exactly. If different parsers accept different "almost JSON", the same file can mean different things depending on which parser reads it. That's a source of bugs and even security problems. Strict means predictable.

**What happens with duplicate keys?**
They're rejected. Other parsers might keep the first one, the last one, or something random. Here there's no ambiguity.

**What happens if a value has the wrong type?**
It throws a clear error naming the path. Show it with the chocolate demo: `"quantity": "12 pcs"` makes Jackson's `asInt()` silently return `0`.

**Are the JSON objects mutable?**
No, they're immutable. Even if you build one from a mutable `Map`, it takes its own copy. That was asked at the talk too.

**Does it keep the order of keys?**
Don't rely on it. In JSON, the order of keys in an object has no meaning. The team said at the talk that they don't plan to guarantee an order.

## "What's the risk?"

**Incubator: could it just disappear?**
Yes, in theory. Incubator means it can change or even be dropped. That's why we only use it outside production for now. The package name will also change when it becomes final.

**When can we use it in production?**
When it's final. Nobody knows when. It could take one round or several; the Vector API is in its 12th incubator round. The next long-term-support version is JDK 29 (September 2027), but there's no guarantee it'll be final by then.

**Java already tried this in 2014. Why would it work now?**
That was JEP 198, and it tried to do everything: streaming, building, transformations. This one is deliberately small, and that's why it has a better chance.

## Tips for answering

- **If you don't know, say so:** "Good question, I'll check the JEP." The JEP is detailed and clearly written: https://openjdk.org/jeps/540
- **Don't oversell it.** The strongest position is honesty: "Not for production yet, but free to try and useful for tools."
- **Point back to the demos:** the "free pralines" moment (`0 x Manon cafe`) answers a lot of "why should I care" questions.
