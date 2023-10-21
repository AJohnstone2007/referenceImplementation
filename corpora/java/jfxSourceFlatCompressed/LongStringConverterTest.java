package test.javafx.util.converter;
import javafx.util.converter.LongStringConverter;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
public class LongStringConverterTest {
private LongStringConverter converter;
@Before public void setup() {
converter = new LongStringConverter();
}
@Test public void fromString_testValidStringInput() {
assertEquals(Long.valueOf(10), converter.fromString("10"));
}
@Test public void fromString_testValidStringInputWithWhiteSpace() {
assertEquals(Long.valueOf(10), converter.fromString("      10      "));
}
@Test public void toString_validInput() {
assertEquals("10", converter.toString(10L));
}
}
