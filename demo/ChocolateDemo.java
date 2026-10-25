import jdk.incubator.json.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AFTER: a nightly batch turns a partner's order file into an invoice,
 * with the JSON API built into JDK 28 (JEP 540). No library.
 *
 * Run:  java --add-modules jdk.incubator.json ChocolateDemo.java [order-file]
 *       (default: chocolate-order.json; try the files in scenarios/)
 *
 * Each scenario is a change in the partner's export script. JEP 540 rules at work:
 *   2-units-in-quantity     "12 pcs"       -> no silent conversion: asInt() refuses text
 *   3-duplicate-key         quantity twice -> strict RFC 8259: duplicate keys are rejected
 *   4-giftwrap-in-french    "oui"          -> no silent conversion: asBoolean() refuses text
 *   5-new-order-id-format   "A-1042"       -> sealed types: the switch handles number AND text ids
 */
public class ChocolateDemo {

    static final double GIFT_WRAP_PRICE = 2.50;

    public static void main(String[] args) throws Exception {
        Path file = Path.of(args.length > 0 ? args[0] : "chocolate-order.json");

        Out.top();
        Out.row("  AFTER  |  JDK 28 + built-in JSON API (JEP 540)", Out.CYAN);
        Out.row("  File: " + file.getFileName(), Out.DIM);
        try {
            JsonValue order = Json.parse(Files.readString(file));   // strict: duplicate keys -> JsonParseException

            Out.row("  Invoice " + orderId(order.get("orderId")) + " for " + order.get("customer").asString(), Out.BOLD);
            Out.line();
            Out.row(Out.columns("Qty", "Praline", "Unit", "Subtotal"), Out.DIM);

            double total = 0;
            for (JsonValue item : order.get("items").asList()) {
                int quantity = item.get("quantity").asInt();       // a number, or an error: never a guess
                double price = item.get("unitPrice").asDouble();
                total += quantity * price;
                Out.row(Out.columns(String.valueOf(quantity), item.get("praline").asString(),
                        Out.eur(price), Out.eur(quantity * price)), null);
            }

            // "giftWrap" is optional: missing or null -> no gift wrap. Present -> must be a real boolean.
            boolean giftWrap = order.tryGet("giftWrap")
                    .flatMap(JsonValue::tryValue)
                    .map(JsonValue::asBoolean)
                    .orElse(false);
            if (giftWrap) {
                total += GIFT_WRAP_PRICE;
                Out.row(Out.columns("", "Gift wrap", "", Out.eur(GIFT_WRAP_PRICE)), null);
            }
            Out.line();
            Out.row(Out.total(total), Out.BOLD);
            Out.bottom();
            Out.status("  [ OK ] Invoice created.", Out.GREEN);

        } catch (JsonParseException e) {
            Out.rejected("FILE REJECTED: this is not valid JSON", e.getMessage());
        } catch (JsonValueException | IllegalArgumentException e) {
            Out.rejected("ORDER REJECTED: a value has the wrong type", e.getMessage());
        }
    }

    /** Old ids are numbers (1042), new ones are text ("A-1042"). Handle both on purpose, reject anything else. */
    static String orderId(JsonValue id) {
        return switch (id) {
            case JsonNumber n -> "#" + n.asLong();
            case JsonString s -> s.asString();
            default -> throw new IllegalArgumentException("orderId must be a number or a text, got: " + id);
        };
    }

    /** Terminal layout: a plain-ASCII box (works in every Windows code page) with optional colours. */
    static class Out {
        static final int W = 56;
        static final boolean COLOR = System.getenv("NO_COLOR") == null;
        static final String RESET = "\u001B[0m", BOLD = "\u001B[1m", DIM = "\u001B[2m",
                RED = "\u001B[1;31m", GREEN = "\u001B[1;32m", CYAN = "\u001B[1;36m";

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

        /** Turns JEP 540's message into Problem / Field / Location lines. */
        static void rejected(String title, String message) {
            line();
            row("  " + title, RED);
            Matcher m = Pattern.compile("^(.*?)\\. Path: \"(.*?)\"\\. Location: (.*?)\\.?$").matcher(message);
            if (m.matches()) {
                row("  Problem  : " + friendly(m.group(1)), null);
                row("  Field    : " + readablePath(m.group(2)), null);
                row("  Location : " + m.group(3), null);
            } else {
                row("  Problem  : " + message, null);
            }
            bottom();
            status("  [ STOPPED ] No invoice created. Ask the partner to fix the file.", RED);
        }

        /** "JsonString is not a JsonNumber" -> "expected a number, found text"; duplicate keys -> plain words. */
        static String friendly(String problem) {
            Matcher m = Pattern.compile("Json(\\w+) is not a Json(\\w+)").matcher(problem);
            if (m.matches()) return "expected " + kind(m.group(2)) + ", found " + kind(m.group(1));
            Matcher dup = Pattern.compile("Duplicate member name: \"(.*)\" was already parsed").matcher(problem);
            if (dup.matches()) return "\"" + dup.group(1) + "\" appears twice";
            return problem;
        }

        static String kind(String jsonType) {
            return switch (jsonType) {
                case "Number" -> "a number";
                case "String" -> "text";
                case "Boolean" -> "true/false";
                case "Null" -> "null";
                case "Object" -> "an object";
                case "Array" -> "a list";
                default -> jsonType;
            };
        }

        /** JEP 540 path "{items[0{quantity" -> "items[0].quantity". */
        static String readablePath(String path) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < path.length(); i++) {
                char c = path.charAt(i);
                if (c == '{') {
                    if (!sb.isEmpty()) sb.append('.');
                } else if (c == '[') {
                    sb.append('[');
                    while (i + 1 < path.length() && Character.isDigit(path.charAt(i + 1))) sb.append(path.charAt(++i));
                    sb.append(']');
                } else {
                    sb.append(c);
                }
            }
            String s = sb.toString();
            return s.endsWith(".") ? s.substring(0, s.length() - 1) : s;
        }
    }
}
