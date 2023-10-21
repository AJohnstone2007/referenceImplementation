package test.javafx.beans.property.adapter;
import java.beans.PropertyVetoException;
import javafx.beans.property.adapter.ReadOnlyJavaBeanProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringPropertyBuilder;
import static org.junit.Assert.assertEquals;
public class ReadOnlyJavaBeanStringPropertyTest extends ReadOnlyJavaBeanPropertyTestBase<String> {
private final static String[] VALUES = new String[] {"Hello World", "JavaFX is cool"};
@Override
protected BeanStub<String> createBean(String initialValue) {
return new StringPOJO(initialValue);
}
@Override
protected void check(String actual, String expected) {
assertEquals(actual, expected);
}
@Override
protected String getValue(int index) {
return VALUES[index];
}
@Override
protected ReadOnlyJavaBeanProperty<String> extractProperty(Object bean) throws NoSuchMethodException {
return ReadOnlyJavaBeanStringPropertyBuilder.create().bean(bean).name("x").build();
}
public class StringPOJO extends BeanStub<String> {
private String x;
private boolean failureMode;
public StringPOJO(String x) {
this.x = x;
}
public String getX() {
if (failureMode) {
throw new RuntimeException("FailureMode activated");
} else {
return x;
}
}
@Override
public String getValue() {
return getX();
}
@Override
public void setValue(String value) throws PropertyVetoException {
this.x = value;
}
@Override
public void setFailureMode(boolean failureMode) {
this.failureMode = failureMode;
}
}
}
