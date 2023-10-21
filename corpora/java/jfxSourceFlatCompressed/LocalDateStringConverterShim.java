package javafx.util.converter;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
public class LocalDateStringConverterShim {
public static DateTimeFormatter getldtConverterFormatter(LocalDateStringConverter c) {
return c.ldtConverter.formatter;
}
public static Locale getldtConverterLocale(LocalDateStringConverter c) {
return c.ldtConverter.locale;
}
public static DateTimeFormatter getldtConverterParser(LocalDateStringConverter c) {
return c.ldtConverter.parser;
}
public static FormatStyle getldtConverterTimeStyle(LocalDateStringConverter c) {
return c.ldtConverter.timeStyle;
}
public static FormatStyle getldtConverterDateStyle(LocalDateStringConverter c) {
return c.ldtConverter.dateStyle;
}
}
