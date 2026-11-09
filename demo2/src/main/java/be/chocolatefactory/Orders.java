package be.chocolatefactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/// Reads an order file from `src/main/resources/orders/` as a String.
///
/// Note: `Json.parse` takes a `String` or a `char[]`, not a file or a stream.
/// The whole document is expected to fit in memory.
public final class Orders {

    private Orders() {
    }

    public static String read(String fileName) {
        try (InputStream in = Orders.class.getResourceAsStream("/orders/" + fileName)) {
            if (in == null) {
                throw new IllegalArgumentException("No such order file: " + fileName);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
