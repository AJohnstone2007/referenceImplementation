package javafx.beans.property;
public class ReadOnlyObjectWrapper<T> extends SimpleObjectProperty<T> {
private ReadOnlyPropertyImpl readOnlyProperty;
public ReadOnlyObjectWrapper() {
}
public ReadOnlyObjectWrapper(T initialValue) {
super(initialValue);
}
public ReadOnlyObjectWrapper(Object bean, String name) {
super(bean, name);
}
public ReadOnlyObjectWrapper(Object bean, String name, T initialValue) {
super(bean, name, initialValue);
}
public ReadOnlyObjectProperty<T> getReadOnlyProperty() {
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
private class ReadOnlyPropertyImpl extends ReadOnlyObjectPropertyBase<T> {
@Override
public T get() {
return ReadOnlyObjectWrapper.this.get();
}
@Override
public Object getBean() {
return ReadOnlyObjectWrapper.this.getBean();
}
@Override
public String getName() {
return ReadOnlyObjectWrapper.this.getName();
}
};
}
