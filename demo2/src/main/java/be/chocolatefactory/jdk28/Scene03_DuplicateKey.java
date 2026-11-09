package be.chocolatefactory.jdk28;

import be.chocolatefactory.Orders;
import jdk.incubator.json.Json;
import jdk.incubator.json.JsonParseException;

/// Scene 3: "quantity" appears twice in the same item. 2 boxes or 2000 boxes?
///
/// The JDK refuses to guess: duplicate member names are always a parse error.
/// Compare with java25/Jackson03_DuplicateKey.
class Scene03_DuplicateKey {

    void main() {
        try {
            Json.parse(Orders.read("duplicate-key.json"));
            IO.println("Parsed?! This should not happen.");
        } catch (JsonParseException e) {
            IO.println("✗ Order rejected");
            IO.println(e.getMessage());
        }
    }
}
