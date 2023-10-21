package javafx.beans.property;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.MapExpression;
import javafx.collections.ObservableMap;
public abstract class ReadOnlyMapProperty<K, V> extends MapExpression<K, V> implements ReadOnlyProperty<ObservableMap<K, V>> {
public ReadOnlyMapProperty() {
}
public void bindContentBidirectional(ObservableMap<K, V> map) {
Bindings.bindContentBidirectional(this, map);
}
public void unbindContentBidirectional(Object object) {
Bindings.unbindContentBidirectional(this, object);
}
public void bindContent(ObservableMap<K, V> map) {
Bindings.bindContent(this, map);
}
public void unbindContent(Object object) {
Bindings.unbindContent(this, object);
}
@Override
public boolean equals(Object obj) {
if (obj == this)
return true;
if (!(obj instanceof Map))
return false;
Map<K,V> m = (Map<K,V>) obj;
if (m.size() != size())
return false;
try {
for (Entry<K,V> e : entrySet()) {
K key = e.getKey();
V value = e.getValue();
if (value == null) {
if (!(m.get(key)==null && m.containsKey(key)))
return false;
} else {
if (!value.equals(m.get(key)))
return false;
}
}
} catch (ClassCastException unused) {
return false;
} catch (NullPointerException unused) {
return false;
}
return true;
}
@Override
public int hashCode() {
int h = 0;
for (Entry<K,V> e : entrySet()) {
h += e.hashCode();
}
return h;
}
@Override
public String toString() {
final Object bean = getBean();
final String name = getName();
final StringBuilder result = new StringBuilder(
"ReadOnlyMapProperty [");
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
