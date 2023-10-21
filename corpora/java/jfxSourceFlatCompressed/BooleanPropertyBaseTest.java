package test.javafx.beans.property;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import test.javafx.beans.InvalidationListenerMock;
import test.javafx.beans.value.ChangeListenerMock;
import javafx.beans.value.ObservableBooleanValueStub;
import javafx.beans.value.ObservableObjectValueStub;
import org.junit.Before;
import org.junit.Test;
public class BooleanPropertyBaseTest {
private static final Object NO_BEAN = null;
private static final String NO_NAME_1 = null;
private static final String NO_NAME_2 = "";
private static final Boolean UNDEFINED = null;
private BooleanPropertyMock property;
private InvalidationListenerMock invalidationListener;
private ChangeListenerMock<Boolean> changeListener;
@Before
public void setUp() throws Exception {
property = new BooleanPropertyMock();
invalidationListener = new InvalidationListenerMock();
changeListener = new ChangeListenerMock<Boolean>(UNDEFINED);
}
private void attachInvalidationListener() {
property.addListener(invalidationListener);
property.get();
invalidationListener.reset();
}
private void attachChangeListener() {
property.addListener(changeListener);
property.get();
changeListener.reset();
}
@Test
public void testConstructor() {
final BooleanProperty p1 = new SimpleBooleanProperty();
assertEquals(false, p1.get());
assertEquals(Boolean.FALSE, p1.getValue());
assertFalse(property.isBound());
final BooleanProperty p2 = new SimpleBooleanProperty(true);
assertEquals(true, p2.get());
assertEquals(Boolean.TRUE, p2.getValue());
assertFalse(property.isBound());
}
@Test
public void testInvalidationListener() {
attachInvalidationListener();
property.set(true);
invalidationListener.check(property, 1);
property.removeListener(invalidationListener);
invalidationListener.reset();
property.set(false);
invalidationListener.check(null, 0);
}
@Test
public void testChangeListener() {
attachChangeListener();
property.set(true);
changeListener.check(property, false, true, 1);
property.removeListener(changeListener);
changeListener.reset();
property.set(false);
changeListener.check(null, UNDEFINED, UNDEFINED, 0);
}
@Test
public void testLazySet() {
attachInvalidationListener();
property.set(true);
assertEquals(true, property.get());
property.check(1);
invalidationListener.check(property, 1);
property.set(true);
assertEquals(true, property.get());
property.check(0);
invalidationListener.check(null, 0);
property.set(false);
property.set(true);
assertEquals(true, property.get());
property.check(1);
invalidationListener.check(property, 1);
}
@Test
public void testEagerSet() {
attachChangeListener();
property.set(true);
assertEquals(true, property.get());
property.check(1);
changeListener.check(property, false, true, 1);
property.set(true);
assertEquals(true, property.get());
property.check(0);
changeListener.check(null, UNDEFINED, UNDEFINED, 0);
property.set(false);
property.set(true);
assertEquals(true, property.get());
property.check(2);
changeListener.check(property, false, true, 2);
}
@Test
public void testLazySetValue() {
attachInvalidationListener();
property.setValue(true);
assertEquals(true, property.get());
property.check(1);
invalidationListener.check(property, 1);
property.setValue(true);
assertEquals(true, property.get());
property.check(0);
invalidationListener.check(null, 0);
property.setValue(false);
property.setValue(true);
assertEquals(true, property.get());
property.check(1);
invalidationListener.check(property, 1);
}
@Test
public void testEagerSetValue() {
attachChangeListener();
property.setValue(true);
assertEquals(true, property.get());
property.check(1);
changeListener.check(property, false, true, 1);
property.setValue(true);
assertEquals(true, property.get());
property.check(0);
changeListener.check(null, UNDEFINED, UNDEFINED, 0);
property.setValue(false);
property.setValue(true);
assertEquals(true, property.get());
property.check(2);
changeListener.check(property, false, true, 2);
}
@Test(expected=RuntimeException.class)
public void testSetBoundValue() {
final BooleanProperty v = new SimpleBooleanProperty(true);
property.bind(v);
property.set(true);
}
@Test
public void testLazyBind_primitive() {
attachInvalidationListener();
final ObservableBooleanValueStub v = new ObservableBooleanValueStub(true);
property.bind(v);
assertEquals(true, property.get());
assertTrue(property.isBound());
property.check(1);
invalidationListener.check(property, 1);
v.set(false);
assertEquals(false, property.get());
property.check(1);
invalidationListener.check(property, 1);
v.set(true);
v.set(false);
assertEquals(false, property.get());
property.check(1);
invalidationListener.check(property, 1);
v.set(true);
v.set(true);
assertEquals(true, property.get());
property.check(1);
invalidationListener.check(property, 1);
}
@Test
public void testEagerBind_primitive() {
attachChangeListener();
final ObservableBooleanValueStub v = new ObservableBooleanValueStub(true);
property.bind(v);
assertEquals(true, property.get());
assertTrue(property.isBound());
property.check(1);
changeListener.check(property, false, true, 1);
v.set(false);
assertEquals(false, property.get());
property.check(1);
changeListener.check(property, true, false, 1);
v.set(true);
v.set(false);
assertEquals(false, property.get());
property.check(2);
changeListener.check(property, true, false, 2);
v.set(true);
v.set(true);
assertEquals(true, property.get());
property.check(2);
changeListener.check(property, false, true, 1);
}
@Test
public void testLazyBind_generic() {
attachInvalidationListener();
final ObservableObjectValueStub<Boolean> v = new ObservableObjectValueStub<Boolean>(true);
property.bind(v);
assertEquals(true, property.get());
assertTrue(property.isBound());
property.check(1);
invalidationListener.check(property, 1);
v.set(false);
assertEquals(false, property.get());
property.check(1);
invalidationListener.check(property, 1);
v.set(true);
v.set(false);
assertEquals(false, property.get());
property.check(1);
invalidationListener.check(property, 1);
v.set(true);
v.set(true);
assertEquals(true, property.get());
property.check(1);
invalidationListener.check(property, 1);
v.set(null);
assertEquals(false, property.get());
property.check(1);
invalidationListener.check(property, 1);
}
@Test
public void testEagerBind_generic() {
attachChangeListener();
final ObservableObjectValueStub<Boolean> v = new ObservableObjectValueStub<Boolean>(true);
property.bind(v);
assertEquals(true, property.get());
assertTrue(property.isBound());
property.check(1);
changeListener.check(property, false, true, 1);
v.set(false);
assertEquals(false, property.get());
property.check(1);
changeListener.check(property, true, false, 1);
v.set(true);
v.set(false);
assertEquals(false, property.get());
property.check(2);
changeListener.check(property, true, false, 2);
v.set(true);
v.set(true);
assertEquals(true, property.get());
property.check(2);
changeListener.check(property, false, true, 1);
v.set(null);
assertEquals(false, property.get());
property.check(1);
changeListener.check(property, true, false, 1);
}
@Test(expected=NullPointerException.class)
public void testBindToNull() {
property.bind(null);
}
@Test
public void testRebind() {
attachInvalidationListener();
final BooleanProperty v1 = new SimpleBooleanProperty(true);
final BooleanProperty v2 = new SimpleBooleanProperty(false);
property.bind(v1);
property.get();
property.reset();
invalidationListener.reset();
property.bind(v2);
assertEquals(false, property.get());
assertTrue(property.isBound());
assertEquals(1, property.counter);
invalidationListener.check(property, 1);
property.reset();
v2.set(true);
assertEquals(true, property.get());
assertEquals(1, property.counter);
invalidationListener.check(property, 1);
property.reset();
v1.set(false);
assertEquals(true, property.get());
assertEquals(0, property.counter);
invalidationListener.check(null, 0);
property.reset();
property.bind(v2);
assertEquals(true, property.get());
assertTrue(property.isBound());
assertEquals(0, property.counter);
invalidationListener.check(null, 0);
}
@Test
public void testUnbind() {
attachInvalidationListener();
final BooleanProperty v = new SimpleBooleanProperty(true);
property.bind(v);
property.unbind();
assertEquals(true, property.get());
assertFalse(property.isBound());
property.reset();
invalidationListener.reset();
v.set(false);
assertEquals(true, property.get());
assertEquals(0, property.counter);
invalidationListener.check(null, 0);
property.reset();
property.set(false);
assertEquals(false, property.get());
assertEquals(1, property.counter);
invalidationListener.check(property, 1);
}
@Test
public void testAddingListenerWillAlwaysReceiveInvalidationEvent() {
final BooleanProperty v = new SimpleBooleanProperty(true);
final InvalidationListenerMock listener2 = new InvalidationListenerMock();
final InvalidationListenerMock listener3 = new InvalidationListenerMock();
property.set(true);
property.addListener(listener2);
listener2.reset();
property.set(false);
listener2.check(property, 1);
property.bind(v);
v.set(false);
property.addListener(listener3);
v.get();
listener3.reset();
v.set(true);
listener3.check(property, 1);
}
@Test
public void testToString() {
final BooleanProperty v = new SimpleBooleanProperty(true);
property.set(true);
assertEquals("BooleanProperty [value: true]", property.toString());
property.bind(v);
assertEquals("BooleanProperty [bound, invalid]", property.toString());
property.get();
assertEquals("BooleanProperty [bound, value: true]", property.toString());
v.set(false);
assertEquals("BooleanProperty [bound, invalid]", property.toString());
property.get();
assertEquals("BooleanProperty [bound, value: false]", property.toString());
final Object bean = new Object();
final String name = "My name";
final BooleanProperty v1 = new BooleanPropertyMock(bean, name);
assertEquals("BooleanProperty [bean: " + bean.toString() + ", name: My name, value: false]", v1.toString());
v1.set(true);
assertEquals("BooleanProperty [bean: " + bean.toString() + ", name: My name, value: true]", v1.toString());
final BooleanProperty v2 = new BooleanPropertyMock(bean, NO_NAME_1);
assertEquals("BooleanProperty [bean: " + bean.toString() + ", value: false]", v2.toString());
v2.set(true);
assertEquals("BooleanProperty [bean: " + bean.toString() + ", value: true]", v2.toString());
final BooleanProperty v3 = new BooleanPropertyMock(bean, NO_NAME_2);
assertEquals("BooleanProperty [bean: " + bean.toString() + ", value: false]", v3.toString());
v3.set(true);
assertEquals("BooleanProperty [bean: " + bean.toString() + ", value: true]", v3.toString());
final BooleanProperty v4 = new BooleanPropertyMock(NO_BEAN, name);
assertEquals("BooleanProperty [name: My name, value: false]", v4.toString());
v4.set(true);
assertEquals("BooleanProperty [name: My name, value: true]", v4.toString());
}
private static class BooleanPropertyMock extends BooleanPropertyBase {
private final Object bean;
private final String name;
private int counter;
private BooleanPropertyMock() {
this.bean = NO_BEAN;
this.name = NO_NAME_1;
}
private BooleanPropertyMock(Object bean, String name) {
this.bean = bean;
this.name = name;
}
@Override
protected void invalidated() {
counter++;
}
private void check(int expected) {
assertEquals(expected, counter);
reset();
}
private void reset() {
counter = 0;
}
@Override public Object getBean() { return bean; }
@Override public String getName() { return name; }
}
}
