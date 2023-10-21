package javafx.util.converter;
import javafx.util.StringConverter;
public class DoubleStringConverter extends StringConverter<Double> {
public DoubleStringConverter() {
}
@Override public Double fromString(String value) {
if (value == null) {
return null;
}
value = value.trim();
if (value.length() < 1) {
return null;
}
return Double.valueOf(value);
}
@Override public String toString(Double value) {
if (value == null) {
return "";
}
return Double.toString(value.doubleValue());
}
}
