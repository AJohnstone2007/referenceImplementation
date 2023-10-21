package test.javafx.beans.property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import test.javafx.beans.InvalidationListenerMock;
import test.javafx.beans.value.ChangeListenerMock;
import javafx.beans.value.ObservableStringValueStub;
import org.junit.Before;
import org.junit.Test;
public class StringPropertyBaseTest {
private static final Object NO_BEAN = null;
private static final String NO_NAME_1 = null;
private static final String NO_NAME_2 = "";
private static final String UNDEFINED = "UNDEFINED";
private static final String VALUE_1a = "Hello World";
private static final String VALUE_1b = "HELLO WORLD";
private static final String VALUE_2a = "Goodbye";
private static final String VALUE_2b = "GOODBYE";
private StringPropertyMock property;
private InvalidationListenerMock invalidationListener;
private ChangeListenerMock<String> changeListener;
@Before
public void setUp() throws Exception {
property = new StringPropertyMock();
invalidationListener = new InvalidationListenerMock();
changeListener = new ChangeListenerMock<String>(UNDEFINED);
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
final StringProperty p1 = new SimpleStringProperty();
assertEquals(null, p1.get());
assertEquals(null, p1.getValue());
assertFalse(property.isBound());
final StringProperty p2 = new SimpleStringProperty(VALUE_1b);
assertEquals(VALUE_1b, p2.get());
assertEquals(VALUE_1b, p2.getValue());
assertFalse(property.isBound());
}
public void testInvalidationListener() {
attachInvalidationListener();
property.set(VALUE_2a);
invalidationListener.check(property, 1);
property.removeListener(invalidationListener);
invalidationListener.reset();
property.set(VALUE_1a);
invalidationListener.check(null, 0);
}
@Test
public void testChangeListener() {
attachChangeListener();
property.set(VALUE_2a);
changeListener.check(property, null, VALUE_2a, 1);
property.removeListener(changeListener);
changeListener.reset();
property.set(VALUE_1a);
changeListener.check(null, UNDEFINED, UNDEFINED, 0);
}
@Test
public void testLazySet() {
attachInvalidationListener();
property.set(VALUE_2a);
assertEquals(VALUE_2a, property.get());
property.check(1);
invalidationListener.check(property, 1);
property.set(VALUE_2a);
assertEquals(VALUE_2a, property.get());
property.check(0);
invalidationListener.check(null, 0);
property.set(VALUE_1a);
property.set(VALUE_1b);
assertEquals(VALUE_1b, property.get());
property.check(1);
invalidationListener.check(property, 1);
}
@Test
public void testEagerSet() {
attachChangeListener();
property.set(VALUE_2a);
assertEquals(VALUE_2a, property.get());
property.check(1);
changeListener.check(property, null, VALUE_2a, 1);
property.set(VALUE_2a);
assertEquals(VALUE_2a, property.get());
property.check(0);
changeListener.check(null, UNDEFINED, UNDEFINED, 0);
property.set(VALUE_1a);
property.set(VALUE_1b);
assertEquals(VALUE_1b, property.get());
property.check(2);
changeListener.check(property, VALUE_1a, VALUE_1b, 2);
}
@Test
public void testLazySetValue() {
attachInvalidationListener();
property.setValue(VALUE_2a);
assertEquals(VALUE_2a, property.get());
property.check(1);
invalidationListener.check(property, 1);
property.setValue(VALUE_2a);
assertEquals(VALUE_2a, property.get());
property.check(0);
invalidationListener.check(null, 0);
property.setValue(VALUE_1a);
property.setValue(VALUE_1b);
assertEquals(VALUE_1b, property.get());
property.check(1);
invalidationListener.check(property, 1);
}
@Test
public void testEagerSetValue() {
attachChangeListener();
property.setValue(VALUE_2a);
assertEquals(VALUE_2a, property.get());
property.check(1);
changeListener.check(property, null, VALUE_2a, 1);
property.setValue(VALUE_2a);
assertEquals(VALUE_2a, property.get());
property.check(0);
changeListener.check(null, UNDEFINED, UNDEFINED, 0);
property.setValue(VALUE_1a);
property.setValue(VALUE_1b);
assertEquals(VALUE_1b, property.get());
property.check(2);
changeListener.check(property, VALUE_1a, VALUE_1b, 2);
}
@Test(expected=RuntimeException.class)
public void testSetBoundValue() {
final StringProperty v = new SimpleStringProperty(VALUE_1a);
property.bind(v);
property.set(VALUE_1a);
}
@Test
public void testLazyBind() {
attachInvalidationListener();
final ObservableStringValueStub v = new ObservableStringValueStub(VALUE_1a);
property.bind(v);
assertEquals(VALUE_1a, property.get());
assertTrue(property.isBound());
property.check(1);
invalidationListener.check(property, 1);
v.set(VALUE_2a);
assertEquals(VALUE_2a, property.get());
property.check(1);
invalidationListener.check(property, 1);
v.set(VALUE_1a);
v.set(VALUE_1b);
assertEquals(VALUE_1b, property.get());
property.check(1);
invalidationListener.check(property, 1);
v.set(VALUE_1a);
v.set(VALUE_1a);
assertEquals(VALUE_1a, property.get());
property.check(1);
invalidationListener.check(property, 1);
}
@Test
public void testEagerBind() {
attachChangeListener();
final ObservableStringValueStub v = new ObservableStringValueStub(VALUE_1a);
property.bind(v);
assertEquals(VALUE_1a, property.get());
assertTrue(property.isBound());
property.check(1);
changeListener.check(property, null, VALUE_1a, 1);
v.set(VALUE_2a);
assertEquals(VALUE_2a, property.get());
property.check(1);
changeListener.check(property, VALUE_1a, VALUE_2a, 1);
v.set(VALUE_1a);
v.set(VALUE_1b);
assertEquals(VALUE_1b, property.get());
property.check(2);
changeListener.check(property, VALUE_1a, VALUE_1b, 2);
v.set(VALUE_1a);
v.set(VALUE_1a);
assertEquals(VALUE_1a, property.get());
property.check(2);
changeListener.check(property, VALUE_1b, VALUE_1a, 1);
}
@Test(expected=NullPointerException.class)
public void testBindToNull() {
property.bind(null);
}
@Test
public void testRebind() {
attachInvalidationListener();
final StringProperty v1 = new SimpleStringProperty(VALUE_1a);
final StringProperty v2 = new SimpleStringProperty(VALUE_2a);
property.bind(v1);
property.get();
property.reset();
invalidationListener.reset();
property.bind(v2);
assertEquals(VALUE_2a, property.get());
assertTrue(property.isBound());
assertEquals(1, property.counter);
invalidationListener.check(property, 1);
property.reset();
v1.set(VALUE_1b);
assertEquals(VALUE_2a, property.get());
assertEquals(0, property.counter);
invalidationListener.check(null, 0);
property.reset();
v2.set(VALUE_2b);
assertEquals(VALUE_2b, property.get());
assertEquals(1, property.counter);
invalidationListener.check(property, 1);
property.reset();
property.bind(v2);
assertEquals(VALUE_2b, property.get());
assertTrue(property.isBound());
assertEquals(0, property.counter);
invalidationListener.check(null, 0);
}
@Test
public void testUnbind() {
attachInvalidationListener();
final StringProperty v = new SimpleStringProperty(VALUE_1a);
property.bind(v);
property.unbind();
assertEquals(VALUE_1a, property.get());
assertFalse(property.isBound());
property.reset();
invalidationListener.reset();
v.set(VALUE_2a);
assertEquals(VALUE_1a, property.get());
assertEquals(0, property.counter);
invalidationListener.check(null, 0);
property.reset();
property.set(VALUE_1b);
assertEquals(VALUE_1b, property.get());
assertEquals(1, property.counter);
invalidationListener.check(property, 1);
}
@Test
public void testAddingListenerWillAlwaysReceiveInvalidationEvent() {
final StringProperty v = new SimpleStringProperty(VALUE_1a);
final InvalidationListenerMock listener2 = new InvalidationListenerMock();
final InvalidationListenerMock listener3 = new InvalidationListenerMock();
property.set(VALUE_1a);
property.addListener(listener2);
listener2.reset();
property.set(VALUE_1b);
listener2.check(property, 1);
property.bind(v);
v.set(VALUE_2a);
property.addListener(listener3);
v.get();
listener3.reset();
v.set(VALUE_2b);
listener3.check(property, 1);
}
@Test
public void testToString() {
final String value1 = "Hello World";
final String value2 = "Goodbye";
final StringProperty v = new SimpleStringProperty(value2);
property.set(value1);
assertEquals("StringProperty [value: " + value1 + "]", property.toString());
property.bind(v);
assertEquals("StringProperty [bound, invalid]", property.toString());
property.get();
assertEquals("StringProperty [bound, value: " + value2 + "]", property.toString());
v.set(value1);
assertEquals("StringProperty [bound, invalid]", property.toString());
property.get();
assertEquals("StringProperty [bound, value: " + value1 + "]", property.toString());
final Object bean = new Object();
final String name = "My name";
final StringProperty v1 = new StringPropertyMock(bean, name);
assertEquals("StringProperty [bean: " + bean.toString() + ", name: My name, value: " + null + "]", v1.toString());
v1.set(value1);
assertEquals("StringProperty [bean: " + bean.toString() + ", name: My name, value: " + value1 + "]", v1.toString());
final StringProperty v2 = new StringPropertyMock(bean, NO_NAME_1);
assertEquals("StringProperty [bean: " + bean.toString() + ", value: " + null + "]", v2.toString());
v2.set(value1);
assertEquals("StringProperty [bean: " + bean.toString() + ", value: " + value1 + "]", v2.toString());
final StringProperty v3 = new StringPropertyMock(bean, NO_NAME_2);
assertEquals("StringProperty [bean: " + bean.toString() + ", value: " + null + "]", v3.toString());
v3.set(value1);
assertEquals("StringProperty [bean: " + bean.toString() + ", value: " + value1 + "]", v3.toString());
final StringProperty v4 = new StringPropertyMock(NO_BEAN, name);
assertEquals("StringProperty [name: My name, value: " + null + "]", v4.toString());
v4.set(value1);
assertEquals("StringProperty [name: My name, value: " + value1 + "]", v4.toString());
}
private static class StringPropertyMock extends StringPropertyBase {
private final Object bean;
private final String name;
private int counter;
private StringPropertyMock() {
this.bean = NO_BEAN;
this.name = NO_NAME_1;
}
private StringPropertyMock(Object bean, String name) {
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
