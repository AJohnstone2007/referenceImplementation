package test.javafx.util.converter;
import javafx.util.converter.ByteStringConverter;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
public class ByteStringConverterTest {
private ByteStringConverter converter;
private final byte byteValue_10 = 10;
@Before public void setup() {
converter = new ByteStringConverter();
}
@Test public void fromString_testValidStringInput() {
assertEquals((Object)byteValue_10, converter.fromString("10"));
}
@Test public void fromString_testValidStringInputWithWhiteSpace() {
assertEquals((Object)byteValue_10, converter.fromString("     10     "));
}
@Test public void toString_testStringInput() {
assertEquals("10", converter.toString(byteValue_10));
}
}
