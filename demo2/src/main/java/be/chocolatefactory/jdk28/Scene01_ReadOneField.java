package be.chocolatefactory.jdk28;

import be.chocolatefactory.Orders;
import jdk.incubator.json.Json;

/// Scene 1: read ONE field. No library, no casting.
///
/// `get` works on any `JsonValue`: you never downcast to `JsonObject` first.
class Scene01_ReadOneField {

    void main() {
        String city = Json.parse(Orders.read("valid-order.json"))
                          .get("customer").get("address").get("city")
                          .asString();

        IO.println("Ship to: " + city);
    }
}
