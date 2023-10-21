package javafx.util.converter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.util.StringConverter;
public class CurrencyStringConverter extends NumberStringConverter {
public CurrencyStringConverter() {
this(Locale.getDefault());
}
public CurrencyStringConverter(Locale locale) {
this(locale, null);
}
public CurrencyStringConverter(String pattern) {
this(Locale.getDefault(), pattern);
}
public CurrencyStringConverter(Locale locale, String pattern) {
super(locale, pattern, null);
}
public CurrencyStringConverter(NumberFormat numberFormat) {
super(null, null, numberFormat);
}
@Override protected NumberFormat getNumberFormat() {
Locale _locale = locale == null ? Locale.getDefault() : locale;
if (numberFormat != null) {
return numberFormat;
} else if (pattern != null) {
DecimalFormatSymbols symbols = new DecimalFormatSymbols(_locale);
return new DecimalFormat(pattern, symbols);
} else {
return NumberFormat.getCurrencyInstance(_locale);
}
}
}
