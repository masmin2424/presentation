import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * BEFORE version of ComicsDemo: Java 21 + Jackson (external library).
 * Run:  java -cp "lib/*" ComicsDemoBefore.java
 *       java -cp "lib/*" ComicsDemoBefore.java dog
 */
public class ComicsDemoBefore {

    public static void main(String[] args) throws Exception {
        String onlySpecies = args.length > 0 ? args[0] : null;
        List<JsonNode> comics = new ArrayList<>();
        new ObjectMapper().readTree(new File("comics.json")).forEach(comics::add);

        System.out.println("Belgian comics, oldest first:");
        System.out.println();
        comics.stream()
                .sorted(Comparator.comparingInt(c -> c.get("firstYear").asInt()))
                .filter(c -> onlySpecies == null
                        || companion(c).map(a -> a.get("species").asText().equals(onlySpecies)).orElse(false))
                .forEach(c -> System.out.printf("%d  %-20s by %-18s %s%n",
                        c.get("firstYear").asInt(),
                        c.get("series").asText(),
                        c.get("creator").asText(),
                        companion(c).map(a -> "with " + a.get("name").asText()
                                + " (" + a.get("species").asText() + ")").orElse("")));

        long withAnimal = comics.stream().filter(c -> companion(c).isPresent()).count();
        double avgYear = comics.stream().mapToInt(c -> c.get("firstYear").asInt()).average().orElse(0);

        System.out.println();
        System.out.println(withAnimal + " of " + comics.size() + " heroes have an animal companion");
        System.out.printf("Average creation year: %.0f%n", avgYear);
    }

    /** Missing field -> null, JSON null -> NullNode: you must remember both cases. */
    static Optional<JsonNode> companion(JsonNode comic) {
        JsonNode c = comic.get("companion");
        return (c == null || c.isNull()) ? Optional.empty() : Optional.of(c);
    }
}
