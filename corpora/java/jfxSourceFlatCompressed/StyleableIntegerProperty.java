package javafx.css;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.value.ObservableValue;
public abstract class StyleableIntegerProperty
extends IntegerPropertyBase implements StyleableProperty<Number> {
public StyleableIntegerProperty() {
super();
}
public StyleableIntegerProperty(int initialValue) {
super(initialValue);
}
@Override
public void applyStyle(StyleOrigin origin, Number v) {
setValue(v);
this.origin = origin;
}
@Override
public void bind(ObservableValue<? extends Number> observable) {
super.bind(observable);
origin = StyleOrigin.USER;
}
@Override
public void set(int v) {
super.set(v);
origin = StyleOrigin.USER;
}
@Override
public StyleOrigin getStyleOrigin() { return origin; }
private StyleOrigin origin = null;
}
