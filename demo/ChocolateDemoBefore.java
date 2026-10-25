import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

/**
 * BEFORE: the same nightly batch on Java 21 with Jackson (external library, default settings).
 *
 * Run:  java -cp "lib/*" ChocolateDemoBefore.java [order-file]
 *       (default: chocolate-order.json; try the files in scenarios/)
 *
 * What Jackson's tree API does by default with each partner change (tested with Jackson 2.18.2):
 *   2-units-in-quantity     "12 pcs"       -> asInt() returns 0: 12 pralines for free
 *   3-duplicate-key         quantity twice -> the last value wins: 1 instead of 12
 *   4-giftwrap-in-french    "oui"          -> asBoolean() returns false: gift wrap silently dropped
 *   5-new-order-id-format   "A-1042"       -> asLong() returns 0: invoice for order #0
 * No error in any case: the batch finishes "green".
 */
public class ChocolateDemoBefore {

    static final double GIFT_WRAP_PRICE = 2.50;

    public static void main(String[] args) throws Exception {
        File file = new File(args.length > 0 ? args[0] : "chocolate-order.json");
        JsonNode order = new ObjectMapper().readTree(file);

        long orderId = order.get("orderId").asLong();
        System.out.println("Invoice for order #" + orderId
                + " - " + order.get("shop").asText() + ", for " + order.get("customer").asText());
        System.out.println();

        double total = 0;
        for (JsonNode item : order.get("items")) {
            int quantity = item.get("quantity").asInt();
            double price = item.get("unitPrice").asDouble();
            double subtotal = quantity * price;
            total += subtotal;
            System.out.printf("  %2d x %-15s %5.2f EUR = %6.2f EUR%n",
                    quantity, item.get("praline").asText(), price, subtotal);
        }

        if (order.has("giftWrap") && order.get("giftWrap").asBoolean()) {
            total += GIFT_WRAP_PRICE;
            System.out.printf("     + gift wrap                         %6.2f EUR%n", GIFT_WRAP_PRICE);
        }
        System.out.printf("%n  TOTAL                                 %6.2f EUR%n", total);
    }
}
