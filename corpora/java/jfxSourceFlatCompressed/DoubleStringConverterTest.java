package test.javafx.util.converter;
import javafx.util.converter.DoubleStringConverter;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
public class DoubleStringConverterTest {
private DoubleStringConverter converter;
@Before public void setup() {
converter = new DoubleStringConverter();
}
@Test public void fromString_testValidStringInput() {
assertEquals(Double.valueOf(10), converter.fromString("10"));
}
@Test public void fromString_testValidStringInputWithWhiteSpace() {
assertEquals(Double.valueOf(10), converter.fromString("      10      "));
}
@Test public void toString_validInput() {
assertEquals("10.0", converter.toString(10D));
}
}
