package javafx.util.converter;
import javafx.util.StringConverter;
public class ByteStringConverter extends StringConverter<Byte> {
public ByteStringConverter() {
}
@Override public Byte fromString(String value) {
if (value == null) {
return null;
}
value = value.trim();
if (value.length() < 1) {
return null;
}
return Byte.valueOf(value);
}
@Override public String toString(Byte value) {
if (value == null) {
return "";
}
return Byte.toString(value.byteValue());
}
}
