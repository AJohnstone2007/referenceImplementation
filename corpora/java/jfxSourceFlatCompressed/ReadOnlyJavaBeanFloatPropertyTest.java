package test.javafx.beans.property.adapter;
import java.beans.PropertyVetoException;
import javafx.beans.property.adapter.ReadOnlyJavaBeanFloatPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanProperty;
import static org.junit.Assert.assertEquals;
public class ReadOnlyJavaBeanFloatPropertyTest extends ReadOnlyJavaBeanPropertyTestBase<Number> {
private static final float EPSILON = 1e-6f;
private final static Float[] VALUES = new Float[] {(float)Math.PI, (float)-Math.E};
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
protected ReadOnlyJavaBeanProperty<Number> extractProperty(Object bean) throws NoSuchMethodException {
return ReadOnlyJavaBeanFloatPropertyBuilder.create().bean(bean).name("x").build();
}
public class FloatPOJO extends BeanStub<Number> {
private Float x;
private boolean failureMode;
public FloatPOJO(Float x) {
this.x = x;
}
public Float getX() {
if (failureMode) {
throw new RuntimeException("FailureMode activated");
} else {
return x;
}
}
@Override
public Float getValue() {
return getX();
}
@Override
public void setValue(Number value) throws PropertyVetoException {
this.x = value.floatValue();
}
@Override
public void setFailureMode(boolean failureMode) {
this.failureMode = failureMode;
}
}
}
