package test.javafx.beans.property;
import javafx.beans.property.ReadOnlyStringPropertyBase;
import test.javafx.beans.InvalidationListenerMock;
import test.javafx.beans.value.ChangeListenerMock;
import org.junit.Before;
import org.junit.Test;
public class ReadOnlyStringPropertyBaseTest {
private static final String UNDEFINED = null;
private static final String DEFAULT = null;
private static final String VALUE_1 = "Hello World!";
private static final String VALUE_2 = "Goodbye World!";
private ReadOnlyPropertyMock property;
private InvalidationListenerMock invalidationListener;
private ChangeListenerMock<String> changeListener;
@Before
public void setUp() throws Exception {
property = new ReadOnlyPropertyMock();
invalidationListener = new InvalidationListenerMock();
changeListener = new ChangeListenerMock<String>(UNDEFINED);
}
@Test
public void testInvalidationListener() {
property.addListener(invalidationListener);
property.get();
invalidationListener.reset();
property.set(VALUE_1);
invalidationListener.check(property, 1);
property.removeListener(invalidationListener);
invalidationListener.reset();
property.set(VALUE_2);
invalidationListener.check(null, 0);
}
@Test
public void testChangeListener() {
property.addListener(changeListener);
property.get();
changeListener.reset();
property.set(VALUE_1);
changeListener.check(property, DEFAULT, VALUE_1, 1);
property.removeListener(changeListener);
changeListener.reset();
property.set(VALUE_2);
changeListener.check(null, UNDEFINED, UNDEFINED, 0);
}
private static class ReadOnlyPropertyMock extends ReadOnlyStringPropertyBase {
private String value;
@Override
public Object getBean() {
return null;
}
@Override
public String getName() {
return null;
}
private void set(String value) {
this.value = value;
fireValueChangedEvent();
}
@Override
public String get() {
return value;
}
}
}
