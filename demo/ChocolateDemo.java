import jdk.incubator.json.*;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Belgian praline order: compute the bill with the built-in JSON API.
 *
 * Run:  java --add-modules jdk.incubator.json ChocolateDemo.java
 *
 * Story: a partner's export script changes and now writes  "quantity": "12 pcs".
 * Live trick: make that change in chocolate-order.json and rerun.
 *   AFTER (this file): the order is REJECTED with a clear error.
 *   BEFORE (Jackson):  quantity silently becomes 0 -> free chocolate!
 */
public class ChocolateDemo {

    static final double GIFT_WRAP_PRICE = 2.50;

    public static void main(String[] args) throws Exception {
        try {
            JsonValue order = Json.parse(Files.readString(Path.of("chocolate-order.json")));

            System.out.println(order.get("shop").asString() + " - order for " + order.get("customer").asString());
            System.out.println();

            double total = 0;
            for (JsonValue item : order.get("items").asList()) {
                int quantity = item.get("quantity").asInt();
                double price = item.get("unitPrice").asDouble();
                double subtotal = quantity * price;
                total += subtotal;
                System.out.printf("  %2d x %-15s %5.2f EUR = %6.2f EUR%n",
                        quantity, item.get("praline").asString(), price, subtotal);
            }

            // "giftWrap" is optional: default = no gift wrap
            if (order.tryGet("giftWrap").map(JsonValue::asBoolean).orElse(false)) {
                total += GIFT_WRAP_PRICE;
                System.out.printf("     + gift wrap                         %6.2f EUR%n", GIFT_WRAP_PRICE);
            }
            System.out.printf("%n  TOTAL                                 %6.2f EUR%n", total);

        } catch (JsonValueException e) {
            System.out.println("ORDER REJECTED -> " + e.getMessage());
        } catch (JsonParseException e) {
            System.out.println("INVALID JSON -> " + e.getMessage());
        }
    }
}
