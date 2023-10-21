package javafx.css.converter;
import javafx.css.Size;
import javafx.css.StyleConverter;
import javafx.css.ParsedValue;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
public final class DeriveColorConverter extends StyleConverter<ParsedValue[], Color> {
private static class Holder {
static final DeriveColorConverter INSTANCE = new DeriveColorConverter();
}
public static DeriveColorConverter getInstance() {
return Holder.INSTANCE;
}
private DeriveColorConverter() {
super();
}
@Override
public Color convert(ParsedValue<ParsedValue[], Color> value, Font font) {
ParsedValue[] values = value.getValue();
final Color color = (Color) values[0].convert(font);
final Size brightness = (Size) values[1].convert(font);
return com.sun.javafx.util.Utils.deriveColor(color, brightness.pixels(font));
}
@Override
public String toString() {
return "DeriveColorConverter";
}
}
