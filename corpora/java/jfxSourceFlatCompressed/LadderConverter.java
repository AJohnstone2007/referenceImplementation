package javafx.css.converter;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
public final class LadderConverter extends StyleConverter<ParsedValue[], Color> {
private static class Holder {
static final LadderConverter INSTANCE = new LadderConverter();
}
public static LadderConverter getInstance() {
return Holder.INSTANCE;
}
private LadderConverter() {
super();
}
@Override
public Color convert(ParsedValue<ParsedValue[], Color> value, Font font) {
final ParsedValue[] values = value.getValue();
final Color color = (Color) values[0].convert(font);
Stop[] stops = new Stop[values.length - 1];
for (int v = 1; v < values.length; v++) {
stops[v - 1] = (Stop) values[v].convert(font);
}
return com.sun.javafx.util.Utils.ladder(color, stops);
}
@Override
public String toString() {
return "LadderConverter";
}
}
