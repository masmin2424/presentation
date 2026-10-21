import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.charset.Charset;

/**
 * BEFORE version of ConfigDemo: Java 21 + Jackson (external library).
 *
 * Needs the Jackson jars in lib/ (a real project adds this to pom.xml):
 *   <dependency>
 *     <groupId>com.fasterxml.jackson.core</groupId>
 *     <artifactId>jackson-databind</artifactId>
 *     <version>2.18.2</version>
 *   </dependency>
 *
 * Run:  java -cp "lib/*" ConfigDemoBefore.java
 *
 * Try "port": "abc" -> Jackson silently prints port = 0 (no error!)
 */
public class ConfigDemoBefore {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = mapper.readTree(new File("config.json"));

            Charset charset = Charset.forName(json.get("charset").asText());
            int port = json.has("port") ? json.get("port").asInt() : 8080;

            System.out.println("charset = " + charset);
            System.out.println("port    = " + port);
            System.out.println();
            System.out.println("Pretty printed:");
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
        } catch (JsonProcessingException e) {
            System.out.println("Invalid JSON  -> " + e.getOriginalMessage());
        }
    }
}
