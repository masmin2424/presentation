import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

/**
 * BEFORE version of ChocolateDemo: Java 21 + Jackson (external library).
 * Run:  java -cp "lib/*" ChocolateDemoBefore.java
 *
 * Set "quantity": "12 pcs" in chocolate-order.json (partner changed their export script)
 * -> Jackson's asInt() returns 0, no error: free chocolate!
 */
public class ChocolateDemoBefore {

    static final double GIFT_WRAP_PRICE = 2.50;

    public static void main(String[] args) throws Exception {
        JsonNode order = new ObjectMapper().readTree(new File("chocolate-order.json"));

        System.out.println(order.get("shop").asText() + " - order for " + order.get("customer").asText());
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
