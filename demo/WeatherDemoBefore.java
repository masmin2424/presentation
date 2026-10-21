import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

/**
 * BEFORE version of WeatherDemo (offline): Java 21 + Jackson (external library).
 * Run:  java -cp "lib/*" WeatherDemoBefore.java
 */
public class WeatherDemoBefore {

    public static void main(String[] args) throws Exception {
        JsonNode json = new ObjectMapper().readTree(new File("forecast.json"));
        JsonNode periods = json.get("properties").get("periods");

        int sum = 0;
        for (JsonNode p : periods) {
            System.out.printf("%-16s %3d %s  %s%n",
                    p.get("name").asText(),
                    p.get("temperature").asInt(),
                    p.get("temperatureUnit").asText(),
                    p.get("shortForecast").asText());
            sum += p.get("temperature").asInt();
        }
        System.out.printf("%nAverage over %d periods: %.1f F%n", periods.size(), (double) sum / periods.size());
    }
}
