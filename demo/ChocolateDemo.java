import jdk.incubator.json.*;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * AFTER: a nightly batch turns a partner's order file into an invoice,
 * with the JSON API built into JDK 28 (JEP 540). No library.
 *
 * Run:  java --add-modules jdk.incubator.json ChocolateDemo.java [order-file]
 *       (default: chocolate-order.json; try the files in scenarios/)
 *
 * Each scenario is a change in the partner's export script. JEP 540 rules at work:
 *   2-units-in-quantity     "12 pcs"       -> no silent conversion: asInt() refuses text
 *   3-duplicate-key         quantity twice -> strict RFC 8259: duplicate keys are rejected
 *   4-giftwrap-in-french    "oui"          -> no silent conversion: asBoolean() refuses text
 *   5-new-order-id-format   "A-1042"       -> sealed types: the switch handles number AND text ids
 */
public class ChocolateDemo {

    static final double GIFT_WRAP_PRICE = 2.50;

    public static void main(String[] args) throws Exception {
        Path file = Path.of(args.length > 0 ? args[0] : "chocolate-order.json");
        try {
            JsonValue order = Json.parse(Files.readString(file));   // strict: duplicate keys -> JsonParseException

            System.out.println("Invoice for order " + orderId(order.get("orderId"))
                    + " - " + order.get("shop").asString() + ", for " + order.get("customer").asString());
            System.out.println();

            double total = 0;
            for (JsonValue item : order.get("items").asList()) {
                int quantity = item.get("quantity").asInt();       // a number, or an error: never a guess
                double price = item.get("unitPrice").asDouble();
                double subtotal = quantity * price;
                total += subtotal;
                System.out.printf("  %2d x %-15s %5.2f EUR = %6.2f EUR%n",
                        quantity, item.get("praline").asString(), price, subtotal);
            }

            // "giftWrap" is optional: missing or null -> no gift wrap. Present -> must be a real boolean.
            boolean giftWrap = order.tryGet("giftWrap")
                    .flatMap(JsonValue::tryValue)
                    .map(JsonValue::asBoolean)
                    .orElse(false);
            if (giftWrap) {
                total += GIFT_WRAP_PRICE;
                System.out.printf("     + gift wrap                         %6.2f EUR%n", GIFT_WRAP_PRICE);
            }
            System.out.printf("%n  TOTAL                                 %6.2f EUR%n", total);

        } catch (JsonParseException e) {
            System.out.println("FILE REJECTED (invalid JSON) -> " + e.getMessage());
        } catch (JsonValueException | IllegalArgumentException e) {
            System.out.println("ORDER REJECTED -> " + e.getMessage());
        }
    }

    /** Old ids are numbers (1042), new ones are text ("A-1042"). Handle both on purpose, reject anything else. */
    static String orderId(JsonValue id) {
        return switch (id) {
            case JsonNumber n -> "#" + n.asLong();
            case JsonString s -> s.asString();
            default -> throw new IllegalArgumentException("orderId must be a number or a text, got: " + id);
        };
    }
}
