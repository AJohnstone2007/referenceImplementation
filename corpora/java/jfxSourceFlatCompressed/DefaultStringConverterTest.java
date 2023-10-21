package test.javafx.util.converter;
import java.util.Arrays;
import java.util.Collection;
import javafx.util.converter.DefaultStringConverter;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
public class DefaultStringConverterTest {
private DefaultStringConverter converter;
@Before public void setup() {
converter = new DefaultStringConverter();
}
@Test public void fromString_testValidStringInput() {
assertEquals("string", converter.fromString("string"));
}
@Test public void fromString_testEmptytringInput() {
assertEquals("", converter.fromString(""));
}
@Test public void toString_testValidStringInput() {
assertEquals("string", converter.toString("string"));
}
@Test public void toString_testEmptyStringInput() {
assertEquals("", converter.toString(""));
}
}
