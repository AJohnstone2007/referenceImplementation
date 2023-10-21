package javafx.util.converter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javafx.util.StringConverter;
public class NumberStringConverter extends StringConverter<Number> {
final Locale locale;
final String pattern;
final NumberFormat numberFormat;
public NumberStringConverter() {
this(Locale.getDefault());
}
public NumberStringConverter(Locale locale) {
this(locale, null);
}
public NumberStringConverter(String pattern) {
this(Locale.getDefault(), pattern);
}
public NumberStringConverter(Locale locale, String pattern) {
this(locale, pattern, null);
}
public NumberStringConverter(NumberFormat numberFormat) {
this(null, null, numberFormat);
}
NumberStringConverter(Locale locale, String pattern, NumberFormat numberFormat) {
this.locale = locale;
this.pattern = pattern;
this.numberFormat = numberFormat;
}
@Override public Number fromString(String value) {
try {
if (value == null) {
return null;
}
value = value.trim();
if (value.length() < 1) {
return null;
}
NumberFormat parser = getNumberFormat();
return parser.parse(value);
} catch (ParseException ex) {
throw new RuntimeException(ex);
}
}
@Override public String toString(Number value) {
if (value == null) {
return "";
}
NumberFormat formatter = getNumberFormat();
return formatter.format(value);
}
protected NumberFormat getNumberFormat() {
Locale _locale = locale == null ? Locale.getDefault() : locale;
if (numberFormat != null) {
return numberFormat;
} else if (pattern != null) {
DecimalFormatSymbols symbols = new DecimalFormatSymbols(_locale);
return new DecimalFormat(pattern, symbols);
} else {
return NumberFormat.getNumberInstance(_locale);
}
}
}
