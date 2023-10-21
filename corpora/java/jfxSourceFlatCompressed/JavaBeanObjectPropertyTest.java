package test.javafx.beans.property.adapter;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import java.beans.PropertyVetoException;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanProperty;
import static org.junit.Assert.assertEquals;
public class JavaBeanObjectPropertyTest extends JavaBeanPropertyTestBase<Object> {
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
protected Property<Object> createObservable(Object value) {
return new SimpleObjectProperty<Object>(value);
}
@Override
protected JavaBeanProperty<Object> extractProperty(Object bean) throws NoSuchMethodException {
return JavaBeanObjectPropertyBuilder.create().bean(bean).name("x").build();
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
public void setX(Object x) {
if (failureMode) {
throw new RuntimeException("FailureMode activated");
} else {
this.x = x;
}
}
@Override
public Object getValue() {
return getX();
}
@Override
public void setValue(Object value) throws PropertyVetoException {
setX(value);
}
@Override
public void setFailureMode(boolean failureMode) {
this.failureMode = failureMode;
}
}
}
