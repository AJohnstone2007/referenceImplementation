package javafx.util.converter;
import java.text.DateFormat;
import java.util.Locale;
public class DateTimeStringConverterShim {
@SuppressWarnings("removal")
public static int getTimeStyle(DateTimeStringConverter tsc) {
return tsc.timeStyle;
}
@SuppressWarnings("removal")
public static String getPattern(DateTimeStringConverter tsc) {
return tsc.pattern;
}
@SuppressWarnings("removal")
public static int getDateStyle(DateTimeStringConverter tsc) {
return tsc.dateStyle;
}
@SuppressWarnings("removal")
public static DateFormat getDateFormat(DateTimeStringConverter tsc) {
return tsc.getDateFormat();
}
@SuppressWarnings("removal")
public static DateFormat getDateFormatVar(DateTimeStringConverter tsc) {
return tsc.dateFormat;
}
@SuppressWarnings("removal")
public static Locale getLocale(DateTimeStringConverter tsc) {
return tsc.locale;
}
}
