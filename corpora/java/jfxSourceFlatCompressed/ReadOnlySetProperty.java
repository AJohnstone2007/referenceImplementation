package javafx.beans.property;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.SetExpression;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
public abstract class ReadOnlySetProperty<E> extends SetExpression<E> implements ReadOnlyProperty<ObservableSet<E>> {
public ReadOnlySetProperty() {
}
public void bindContentBidirectional(ObservableSet<E> set) {
Bindings.bindContentBidirectional(this, set);
}
public void unbindContentBidirectional(Object object) {
Bindings.unbindContentBidirectional(this, object);
}
public void bindContent(ObservableSet<E> set) {
Bindings.bindContent(this, set);
}
public void unbindContent(Object object) {
Bindings.unbindContent(this, object);
}
@Override
public boolean equals(Object obj) {
if (obj == this)
return true;
if (!(obj instanceof Set))
return false;
Set c = (Set) obj;
if (c.size() != size())
return false;
try {
return containsAll(c);
} catch (ClassCastException unused) {
return false;
} catch (NullPointerException unused) {
return false;
}
}
@Override
public int hashCode() {
int h = 0;
for (E e : this) {
if (e != null)
h += e.hashCode();
}
return h;
}
@Override
public String toString() {
final Object bean = getBean();
final String name = getName();
final StringBuilder result = new StringBuilder(
"ReadOnlySetProperty [");
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
