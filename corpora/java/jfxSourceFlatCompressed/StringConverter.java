package javafx.css.converter;
import com.sun.javafx.util.Utils;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;
public final class StringConverter extends StyleConverter<String, String> {
private static class Holder {
static final StringConverter INSTANCE = new StringConverter();
static final SequenceConverter SEQUENCE_INSTANCE = new SequenceConverter();
}
public static StyleConverter<String, String> getInstance() {
return Holder.INSTANCE;
}
private StringConverter() {
super();
}
@Override
public String convert(ParsedValue<String, String> value, Font font) {
String string = value.getValue();
if (string == null) {
return null;
}
return Utils.convertUnicode(string);
}
@Override
public String toString() {
return "StringConverter";
}
public static final class SequenceConverter extends StyleConverter<ParsedValue<String, String>[], String[]> {
public static SequenceConverter getInstance() {
return Holder.SEQUENCE_INSTANCE;
}
private SequenceConverter() {
super();
}
@Override
public String[] convert(ParsedValue<ParsedValue<String, String>[], String[]> value, Font font) {
ParsedValue<String, String>[] layers = value.getValue();
String[] strings = new String[layers.length];
for (int layer = 0; layer < layers.length; layer++) {
strings[layer] = StringConverter.getInstance().convert(layers[layer], font);
}
return strings;
}
@Override
public String toString() {
return "String.SequenceConverter";
}
}
}
