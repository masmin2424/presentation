# The catch · It's an incubator

**Remember:** one flag, twice (compile time and run time), and the API can still change.

| | |
|---|---|
| Open | `pom.xml` |
| Run | any scene, for example `1. Read one field` |
| Slides | 18 and 19 |
| Time | 2 min |

## 1. The reveal

On slide 2 you said "zero dependencies*, one flag". Now cash it in:

> "Remember the asterisk? Here it is."

## 2. Live: break the build

1. Open `pom.xml`, find `maven-compiler-plugin`.
2. Comment out the two lines:

   ```xml
   <arg>--add-modules</arg>
   <arg>jdk.incubator.json</arg>
   ```

3. Reload Maven, build. **Compilation fails**: the `jdk.incubator.json` module isn't resolved, so the package isn't visible.

> "Without the flag, the code doesn't even compile."

4. Uncomment the lines, reload Maven, run any scene. Point at the first console line:

   ```
   WARNING: Using incubator modules: jdk.incubator.json
   ```

> "And you need the flag again at run time, and Java warns you every time it starts. The run configurations have been passing it all along."

## 3. What "incubator" means (slide 18)

- **One flag, twice:** `--add-modules jdk.incubator.json` at compile time **and** at run time.
- **A warning at every start.**
- **The package will change.** If the API graduates, it leaves `jdk.incubator`: your imports break. If it doesn't graduate, it can be removed.
  - Precedent: the HTTP client moved from `jdk.incubator.http` to `java.net.http`.

## 4. How long will it incubate? (slide 19)

> "Nobody knows. History gives a range."

- **HTTP Client:** incubator in JDK 9 and 10, standard in JDK 11. Two releases.
- **Vector API:** incubating since JDK 16, on its 12th round in JDK 27, waiting for Project Valhalla.
- **JSON API:** first incubator in JDK 28.

And before that, it has to ship:

- **JDK 28:** GA planned for **9 March 2027**. A feature release, **not an LTS**.
- **Next LTS:** JDK 29, September 2027, on Oracle's roadmap.
- **Us:** we're on Java 25 (an LTS). Nothing changes for our services today.

## 5. Transition to the conclusion

> "So what do we do on Monday?"

Go to slide 20: keep Jackson for services; try JDK 28 EA with `jshell --add-modules jdk.incubator.json` for scripts, tools and tests; not in production yet.

## Questions you may get

- **"Can I use it in a Spring Boot app?"** Technically yes with the flag, but don't: incubator APIs can change or disappear, and Spring Boot 4 already ships Jackson 3 as its default.
- **"Will the code I write today still work in JDK 29?"** Not guaranteed: names may change and imports will change when it graduates.
- **"Where do I send feedback?"** core-libs-dev@openjdk.org. The JEP explicitly wants feedback on generating and transforming JSON.
