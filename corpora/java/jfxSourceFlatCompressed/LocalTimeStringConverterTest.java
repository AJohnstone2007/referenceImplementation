package test.javafx.util.converter;
import java.time.LocalTime;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import javafx.util.StringConverter;
import javafx.util.converter.LocalTimeStringConverterShim;
import javafx.util.converter.LocalTimeStringConverter;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
@RunWith(Parameterized.class)
public class LocalTimeStringConverterTest {
private static final LocalTime VALID_TIME_WITH_SECONDS;
private static final LocalTime VALID_TIME_WITHOUT_SECONDS;
static {
VALID_TIME_WITH_SECONDS = LocalTime.of(12, 34, 56);
VALID_TIME_WITHOUT_SECONDS = LocalTime.of(12, 34, 0);
}
private static final DateTimeFormatter aFormatter = DateTimeFormatter.ofPattern("HH mm ss");
private static final DateTimeFormatter aParser = DateTimeFormatter.ofPattern("hh mm ss a");
@Parameterized.Parameters public static Collection implementations() {
return Arrays.asList(new Object[][] {
{ new LocalTimeStringConverter(),
Locale.getDefault(Locale.Category.FORMAT), FormatStyle.SHORT,
VALID_TIME_WITHOUT_SECONDS, null, null },
{ new LocalTimeStringConverter(aFormatter, aParser),
Locale.getDefault(Locale.Category.FORMAT), null,
VALID_TIME_WITH_SECONDS, aFormatter, aParser },
{ new LocalTimeStringConverter(FormatStyle.SHORT, Locale.UK),
Locale.UK, FormatStyle.SHORT,
VALID_TIME_WITHOUT_SECONDS, null, null },
});
}
private LocalTimeStringConverter converter;
private Locale locale;
private FormatStyle timeStyle;
private DateTimeFormatter formatter, parser;
private LocalTime validTime;
public LocalTimeStringConverterTest(LocalTimeStringConverter converter, Locale locale, FormatStyle timeStyle, LocalTime validTime, DateTimeFormatter formatter, DateTimeFormatter parser) {
this.converter = converter;
this.locale = locale;
this.timeStyle = timeStyle;
this.validTime = validTime;
this.formatter = formatter;
this.parser = parser;
}
@Before public void setup() {
}
@Test public void testConstructor() {
assertEquals(locale,
LocalTimeStringConverterShim.getldtConverterLocale(converter));
assertNull(LocalTimeStringConverterShim.getldtConverterDateStyle(converter));
assertEquals((timeStyle != null) ? timeStyle : FormatStyle.SHORT,
LocalTimeStringConverterShim.getldtConverterTimeStyle(converter));
if (formatter != null) {
assertEquals(formatter,
LocalTimeStringConverterShim.getldtConverterFormatter(converter));
}
if (parser != null) {
assertEquals(parser,
LocalTimeStringConverterShim.getldtConverterParser(converter));
} else if (formatter != null) {
assertEquals(formatter,
LocalTimeStringConverterShim.getldtConverterFormatter(converter));
}
}
@Test public void toString_to_fromString_testRoundtrip() {
if (formatter == null) {
assertEquals(validTime, converter.fromString(converter.toString(validTime)));
}
}
@Test(expected=RuntimeException.class)
public void fromString_testInvalidInput() {
converter.fromString("abcdefg");
}
}
