package javafx.css;
import javafx.scene.text.Font;
public class ParsedValue<V, T> {
final protected V value;
public final V getValue() { return value; }
final protected StyleConverter<V, T> converter;
public final StyleConverter<V, T> getConverter() { return converter; }
@SuppressWarnings("unchecked")
public T convert(Font font) {
return (T)((converter != null) ? converter.convert(this, font) : value);
}
public boolean isContainsLookups() { return false; }
public boolean isLookup() { return false; }
protected ParsedValue(V value, StyleConverter<V, T> converter) {
this.value = value;
this.converter = converter;
}
}
