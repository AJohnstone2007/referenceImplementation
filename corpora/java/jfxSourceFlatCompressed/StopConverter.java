package javafx.css.converter;
import javafx.css.Size;
import javafx.css.StyleConverter;
import javafx.css.ParsedValue;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
public final class StopConverter extends StyleConverter<ParsedValue[], Stop> {
private static class Holder {
static final StopConverter INSTANCE = new StopConverter();
}
public static StopConverter getInstance() {
return Holder.INSTANCE;
}
private StopConverter() {
super();
}
@Override
public Stop convert(ParsedValue<ParsedValue[], Stop> value, Font font) {
ParsedValue[] values = value.getValue();
final Double offset = ((Size) values[0].convert(font)).pixels(font);
final Color color = (Color) values[1].convert(font);
return new Stop(offset, color);
}
@Override
public String toString() {
return "StopConverter";
}
}
