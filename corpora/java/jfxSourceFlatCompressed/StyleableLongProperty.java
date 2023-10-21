package javafx.css;
import javafx.beans.property.LongPropertyBase;
import javafx.beans.value.ObservableValue;
public abstract class StyleableLongProperty
extends LongPropertyBase implements StyleableProperty<Number> {
public StyleableLongProperty() {
super();
}
public StyleableLongProperty(long initialValue) {
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
public void set(long v) {
super.set(v);
origin = StyleOrigin.USER;
}
@Override
public StyleOrigin getStyleOrigin() { return origin; }
private StyleOrigin origin = null;
}
