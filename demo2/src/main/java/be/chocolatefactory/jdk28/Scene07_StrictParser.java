package be.chocolatefactory.jdk28;

import be.chocolatefactory.Orders;
import jdk.incubator.json.Json;
import jdk.incubator.json.JsonParseException;

import java.util.List;

/// Scene 7: strict RFC 8259. No trailing commas, no comments, no options to allow them.
class Scene07_StrictParser {

    void main() {
        for (String file : List.of("trailing-comma.json", "with-comments.json")) {
            try {
                Json.parse(Orders.read(file));
                IO.println("✓ " + file);
            } catch (JsonParseException e) {
                IO.println("✗ " + file + " → " + e.getMessage());
            }
        }

        // The workaround suggested by the JEP: clean the text yourself, then parse.
        String cleaned = Orders.read("with-comments.json").replaceAll("(?m)^\\s*//.*$", "");
        IO.println("✓ after cleanup → " + Json.parse(cleaned).get("orderId").asString());
    }
}
