package javafx.util.converter;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.chrono.Chronology;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DecimalStyle;
import java.time.format.FormatStyle;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import javafx.util.StringConverter;
import com.sun.javafx.binding.Logging;
public class LocalDateTimeStringConverter extends StringConverter<LocalDateTime> {
LdtConverter<LocalDateTime> ldtConverter;
public LocalDateTimeStringConverter() {
ldtConverter = new LdtConverter<LocalDateTime>(LocalDateTime.class, null, null,
null, null, null, null);
}
public LocalDateTimeStringConverter(FormatStyle dateStyle, FormatStyle timeStyle) {
ldtConverter = new LdtConverter<LocalDateTime>(LocalDateTime.class, null, null,
dateStyle, timeStyle, null, null);
}
public LocalDateTimeStringConverter(DateTimeFormatter formatter, DateTimeFormatter parser) {
ldtConverter = new LdtConverter<LocalDateTime>(LocalDateTime.class, formatter, parser,
null, null, null, null);
}
public LocalDateTimeStringConverter(FormatStyle dateStyle, FormatStyle timeStyle,
Locale locale, Chronology chronology) {
ldtConverter = new LdtConverter<LocalDateTime>(LocalDateTime.class, null, null,
dateStyle, timeStyle, locale, chronology);
}
@Override public LocalDateTime fromString(String value) {
return ldtConverter.fromString(value);
}
@Override public String toString(LocalDateTime value) {
return ldtConverter.toString(value);
}
static class LdtConverter<T extends Temporal> extends StringConverter<T> {
private Class<T> type;
Locale locale;
Chronology chronology;
DateTimeFormatter formatter;
DateTimeFormatter parser;
FormatStyle dateStyle;
FormatStyle timeStyle;
LdtConverter(Class<T> type, DateTimeFormatter formatter, DateTimeFormatter parser,
FormatStyle dateStyle, FormatStyle timeStyle, Locale locale, Chronology chronology) {
this.type = type;
this.formatter = formatter;
this.parser = (parser != null) ? parser : formatter;
this.locale = (locale != null) ? locale : Locale.getDefault(Locale.Category.FORMAT);
this.chronology = (chronology != null) ? chronology : IsoChronology.INSTANCE;
if (type == LocalDate.class || type == LocalDateTime.class) {
this.dateStyle = (dateStyle != null) ? dateStyle : FormatStyle.SHORT;
}
if (type == LocalTime.class || type == LocalDateTime.class) {
this.timeStyle = (timeStyle != null) ? timeStyle : FormatStyle.SHORT;
}
}
@SuppressWarnings({"unchecked"})
@Override public T fromString(String text) {
if (text == null || text.isEmpty()) {
return null;
}
text = text.trim();
if (parser == null) {
parser = getDefaultParser();
}
TemporalAccessor temporal = parser.parse(text);
if (type == LocalDate.class) {
return (T) LocalDate.from(temporal);
} else if (type == LocalTime.class) {
return (T) LocalTime.from(temporal);
} else {
return (T) LocalDateTime.from(temporal);
}
}
@Override public String toString(T value) {
if (value == null) {
return "";
}
if (formatter == null) {
formatter = getDefaultFormatter();
}
return formatter.format(value);
}
private DateTimeFormatter getDefaultParser() {
String pattern =
DateTimeFormatterBuilder.getLocalizedDateTimePattern(dateStyle, timeStyle,
chronology, locale);
return new DateTimeFormatterBuilder().parseLenient()
.appendPattern(pattern)
.toFormatter()
.withChronology(chronology)
.withDecimalStyle(DecimalStyle.of(locale));
}
private DateTimeFormatter getDefaultFormatter() {
DateTimeFormatter formatter;
if (dateStyle != null && timeStyle != null) {
formatter = DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle);
} else if (dateStyle != null) {
formatter = DateTimeFormatter.ofLocalizedDate(dateStyle);
} else {
formatter = DateTimeFormatter.ofLocalizedTime(timeStyle);
}
formatter = formatter.withLocale(locale)
.withChronology(chronology)
.withDecimalStyle(DecimalStyle.of(locale));
if (dateStyle != null) {
formatter = fixFourDigitYear(formatter, dateStyle, timeStyle,
chronology, locale);
}
return formatter;
}
private DateTimeFormatter fixFourDigitYear(DateTimeFormatter formatter,
FormatStyle dateStyle, FormatStyle timeStyle,
Chronology chronology, Locale locale) {
String pattern =
DateTimeFormatterBuilder.getLocalizedDateTimePattern(dateStyle, timeStyle,
chronology, locale);
if (pattern.contains("yy") && !pattern.contains("yyy")) {
String newPattern = pattern.replace("yy", "yyyy");
formatter = DateTimeFormatter.ofPattern(newPattern)
.withDecimalStyle(DecimalStyle.of(locale));
}
return formatter;
}
}
}
