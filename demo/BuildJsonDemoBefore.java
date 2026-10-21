import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * BEFORE version of BuildJsonDemo: Java 21 + Jackson (external library).
 * Run:  java -cp "lib/*" BuildJsonDemoBefore.java
 */
public class BuildJsonDemoBefore {

    public static void main(String[] args) throws Exception {
        String user = "alice";
        String msg = "He said \"hi\"\nand left";

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        body.put("user", user);
        body.put("msg", msg);
        body.putArray("tags").add("java").add("json");

        System.out.println("Compact - what you send over HTTP:");
        System.out.println(mapper.writeValueAsString(body));
        System.out.println();
        System.out.println("Pretty printed:");
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));

        String back = mapper.readTree(mapper.writeValueAsString(body)).get("msg").asText();
        System.out.println();
        System.out.println("Round trip identical: " + back.equals(msg));
    }
}
