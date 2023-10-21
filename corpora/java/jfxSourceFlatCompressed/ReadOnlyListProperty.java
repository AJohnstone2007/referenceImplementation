package javafx.beans.property;
import java.util.List;
import java.util.ListIterator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ListExpression;
import javafx.collections.ObservableList;
public abstract class ReadOnlyListProperty<E> extends ListExpression<E>
implements ReadOnlyProperty<ObservableList<E>> {
public ReadOnlyListProperty() {
}
public void bindContentBidirectional(ObservableList<E> list) {
Bindings.bindContentBidirectional(this, list);
}
public void unbindContentBidirectional(Object object) {
Bindings.unbindContentBidirectional(this, object);
}
public void bindContent(ObservableList<E> list) {
Bindings.bindContent(this, list);
}
public void unbindContent(Object object) {
Bindings.unbindContent(this, object);
}
@Override
public boolean equals(Object obj) {
if (this == obj) {
return true;
}
if (!(obj instanceof List)) {
return false;
}
final List list = (List)obj;
if (size() != list.size()) {
return false;
}
ListIterator<E> e1 = listIterator();
ListIterator e2 = list.listIterator();
while (e1.hasNext() && e2.hasNext()) {
E o1 = e1.next();
Object o2 = e2.next();
if (!(o1==null ? o2==null : o1.equals(o2)))
return false;
}
return true;
}
@Override
public int hashCode() {
int hashCode = 1;
for (E e : this)
hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
return hashCode;
}
@Override
public String toString() {
final Object bean = getBean();
final String name = getName();
final StringBuilder result = new StringBuilder(
"ReadOnlyListProperty [");
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
