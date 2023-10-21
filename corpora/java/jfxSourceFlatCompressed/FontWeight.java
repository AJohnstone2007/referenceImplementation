package javafx.scene.text;
public enum FontWeight {
THIN(100, "Thin"),
EXTRA_LIGHT(200, "Extra Light", "Ultra Light"),
LIGHT(300, "Light"),
NORMAL(400, "Normal", "Regular"),
MEDIUM(500, "Medium"),
SEMI_BOLD(600, "Semi Bold", "Demi Bold"),
BOLD(700, "Bold"),
EXTRA_BOLD(800, "Extra Bold", "Ultra Bold"),
BLACK(900, "Black", "Heavy");
private final int weight;
private final String[] names;
private FontWeight(int weight, String... names) {
this.weight = weight;
this.names = names;
}
public int getWeight() {
return weight;
}
public static FontWeight findByName(String name) {
if (name == null) return null;
for (FontWeight w : FontWeight.values()) {
for (String n : w.names) {
if (n.equalsIgnoreCase(name)) return w;
}
}
return null;
}
public static FontWeight findByWeight(int weight) {
if (weight <= 150) {
return THIN;
} else if (weight <= 250) {
return EXTRA_LIGHT;
} else if (weight < 350) {
return LIGHT;
} else if (weight <= 450) {
return NORMAL;
} else if (weight <= 550) {
return MEDIUM;
} else if (weight < 650) {
return SEMI_BOLD;
} else if (weight <= 750) {
return BOLD;
} else if (weight <= 850) {
return EXTRA_BOLD;
} else {
return BLACK;
}
}
}
