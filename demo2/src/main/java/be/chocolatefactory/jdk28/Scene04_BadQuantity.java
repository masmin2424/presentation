package be.chocolatefactory.jdk28;

import be.chocolatefactory.Orders;
import jdk.incubator.json.Json;
import jdk.incubator.json.JsonValue;
import jdk.incubator.json.JsonValueException;

/// Scene 4: five bad quantities: "2" (a string), 2.5, 3000000000, null, and missing.
///
/// `asInt()` only succeeds for a JSON number that fits in an int exactly.
/// Everything else fails fast with a message that shows the path to the bad value.
/// Compare with java25/Jackson04_BadQuantity.
class Scene04_BadQuantity {

    void main() {
        JsonValue order = Json.parse(Orders.read("bad-quantity.json"));

        for (JsonValue item : order.get("items").asList()) {
            String sku = item.get("sku").asString();
            try {
                IO.println("✓ " + sku + " → " + item.get("quantity").asInt());
            } catch (JsonValueException e) {
                IO.println("✗ " + sku + " → " + e.getMessage());
            }
        }
    }
}
