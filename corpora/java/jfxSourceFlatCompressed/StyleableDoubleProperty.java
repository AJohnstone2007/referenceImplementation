package javafx.css;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.value.ObservableValue;
public abstract class StyleableDoubleProperty
extends DoublePropertyBase implements StyleableProperty<Number> {
public StyleableDoubleProperty() {
super();
}
public StyleableDoubleProperty(double initialValue) {
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
public void set(double v) {
super.set(v);
origin = StyleOrigin.USER;
}
@Override
public StyleOrigin getStyleOrigin() { return origin; }
private StyleOrigin origin = null;
}
