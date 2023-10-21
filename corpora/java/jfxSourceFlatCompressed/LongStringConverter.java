package javafx.util.converter;
import javafx.util.StringConverter;
public class LongStringConverter extends StringConverter<Long> {
public LongStringConverter() {
}
@Override public Long fromString(String value) {
if (value == null) {
return null;
}
value = value.trim();
if (value.length() < 1) {
return null;
}
return Long.valueOf(value);
}
@Override public String toString(Long value) {
if (value == null) {
return "";
}
return Long.toString(((Long)value).longValue());
}
}
