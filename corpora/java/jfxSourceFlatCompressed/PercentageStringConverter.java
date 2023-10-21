package javafx.util.converter;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.util.StringConverter;
public class PercentageStringConverter extends NumberStringConverter {
public PercentageStringConverter() {
this(Locale.getDefault());
}
public PercentageStringConverter(Locale locale) {
super(locale, null, null);
}
public PercentageStringConverter(NumberFormat numberFormat) {
super(null, null, numberFormat);
}
@Override public NumberFormat getNumberFormat() {
Locale _locale = locale == null ? Locale.getDefault() : locale;
if (numberFormat != null) {
return numberFormat;
} else {
return NumberFormat.getPercentInstance(_locale);
}
}
}
