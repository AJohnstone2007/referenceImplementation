package javafx.util.converter;
import java.math.BigDecimal;
import javafx.util.StringConverter;
public class BigDecimalStringConverter extends StringConverter<BigDecimal> {
public BigDecimalStringConverter() {
}
@Override public BigDecimal fromString(String value) {
if (value == null) {
return null;
}
value = value.trim();
if (value.length() < 1) {
return null;
}
return new BigDecimal(value);
}
@Override public String toString(BigDecimal value) {
if (value == null) {
return "";
}
return ((BigDecimal)value).toString();
}
}
