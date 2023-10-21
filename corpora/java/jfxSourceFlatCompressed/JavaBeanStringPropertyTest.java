package test.javafx.beans.property.adapter;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import java.beans.PropertyVetoException;
import javafx.beans.property.adapter.JavaBeanProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import static org.junit.Assert.assertEquals;
public class JavaBeanStringPropertyTest extends JavaBeanPropertyTestBase<String> {
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
protected Property<String> createObservable(String value) {
return new SimpleStringProperty(value);
}
@Override
protected JavaBeanProperty<String> extractProperty(Object bean) throws NoSuchMethodException {
return JavaBeanStringPropertyBuilder.create().bean(bean).name("x").build();
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
public void setX(String x) {
if (failureMode) {
throw new RuntimeException("FailureMode activated");
} else {
this.x = x;
}
}
@Override
public String getValue() {
return getX();
}
@Override
public void setValue(String value) throws PropertyVetoException {
setX(value);
}
@Override
public void setFailureMode(boolean failureMode) {
this.failureMode = failureMode;
}
}
}
