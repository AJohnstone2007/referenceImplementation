package javafx.css.converter;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;
public final class BooleanConverter extends StyleConverter<String, Boolean> {
private static class Holder {
static final BooleanConverter INSTANCE = new BooleanConverter();
}
public static StyleConverter<String, Boolean> getInstance() {
return Holder.INSTANCE;
}
private BooleanConverter() {
super();
}
@Override
public Boolean convert(ParsedValue<String, Boolean> value, Font not_used) {
String str = value.getValue();
return Boolean.valueOf(str);
}
@Override
public String toString() {
return "BooleanConverter";
}
}
