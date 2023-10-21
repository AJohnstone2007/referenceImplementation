package test.javafx.beans.property.adapter;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import java.beans.PropertyVetoException;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.JavaBeanProperty;
import static org.junit.Assert.assertEquals;
public class JavaBeanDoubleProperty_prim_Test extends JavaBeanPropertyTestBase<Number> {
private static final double EPSILON = 1e-12;
private static final double[] VALUES = new double[] {Math.PI, -Math.E};
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
protected Property<Number> createObservable(Number value) {
return new SimpleDoubleProperty(value.doubleValue());
}
@Override
protected JavaBeanProperty<Number> extractProperty(Object bean) throws NoSuchMethodException {
return JavaBeanDoublePropertyBuilder.create().bean(bean).name("x").build();
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
public void setX(double x) {
if (failureMode) {
throw new RuntimeException("FailureMode activated");
} else {
this.x = x;
}
}
@Override
public Double getValue() {
return getX();
}
@Override
public void setValue(Number value) throws PropertyVetoException {
setX(value.doubleValue());
}
@Override
public void setFailureMode(boolean failureMode) {
this.failureMode = failureMode;
}
}
}
