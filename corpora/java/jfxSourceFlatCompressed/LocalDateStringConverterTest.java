package test.javafx.util.converter;
import java.util.Arrays;
import java.time.LocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.Locale;
import javafx.util.StringConverter;
import javafx.util.converter.LocalTimeStringConverterShim;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LocalDateStringConverterShim;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
@RunWith(Parameterized.class)
public class LocalDateStringConverterTest {
private static final LocalDate VALID_DATE = LocalDate.of(1985, 1, 12);
private static final DateTimeFormatter aFormatter = DateTimeFormatter.ofPattern("dd MM yyyy");
private static final DateTimeFormatter aParser = DateTimeFormatter.ofPattern("yyyy MM dd");
@Parameterized.Parameters public static Collection implementations() {
return Arrays.asList(new Object[][] {
{ new LocalDateStringConverter(),
Locale.getDefault(Locale.Category.FORMAT), FormatStyle.SHORT,
VALID_DATE, null, null },
{ new LocalDateStringConverter(aFormatter, aParser),
Locale.getDefault(Locale.Category.FORMAT), null,
VALID_DATE, aFormatter, aParser },
{ new LocalDateStringConverter(FormatStyle.SHORT, Locale.UK, IsoChronology.INSTANCE),
Locale.UK, FormatStyle.SHORT,
VALID_DATE, null, null },
});
}
private LocalDateStringConverter converter;
private Locale locale;
private FormatStyle dateStyle;
private DateTimeFormatter formatter, parser;
private LocalDate validDate;
public LocalDateStringConverterTest(LocalDateStringConverter converter, Locale locale, FormatStyle dateStyle, LocalDate validDate, DateTimeFormatter formatter, DateTimeFormatter parser) {
this.converter = converter;
this.locale = locale;
this.dateStyle = dateStyle;
this.validDate = validDate;
this.formatter = formatter;
this.parser = parser;
}
@Before public void setup() {
}
@Test public void testConstructor() {
assertEquals(locale, LocalDateStringConverterShim.getldtConverterLocale(converter));
assertEquals((dateStyle != null) ? dateStyle : FormatStyle.SHORT,
LocalDateStringConverterShim.getldtConverterDateStyle(converter));
assertNull(LocalDateStringConverterShim.getldtConverterTimeStyle(converter));
if (formatter != null) {
assertEquals(formatter,
LocalDateStringConverterShim.getldtConverterFormatter(converter));
}
if (parser != null) {
assertEquals(parser,
LocalDateStringConverterShim.getldtConverterParser(converter));
} else if (formatter != null) {
assertEquals(formatter,
LocalDateStringConverterShim.getldtConverterParser(converter));
}
}
@Test public void toString_to_fromString_testRoundtrip() {
if (formatter == null) {
assertEquals(validDate, converter.fromString(converter.toString(validDate)));
}
}
@Test(expected=RuntimeException.class)
public void fromString_testInvalidInput() {
converter.fromString("abcdefg");
}
}
