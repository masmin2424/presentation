package be.chocolatefactory.java25;

import be.chocolatefactory.Orders;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

/// Java 25 version of Scene 3, with Jackson 2.x.
///
/// By default, Jackson keeps the LAST "quantity" and says nothing.
/// Strict mode is opt-in: DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY
class Jackson03_DuplicateKey {

    void main() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        // mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);   // <- opt-in strict mode

        JsonNode item = mapper.readTree(Orders.read("duplicate-key.json")).get("items").get(0);

        int quantity = item.get("quantity").asInt();
        BigDecimal total = item.get("unitPrice").decimalValue().multiply(BigDecimal.valueOf(quantity));

        IO.println("Invoiced: " + quantity + " boxes = EUR " + total);
    }
}
