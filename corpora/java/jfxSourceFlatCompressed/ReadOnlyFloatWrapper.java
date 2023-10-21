package javafx.beans.property;
public class ReadOnlyFloatWrapper extends SimpleFloatProperty {
private ReadOnlyPropertyImpl readOnlyProperty;
public ReadOnlyFloatWrapper() {
}
public ReadOnlyFloatWrapper(float initialValue) {
super(initialValue);
}
public ReadOnlyFloatWrapper(Object bean, String name) {
super(bean, name);
}
public ReadOnlyFloatWrapper(Object bean, String name, float initialValue) {
super(bean, name, initialValue);
}
public ReadOnlyFloatProperty getReadOnlyProperty() {
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
private class ReadOnlyPropertyImpl extends ReadOnlyFloatPropertyBase {
@Override
public float get() {
return ReadOnlyFloatWrapper.this.get();
}
@Override
public Object getBean() {
return ReadOnlyFloatWrapper.this.getBean();
}
@Override
public String getName() {
return ReadOnlyFloatWrapper.this.getName();
}
};
}
