package javafx.util.converter;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
public class LocalDateTimeStringConverterShim {
public static DateTimeFormatter getldtConverterParser(LocalDateTimeStringConverter c) {
return c.ldtConverter.parser;
}
public static DateTimeFormatter getldtConverterFormatter(LocalDateTimeStringConverter c) {
return c.ldtConverter.formatter;
}
public static Locale getldtConverterLocale(LocalDateTimeStringConverter c) {
return c.ldtConverter.locale;
}
public static FormatStyle getldtConverterTimeStyle(LocalDateTimeStringConverter c) {
return c.ldtConverter.timeStyle;
}
public static FormatStyle getldtConverterDateStyle(LocalDateTimeStringConverter c) {
return c.ldtConverter.dateStyle;
}
}
