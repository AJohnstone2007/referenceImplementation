package test.javafx.beans.property;
import javafx.beans.property.ReadOnlyDoublePropertyBase;
import test.javafx.beans.InvalidationListenerMock;
import test.javafx.beans.value.ChangeListenerMock;
import org.junit.Before;
import org.junit.Test;
public class ReadOnlyDoublePropertyBaseTest {
private static final Double UNDEFINED = null;
private static final double DEFAULT = 0.0;
private static final double VALUE_1 = -Math.PI;
private static final double VALUE_2 = Math.E;
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
private static class ReadOnlyPropertyMock extends ReadOnlyDoublePropertyBase {
private double value;
@Override
public Object getBean() {
return null;
}
@Override
public String getName() {
return null;
}
private void set(double value) {
this.value = value;
fireValueChangedEvent();
}
@Override
public double get() {
return value;
}
}
}
