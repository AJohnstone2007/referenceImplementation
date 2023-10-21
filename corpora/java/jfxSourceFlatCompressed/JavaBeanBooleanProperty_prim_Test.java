package test.javafx.beans.property.adapter;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import java.beans.PropertyVetoException;
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanProperty;
import static org.junit.Assert.assertEquals;
public class JavaBeanBooleanProperty_prim_Test extends JavaBeanPropertyTestBase<Boolean> {
private final static Boolean[] VALUES = new Boolean[] {true, false};
@Override
protected BeanStub<Boolean> createBean(Boolean initialValue) {
return new BooleanPOJO(initialValue);
}
@Override
protected void check(Boolean actual, Boolean expected) {
assertEquals(actual, expected);
}
@Override
protected Boolean getValue(int index) {
return VALUES[index];
}
@Override
protected Property<Boolean> createObservable(Boolean value) {
return new SimpleBooleanProperty(value);
}
@Override
protected JavaBeanProperty<Boolean> extractProperty(Object bean) throws NoSuchMethodException {
return JavaBeanBooleanPropertyBuilder.create().bean(bean).name("x").build();
}
public class BooleanPOJO extends BeanStub<Boolean> {
private boolean x;
private boolean failureMode;
public BooleanPOJO(boolean x) {
this.x = x;
}
public boolean isX() {
if (failureMode) {
throw new RuntimeException("FailureMode activated");
} else {
return x;
}
}
public void setX(boolean x) {
if (failureMode) {
throw new RuntimeException("FailureMode activated");
} else {
this.x = x;
}
}
@Override
public Boolean getValue() {
return isX();
}
@Override
public void setValue(Boolean value) throws PropertyVetoException {
setX(value);
}
@Override
public void setFailureMode(boolean failureMode) {
this.failureMode = failureMode;
}
}
}
