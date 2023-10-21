package test.javafx.beans.property.adapter;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleFloatProperty;
import java.beans.PropertyVetoException;
import javafx.beans.property.adapter.JavaBeanFloatPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanProperty;
import static org.junit.Assert.assertEquals;
public class JavaBeanFloatProperty_prim_Test extends JavaBeanPropertyTestBase<Number> {
private static final float EPSILON = 1e-6f;
private static final float[] VALUES = new float[] {(float)Math.PI, (float)-Math.E};
@Override
protected BeanStub<Number> createBean(Number initialValue) {
return new FloatPOJO(initialValue.floatValue());
}
@Override
protected void check(Number actual, Number expected) {
assertEquals(actual.floatValue(), expected.floatValue(), EPSILON);
}
@Override
protected Number getValue(int index) {
return VALUES[index];
}
@Override
protected Property<Number> createObservable(Number value) {
return new SimpleFloatProperty(value.floatValue());
}
@Override
protected JavaBeanProperty<Number> extractProperty(Object bean) throws NoSuchMethodException {
return JavaBeanFloatPropertyBuilder.create().bean(bean).name("x").build();
}
public class FloatPOJO extends BeanStub<Number> {
private float x;
private boolean failureMode;
public FloatPOJO(float x) {
this.x = x;
}
public float getX() {
if (failureMode) {
throw new RuntimeException("FailureMode activated");
} else {
return x;
}
}
public void setX(float x) {
if (failureMode) {
throw new RuntimeException("FailureMode activated");
} else {
this.x = x;
}
}
@Override
public Float getValue() {
return getX();
}
@Override
public void setValue(Number value) throws PropertyVetoException {
setX(value.floatValue());
}
@Override
public void setFailureMode(boolean failureMode) {
this.failureMode = failureMode;
}
}
}
