package javafx.util.converter;
import javafx.util.StringConverter;
public class DefaultStringConverter extends StringConverter<String> {
public DefaultStringConverter() {
}
@Override public String toString(String value) {
return (value != null) ? value : "";
}
@Override public String fromString(String value) {
return value;
}
}
