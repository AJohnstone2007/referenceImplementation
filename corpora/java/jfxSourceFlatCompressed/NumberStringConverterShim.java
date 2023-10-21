package javafx.util.converter;
import java.text.NumberFormat;
import java.util.Locale;
public class NumberStringConverterShim {
public static NumberFormat getNumberFormatVar(NumberStringConverter nsc) {
return nsc.numberFormat;
}
public static NumberFormat getNumberFormat(NumberStringConverter nsc) {
return nsc.getNumberFormat();
}
public static String getPattern(NumberStringConverter nsc) {
return nsc.pattern;
}
public static Locale getLocale(NumberStringConverter nsc) {
return nsc.locale;
}
}
