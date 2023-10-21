package javafx.css.converter;
import javafx.css.Size;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;
public final class SizeConverter extends StyleConverter<ParsedValue<?, Size>, Number> {
private static class Holder {
static final SizeConverter INSTANCE = new SizeConverter();
static final SequenceConverter SEQUENCE_INSTANCE = new SequenceConverter();
}
public static StyleConverter<ParsedValue<?, Size>, Number> getInstance() {
return Holder.INSTANCE;
}
private SizeConverter() {
super();
}
@Override
public Number convert(ParsedValue<ParsedValue<?, Size>, Number> value, Font font) {
ParsedValue<?, Size> size = value.getValue();
return size.convert(font).pixels(font);
}
@Override
public String toString() {
return "SizeConverter";
}
public static final class SequenceConverter extends StyleConverter<ParsedValue[], Number[]> {
public static SequenceConverter getInstance() {
return Holder.SEQUENCE_INSTANCE;
}
private SequenceConverter() {
super();
}
@Override
public Number[] convert(ParsedValue<ParsedValue[], Number[]> value, Font font) {
ParsedValue[] sizes = value.getValue();
Number[] doubles = new Number[sizes.length];
for (int i = 0; i < sizes.length; i++) {
doubles[i] = ((Size)sizes[i].convert(font)).pixels(font);
}
return doubles;
}
@Override
public String toString() {
return "Size.SequenceConverter";
}
}
}
