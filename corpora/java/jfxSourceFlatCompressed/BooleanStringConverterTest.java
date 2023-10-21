package test.javafx.util.converter;
import java.math.BigInteger;
import javafx.util.converter.BooleanStringConverter;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
public class BooleanStringConverterTest {
private BooleanStringConverter converter;
@Before public void setup() {
converter = new BooleanStringConverter();
}
@Test public void fromString_testValidStringInput_lowercase_true() {
assertEquals(Boolean.TRUE, converter.fromString("true"));
}
@Test public void fromString_testValidStringInput_uppercase_true() {
assertEquals(Boolean.TRUE, converter.fromString("TRUE"));
}
@Test public void fromString_testValidStringInput_mixedCase_true() {
assertEquals(Boolean.TRUE, converter.fromString("tRUe"));
}
@Test public void fromString_testValidStringInput_lowercase_false() {
assertEquals(Boolean.FALSE, converter.fromString("false"));
}
@Test public void fromString_testValidStringInput_uppercase_false() {
assertEquals(Boolean.FALSE, converter.fromString("FALSE"));
}
@Test public void fromString_testValidStringInput_mixedCase_false() {
assertEquals(Boolean.FALSE, converter.fromString("fALsE"));
}
@Test public void fromString_testValidStringInputWithWhiteSpace_true() {
assertEquals(Boolean.TRUE, converter.fromString("      true      "));
}
@Test public void fromString_testValidStringInputWithWhiteSpace_false() {
assertEquals(Boolean.FALSE, converter.fromString("     false      "));
}
@Test public void toString_true() {
assertEquals("true", converter.toString(true));
}
@Test public void toString_false() {
assertEquals("false", converter.toString(false));
}
}
