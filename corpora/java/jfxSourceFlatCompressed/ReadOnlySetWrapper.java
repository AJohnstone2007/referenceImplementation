package javafx.beans.property;
import javafx.collections.ObservableSet;
import static javafx.collections.SetChangeListener.Change;
public class ReadOnlySetWrapper<E> extends SimpleSetProperty<E> {
private ReadOnlyPropertyImpl readOnlyProperty;
public ReadOnlySetWrapper() {
}
public ReadOnlySetWrapper(ObservableSet<E> initialValue) {
super(initialValue);
}
public ReadOnlySetWrapper(Object bean, String name) {
super(bean, name);
}
public ReadOnlySetWrapper(Object bean, String name,
ObservableSet<E> initialValue) {
super(bean, name, initialValue);
}
public ReadOnlySetProperty<E> getReadOnlyProperty() {
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
protected void fireValueChangedEvent(Change<? extends E> change) {
super.fireValueChangedEvent(change);
if (readOnlyProperty != null) {
readOnlyProperty.fireValueChangedEvent(change);
}
}
private class ReadOnlyPropertyImpl extends ReadOnlySetPropertyBase<E> {
@Override
public ObservableSet<E> get() {
return ReadOnlySetWrapper.this.get();
}
@Override
public Object getBean() {
return ReadOnlySetWrapper.this.getBean();
}
@Override
public String getName() {
return ReadOnlySetWrapper.this.getName();
}
@Override
public ReadOnlyIntegerProperty sizeProperty() {
return ReadOnlySetWrapper.this.sizeProperty();
}
@Override
public ReadOnlyBooleanProperty emptyProperty() {
return ReadOnlySetWrapper.this.emptyProperty();
}
}
}
