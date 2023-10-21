package javafx.beans.property;
public class ReadOnlyIntegerWrapper extends SimpleIntegerProperty {
private ReadOnlyPropertyImpl readOnlyProperty;
public ReadOnlyIntegerWrapper() {
}
public ReadOnlyIntegerWrapper(int initialValue) {
super(initialValue);
}
public ReadOnlyIntegerWrapper(Object bean, String name) {
super(bean, name);
}
public ReadOnlyIntegerWrapper(Object bean, String name, int initialValue) {
super(bean, name, initialValue);
}
public ReadOnlyIntegerProperty getReadOnlyProperty() {
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
private class ReadOnlyPropertyImpl extends ReadOnlyIntegerPropertyBase {
@Override
public int get() {
return ReadOnlyIntegerWrapper.this.get();
}
@Override
public Object getBean() {
return ReadOnlyIntegerWrapper.this.getBean();
}
@Override
public String getName() {
return ReadOnlyIntegerWrapper.this.getName();
}
};
}
