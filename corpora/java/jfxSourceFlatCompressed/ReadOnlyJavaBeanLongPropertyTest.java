package test.javafx.beans.property.adapter;
import java.beans.PropertyVetoException;
import javafx.beans.property.adapter.ReadOnlyJavaBeanLongPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanProperty;
import static org.junit.Assert.assertEquals;
public class ReadOnlyJavaBeanLongPropertyTest extends ReadOnlyJavaBeanPropertyTestBase<Number> {
private final static Long[] VALUES = new Long[] {Long.MIN_VALUE, Long.MAX_VALUE};
@Override
protected BeanStub<Number> createBean(Number initialValue) {
return new LongPOJO(initialValue.longValue());
}
@Override
protected void check(Number actual, Number expected) {
assertEquals(actual.longValue(), expected.longValue());
}
@Override
protected Number getValue(int index) {
return VALUES[index];
}
@Override
protected ReadOnlyJavaBeanProperty<Number> extractProperty(Object bean) throws NoSuchMethodException {
return ReadOnlyJavaBeanLongPropertyBuilder.create().bean(bean).name("x").build();
}
public class LongPOJO extends BeanStub<Number> {
private Long x;
private boolean failureMode;
public LongPOJO(Long x) {
this.x = x;
}
public Long getX() {
if (failureMode) {
throw new RuntimeException("FailureMode activated");
} else {
return x;
}
}
@Override
public Long getValue() {
return getX();
}
@Override
public void setValue(Number value) throws PropertyVetoException {
this.x = value.longValue();
}
@Override
public void setFailureMode(boolean failureMode) {
this.failureMode = failureMode;
}
}
}
