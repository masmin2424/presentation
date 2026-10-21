# JEP 540 – Everyday JSON demo

Plain Java files. No Maven, no IDE setup, no scripts. Just a terminal and JDK 28.

## Setup (once)

1. Download **JDK 28** from https://jdk.java.net/28/ and unzip it to `C:\jdk-28`.
2. Open a terminal **in this folder** and run (PowerShell):

```
$env:Path = "C:\jdk-28\bin;$env:Path"
java -version
```

You should see `28`. This only changes the current terminal; your normal Java stays the default everywhere else.

## Run

Every command has the same shape: `java --add-modules jdk.incubator.json <File>.java`
(the `--add-modules` part switches on the new JSON API — that's the only "incubator" thing to remember).

```
java --add-modules jdk.incubator.json ConfigDemo.java
java --add-modules jdk.incubator.json BuildJsonDemo.java
java --add-modules jdk.incubator.json WeatherDemo.java
java --add-modules jdk.incubator.json WeatherDemo.java --live
java GenerateDump.java
java --add-modules jdk.incubator.json ThreadDumpDemo.java
java --add-modules jdk.incubator.json ThreadDumpDemo.java dump-from-java21.json
jshell --add-modules jdk.incubator.json Tour.jsh
```

Tip: press ↑ in the terminal to rerun the last command.

## What each one shows

| File | Point to make |
|---|---|
| ConfigDemo | Read `config.json`. Edit it live: `"port": "abc"` → clear error (Jackson would silently give 0); delete port → 8080; trailing comma → rejected |
| BuildJsonDemo | Hand-written JSON breaks on quotes/newlines; `JsonObject` doesn't |
| WeatherDemo | Average temperature from a forecast (offline file; `--live` = real API) |
| GenerateDump | Makes a real thread dump of itself → `dump.json` |
| ThreadDumpDemo | Lists the threads in a dump. Add `--virtual` for virtual threads only |
| ThreadDumpDemo dump-from-java21.json | Same tool on a dump made by **Java 21**, like our apps today |
| Tour.jsh | Interactive JShell walkthrough; stays open for questions |

For one of our real apps: `jcmd <pid> Thread.dump_to_file -format=json mydump.json`
then `java --add-modules jdk.incubator.json ThreadDumpDemo.java mydump.json`.

## Before / After comparison

Each demo has a **Before** version using Java 21 + Jackson (the way it's done today):
`ConfigDemoBefore.java`, `BuildJsonDemoBefore.java`, `WeatherDemoBefore.java`, `ThreadDumpDemoBefore.java`.

They need the Jackson library (not included in this repository). Download it once into `lib/` (PowerShell, in this folder, ~2.3 MB from Maven Central):

```
mkdir lib; $m = "https://repo1.maven.org/maven2/com/fasterxml/jackson/core"; foreach ($a in "jackson-databind","jackson-core","jackson-annotations") { Invoke-WebRequest "$m/$a/2.18.2/$a-2.18.2.jar" -OutFile "lib\$a-2.18.2.jar" }
```

In a real project this would come from Maven/Gradle — that's exactly the extra step the new API removes.

Run them with your normal Java 21 (no JDK 28 needed):

```
java -cp "lib/*" ConfigDemoBefore.java
java -cp "lib/*" BuildJsonDemoBefore.java
java -cp "lib/*" WeatherDemoBefore.java
java -cp "lib/*" ThreadDumpDemoBefore.java
```

Best moment for the audience: set `"port": "abc"` in `config.json`, then run both:
- Before (Jackson): prints `port = 0` — silently wrong
- After (JDK 28): clear error

## Notes

- A `WARNING: Using incubator modules` line is normal.
- Feedback to the OpenJDK team: core-libs-dev@openjdk.org
