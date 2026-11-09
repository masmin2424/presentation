package be.chocolatefactory.jdk28;

import be.chocolatefactory.Orders;
import jdk.incubator.json.Json;
import jdk.incubator.json.JsonArray;
import jdk.incubator.json.JsonNumber;
import jdk.incubator.json.JsonObject;
import jdk.incubator.json.JsonValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/// Scene 2: order in, invoice out. Read (navigate + convert), then write (build + print).
class Scene02_OrderToInvoice {

    void main() {
        JsonValue order = Json.parse(Orders.read("valid-order.json"));

        List<JsonValue> lines = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (JsonValue item : order.get("items").asList()) {
            int quantity = item.get("quantity").asInt();
            // Money: no double. toString() keeps the number exactly as written ("24.90").
            BigDecimal unitPrice = new BigDecimal(item.get("unitPrice").toString());
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            total = total.add(lineTotal);

            var line = new LinkedHashMap<String, JsonValue>();   // keeps our member order
            line.put("product", item.get("product"));
            line.put("quantity", JsonNumber.of(quantity));
            line.put("lineTotal", JsonNumber.of(lineTotal.toPlainString()));
            lines.add(JsonObject.of(line));
        }

        var invoice = new LinkedHashMap<String, JsonValue>();
        invoice.put("invoiceFor", order.get("orderId"));
        invoice.put("customer", order.get("customer").get("name"));
        invoice.put("lines", JsonArray.of(lines));
        invoice.put("total", JsonNumber.of(total.toPlainString()));
        invoice.put("currency", order.get("currency"));
        JsonObject json = JsonObject.of(invoice);

        IO.println(json);                               // compact: one line, no spaces
        IO.println(Json.toDisplayString(json, "  "));   // pretty-printed, 2-space indent
    }
}
