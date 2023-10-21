package test.javafx.util.converter;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.chrono.IsoChronology;
import java.time.chrono.JapaneseChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import static org.junit.Assert.*;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;
import javafx.util.converter.LocalDateTimeStringConverterShim;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
@RunWith(Parameterized.class)
public class LocalDateTimeStringConverterTest {
private static final String JAPANESE_DATE_STRING = "Saturday, January 12, 60 Sh\u00C5\u008Dwa, 12:34:56 PM";
private static final LocalDateTime VALID_LDT_WITH_SECONDS = LocalDateTime.of(1985, 1, 12, 12, 34, 56);
private static final LocalDateTime VALID_LDT_WITHOUT_SECONDS = LocalDateTime.of(1985, 1, 12, 12, 34, 0);
private static final DateTimeFormatter aFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH mm ss");
private static final DateTimeFormatter aParser = DateTimeFormatter.ofPattern("yyyy MM dd hh mm ss a");
@Parameterized.Parameters public static Collection implementations() {
Locale.setDefault(Locale.US);
return Arrays.asList(new Object[][] {
{ new LocalDateTimeStringConverter(),
Locale.getDefault(Locale.Category.FORMAT), FormatStyle.SHORT, FormatStyle.SHORT,
VALID_LDT_WITHOUT_SECONDS, null, null },
{ new LocalDateTimeStringConverter(aFormatter, aParser),
Locale.getDefault(Locale.Category.FORMAT), null, null,
VALID_LDT_WITH_SECONDS, aFormatter, aParser },
{ new LocalDateTimeStringConverter(FormatStyle.SHORT, FormatStyle.SHORT, Locale.UK, IsoChronology.INSTANCE),
Locale.UK, FormatStyle.SHORT, FormatStyle.SHORT,
VALID_LDT_WITHOUT_SECONDS, null, null },
});
}
private LocalDateTimeStringConverter converter;
private Locale locale;
private FormatStyle dateStyle;
private FormatStyle timeStyle;
private DateTimeFormatter formatter, parser;
private LocalDateTime validDateTime;
public LocalDateTimeStringConverterTest(LocalDateTimeStringConverter converter, Locale locale, FormatStyle dateStyle, FormatStyle timeStyle, LocalDateTime validDateTime, DateTimeFormatter formatter, DateTimeFormatter parser) {
this.converter = converter;
this.locale = locale;
this.dateStyle = dateStyle;
this.timeStyle = timeStyle;
this.validDateTime = validDateTime;
this.formatter = formatter;
this.parser = parser;
}
@Test public void testConstructor() {
assertEquals(locale,
LocalDateTimeStringConverterShim.getldtConverterLocale(converter));
assertEquals((dateStyle != null) ? dateStyle : FormatStyle.SHORT,
LocalDateTimeStringConverterShim.getldtConverterDateStyle(converter));
assertEquals((timeStyle != null) ? timeStyle : FormatStyle.SHORT,
LocalDateTimeStringConverterShim.getldtConverterTimeStyle(converter));
if (formatter != null) {
assertEquals(formatter,
LocalDateTimeStringConverterShim.getldtConverterFormatter(converter));
}
if (parser != null) {
assertEquals(parser,
LocalDateTimeStringConverterShim.getldtConverterParser(converter));
} else if (formatter != null) {
assertEquals(formatter,
LocalDateTimeStringConverterShim.getldtConverterFormatter(converter));
}
}
@Test public void toString_to_fromString_testRoundtrip() {
if (formatter == null) {
assertEquals(validDateTime, converter.fromString(converter.toString(validDateTime)));
}
}
@Test(expected=RuntimeException.class)
public void fromString_testInvalidInput() {
converter.fromString("abcdefg");
}
@Test public void converter_with_specified_formatter_and_parser() {
String formatPattern = "dd MMMM yyyy, HH:mm:ss";
String parsePattern = "MMMM dd, yyyy, HH:mm:ss";
DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
DateTimeFormatter parser = DateTimeFormatter.ofPattern(parsePattern);
StringConverter<LocalDateTime> converter = new LocalDateTimeStringConverter(formatter, parser);
assertEquals("12 January 1985, 12:34:56", converter.toString(VALID_LDT_WITH_SECONDS));
assertEquals(VALID_LDT_WITH_SECONDS, converter.fromString("January 12, 1985, 12:34:56"));
}
@Test public void converter_with_specified_formatter_and_null_parser() {
String pattern = "dd MMMM yyyy, HH:mm:ss";
DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
StringConverter<LocalDateTime> converter = new LocalDateTimeStringConverter(formatter, null);
assertEquals("12 January 1985, 12:34:56", converter.toString(VALID_LDT_WITH_SECONDS));
assertEquals(VALID_LDT_WITH_SECONDS, converter.fromString("12 January 1985, 12:34:56"));
}
@Test
public void testChronologyConsistency() {
var converter = new LocalDateTimeStringConverter(FormatStyle.FULL, FormatStyle.MEDIUM, null, JapaneseChronology.INSTANCE);
assertEquals(JAPANESE_DATE_STRING, converter.toString(VALID_LDT_WITH_SECONDS));
try {
converter.toString(LocalDateTime.of(1, 1, 1, 1, 1, 1));
} catch (DateTimeException e) {}
assertEquals(VALID_LDT_WITH_SECONDS, converter.fromString(JAPANESE_DATE_STRING));
}
}
