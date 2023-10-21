package javafx.css;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.value.ObservableValue;
public abstract class StyleableBooleanProperty
extends BooleanPropertyBase implements StyleableProperty<Boolean> {
public StyleableBooleanProperty() {
super();
}
public StyleableBooleanProperty(boolean initialValue) {
super(initialValue);
}
@Override
public void applyStyle(StyleOrigin origin, Boolean v) {
set(v.booleanValue());
this.origin = origin;
}
@Override
public void bind(ObservableValue<? extends Boolean> observable) {
super.bind(observable);
origin = StyleOrigin.USER;
}
@Override
public void set(boolean v) {
super.set(v);
origin = StyleOrigin.USER;
}
@Override
public StyleOrigin getStyleOrigin() { return origin; }
private StyleOrigin origin = null;
}
