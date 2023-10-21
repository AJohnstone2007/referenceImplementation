package javafx.css.converter;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.Cursor;
import javafx.scene.text.Font;
public final class CursorConverter extends StyleConverter<String, Cursor> {
private static class Holder {
static final CursorConverter INSTANCE = new CursorConverter();
}
public static StyleConverter<String, Cursor> getInstance() {
return Holder.INSTANCE;
}
private CursorConverter() {
super();
}
@Override
public Cursor convert(ParsedValue<String, Cursor> value, Font not_used) {
String string = value.getValue();
if (string != null) {
int index = string.indexOf("Cursor.");
if (index > -1) {
string = string.substring(index+"Cursor.".length());
}
string = string.replace('-','_').toUpperCase();
}
try {
return Cursor.cursor(string);
} catch (IllegalArgumentException | NullPointerException exception) {
return Cursor.DEFAULT;
}
}
@Override
public String toString() {
return "CursorConverter";
}
}
