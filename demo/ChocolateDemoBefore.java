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

        Out.top();
        Out.row("  BEFORE  |  Java 21 + Jackson", Out.CYAN);
        Out.row("  File: " + file.getName(), Out.DIM);

        long orderId = order.get("orderId").asLong();
        Out.row("  Invoice #" + orderId + " for " + order.get("customer").asText(), Out.BOLD);
        Out.line();
        Out.row(Out.columns("Qty", "Praline", "Unit", "Subtotal"), Out.DIM);

        double total = 0;
        for (JsonNode item : order.get("items")) {
            int quantity = item.get("quantity").asInt();
            double price = item.get("unitPrice").asDouble();
            total += quantity * price;
            Out.row(Out.columns(String.valueOf(quantity), item.get("praline").asText(),
                    Out.eur(price), Out.eur(quantity * price)), null);
        }

        if (order.has("giftWrap") && order.get("giftWrap").asBoolean()) {
            total += GIFT_WRAP_PRICE;
            Out.row(Out.columns("", "Gift wrap", "", Out.eur(GIFT_WRAP_PRICE)), null);
        }
        Out.line();
        Out.row(Out.total(total), Out.BOLD);
        Out.bottom();
        Out.status("  [ OK ] Invoice created. No errors.", Out.GREEN);
    }

    /** Terminal layout: a plain-ASCII box (works in every Windows code page) with optional colours. */
    static class Out {
        static final int W = 56;
        static final boolean COLOR = System.getenv("NO_COLOR") == null;
        static final String RESET = "\u001B[0m", BOLD = "\u001B[1m", DIM = "\u001B[2m",
                GREEN = "\u001B[1;32m", CYAN = "\u001B[1;36m";

        static void top()    { System.out.println("+" + "-".repeat(W) + "+"); }
        static void line()   { top(); }
        static void bottom() { top(); }

        static void row(String text, String color) {
            String cell = text.length() > W ? text.substring(0, W) : text + " ".repeat(W - text.length());
            System.out.println("|" + (COLOR && color != null ? color + cell + RESET : cell) + "|");
        }

        static void status(String text, String color) {
            System.out.println(COLOR ? color + text + RESET : text);
            System.out.println();
        }

        static String columns(String qty, String name, String unit, String subtotal) {
            return String.format("  %4s  %-18s %10s %13s", qty, name, unit, subtotal);
        }

        static String total(double total) {
            return String.format("  %-36s %13s", "TOTAL", eur(total));
        }

        static String eur(double amount) {
            return String.format("%.2f EUR", amount);
        }
    }
}
