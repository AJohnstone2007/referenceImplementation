package javafx.scene.text;
public enum FontPosture {
REGULAR("", "regular"),
ITALIC("italic");
private final String[] names;
private FontPosture(String... names) {
this.names = names;
}
public static FontPosture findByName(String name) {
if (name == null) return null;
for (FontPosture s : FontPosture.values()) {
for (String n : s.names) {
if (n.equalsIgnoreCase(name)) return s;
}
}
return null;
}
}
