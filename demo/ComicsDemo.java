import jdk.incubator.json.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

/**
 * Belgian comics demo: explore a JSON list with the built-in API.
 *
 * Run:  java --add-modules jdk.incubator.json ComicsDemo.java
 *       java --add-modules jdk.incubator.json ComicsDemo.java dog     (only series with a dog)
 *
 * Shows: arrays, nested objects, OPTIONAL fields ("companion" missing) and JSON null.
 */
public class ComicsDemo {

    public static void main(String[] args) throws Exception {
        String onlySpecies = args.length > 0 ? args[0] : null;
        var comics = Json.parse(Files.readString(Path.of("comics.json"))).asList();

        System.out.println("Belgian comics, oldest first:");
        System.out.println();
        comics.stream()
                .sorted(Comparator.comparingInt(c -> c.get("firstYear").asInt()))
                .filter(c -> onlySpecies == null
                        || companion(c).map(a -> a.get("species").asString().equals(onlySpecies)).orElse(false))
                .forEach(c -> System.out.printf("%d  %-20s by %-18s %s%n",
                        c.get("firstYear").asInt(),
                        c.get("series").asString(),
                        c.get("creator").asString(),
                        companion(c).map(a -> "with " + a.get("name").asString()
                                + " (" + a.get("species").asString() + ")").orElse("")));

        long withAnimal = comics.stream().filter(c -> companion(c).isPresent()).count();
        double avgYear = comics.stream().mapToInt(c -> c.get("firstYear").asInt()).average().orElse(0);

        System.out.println();
        System.out.println(withAnimal + " of " + comics.size() + " heroes have an animal companion");
        System.out.printf("Average creation year: %.0f%n", avgYear);
    }

    /** "companion" may be missing OR JSON null: tryGet + tryValue handle both. */
    static Optional<JsonValue> companion(JsonValue comic) {
        return comic.tryGet("companion").flatMap(JsonValue::tryValue);
    }
}
