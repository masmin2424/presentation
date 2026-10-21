import jdk.incubator.json.*;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Demo 4: analyse a JSON thread dump with a few lines of code.
 *
 * Usage:  ThreadDumpDemo [dump.json] [--virtual]
 *   default file: dump.json (made by GenerateDump), fallback dump-from-java21.json
 *
 * Get a dump from any running Java 21+ app:
 *   jcmd <pid> Thread.dump_to_file -format=json dump.json
 */
public class ThreadDumpDemo {

    public static void main(String[] args) throws Exception {
        Path file = args.length > 0 && !args[0].startsWith("--") ? Path.of(args[0]) : defaultDump();
        boolean onlyVirtual = String.join(" ", args).contains("--virtual");

        JsonValue dump = Json.parse(Files.readString(file)).get("threadDump");
        System.out.println("File:    " + file);
        System.out.println("Runtime: " + dump.get("runtimeVersion").asString());
        System.out.println();

        int total = 0;
        for (JsonValue container : dump.get("threadContainers").asList()) {
            var threads = container.get("threads").asList().stream()
                    .filter(t -> !onlyVirtual || isVirtual(t))
                    .toList();
            if (threads.isEmpty()) continue;

            System.out.println("== " + container.get("container").asString()
                    + "  (" + threads.size() + " threads)");
            for (JsonValue t : threads) {
                System.out.printf("   #%-4d %s%s%n",
                        toLong(t.get("tid")),
                        t.get("name").asString(),
                        isVirtual(t) ? "  [virtual]" : "");
            }
            total += threads.size();
        }
        System.out.println();
        System.out.println(total + (onlyVirtual ? " virtual" : "") + " threads");
    }

    /** "virtual" is an OPTIONAL boolean (newer JDKs write it; Java 21 dumps don't). */
    static boolean isVirtual(JsonValue thread) {
        return thread.tryGet("virtual").map(JsonValue::asBoolean).orElse(false);
    }

    /** The thread id may be a JSON string ("42") or a JSON number (42): handle both, reject the rest. */
    static long toLong(JsonValue v) {
        return switch (v) {
            case JsonNumber n -> n.asLong();
            case JsonString s -> Long.parseLong(s.asString());
            default -> throw new IllegalArgumentException("Unexpected tid: " + v);
        };
    }

    static Path defaultDump() {
        Path generated = Path.of("dump.json");
        return Files.exists(generated) ? generated : Path.of("dump-from-java21.json");
    }
}
