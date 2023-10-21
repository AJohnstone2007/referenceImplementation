package javafx.util.converter;
import java.math.BigInteger;
import javafx.util.StringConverter;
public class BigIntegerStringConverter extends StringConverter<BigInteger> {
public BigIntegerStringConverter() {
}
@Override public BigInteger fromString(String value) {
if (value == null) {
return null;
}
value = value.trim();
if (value.length() < 1) {
return null;
}
return new BigInteger(value);
}
@Override public String toString(BigInteger value) {
if (value == null) {
return "";
}
return ((BigInteger)value).toString();
}
}
