package javafx.util.converter;
import javafx.util.StringConverter;
public class BooleanStringConverter extends StringConverter<Boolean> {
public BooleanStringConverter() {
}
@Override public Boolean fromString(String value) {
if (value == null) {
return null;
}
value = value.trim();
if (value.length() < 1) {
return null;
}
return Boolean.valueOf(value);
}
@Override public String toString(Boolean value) {
if (value == null) {
return "";
}
return value.toString();
}
}
