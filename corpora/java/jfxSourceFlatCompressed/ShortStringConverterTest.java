package test.javafx.util.converter;
import javafx.util.converter.ShortStringConverter;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
public class ShortStringConverterTest {
private ShortStringConverter converter;
private final short shortValue_10 = 10;
@Before public void setup() {
converter = new ShortStringConverter();
}
@Test public void fromString_testValidStringInput() {
assertEquals((Object)shortValue_10, converter.fromString("10"));
}
@Test public void fromString_testValidStringInputWithWhiteSpace() {
assertEquals((Object)shortValue_10, converter.fromString("     10     "));
}
@Test public void toString_testStringInput() {
assertEquals("10", converter.toString(shortValue_10));
}
}
