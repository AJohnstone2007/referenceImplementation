package javafx.css;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.ObservableValue;
public abstract class StyleableStringProperty
extends StringPropertyBase implements StyleableProperty<String> {
public StyleableStringProperty() {
super();
}
public StyleableStringProperty(String initialValue) {
super(initialValue);
}
@Override
public void applyStyle(StyleOrigin origin, String v) {
set(v);
this.origin = origin;
}
@Override
public void bind(ObservableValue<? extends String> observable) {
super.bind(observable);
origin = StyleOrigin.USER;
}
@Override
public void set(String v) {
super.set(v);
origin = StyleOrigin.USER;
}
@Override
public StyleOrigin getStyleOrigin() { return origin; }
private StyleOrigin origin = null;
}
