package test.javafx.util.converter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import javafx.util.converter.LocalTimeStringConverterShim;
import javafx.util.converter.CurrencyStringConverter;
import javafx.util.converter.NumberStringConverterShim;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
public class CurrencyStringConverterTest {
private CurrencyStringConverter converter;
@Before public void setup() {
converter = new CurrencyStringConverter(Locale.US);
}
@Test public void testDefaultConstructor() {
CurrencyStringConverter c = new CurrencyStringConverter();
assertEquals(Locale.getDefault(), NumberStringConverterShim.getLocale(c));
assertNull(NumberStringConverterShim.getPattern(c));
assertNull(NumberStringConverterShim.getNumberFormatVar(c));
}
@Test public void testConstructor_locale() {
CurrencyStringConverter c = new CurrencyStringConverter(Locale.CANADA);
assertEquals(Locale.CANADA, NumberStringConverterShim.getLocale(c));
assertNull(NumberStringConverterShim.getPattern(c));
assertNull(NumberStringConverterShim.getNumberFormatVar(c));
}
@Test public void testConstructor_pattern() {
CurrencyStringConverter c = new CurrencyStringConverter("#,##,###,####");
assertEquals(Locale.getDefault(), NumberStringConverterShim.getLocale(c));
assertEquals("#,##,###,####", NumberStringConverterShim.getPattern(c));
assertNull(NumberStringConverterShim.getNumberFormatVar(c));
}
@Test public void testConstructor_locale_pattern() {
CurrencyStringConverter c = new CurrencyStringConverter(Locale.CANADA, "#,##,###,####");
assertEquals(Locale.CANADA, NumberStringConverterShim.getLocale(c));
assertEquals("#,##,###,####", NumberStringConverterShim.getPattern(c));
assertNull(NumberStringConverterShim.getNumberFormatVar(c));
}
@Test public void testConstructor_numberFormat() {
NumberFormat format = NumberFormat.getCurrencyInstance(Locale.JAPAN);
CurrencyStringConverter c = new CurrencyStringConverter(format);
assertNull(NumberStringConverterShim.getLocale(c));
assertNull(NumberStringConverterShim.getPattern(c));
assertEquals(format, NumberStringConverterShim.getNumberFormatVar(c));
}
@Test public void getNumberFormat_default() {
assertNotNull(NumberStringConverterShim.getNumberFormat(converter));
}
@Test public void getNumberFormat_nonNullPattern() {
converter = new CurrencyStringConverter("#,##,###,####");
assertTrue(
NumberStringConverterShim.getNumberFormat(converter)
instanceof DecimalFormat);
}
@Test public void getNumberFormat_nonNullNumberFormat() {
NumberFormat nf = NumberFormat.getCurrencyInstance();
converter = new CurrencyStringConverter(nf);
assertEquals(nf, NumberStringConverterShim.getNumberFormat(converter));
}
@Test public void fromString_testValidStringInput() {
assertEquals(10.32, converter.fromString("$10.32"));
}
@Test public void fromString_testValidStringInputWithWhiteSpace() {
assertEquals(10.32, converter.fromString("      $10.32      "));
}
@Test public void toString_validInput() {
assertEquals("$10.32", converter.toString(10.32));
}
}
