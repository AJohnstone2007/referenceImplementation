package javafx.util.converter;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
public class LocalTimeStringConverterShim {
public static Locale getldtConverterLocale(LocalTimeStringConverter c) {
return c.ldtConverter.locale;
}
public static FormatStyle getldtConverterDateStyle(LocalTimeStringConverter c) {
return c.ldtConverter.dateStyle;
}
public static FormatStyle getldtConverterTimeStyle(LocalTimeStringConverter c) {
return c.ldtConverter.timeStyle;
}
public static DateTimeFormatter getldtConverterParser(LocalTimeStringConverter c) {
return c.ldtConverter.parser;
}
public static DateTimeFormatter getldtConverterFormatter(LocalTimeStringConverter c) {
return c.ldtConverter.formatter;
}
}
