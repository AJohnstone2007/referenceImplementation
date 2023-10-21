package javafx.util.converter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateTimeStringConverter.LdtConverter;
public class LocalTimeStringConverter extends StringConverter<LocalTime> {
LdtConverter<LocalTime> ldtConverter;
public LocalTimeStringConverter() {
ldtConverter = new LdtConverter<LocalTime>(LocalTime.class, null, null,
null, null, null, null);
}
public LocalTimeStringConverter(FormatStyle timeStyle) {
ldtConverter = new LdtConverter<LocalTime>(LocalTime.class, null, null,
null, timeStyle, null, null);
}
public LocalTimeStringConverter(FormatStyle timeStyle, Locale locale) {
ldtConverter = new LdtConverter<LocalTime>(LocalTime.class, null, null,
null, timeStyle, locale, null);
}
public LocalTimeStringConverter(DateTimeFormatter formatter, DateTimeFormatter parser) {
ldtConverter = new LdtConverter<LocalTime>(LocalTime.class, formatter, parser,
null, null, null, null);
}
@Override public LocalTime fromString(String value) {
return ldtConverter.fromString(value);
}
@Override public String toString(LocalTime value) {
return ldtConverter.toString(value);
}
}
