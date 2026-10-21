import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

/**
 * BEFORE version of ThreadDumpDemo: Java 21 + Jackson (external library).
 * Run:  java -cp "lib/*" ThreadDumpDemoBefore.java dump-from-java21.json
 */
public class ThreadDumpDemoBefore {

    public static void main(String[] args) throws Exception {
        String file = args.length > 0 ? args[0] : "dump-from-java21.json";
        JsonNode dump = new ObjectMapper().readTree(new File(file)).get("threadDump");
        System.out.println("File:    " + file);
        System.out.println("Runtime: " + dump.get("runtimeVersion").asText());
        System.out.println();

        int total = 0;
        for (JsonNode container : dump.get("threadContainers")) {
            JsonNode threads = container.get("threads");
            if (threads.isEmpty()) continue;

            System.out.println("== " + container.get("container").asText()
                    + "  (" + threads.size() + " threads)");
            for (JsonNode t : threads) {
                boolean virtual = t.has("virtual") && t.get("virtual").asBoolean();
                System.out.printf("   #%-4d %s%s%n",
                        t.get("tid").asLong(),
                        t.get("name").asText(),
                        virtual ? "  [virtual]" : "");
            }
            total += threads.size();
        }
        System.out.println();
        System.out.println(total + " threads");
    }
}
