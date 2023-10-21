package javafx.css;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ObservableValue;
public abstract class StyleableObjectProperty<T>
extends ObjectPropertyBase<T> implements StyleableProperty<T> {
public StyleableObjectProperty() {
super();
}
public StyleableObjectProperty(T initialValue) {
super(initialValue);
}
@Override
public void applyStyle(StyleOrigin origin, T v) {
set(v);
this.origin = origin;
}
@Override
public void bind(ObservableValue<? extends T> observable) {
super.bind(observable);
origin = StyleOrigin.USER;
}
@Override
public void set(T v) {
super.set(v);
origin = StyleOrigin.USER;
}
@Override
public StyleOrigin getStyleOrigin() { return origin; }
private StyleOrigin origin = null;
}
