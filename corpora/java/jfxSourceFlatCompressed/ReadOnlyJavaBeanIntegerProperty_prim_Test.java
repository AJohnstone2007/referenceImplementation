package test.javafx.beans.property.adapter;
import java.beans.PropertyVetoException;
import javafx.beans.property.adapter.ReadOnlyJavaBeanIntegerPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanProperty;
import static org.junit.Assert.assertEquals;
public class ReadOnlyJavaBeanIntegerProperty_prim_Test extends ReadOnlyJavaBeanPropertyTestBase<Number> {
private final static Integer[] VALUES = new Integer[] {Integer.MIN_VALUE, Integer.MAX_VALUE};
@Override
protected BeanStub<Number> createBean(Number initialValue) {
return new IntegerPOJO(initialValue.intValue());
}
@Override
protected void check(Number actual, Number expected) {
assertEquals(actual.intValue(), expected.intValue());
}
@Override
protected Number getValue(int index) {
return VALUES[index];
}
@Override
protected ReadOnlyJavaBeanProperty<Number> extractProperty(Object bean) throws NoSuchMethodException {
return ReadOnlyJavaBeanIntegerPropertyBuilder.create().bean(bean).name("x").build();
}
public class IntegerPOJO extends BeanStub<Number> {
private int x;
private boolean failureMode;
public IntegerPOJO(int x) {
this.x = x;
}
public int getX() {
if (failureMode) {
throw new RuntimeException("FailureMode activated");
} else {
return x;
}
}
@Override
public Integer getValue() {
return getX();
}
@Override
public void setValue(Number value) throws PropertyVetoException {
this.x = value.intValue();
}
@Override
public void setFailureMode(boolean failureMode) {
this.failureMode = failureMode;
}
}
}
