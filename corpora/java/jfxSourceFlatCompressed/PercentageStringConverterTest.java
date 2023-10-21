package test.javafx.util.converter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.util.converter.LocalTimeStringConverterShim;
import javafx.util.converter.NumberStringConverterShim;
import javafx.util.converter.PercentageStringConverter;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
public class PercentageStringConverterTest {
private PercentageStringConverter converter;
@Before public void setup() {
converter = new PercentageStringConverter(Locale.US);
}
@Test public void testDefaultConstructor() {
PercentageStringConverter c = new PercentageStringConverter();
assertEquals(Locale.getDefault(), NumberStringConverterShim.getLocale(c));
assertNull(NumberStringConverterShim.getPattern(c));
assertNull(NumberStringConverterShim.getNumberFormatVar(c));
}
@Test public void testConstructor_locale() {
PercentageStringConverter c = new PercentageStringConverter(Locale.CANADA);
assertEquals(Locale.CANADA, NumberStringConverterShim.getLocale(c));
assertNull(NumberStringConverterShim.getPattern(c));
assertNull(NumberStringConverterShim.getNumberFormatVar(c));
}
@Test public void testConstructor_numberFormat() {
NumberFormat format = NumberFormat.getCurrencyInstance(Locale.JAPAN);
PercentageStringConverter c = new PercentageStringConverter(format);
assertNull(NumberStringConverterShim.getLocale(c));
assertNull(NumberStringConverterShim.getPattern(c));
assertEquals(format, NumberStringConverterShim.getNumberFormatVar(c));
}
@Test public void getNumberFormat_default() {
assertNotNull(NumberStringConverterShim.getNumberFormat(converter));
}
@Test public void getNumberFormat_nonNullNumberFormat() {
NumberFormat nf = NumberFormat.getCurrencyInstance();
converter = new PercentageStringConverter(nf);
assertEquals(nf, NumberStringConverterShim.getNumberFormat(converter));
}
@Test public void fromString_testValidStringInput() {
assertEquals(.1032, converter.fromString("10.32%"));
}
@Test public void fromString_testValidStringInputWithWhiteSpace() {
assertEquals(.1032, converter.fromString("      10.32%      "));
}
@Test public void toString_validInput() {
assertEquals("10%", converter.toString(.10));
}
}
