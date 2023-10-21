package javafx.beans.property;
public class ReadOnlyBooleanWrapper extends SimpleBooleanProperty {
private ReadOnlyPropertyImpl readOnlyProperty;
public ReadOnlyBooleanWrapper() {
}
public ReadOnlyBooleanWrapper(boolean initialValue) {
super(initialValue);
}
public ReadOnlyBooleanWrapper(Object bean, String name) {
super(bean, name);
}
public ReadOnlyBooleanWrapper(Object bean, String name,
boolean initialValue) {
super(bean, name, initialValue);
}
public ReadOnlyBooleanProperty getReadOnlyProperty() {
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
private class ReadOnlyPropertyImpl extends ReadOnlyBooleanPropertyBase {
@Override
public boolean get() {
return ReadOnlyBooleanWrapper.this.get();
}
@Override
public Object getBean() {
return ReadOnlyBooleanWrapper.this.getBean();
}
@Override
public String getName() {
return ReadOnlyBooleanWrapper.this.getName();
}
};
}
