package be.chocolatefactory.jdk28;

import be.chocolatefactory.Orders;
import jdk.incubator.json.Json;
import jdk.incubator.json.JsonValue;
import jdk.incubator.json.JsonValueException;

/// Scene 5: optional members and null values, without exceptions.
///
///  - `tryGet(name)` : the member may be ABSENT      -> Optional
///  - `tryValue()`   : the value may be JSON `null`  -> Optional
///  - `get(name)`    : the member MUST be there      -> fails fast
class Scene05_MissingAndNull {

    void main() {
        JsonValue order = Json.parse(Orders.read("valid-order.json"));

        // "giftWrap" is only on some items
        for (JsonValue item : order.get("items").asList()) {
            boolean giftWrap = item.tryGet("giftWrap").map(JsonValue::asBoolean).orElse(false);
            IO.println(item.get("sku").asString() + " gift wrap: " + giftWrap);
        }

        // "discountCode" is present, but null
        String discount = order.get("discountCode").tryValue()
                               .map(JsonValue::asString)
                               .orElse("none");
        IO.println("Discount code: " + discount);

        // get() on a member that is not there: fails fast, and tells you where
        try {
            order.get("customer").get("vatNumber");
        } catch (JsonValueException e) {
            IO.println("✗ " + e.getMessage());
        }
    }
}
