import jdk.incubator.json.*;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Demo 1: read a config file with nothing but the JDK.
 *
 * Live during the demo, edit config.json and rerun:
 *   "port": "abc"        -> clear JsonValueException (Jackson's asInt() would silently give 0)
 *   delete the port line -> falls back to 8080
 *   add a trailing comma -> JsonParseException (strict RFC 8259)
 */
public class ConfigDemo {

    public static void main(String[] args) throws Exception {
        String text = Files.readString(Path.of("config.json"));
        try {
            JsonValue json = Json.parse(text);

            Charset charset = Charset.forName(json.get("charset").asString());
            int port = json.tryGet("port").map(JsonValue::asInt).orElse(8080);

            System.out.println("charset = " + charset);
            System.out.println("port    = " + port);
            System.out.println();
            System.out.println("Pretty printed:");
            System.out.println(Json.toDisplayString(json, "  "));
        } catch (JsonParseException e) {
            System.out.println("Invalid JSON  -> " + e.getMessage());
        } catch (JsonValueException e) {
            System.out.println("Wrong value   -> " + e.getMessage());
        }
    }
}
