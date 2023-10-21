package javafx.css.converter;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.css.StyleConverter;
import javafx.css.ParsedValue;
import javafx.scene.text.Font;
public final class DeriveSizeConverter extends StyleConverter<ParsedValue<Size, Size>[], Size> {
private static class Holder {
static final DeriveSizeConverter INSTANCE = new DeriveSizeConverter();
}
public static DeriveSizeConverter getInstance() {
return Holder.INSTANCE;
}
private DeriveSizeConverter() {
super();
}
@Override
public Size convert(ParsedValue<ParsedValue<Size, Size>[], Size> value, Font font) {
final ParsedValue<Size, Size>[] sizes = value.getValue();
final double px1 = sizes[0].convert(font).pixels(font);
final double px2 = sizes[1].convert(font).pixels(font);
return new Size(px1 + px2, SizeUnits.PX);
}
@Override
public String toString() {
return "DeriveSizeConverter";
}
}
