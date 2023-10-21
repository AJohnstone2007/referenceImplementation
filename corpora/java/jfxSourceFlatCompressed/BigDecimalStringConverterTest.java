package test.javafx.util.converter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import javafx.util.StringConverter;
import javafx.util.converter.BigDecimalStringConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
public class BigDecimalStringConverterTest {
private BigDecimalStringConverter converter;
private final BigDecimal bigDecimal = new BigDecimal(BigInteger.TEN);
@Before public void setup() {
converter = new BigDecimalStringConverter();
}
@Test public void fromString_testValidStringInput() {
assertEquals(bigDecimal, converter.fromString("10"));
}
@Test public void fromString_testValidStringInputWithWhiteSpace() {
assertEquals(bigDecimal, converter.fromString("      10      "));
}
@Test public void toString_testStringInput() {
assertEquals("10", converter.toString(bigDecimal));
}
}
