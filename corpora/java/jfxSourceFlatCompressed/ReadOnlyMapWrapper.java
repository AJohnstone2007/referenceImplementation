package javafx.beans.property;
import javafx.collections.ObservableMap;
import static javafx.collections.MapChangeListener.Change;
public class ReadOnlyMapWrapper<K, V> extends SimpleMapProperty<K, V> {
private ReadOnlyPropertyImpl readOnlyProperty;
public ReadOnlyMapWrapper() {
}
public ReadOnlyMapWrapper(ObservableMap<K, V> initialValue) {
super(initialValue);
}
public ReadOnlyMapWrapper(Object bean, String name) {
super(bean, name);
}
public ReadOnlyMapWrapper(Object bean, String name,
ObservableMap<K, V> initialValue) {
super(bean, name, initialValue);
}
public ReadOnlyMapProperty<K, V> getReadOnlyProperty() {
if (readOnlyProperty == null) {
readOnlyProperty = new ReadOnlyPropertyImpl();
}
return readOnlyProperty;
}
@Override
protected void fireValueChangedEvent() {
super.fireValueChangedEvent();
if (readOnlyProperty != null) {
readOnlyProperty.fireValueChangedEvent();
}
}
@Override
protected void fireValueChangedEvent(Change<? extends K, ? extends V> change) {
super.fireValueChangedEvent(change);
if (readOnlyProperty != null) {
readOnlyProperty.fireValueChangedEvent(change);
}
}
private class ReadOnlyPropertyImpl extends ReadOnlyMapPropertyBase<K, V> {
@Override
public ObservableMap<K, V> get() {
return ReadOnlyMapWrapper.this.get();
}
@Override
public Object getBean() {
return ReadOnlyMapWrapper.this.getBean();
}
@Override
public String getName() {
return ReadOnlyMapWrapper.this.getName();
}
@Override
public ReadOnlyIntegerProperty sizeProperty() {
return ReadOnlyMapWrapper.this.sizeProperty();
}
@Override
public ReadOnlyBooleanProperty emptyProperty() {
return ReadOnlyMapWrapper.this.emptyProperty();
}
}
}
