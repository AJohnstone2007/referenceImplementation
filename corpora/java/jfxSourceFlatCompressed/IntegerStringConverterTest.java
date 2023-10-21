package test.javafx.util.converter;
import javafx.util.converter.IntegerStringConverter;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
public class IntegerStringConverterTest {
private IntegerStringConverter converter;
@Before public void setup() {
converter = new IntegerStringConverter();
}
@Test public void fromString_testValidStringInput() {
assertEquals((Integer) 10, converter.fromString("10"));
}
@Test public void fromString_testValidStringInputWithWhiteSpace() {
assertEquals((Integer) 10, converter.fromString("      10      "));
}
@Test public void toString_validInput() {
assertEquals("10", converter.toString(10));
}
}
