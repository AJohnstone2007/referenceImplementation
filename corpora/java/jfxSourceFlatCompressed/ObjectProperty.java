package javafx.beans.property;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableObjectValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
public abstract class ObjectProperty<T> extends ReadOnlyObjectProperty<T>
implements Property<T>, WritableObjectValue<T> {
public ObjectProperty() {
}
@Override
public void setValue(T v) {
set(v);
}
@Override
public void bindBidirectional(Property<T> other) {
Bindings.bindBidirectional(this, other);
}
@Override
public void unbindBidirectional(Property<T> other) {
Bindings.unbindBidirectional(this, other);
}
@Override
public String toString() {
final Object bean = getBean();
final String name = getName();
final StringBuilder result = new StringBuilder(
"ObjectProperty [");
if (bean != null) {
result.append("bean: ").append(bean).append(", ");
}
if ((name != null) && (!name.equals(""))) {
result.append("name: ").append(name).append(", ");
}
result.append("value: ").append(get()).append("]");
return result.toString();
}
}
