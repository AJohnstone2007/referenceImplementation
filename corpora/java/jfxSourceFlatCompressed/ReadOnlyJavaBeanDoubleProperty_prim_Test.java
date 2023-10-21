package test.javafx.beans.property.adapter;
import java.beans.PropertyVetoException;
import javafx.beans.property.adapter.ReadOnlyJavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanProperty;
import static org.junit.Assert.assertEquals;
public class ReadOnlyJavaBeanDoubleProperty_prim_Test extends ReadOnlyJavaBeanPropertyTestBase<Number> {
private static final double EPSILON = 1e-12;
private final static Double[] VALUES = new Double[] {Math.PI, -Math.E};
@Override
protected BeanStub<Number> createBean(Number initialValue) {
return new DoublePOJO(initialValue.doubleValue());
}
@Override
protected void check(Number actual, Number expected) {
assertEquals(actual.doubleValue(), expected.doubleValue(), EPSILON);
}
@Override
protected Number getValue(int index) {
return VALUES[index];
}
@Override
protected ReadOnlyJavaBeanProperty<Number> extractProperty(Object bean) throws NoSuchMethodException {
return ReadOnlyJavaBeanDoublePropertyBuilder.create().bean(bean).name("x").build();
}
public class DoublePOJO extends BeanStub<Number> {
private double x;
private boolean failureMode;
public DoublePOJO(double x) {
this.x = x;
}
public double getX() {
if (failureMode) {
throw new RuntimeException("FailureMode activated");
} else {
return x;
}
}
@Override
public Double getValue() {
return getX();
}
@Override
public void setValue(Number value) throws PropertyVetoException {
this.x = value.doubleValue();
}
@Override
public void setFailureMode(boolean failureMode) {
this.failureMode = failureMode;
}
}
}
