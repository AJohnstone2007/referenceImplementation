package test.javafx.beans.property.adapter;
import java.beans.PropertyVetoException;
import javafx.beans.property.adapter.ReadOnlyJavaBeanBooleanPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanProperty;
import static org.junit.Assert.assertEquals;
public class ReadOnlyJavaBeanBooleanProperty_prim_Test extends ReadOnlyJavaBeanPropertyTestBase<Boolean> {
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
protected ReadOnlyJavaBeanProperty<Boolean> extractProperty(Object bean) throws NoSuchMethodException {
return ReadOnlyJavaBeanBooleanPropertyBuilder.create().bean(bean).name("x").build();
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
@Override
public Boolean getValue() {
return isX();
}
@Override
public void setValue(Boolean value) throws PropertyVetoException {
this.x = value;
}
@Override
public void setFailureMode(boolean failureMode) {
this.failureMode = failureMode;
}
}
}
