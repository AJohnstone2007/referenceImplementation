package javafx.util.converter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javafx.util.StringConverter;
public class DateTimeStringConverter extends StringConverter<Date> {
final Locale locale;
final String pattern;
final DateFormat dateFormat;
final int dateStyle;
final int timeStyle;
public DateTimeStringConverter() {
this(null, null, null, DateFormat.DEFAULT, DateFormat.DEFAULT);
}
public DateTimeStringConverter(int dateStyle, int timeStyle) {
this(null, null, null, dateStyle, timeStyle);
}
public DateTimeStringConverter(Locale locale) {
this(locale, null, null, DateFormat.DEFAULT, DateFormat.DEFAULT);
}
public DateTimeStringConverter(Locale locale, int dateStyle, int timeStyle) {
this(locale, null, null, dateStyle, timeStyle);
}
public DateTimeStringConverter(String pattern) {
this(null, pattern, null, DateFormat.DEFAULT, DateFormat.DEFAULT);
}
public DateTimeStringConverter(Locale locale, String pattern) {
this(locale, pattern, null, DateFormat.DEFAULT, DateFormat.DEFAULT);
}
public DateTimeStringConverter(DateFormat dateFormat) {
this(null, null, dateFormat, DateFormat.DEFAULT, DateFormat.DEFAULT);
}
DateTimeStringConverter(Locale locale, String pattern, DateFormat dateFormat,
int dateStyle, int timeStyle) {
this.locale = (locale != null) ? locale : Locale.getDefault(Locale.Category.FORMAT);
this.pattern = pattern;
this.dateFormat = dateFormat;
this.dateStyle = dateStyle;
this.timeStyle = timeStyle;
}
@Override public Date fromString(String value) {
try {
if (value == null) {
return (null);
}
value = value.trim();
if (value.length() < 1) {
return (null);
}
DateFormat parser = getDateFormat();
return parser.parse(value);
} catch (ParseException ex) {
throw new RuntimeException(ex);
}
}
@Override public String toString(Date value) {
if (value == null) {
return "";
}
DateFormat formatter = getDateFormat();
return formatter.format(value);
}
DateFormat getDateFormat() {
DateFormat df = null;
if (dateFormat != null) {
return dateFormat;
} else if (pattern != null) {
df = new SimpleDateFormat(pattern, locale);
} else {
df = DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
}
df.setLenient(false);
return df;
}
}
