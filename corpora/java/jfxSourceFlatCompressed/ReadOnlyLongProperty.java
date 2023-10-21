package javafx.beans.property;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.LongExpression;
public abstract class ReadOnlyLongProperty extends LongExpression implements
ReadOnlyProperty<Number> {
public ReadOnlyLongProperty() {
}
@Override
public String toString() {
final Object bean = getBean();
final String name = getName();
final StringBuilder result = new StringBuilder("ReadOnlyLongProperty [");
if (bean != null) {
result.append("bean: ").append(bean).append(", ");
}
if ((name != null) && !name.equals("")) {
result.append("name: ").append(name).append(", ");
}
result.append("value: ").append(get()).append("]");
return result.toString();
}
public static <T extends Number> ReadOnlyLongProperty readOnlyLongProperty(final ReadOnlyProperty<T> property) {
if (property == null) {
throw new NullPointerException("Property cannot be null");
}
return property instanceof ReadOnlyLongProperty ? (ReadOnlyLongProperty) property:
new ReadOnlyLongPropertyBase() {
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
public long get() {
valid = true;
final T value = property.getValue();
return value == null ? 0L : value.longValue();
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
public ReadOnlyObjectProperty<Long> asObject() {
return new ReadOnlyObjectPropertyBase<Long>() {
private boolean valid = true;
private final InvalidationListener listener = observable -> {
if (valid) {
valid = false;
fireValueChangedEvent();
}
};
{
ReadOnlyLongProperty.this.addListener(new WeakInvalidationListener(listener));
}
@Override
public Object getBean() {
return null;
}
@Override
public String getName() {
return ReadOnlyLongProperty.this.getName();
}
@Override
public Long get() {
valid = true;
return ReadOnlyLongProperty.this.getValue();
}
};
};
}
