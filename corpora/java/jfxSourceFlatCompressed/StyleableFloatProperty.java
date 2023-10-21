package javafx.css;
import javafx.beans.property.FloatPropertyBase;
import javafx.beans.value.ObservableValue;
public abstract class StyleableFloatProperty
extends FloatPropertyBase implements StyleableProperty<Number> {
public StyleableFloatProperty() {
super();
}
public StyleableFloatProperty(float initialValue) {
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
public void set(float v) {
super.set(v);
origin = StyleOrigin.USER;
}
@Override
public StyleOrigin getStyleOrigin() { return origin; }
private StyleOrigin origin = null;
}
