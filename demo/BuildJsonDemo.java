import jdk.incubator.json.*;

import java.util.List;
import java.util.Map;

/**
 * Demo 2: BUILD a JSON request body.
 * BEFORE: string templates break as soon as a value contains a quote or a newline.
 * NOW:    build a JsonObject, toString() escapes everything correctly.
 */
public class BuildJsonDemo {

    public static void main(String[] args) {
        String user = "alice";
        String msg = "He said \"hi\"\nand left";   // quote + newline: the classic trap

        // ---- BEFORE: hand-made JSON ------------------------------------------
        String handMade = """
                {"user": "%s", "msg": "%s"}""".formatted(user, msg);
        System.out.println("BEFORE (string template):");
        System.out.println(handMade);
        try {
            Json.parse(handMade);
            System.out.println("-> valid");
        } catch (JsonParseException e) {
            System.out.println("-> BROKEN JSON: " + e.getMessage());
        }

        // ---- NOW: a real JSON value ------------------------------------------
        JsonObject body = JsonObject.of(Map.of(
                "user", JsonString.of(user),
                "msg", JsonString.of(msg),
                "tags", JsonArray.of(List.of(JsonString.of("java"), JsonString.of("json")))));

        System.out.println();
        System.out.println("NOW (JsonObject), compact - what you send over HTTP:");
        System.out.println(body);
        System.out.println();
        System.out.println("NOW, pretty printed:");
        System.out.println(Json.toDisplayString(body, "  "));

        // Round trip: parse what we produced and get the exact original string back
        String back = Json.parse(body.toString()).get("msg").asString();
        System.out.println();
        System.out.println("Round trip identical: " + back.equals(msg));

        // Ready for the built-in HttpClient:
        //   HttpRequest.newBuilder(uri)
        //       .header("Content-Type", "application/json")
        //       .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
        //       .build();
    }
}
