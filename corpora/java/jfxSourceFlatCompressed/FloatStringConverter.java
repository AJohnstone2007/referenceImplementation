package javafx.util.converter;
import javafx.util.StringConverter;
public class FloatStringConverter extends StringConverter<Float> {
public FloatStringConverter() {
}
@Override public Float fromString(String value) {
if (value == null) {
return null;
}
value = value.trim();
if (value.length() < 1) {
return null;
}
return Float.valueOf(value);
}
@Override public String toString(Float value) {
if (value == null) {
return "";
}
return Float.toString(((Float)value).floatValue());
}
}
