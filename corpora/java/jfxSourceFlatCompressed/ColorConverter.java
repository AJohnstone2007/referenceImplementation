package javafx.css.converter;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
public final class ColorConverter extends StyleConverter<String, Color> {
private static class Holder {
static final ColorConverter COLOR_INSTANCE = new ColorConverter();
}
public static StyleConverter<String, Color> getInstance() {
return Holder.COLOR_INSTANCE;
}
private ColorConverter() {
super();
}
@Override
public Color convert(ParsedValue<String, Color> value, Font font) {
Object val = value.getValue();
if (val == null) {
return null;
}
if (val instanceof Color) {
return (Color)val;
}
if (val instanceof String) {
String str = (String)val;
if (str.isEmpty() || "null".equals(str)) {
return null;
}
try {
return Color.web((String)val);
} catch (IllegalArgumentException iae) {
}
}
System.err.println("not a color: " + value);
return Color.BLACK;
}
@Override
public String toString() {
return "ColorConverter";
}
}
