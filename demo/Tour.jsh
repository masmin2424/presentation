// JShell tour - run with:  jshell --add-modules jdk.incubator.json Tour.jsh
// Each line runs and you stay in JShell afterwards to keep exploring.

import jdk.incubator.json.*

var v = Json.parse("""
    {"team": "core", "members": ["Ana", "Bo"], "size": 2, "budget": null}
    """)

v.get("members").asList()
v.get("members").get(0).asString()
v.get("size").asInt()

// Optional values
v.tryGet("missing")
v.tryGet("size").map(JsonValue::asInt).orElse(0)

// JSON null: tryValue() gives an empty Optional
v.get("budget").tryValue()

// Pretty print
System.out.println(Json.toDisplayString(v, "  "))

// No silent coercion: this THROWS (a string is not an int)
v.get("team").asInt()

// Strict RFC 8259: trailing comma is rejected
Json.parse("{\"a\": 1,}")

System.out.println("\nYour turn - try Json.parse(...) yourself. /exit to quit.")
