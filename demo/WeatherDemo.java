import jdk.incubator.json.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Demo 3: the JEP's motivating example - average forecast temperature for Santa Clara.
 *
 *   WeatherDemo          -> offline, reads forecast.json (safe for a meeting room)
 *   WeatherDemo --live   -> calls the US National Weather Service API (api.weather.gov)
 */
public class WeatherDemo {

    public static void main(String[] args) throws Exception {
        boolean live = args.length > 0 && args[0].equals("--live");
        String text = live ? fetchLive() : Files.readString(Path.of("forecast.json"));

        JsonValue json = Json.parse(text);
        var periods = json.get("properties").get("periods").asList();

        for (JsonValue p : periods) {
            System.out.printf("%-16s %3d %s  %s%n",
                    p.get("name").asString(),
                    p.get("temperature").asInt(),
                    p.get("temperatureUnit").asString(),
                    p.get("shortForecast").asString());
        }

        periods.stream()
                .mapToInt(p -> p.get("temperature").asInt())
                .average()
                .ifPresent(avg -> System.out.printf("%nAverage over %d periods: %.1f F%n", periods.size(), avg));
    }

    /** Two calls: /points gives the forecast URL for a lat/lon, then fetch that forecast. */
    static String fetchLive() throws Exception {
        try (HttpClient client = HttpClient.newHttpClient()) {
            String points = get(client, "https://api.weather.gov/points/37.3541,-121.9552");
            String forecastUrl = Json.parse(points).get("properties").get("forecast").asString();
            System.out.println("Forecast URL: " + forecastUrl + "\n");
            return get(client, forecastUrl);
        }
    }

    static String get(HttpClient client, String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("User-Agent", "jep540-demo")       // required by api.weather.gov
                .header("Accept", "application/geo+json")
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }
}
