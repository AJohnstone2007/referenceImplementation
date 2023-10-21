package test.javafx.beans.property.adapter;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import java.beans.PropertyVetoException;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanProperty;
import static org.junit.Assert.assertEquals;
public class JavaBeanIntegerPropertyTest extends JavaBeanPropertyTestBase<Number> {
private static final int[] VALUES = new int[] {Integer.MAX_VALUE, Integer.MIN_VALUE};
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
protected Property<Number> createObservable(Number value) {
return new SimpleIntegerProperty(value.intValue());
}
@Override
protected JavaBeanProperty<Number> extractProperty(Object bean) throws NoSuchMethodException {
return JavaBeanIntegerPropertyBuilder.create().bean(bean).name("x").build();
}
public class IntegerPOJO extends BeanStub<Number> {
private Integer x;
private boolean failureMode;
public IntegerPOJO(Integer x) {
this.x = x;
}
public Integer getX() {
if (failureMode) {
throw new RuntimeException("FailureMode activated");
} else {
return x;
}
}
public void setX(Integer x) {
if (failureMode) {
throw new RuntimeException("FailureMode activated");
} else {
this.x = x;
}
}
@Override
public Integer getValue() {
return getX();
}
@Override
public void setValue(Number value) throws PropertyVetoException {
setX(value.intValue());
}
@Override
public void setFailureMode(boolean failureMode) {
this.failureMode = failureMode;
}
}
}
