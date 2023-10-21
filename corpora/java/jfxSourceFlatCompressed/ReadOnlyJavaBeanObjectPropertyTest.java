package test.javafx.beans.property.adapter;
import java.beans.PropertyVetoException;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanProperty;
import static org.junit.Assert.assertEquals;
public class ReadOnlyJavaBeanObjectPropertyTest extends ReadOnlyJavaBeanPropertyTestBase<Object> {
private final static Object[] VALUES = new Object[] {new Object(), new Object()};
@Override
protected BeanStub<Object> createBean(Object initialValue) {
return new ObjectPOJO(initialValue);
}
@Override
protected void check(Object actual, Object expected) {
assertEquals(actual, expected);
}
@Override
protected Object getValue(int index) {
return VALUES[index];
}
@Override
protected ReadOnlyJavaBeanProperty<Object> extractProperty(Object bean) throws NoSuchMethodException {
return ReadOnlyJavaBeanObjectPropertyBuilder.<Object>create().bean(bean).name("x").build();
}
public class ObjectPOJO extends BeanStub<Object> {
private Object x;
private boolean failureMode;
public ObjectPOJO(Object x) {
this.x = x;
}
public Object getX() {
if (failureMode) {
throw new RuntimeException("FailureMode activated");
} else {
return x;
}
}
@Override
public Object getValue() {
return getX();
}
@Override
public void setValue(Object value) throws PropertyVetoException {
this.x = value;
}
@Override
public void setFailureMode(boolean failureMode) {
this.failureMode = failureMode;
}
}
}
