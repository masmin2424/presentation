package be.chocolatefactory.java25;

import be.chocolatefactory.Orders;
import com.fasterxml.jackson.databind.ObjectMapper;

/// Java 25 version of Scene 1, with Jackson 2.x.
///
/// About the same number of lines. The difference is elsewhere: a dependency
/// (databind + core + annotations) to add, update and patch, and a checked exception.
class Jackson01_ReadOneField {

    void main() throws Exception {
        String city = new ObjectMapper()
                .readTree(Orders.read("valid-order.json"))
                .get("customer").get("address").get("city")
                .asText();

        IO.println("Ship to: " + city);
    }
}
