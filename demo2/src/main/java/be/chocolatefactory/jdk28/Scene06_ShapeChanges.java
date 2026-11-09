package be.chocolatefactory.jdk28;

import be.chocolatefactory.Orders;
import jdk.incubator.json.Json;
import jdk.incubator.json.JsonArray;
import jdk.incubator.json.JsonBoolean;
import jdk.incubator.json.JsonNull;
import jdk.incubator.json.JsonNumber;
import jdk.incubator.json.JsonObject;
import jdk.incubator.json.JsonString;
import jdk.incubator.json.JsonValue;
import jdk.incubator.json.JsonValueException;

import java.util.List;

/// Scene 6: when the JSON changes shape. `JsonValue` is sealed: it is always one of 6 types.
///
/// The web shop sends  "orderId": "CHOC-2027-0042"  (a string),
/// the old till system sends  "orderId": 42          (a number).
class Scene06_ShapeChanges {

    void main() {
        for (String file : List.of("valid-order.json", "legacy-order.json")) {
            JsonValue orderId = Json.parse(Orders.read(file)).get("orderId");

            String id = switch (orderId) {
                case JsonString s -> s.asString();
                case JsonNumber n -> "LEGACY-" + n.asLong();
                default -> throw new JsonValueException("Unexpected orderId: " + orderId);
            };
            IO.println(file + " → " + id);
        }

        IO.println("");
        Json.parse(Orders.read("valid-order.json")).asMap()
            .forEach((name, value) -> IO.println(name + " : " + describe(value)));
    }

    /// All six cases are covered, so the switch is exhaustive: no default branch needed.
    static String describe(JsonValue value) {
        return switch (value) {
            case JsonObject o  -> "object " + o.asMap().keySet();
            case JsonArray a   -> "array of " + a.asList().size();
            case JsonString s  -> "string \"" + s.asString() + "\"";
            case JsonNumber n  -> "number " + n;
            case JsonBoolean b -> "boolean " + b.asBoolean();
            case JsonNull _    -> "null";
        };
    }
}
