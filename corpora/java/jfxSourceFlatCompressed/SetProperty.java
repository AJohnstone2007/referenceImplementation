package javafx.beans.property;
import javafx.beans.binding.Bindings;
import javafx.beans.value.WritableListValue;
import javafx.beans.value.WritableSetValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
public abstract class SetProperty<E> extends ReadOnlySetProperty<E> implements
Property<ObservableSet<E>>, WritableSetValue<E> {
public SetProperty() {
}
@Override
public void setValue(ObservableSet<E> v) {
set(v);
}
@Override
public void bindBidirectional(Property<ObservableSet<E>> other) {
Bindings.bindBidirectional(this, other);
}
@Override
public void unbindBidirectional(Property<ObservableSet<E>> other) {
Bindings.unbindBidirectional(this, other);
}
@Override
public String toString() {
final Object bean = getBean();
final String name = getName();
final StringBuilder result = new StringBuilder(
"SetProperty [");
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
