package javafx.util.converter;
import java.time.LocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateTimeStringConverter.LdtConverter;
public class LocalDateStringConverter extends StringConverter<LocalDate> {
LdtConverter<LocalDate> ldtConverter;
public LocalDateStringConverter() {
ldtConverter = new LdtConverter<LocalDate>(LocalDate.class, null, null,
null, null, null, null);
}
public LocalDateStringConverter(FormatStyle dateStyle) {
ldtConverter = new LdtConverter<LocalDate>(LocalDate.class, null, null,
dateStyle, null, null, null);
}
public LocalDateStringConverter(DateTimeFormatter formatter, DateTimeFormatter parser) {
ldtConverter = new LdtConverter<LocalDate>(LocalDate.class, formatter, parser,
null, null, null, null);
}
public LocalDateStringConverter(FormatStyle dateStyle, Locale locale, Chronology chronology) {
ldtConverter = new LdtConverter<LocalDate>(LocalDate.class, null, null,
dateStyle, null, locale, chronology);
}
@Override public LocalDate fromString(String value) {
return ldtConverter.fromString(value);
}
@Override public String toString(LocalDate value) {
return ldtConverter.toString(value);
}
}
