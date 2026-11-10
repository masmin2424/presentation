# Speaker script · JEP 540, the Chocolate Factory

The full talk, slide by slide, for a room with **both developers and business colleagues**.
The same text is in the speaker notes of `JEP540-Chocolate-Factory.pptx`.

How to read it:

- Plain text is what you say. Say it in your own words; don't read it.
- *[Brackets]* are stage directions: what to do, where to point, when to pause.
- **Business translation** lines turn a technical point into what it means for the company. Look at the business people when you say them.
- For the details of each live scene (files to open, expected output, questions), see the scene pages in this folder.

Delivery tips:

- **Talk to two audiences on purpose.** Developers get the code; business colleagues get the consequence. Turn your body towards the group you're addressing.
- **Pauses are your best tool.** After a punchline (2000 boxes, negative pralines), stop talking for two seconds. Let the room react.
- **Be fair to Jackson.** Every time you admit where Jackson is better, the audience trusts the rest of what you say.
- **Repeat the motto.** "Small by design, strict by design." Three or four times in the talk is right.
- Timing: about 35 minutes plus questions. If you're late, shorten slides 6 and 20; never cut scenes 3 and 4.

---

## Slide 1 · JSON, built in. Finally.

*[Walk to the middle of the stage. Don't touch the laptop yet. Smile. Wait two seconds.]*

Quick show of hands: who in this room has ever eaten Belgian chocolate? *[hands]* Perfect. Everyone is qualified for this talk.

Today I'm taking you into a chocolate factory. Not to make chocolate: to follow the paperwork. Orders come in, invoices go out. And in between, there's a program reading something called JSON.

If you're not a developer: JSON is simply the order form that computer systems send to each other. Every app on your phone, every web shop, every banking screen speaks JSON all day long.

And here's the news: after more than thirty years, Java, the language a big part of our software runs on, can finally read that order form on its own. No extra parts. That's JEP 540.

I'll show you why it took twelve years, what it does, what it refuses to do, and the catch. Live. On a version of Java that isn't officially released yet. *[beat]* What could possibly go wrong?

## Slide 2 · A whole library… to read one field

*[Turn to the developers.]* Developers, this one is for you. Who has ever added Jackson to a project just to read one field? *[hands]* Yes. Me too.

*[Turn to the business side.]* Let me translate. Jackson is a library: a ready-made component, written outside our company, that we plug into our software. Like buying ready-made praline fillings from a supplier. It saves a lot of time.

But now you depend on that supplier. You follow their updates. And when they have a security recall, you have to react, fast.

**Business translation:** every outside component is a supplier to manage: updates to plan, security alerts to answer, sometimes audit questions to explain.

*[Point left.]* This is the price today, in Java 25: three components, versions to keep in line, security advisories to follow.

*[Point right.]* This is JDK 28. The import comes from Java itself. Zero dependencies.

*[Lower your voice.]* Well… almost zero. See that little star? Remember it. That's the catch, and I'll come back to it at the end.

## Slide 3 · Four questions, one live demo

Four questions today. Why did it take twelve years? What does it do? What does it refuse to do, on purpose? And what's the catch?

In the middle: a live demo. Seven short scenes. Chocolate orders go in, invoices come out, and some of the orders are… let's say, creative.

If you remember one sentence from today, make it this one: **small by design, strict by design.** You'll hear it again.

## Slide 4 · Nine words you'll hear today

Before we go further: nine words in plain language. Developers, give me sixty seconds. Business colleagues, this slide is your survival kit.

*[One sentence per card. Keep the rhythm fast.]*

- **JSON:** the order form systems send each other.
- **JDK:** the Java toolbox. Every Java application runs on it.
- **JEP:** a JDK Enhancement Proposal. A public, numbered change request for Java itself. Ours is number 540.
- **Library, or dependency:** a ready-made part from an outside supplier. Jackson is the famous one for JSON.
- **Parsing:** reading the form, so the program can work with it.
- **Data binding:** a machine that pours the form straight into your own mould, your own "Order" object. Keep this one in mind: JEP 540 doesn't have it, on purpose.
- **CLI tool, or script:** a small program you start from a terminal. The nightly batch that moves files, a migration tool, a test helper.
- **Incubator:** the test kitchen. It's shipped, you can taste it, but the recipe can still change. Not for production.
- **LTS:** long-term support. The version companies standardise on for years. For us, that's Java 25.

## Slide 5 · Second attempt, smaller on purpose

So, why twelve years? Let's go back to June 2014. *[Point at the first card.]* JEP 198, "Light-Weight JSON API". Status: Closed, Withdrawn. *[pause]*

It wasn't light-weight at all. The plan: token streams, event streams, trees, builders, generators, transformers. That's not a feature. That's a whole JSON library inside Java. And it never shipped.

Why? Officially, one line: "circumstances have changed". My own reading, and I stress it's mine: in 2014, the Java language wasn't ready for a small, elegant JSON API. The features that make it pleasant (records, sealed types, pattern matching) arrived between Java 16 and Java 25. Java had to grow first.

The demand never went away. In May 2025, Paul Sandoz from Oracle restarted the discussion. In July 2026, JEP 540 became a candidate. Today it's integrated in JDK 28, planned for March 2027.

**Business translation:** the second attempt worked because they cut the scope. A smaller project that ships beats a big one that doesn't. *[smile]* Sounds familiar?

## Slide 6 · The whole API fits on one slide

Here's the whole API. All of it. On one slide. *[pause]* Try that with Jackson's documentation.

On the left: every JSON value is one of six types. Text, number, true or false, null, object, list. That's JSON itself, nothing more. And the word *sealed* means Java knows there will never be a seventh. Remember it: it pays off in scene six.

On the right: the verbs. *Parse*: read the form. *Navigate*: get this field, get that element. *Convert*: give me a Java text, a number, a list. *Generate*: build JSON back, and *print* it. When something is wrong: two kinds of errors, and both are unchecked, which keeps the code short.

**Business translation:** this is what "small by design" looks like. Small means quick to learn, easy to review, and cheap to maintain for decades.

## Slide 7 · Chocolate orders in, invoices out

Meet our customer: Café Praline, in Bruges. Four boxes of pralines, ten boxes of truffles, twenty-five dark bars.

This file contains every kind of JSON value on purpose: text, numbers, a true, a null, nested objects, a list. And one optional field: gift wrap, only on the first item.

*[Point at the list on the right.]* And these are the bad days. Every developer here has seen them. A field written twice. Quantities that aren't quite numbers. An old system that sends the order number as a number instead of text. And files typed by hand, with comments and an extra comma.

IntelliJ already underlines three of them in red. Java is going to be… less polite.

## Slide 8 · The Chocolate Factory (live demo)

Enough slides. Let's open the factory. *[Switch to IntelliJ.]*

Seven scenes. Each one is a tiny program that I start from this menu. On the side, the same thing in today's Java 25 with Jackson, so we can compare honestly.

For the non-developers: you don't need to read the code. Watch the output at the bottom. A green tick means "accepted", a red cross means "refused". *[beat]* Keep an eye on which one is actually good news.

## Slide 9 · Scene 1 · Read one field, no cast

Scene one. The simplest job in the factory: where do we ship this order?

*[Show the code.]* `Json.parse`. Then get customer, get address, get city. And `asString` at the end. That's it.

Notice what's *not* there: no library, no casting, no ceremony. You walk through the order the way you walk through folders on your computer.

*[Run.]* "Ship to: Bruges." *[pause]* Applause is optional.

Now let me be honest, because honesty is a feature. *[Split the editor with the Jackson version.]* Jackson does this in the same number of lines. The difference isn't the code. It's the supplier you no longer need, and what happens when the order is wrong. That's coming.

*[Optional: change "city" to "town", run, show the clear error with the path, undo.]*

## Slide 10 · Scene 2 · Navigate, convert, and keep the money exact

Scene two. Real work: order in, invoice out.

We loop over the items: that's a normal Java list. For each item, the quantity as a whole number. And then, *[point at the highlighted line]* the most important line of the scene: money.

Developers: never put money in a `double`. A double is approximate. Great for temperatures, terrible for invoices.

**Business translation:** imagine a calculator that rounds every price by a tiny amount, a million times a day. Nobody notices… until the auditor does.

JEP 540 keeps the number exactly as it was written in the file: 24.90, with the zero. We turn it into a `BigDecimal`, Java's exact type for money. No rounding, ever.

## Slide 11 · Scene 2 · Build the invoice, print it two ways

Now we write the invoice. Plain Java maps and lists, nothing new to learn, wrapped with `of`.

Then two ways to print. Compact: one line, no spaces, for machines. Pretty: indented, for humans and log files.

*[Run.]* Total: two hundred ninety-four euros sixty. *[Point at the zero.]* That trailing zero is still there, because we never went through an approximate number.

And to be fair: Jackson's builder is nicer for writing JSON. If writing JSON is your main job, Jackson is still your friend. The JDK team itself says writing is where they want feedback.

## Slide 12 · Same field, same length, different cost

Side by side. Top: Java 25 with Jackson. Bottom: JDK 28. Same length, I told you. Now look at the table.

Dependency: three components against none. **Business translation:** one supplier less, fewer security alerts, fewer upgrade tickets.

Exceptions: Jackson forces developers to handle an error type that the JDK doesn't.

Duplicate keys and decimals: that's the next two scenes, and that's where the real money is.

The last two lines: data binding and streaming. Jackson has them. The JDK doesn't, on purpose. Hold that thought.

## Slide 13 · Scene 3 · Which quantity do we invoice?

Scene three. Look at this order. *[Point.]* Quantity: 2. Unit price. Quantity: 2000.

Hands up: who thinks the customer wants 2 boxes? *[hands]* Who thinks 2000? *[hands]* *[pause]* Nobody knows. That's exactly the problem.

The official JSON standard only says names *should* be unique. Should, not must. So different programs choose different answers. In a real company, one system checks the order with 2 boxes, and another one bills 2000.

*[Run the JDK version.]* JDK 28: refused. It names the field and tells you where it is. No guessing.

*[Run the Jackson version.]* Jackson: no error, no warning. Two thousand boxes. Forty-nine thousand eight hundred euros of pralines. *[pause]* Café Praline is going to call.

To be fair, Jackson can be strict, if you know the right switch. The JDK has no switch: it's always strict. **Strict by design.**

## Slide 14 · Scene 4 · Five bad quantities, two behaviours

Scene four. The one people remember. Five quantities, five ways real data goes wrong: the text "2", a two and a half, a number that's far too big, a null, and one that's simply missing.

*[Run the JDK version.]* Five red crosses. Every one refused, and each message says exactly which item.

*[Run the Jackson version. Read slowly.]* Now Jackson. Five green ticks. Two. Two: it cut off the half box. Then… minus one billion two hundred ninety-four million. *[long pause]* Negative pralines. The number was too big, so it wrapped around. Null becomes zero. Missing becomes zero.

**Business translation:** Jackson accepted every order and invoiced nonsense, without a single alert. A silent error is the most expensive kind: you find it at month-end closing, or your customer finds it first.

Jackson isn't wrong. It's lenient by design, and that's often convenient. But when it's about money, I'd rather have a program that says "I don't know" than one that guesses.

## Slide 15 · Scene 5 · Missing is not the same as null

Scene five. Not every missing field is an error.

Gift wrap: only some items have it. That's optional. Discount code: it's there, but empty. That's null.

Two different situations, two tools. `tryGet`: maybe the field isn't there. `tryValue`: maybe it's null. Both give you an `Optional`, Java's "maybe" box. And when a field must be there, like a VAT number, you keep using `get`, and it fails immediately with a clear message.

**Business translation:** the code itself says which fields are optional and which are mandatory. Your business rules, written where a developer can't miss them.

## Slide 16 · Scene 6 · When the JSON changes shape

Scene six. The same field changes type. The new web shop sends the order number as text. The old till system sends it as a number. This even happens inside Java: thread dumps changed exactly like this between JDK 26 and 27.

The top switch handles both, on purpose.

The bottom one is the beautiful part. Six cases, one per JSON type, and no default. Because `JsonValue` is sealed, the compiler knows the list is complete.

*[Delete the JsonNull line.]* Watch. I remove one case… red. The compiler refuses to build. *[Undo.]*

**Business translation:** it's a checklist the computer verifies for you, before the program ever reaches production.

And that's the real answer to "why twelve years": in 2014, Java couldn't do this.

## Slide 17 · Scene 7 · Strict RFC 8259, and nothing else

Last scene. Files typed by humans: a comment, an extra comma. Very common in configuration files.

*[Run.]* Both refused. JDK 28 accepts the official JSON standard (RFC 8259, if you want the name) and nothing else. No options.

If you really have hand-written files, clean them first: one line of code, and it's the JEP's own suggestion.

Honest again: Jackson's default settings refuse these too. The difference is that Jackson has switches, and the JDK doesn't.

**Small by design, strict by design.** That's the whole API. *[Switch back to the slides.]*

## Slide 18 · Small on purpose

Now: what it refuses to do. Four things, on purpose.

No data binding. Remember the machine that pours the form into your own mould? Not here. You pick the fields yourself.

No streaming. The whole file must fit in memory: fine for an order, not for a five-gigabyte export.

No syntax extensions, and no configuration. One parser, one behaviour.

Some developers online call it useless without data binding. I understand them. But whatever goes into the JDK has to be supported for decades, and data binding is a framework with a thousand options. So the team picked the everyday job, reading a few fields out of a message, and did that one job well.

So, is Jackson dead? *[pause]* No. The JEP has exactly one non-goal: not replacing libraries like Jackson. Starting with the JDK and moving to Jackson later is, in their words, not a failure.

## Slide 19 · It's an incubator module

And now… the catch. Remember the little star on slide two?

*[Switch to IntelliJ. Open pom.xml. Comment out the two lines. Reload Maven. Build.]* Watch. I remove one flag… and the code doesn't even compile.

*[Put the lines back. Run any scene.]* Back. And look at the first line of every run: a warning. "Using incubator modules."

Incubator means test kitchen. You need the flag when you build and when you run. Java warns you every time. When it graduates, the package name changes, so today's imports will break. And if it doesn't graduate, it can be removed.

**Business translation:** perfect for experiments, tools and tests. Not something we put in a production service.

## Slide 20 · Nobody knows. History gives a range

How long does a recipe stay in the test kitchen? Nobody knows. History gives a range.

The HTTP client: two releases, then standard in Java 11. The Vector API: in the incubator since Java 16, still there in Java 27, twelve rounds later.

And first, JDK 28 has to ship: March 2027. It's not a long-term-support version. The next one is Java 29, in September 2027.

For us, on Java 25: nothing changes today. And that's fine.

## Slide 21 · Keep Jackson. Try JDK 28 on the side

So what do we do on Monday?

Services, Spring Boot, our Java 25 applications: change nothing. Jackson is still the right tool, and Spring Boot 4 even uses Jackson 3 by default.

Scripts, command-line tools, test helpers, small batch programs: download the JDK 28 early access, open jshell (Java's interactive console) with the flag, and parse something real from your own work. Ten minutes. That's your homework.

Just not in production. Not yet.

## Slide 22 · Questions?

Let me finish where we started. *[Point left.]* 2014: a whole JSON library. Never shipped. *[Point right.]* 2026: the smallest thing that works. Integrated.

**Small by design, strict by design.** Sometimes the best feature is the one you dare to leave out.

Thank you. *[pause]* Questions? *[beat]* And yes, the chocolate in the demo was fictional. Sorry.
