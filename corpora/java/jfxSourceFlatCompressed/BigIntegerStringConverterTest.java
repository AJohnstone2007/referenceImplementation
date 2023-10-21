package test.javafx.util.converter;
import java.math.BigInteger;
import javafx.util.converter.BigIntegerStringConverter;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
public class BigIntegerStringConverterTest {
private BigIntegerStringConverter converter;
@Before public void setup() {
converter = new BigIntegerStringConverter();
}
@Test public void fromString_testValidStringInput() {
assertEquals(BigInteger.TEN, converter.fromString("10"));
}
@Test public void fromString_testValidStringInputWithWhiteSpace() {
assertEquals(BigInteger.TEN, converter.fromString("      10      "));
}
@Test public void toString_testStringInput() {
assertEquals("10", converter.toString(BigInteger.TEN));
}
}
