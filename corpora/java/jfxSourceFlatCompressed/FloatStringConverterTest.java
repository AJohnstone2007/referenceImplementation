package test.javafx.util.converter;
import javafx.util.converter.FloatStringConverter;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
public class FloatStringConverterTest {
private FloatStringConverter converter;
@Before public void setup() {
converter = new FloatStringConverter();
}
@Test public void fromString_testValidStringInput() {
assertEquals((Float)10.3521f, converter.fromString("10.3521"));
}
@Test public void fromString_testValidStringInputWithWhiteSpace() {
assertEquals((Float)10.3521f, converter.fromString("      10.3521     "));
}
@Test public void toString_validInput() {
assertEquals("10.3521", converter.toString(10.3521f));
}
}
