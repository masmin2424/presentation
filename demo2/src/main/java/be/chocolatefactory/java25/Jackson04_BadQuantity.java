package be.chocolatefactory.java25;

import be.chocolatefactory.Orders;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/// Java 25 version of Scene 4, with Jackson 2.x.
///
/// asInt() never complains: it coerces, truncates, overflows or returns 0.
/// path() is the usual "safe" idiom for a field that may be missing (get() would return null).
class Jackson04_BadQuantity {

    void main() throws Exception {
        JsonNode order = new ObjectMapper().readTree(Orders.read("bad-quantity.json"));

        for (JsonNode item : order.get("items")) {
            IO.println("✓ " + item.get("sku").asText() + " → " + item.path("quantity").asInt());
        }
    }
}
