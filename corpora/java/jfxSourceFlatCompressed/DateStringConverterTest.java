package test.javafx.util.converter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javafx.util.converter.LocalTimeStringConverterShim;
import javafx.util.converter.DateStringConverter;
import javafx.util.converter.DateTimeStringConverterShim;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
@RunWith(Parameterized.class)
public class DateStringConverterTest {
private static final Date VALID_DATE;
static {
TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
Calendar c = Calendar.getInstance();
c.set(Calendar.YEAR, 1985);
c.set(Calendar.MONTH, Calendar.JANUARY);
c.set(Calendar.DAY_OF_MONTH, 12);
c.set(Calendar.HOUR_OF_DAY, 0);
c.set(Calendar.MINUTE, 0);
c.set(Calendar.SECOND, 0);
c.set(Calendar.MILLISECOND, 0);
VALID_DATE = c.getTime();
}
@Parameterized.Parameters public static Collection implementations() {
return Arrays.asList(new Object[][] {
{ new DateStringConverter(),
Locale.getDefault(Locale.Category.FORMAT), DateFormat.DEFAULT,
VALID_DATE, null, null },
{ new DateStringConverter(DateFormat.SHORT),
Locale.getDefault(Locale.Category.FORMAT), DateFormat.SHORT,
VALID_DATE, null, null },
{ new DateStringConverter(Locale.UK),
Locale.UK, DateFormat.DEFAULT,
VALID_DATE, null, null },
{ new DateStringConverter(Locale.UK, DateFormat.SHORT),
Locale.UK, DateFormat.SHORT,
VALID_DATE, null, null },
{ new DateStringConverter("dd MM yyyy"),
Locale.getDefault(Locale.Category.FORMAT), DateFormat.DEFAULT,
VALID_DATE, "dd MM yyyy", null },
{ new DateStringConverter(DateFormat.getDateInstance(DateFormat.LONG)),
Locale.getDefault(Locale.Category.FORMAT), DateFormat.DEFAULT,
VALID_DATE, null, DateFormat.getDateInstance(DateFormat.LONG) },
});
}
private DateStringConverter converter;
private Locale locale;
private int dateStyle;
private String pattern;
private DateFormat dateFormat;
private Date validDate;
private DateFormat validFormatter;
public DateStringConverterTest(DateStringConverter converter, Locale locale, int dateStyle, Date validDate, String pattern, DateFormat dateFormat) {
this.converter = converter;
this.locale = locale;
this.dateStyle = dateStyle;
this.validDate = validDate;
this.pattern = pattern;
this.dateFormat = dateFormat;
if (dateFormat != null) {
validFormatter = dateFormat;
} else if (pattern != null) {
validFormatter = new SimpleDateFormat(pattern);
} else {
validFormatter = DateFormat.getDateInstance(dateStyle, locale);
}
}
@Before public void setup() {
}
@Test public void testConstructor() {
assertEquals(locale, DateTimeStringConverterShim.getLocale(converter));
assertEquals(dateStyle, DateTimeStringConverterShim.getDateStyle(converter));
assertEquals(pattern, DateTimeStringConverterShim.getPattern(converter));
assertEquals(dateFormat, DateTimeStringConverterShim.getDateFormatVar(converter));
}
@Test public void getDateFormat() {
assertNotNull(DateTimeStringConverterShim.getDateFormat(converter));
}
@Test public void getDateFormat_nonNullPattern() {
converter = new DateStringConverter("yyyy");
assertTrue(
DateTimeStringConverterShim.getDateFormat(converter)
instanceof SimpleDateFormat);
}
@Test public void fromString_testValidInput() {
String input = validFormatter.format(validDate);
assertEquals("Input = "+input, validDate, converter.fromString(input));
}
@Test public void fromString_testValidInputWithWhiteSpace() {
String input = validFormatter.format(validDate);
assertEquals("Input = "+input, validDate, converter.fromString("      " + input + "      "));
}
@Test(expected=RuntimeException.class)
public void fromString_testInvalidInput() {
converter.fromString("abcdefg");
}
@Test public void toString_validOutput() {
assertEquals(validFormatter.format(validDate), converter.toString(validDate));
}
}
