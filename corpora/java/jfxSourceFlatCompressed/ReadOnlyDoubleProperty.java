package javafx.beans.property;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.DoubleExpression;
public abstract class ReadOnlyDoubleProperty extends DoubleExpression implements
ReadOnlyProperty<Number> {
public ReadOnlyDoubleProperty() {
}
@Override
public String toString() {
final Object bean = getBean();
final String name = getName();
final StringBuilder result = new StringBuilder(
"ReadOnlyDoubleProperty [");
if (bean != null) {
result.append("bean: ").append(bean).append(", ");
}
if ((name != null) && !name.equals("")) {
result.append("name: ").append(name).append(", ");
}
result.append("value: ").append(get()).append("]");
return result.toString();
}
public static <T extends Number> ReadOnlyDoubleProperty readOnlyDoubleProperty(final ReadOnlyProperty<T> property) {
if (property == null) {
throw new NullPointerException("Property cannot be null");
}
return property instanceof ReadOnlyDoubleProperty ? (ReadOnlyDoubleProperty) property:
new ReadOnlyDoublePropertyBase() {
private boolean valid = true;
private final InvalidationListener listener = observable -> {
if (valid) {
valid = false;
fireValueChangedEvent();
}
};
{
property.addListener(new WeakInvalidationListener(listener));
}
@Override
public double get() {
valid = true;
final T value = property.getValue();
return value == null ? 0.0 : value.doubleValue();
}
@Override
public Object getBean() {
return null;
}
@Override
public String getName() {
return property.getName();
}
};
}
@Override
public ReadOnlyObjectProperty<Double> asObject() {
return new ReadOnlyObjectPropertyBase<Double>() {
private boolean valid = true;
private final InvalidationListener listener = observable -> {
if (valid) {
valid = false;
fireValueChangedEvent();
}
};
{
ReadOnlyDoubleProperty.this.addListener(new WeakInvalidationListener(listener));
}
@Override
public Object getBean() {
return null;
}
@Override
public String getName() {
return ReadOnlyDoubleProperty.this.getName();
}
@Override
public Double get() {
valid = true;
return ReadOnlyDoubleProperty.this.getValue();
}
};
};
}
