package test.javafx.beans.property;
import javafx.beans.property.ReadOnlyFloatPropertyBase;
import test.javafx.beans.InvalidationListenerMock;
import test.javafx.beans.value.ChangeListenerMock;
import org.junit.Before;
import org.junit.Test;
public class ReadOnlyFloatPropertyBaseTest {
private static final Float UNDEFINED = null;
private static final float DEFAULT = 0.0f;
private static final float VALUE_1 = (float)Math.E;
private static final float VALUE_2 = (float)Math.PI;
private ReadOnlyPropertyMock property;
private InvalidationListenerMock invalidationListener;
private ChangeListenerMock<Number> changeListener;
@Before
public void setUp() throws Exception {
property = new ReadOnlyPropertyMock();
invalidationListener = new InvalidationListenerMock();
changeListener = new ChangeListenerMock<Number>(UNDEFINED);
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
private static class ReadOnlyPropertyMock extends ReadOnlyFloatPropertyBase {
private float value;
@Override
public Object getBean() {
return null;
}
@Override
public String getName() {
return null;
}
private void set(float value) {
this.value = value;
fireValueChangedEvent();
}
@Override
public float get() {
return value;
}
}
}
