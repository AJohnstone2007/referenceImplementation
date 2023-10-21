package javafx.util.converter;
import javafx.util.StringConverter;
public class ShortStringConverter extends StringConverter<Short> {
public ShortStringConverter() {
}
@Override public Short fromString(String text) {
if (text == null) {
return null;
}
text = text.trim();
if (text.length() < 1) {
return null;
}
return Short.valueOf(text);
}
@Override public String toString(Short value) {
if (value == null) {
return "";
}
return Short.toString(((Short)value).shortValue());
}
}
