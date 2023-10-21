package javafx.beans.property;
import javafx.beans.binding.ObjectExpression;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
public abstract class ReadOnlyObjectProperty<T> extends ObjectExpression<T>
implements ReadOnlyProperty<T> {
public ReadOnlyObjectProperty() {
}
@Override
public String toString() {
final Object bean = getBean();
final String name = getName();
final StringBuilder result = new StringBuilder(
"ReadOnlyObjectProperty [");
if (bean != null) {
result.append("bean: ").append(bean).append(", ");
}
if ((name != null) && !name.equals("")) {
result.append("name: ").append(name).append(", ");
}
result.append("value: ").append(get()).append("]");
return result.toString();
}
}
