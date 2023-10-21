package javafx.util.converter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javafx.util.StringConverter;
public class TimeStringConverter extends DateTimeStringConverter {
public TimeStringConverter() {
this(null, null, null, DateFormat.DEFAULT);
}
public TimeStringConverter(int timeStyle) {
this(null, null, null, timeStyle);
}
public TimeStringConverter(Locale locale) {
this(locale, null, null, DateFormat.DEFAULT);
}
public TimeStringConverter(Locale locale, int timeStyle) {
this(locale, null, null, timeStyle);
}
public TimeStringConverter(String pattern) {
this(null, pattern, null, DateFormat.DEFAULT);
}
public TimeStringConverter(Locale locale, String pattern) {
this(locale, pattern, null, DateFormat.DEFAULT);
}
public TimeStringConverter(DateFormat dateFormat) {
this(null, null, dateFormat, DateFormat.DEFAULT);
}
private TimeStringConverter(Locale locale, String pattern, DateFormat dateFormat, int timeStyle) {
super(locale, pattern, dateFormat, DateFormat.DEFAULT, timeStyle);
}
@SuppressWarnings("removal")
@Override protected DateFormat getDateFormat() {
DateFormat df = null;
if (dateFormat != null) {
return dateFormat;
} else if (pattern != null) {
df = new SimpleDateFormat(pattern, locale);
} else {
df = DateFormat.getTimeInstance(timeStyle, locale);
}
df.setLenient(false);
return df;
}
}
