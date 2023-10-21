package test.javafx.util.converter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.util.converter.LocalTimeStringConverterShim;
import javafx.util.converter.NumberStringConverter;
import javafx.util.converter.NumberStringConverterShim;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
public class NumberStringConverterTest {
private NumberStringConverter converter;
@Before public void setup() {
converter = new NumberStringConverter();
}
@Test public void testDefaultConstructor() {
NumberStringConverter c = new NumberStringConverter();
assertEquals(Locale.getDefault(), NumberStringConverterShim.getLocale(c));
assertNull(NumberStringConverterShim.getPattern(c));
assertNull(NumberStringConverterShim.getNumberFormatVar(c));
}
@Test public void testConstructor_locale() {
NumberStringConverter c = new NumberStringConverter(Locale.CANADA);
assertEquals(Locale.CANADA, NumberStringConverterShim.getLocale(c));
assertNull(NumberStringConverterShim.getPattern(c));
assertNull(NumberStringConverterShim.getNumberFormatVar(c));
}
@Test public void testConstructor_pattern() {
NumberStringConverter c = new NumberStringConverter("#,##,###,####");
assertEquals(Locale.getDefault(), NumberStringConverterShim.getLocale(c));
assertEquals("#,##,###,####", NumberStringConverterShim.getPattern(c));
assertNull(NumberStringConverterShim.getNumberFormatVar(c));
}
@Test public void testConstructor_locale_pattern() {
NumberStringConverter c = new NumberStringConverter(Locale.CANADA, "#,##,###,####");
assertEquals(Locale.CANADA, NumberStringConverterShim.getLocale(c));
assertEquals("#,##,###,####", NumberStringConverterShim.getPattern(c));
assertNull(NumberStringConverterShim.getNumberFormatVar(c));
}
@Test public void testConstructor_numberFormat() {
NumberFormat format = NumberFormat.getCurrencyInstance(Locale.JAPAN);
NumberStringConverter c = new NumberStringConverter(format);
assertNull(NumberStringConverterShim.getLocale(c));
assertNull(NumberStringConverterShim.getPattern(c));
assertEquals(format, NumberStringConverterShim.getNumberFormatVar(c));
}
@Test public void getNumberFormat_default() {
assertNotNull(NumberStringConverterShim.getNumberFormat(converter));
}
@Test public void getNumberFormat_nonNullPattern() {
converter = new NumberStringConverter("#,##,###,####");
assertTrue(
NumberStringConverterShim.getNumberFormat(converter)
instanceof DecimalFormat);
}
@Test public void getNumberFormat_nonNullNumberFormat() {
NumberFormat nf = NumberFormat.getCurrencyInstance();
converter = new NumberStringConverter(nf);
assertEquals(nf, NumberStringConverterShim.getNumberFormat(converter));
}
@Test public void fromString_testValidInput() {
assertEquals(10L, converter.fromString("10"));
}
@Test public void fromString_testValidInputWithWhiteSpace() {
assertEquals(10L, converter.fromString("      10      "));
}
@Test(expected=RuntimeException.class)
public void fromString_testInvalidInput() {
converter.fromString("abcdefg");
}
@Test public void toString_validInput() {
assertEquals("10", converter.toString(10L));
}
}
