package javafx.beans.property;
public class ReadOnlyLongWrapper extends SimpleLongProperty {
private ReadOnlyPropertyImpl readOnlyProperty;
public ReadOnlyLongWrapper() {
}
public ReadOnlyLongWrapper(long initialValue) {
super(initialValue);
}
public ReadOnlyLongWrapper(Object bean, String name) {
super(bean, name);
}
public ReadOnlyLongWrapper(Object bean, String name, long initialValue) {
super(bean, name, initialValue);
}
public ReadOnlyLongProperty getReadOnlyProperty() {
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
private class ReadOnlyPropertyImpl extends ReadOnlyLongPropertyBase {
@Override
public long get() {
return ReadOnlyLongWrapper.this.get();
}
@Override
public Object getBean() {
return ReadOnlyLongWrapper.this.getBean();
}
@Override
public String getName() {
return ReadOnlyLongWrapper.this.getName();
}
};
}
