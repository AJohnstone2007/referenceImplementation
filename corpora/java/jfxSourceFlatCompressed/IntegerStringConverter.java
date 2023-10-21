package javafx.util.converter;
import javafx.util.StringConverter;
public class IntegerStringConverter extends StringConverter<Integer> {
public IntegerStringConverter() {
}
@Override public Integer fromString(String value) {
if (value == null) {
return null;
}
value = value.trim();
if (value.length() < 1) {
return null;
}
return Integer.valueOf(value);
}
@Override public String toString(Integer value) {
if (value == null) {
return "";
}
return (Integer.toString(((Integer)value).intValue()));
}
}
