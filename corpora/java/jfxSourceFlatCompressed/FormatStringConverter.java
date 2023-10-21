package javafx.util.converter;
import java.text.*;
import javafx.beans.NamedArg;
import javafx.util.StringConverter;
public class FormatStringConverter<T> extends StringConverter<T> {
final Format format;
public FormatStringConverter(@NamedArg("format") Format format) {
this.format = format;
}
@Override public T fromString(String value) {
if (value == null) {
return null;
}
value = value.trim();
if (value.length() < 1) {
return null;
}
Format _format = getFormat();
final ParsePosition pos = new ParsePosition(0);
T result = (T) _format.parseObject(value, pos);
if (pos.getIndex() != value.length()) {
throw new RuntimeException("Parsed string not according to the format");
}
return result;
}
@Override public String toString(T value) {
if (value == null) {
return "";
}
Format _format = getFormat();
return _format.format(value);
}
protected Format getFormat() {
return format;
}
}
