package be.chocolatefactory.java25;

import be.chocolatefactory.Orders;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;

/// Java 25 version of Scene 2, with Jackson 2.x.
///
/// To be fair: Jackson's builder (put/putArray/addObject) is nicer for writing JSON.
class Jackson02_OrderToInvoice {

    void main() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode order = mapper.readTree(Orders.read("valid-order.json"));

        ObjectNode invoice = mapper.createObjectNode();
        invoice.put("invoiceFor", order.get("orderId").asText());
        invoice.put("customer", order.get("customer").get("name").asText());
        ArrayNode lines = invoice.putArray("lines");

        BigDecimal total = BigDecimal.ZERO;
        for (JsonNode item : order.get("items")) {
            int quantity = item.get("quantity").asInt();
            // 24.90 was read as a double: decimalValue() gives 24.9 (the trailing zero is gone)
            BigDecimal unitPrice = item.get("unitPrice").decimalValue();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            total = total.add(lineTotal);

            lines.addObject()
                 .put("product", item.get("product").asText())
                 .put("quantity", quantity)
                 .put("lineTotal", lineTotal);
        }
        invoice.put("total", total);
        invoice.put("currency", order.get("currency").asText());

        IO.println(invoice);
        IO.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(invoice));
    }
}
