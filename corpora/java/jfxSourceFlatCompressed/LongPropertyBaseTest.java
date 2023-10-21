package test.javafx.beans.property;
import javafx.beans.property.LongProperty;
import javafx.beans.property.LongPropertyBase;
import javafx.beans.property.SimpleLongProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import test.javafx.beans.InvalidationListenerMock;
import test.javafx.beans.value.ChangeListenerMock;
import javafx.beans.value.ObservableLongValueStub;
import javafx.beans.value.ObservableValueStub;
import org.junit.Before;
import org.junit.Test;
public class LongPropertyBaseTest {
private static final Object NO_BEAN = null;
private static final String NO_NAME_1 = null;
private static final String NO_NAME_2 = "";
private static final Long UNDEFINED = Long.MAX_VALUE;
private static final long VALUE_1 = 42;
private static final long VALUE_2 = 12345;
private LongPropertyMock property;
private InvalidationListenerMock invalidationListener;
private ChangeListenerMock<Number> changeListener;
@Before
public void setUp() throws Exception {
property = new LongPropertyMock();
invalidationListener = new InvalidationListenerMock();
changeListener = new ChangeListenerMock<Number>(UNDEFINED);
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
final LongProperty p1 = new SimpleLongProperty();
assertEquals(0, p1.get());
assertEquals(Long.valueOf(0), p1.getValue());
assertFalse(property.isBound());
final LongProperty p2 = new SimpleLongProperty(-VALUE_1);
assertEquals(-VALUE_1, p2.get());
assertEquals(Long.valueOf(-VALUE_1), p2.getValue());
assertFalse(property.isBound());
}
@Test
public void testInvalidationListener() {
attachInvalidationListener();
property.set(VALUE_2);
invalidationListener.check(property, 1);
property.removeListener(invalidationListener);
invalidationListener.reset();
property.set(VALUE_1);
invalidationListener.check(null, 0);
}
@Test
public void testChangeListener() {
attachChangeListener();
property.set(VALUE_2);
changeListener.check(property, 0L, VALUE_2, 1);
property.removeListener(changeListener);
changeListener.reset();
property.set(VALUE_1);
changeListener.check(null, UNDEFINED, UNDEFINED, 0);
}
@Test
public void testLazySet() {
attachInvalidationListener();
property.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(1);
invalidationListener.check(property, 1);
property.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(0);
invalidationListener.check(null, 0);
property.set(VALUE_1);
property.set(-VALUE_1);
assertEquals(-VALUE_1, property.get());
property.check(1);
invalidationListener.check(property, 1);
}
@Test
public void testEagerSet() {
attachChangeListener();
property.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(1);
changeListener.check(property, 0L, VALUE_2, 1);
property.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(0);
changeListener.check(null, UNDEFINED, UNDEFINED, 0);
property.set(VALUE_1);
property.set(-VALUE_1);
assertEquals(-VALUE_1, property.get());
property.check(2);
changeListener.check(property, VALUE_1, -VALUE_1, 2);
}
@Test
public void testLazySetValue() {
attachInvalidationListener();
property.setValue(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(1);
invalidationListener.check(property, 1);
property.setValue(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(0);
invalidationListener.check(null, 0);
property.setValue(VALUE_1);
property.setValue(-VALUE_1);
assertEquals(-VALUE_1, property.get());
property.check(1);
invalidationListener.check(property, 1);
}
@Test
public void testEagerSetValue() {
attachChangeListener();
property.setValue(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(1);
changeListener.check(property, 0L, VALUE_2, 1);
property.setValue(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(0);
changeListener.check(null, UNDEFINED, UNDEFINED, 0);
property.setValue(VALUE_1);
property.setValue(-VALUE_1);
assertEquals(-VALUE_1, property.get());
property.check(2);
changeListener.check(property, VALUE_1, -VALUE_1, 2);
}
@Test(expected=RuntimeException.class)
public void testSetBoundValue() {
final LongProperty v = new SimpleLongProperty(VALUE_1);
property.bind(v);
property.set(VALUE_1);
}
@Test
public void testLazyBind() {
attachInvalidationListener();
final ObservableLongValueStub v = new ObservableLongValueStub(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get());
assertTrue(property.isBound());
property.check(1);
invalidationListener.check(property, 1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(1);
invalidationListener.check(property, 1);
v.set(VALUE_1);
v.set(-VALUE_1);
assertEquals(-VALUE_1, property.get());
property.check(1);
invalidationListener.check(property, 1);
v.set(VALUE_1);
v.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(1);
invalidationListener.check(property, 1);
}
@Test
public void testEagerBind() {
attachChangeListener();
final ObservableLongValueStub v = new ObservableLongValueStub(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get());
assertTrue(property.isBound());
property.check(1);
changeListener.check(property, 0L, VALUE_1, 1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(1);
changeListener.check(property, VALUE_1, VALUE_2, 1);
v.set(VALUE_1);
v.set(-VALUE_1);
assertEquals(-VALUE_1, property.get());
property.check(2);
changeListener.check(property, VALUE_1, -VALUE_1, 2);
v.set(VALUE_1);
v.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(2);
changeListener.check(property, -VALUE_1, VALUE_1, 1);
}
@Test
public void testLazyBindObservableValue() {
final long value1 = 9876543212345L;
final long value2 = -123456789098765L;
attachInvalidationListener();
final ObservableValueStub<Number> v = new ObservableValueStub<Number>(value1);
property.bind(v);
assertEquals(value1, property.get());
assertTrue(property.isBound());
property.check(1);
invalidationListener.check(property, 1);
v.set(value2);
assertEquals(value2, property.get());
property.check(1);
invalidationListener.check(property, 1);
v.set(value1);
v.set(value2);
assertEquals(value2, property.get());
property.check(1);
invalidationListener.check(property, 1);
v.set(value1);
v.set(value1);
assertEquals(value1, property.get());
property.check(1);
invalidationListener.check(property, 1);
v.set(null);
assertEquals(0L, property.get());
property.check(1);
invalidationListener.check(property, 1);
}
@Test
public void testEagerBindObservableValue() {
final long value1 = 9876543212345L;
final long value2 = -123456789098765L;
attachChangeListener();
final ObservableValueStub<Number> v = new ObservableValueStub<Number>(value1);
property.bind(v);
assertEquals(value1, property.get());
assertTrue(property.isBound());
property.check(1);
changeListener.check(property, 0L, value1, 1);
v.set(value2);
assertEquals(value2, property.get());
property.check(1);
changeListener.check(property, value1, value2, 1);
v.set(value1);
v.set(value2);
assertEquals(value2, property.get());
property.check(2);
changeListener.check(property, value1, value2, 2);
v.set(value1);
v.set(value1);
assertEquals(value1, property.get());
property.check(2);
changeListener.check(property, value2, value1, 1);
}
@Test(expected=NullPointerException.class)
public void testBindToNull() {
property.bind(null);
}
@Test
public void testRebind() {
attachInvalidationListener();
final LongProperty v1 = new SimpleLongProperty(VALUE_1);
final LongProperty v2 = new SimpleLongProperty(VALUE_2);
property.bind(v1);
property.get();
property.reset();
invalidationListener.reset();
property.bind(v2);
assertEquals(VALUE_2, property.get());
assertTrue(property.isBound());
assertEquals(1, property.counter);
invalidationListener.check(property, 1);
property.reset();
v1.set(-VALUE_1);
assertEquals(VALUE_2, property.get());
assertEquals(0, property.counter);
invalidationListener.check(null, 0);
property.reset();
v2.set(-VALUE_2);
assertEquals(-VALUE_2, property.get());
assertEquals(1, property.counter);
invalidationListener.check(property, 1);
property.reset();
property.bind(v2);
assertEquals(-VALUE_2, property.get());
assertTrue(property.isBound());
assertEquals(0, property.counter);
invalidationListener.check(null, 0);
}
@Test
public void testUnbind() {
attachInvalidationListener();
final LongProperty v = new SimpleLongProperty(VALUE_1);
property.bind(v);
property.unbind();
assertEquals(VALUE_1, property.get());
assertFalse(property.isBound());
property.reset();
invalidationListener.reset();
v.set(VALUE_2);
assertEquals(VALUE_1, property.get());
assertEquals(0, property.counter);
invalidationListener.check(null, 0);
property.reset();
property.set(-VALUE_1);
assertEquals(-VALUE_1, property.get());
assertEquals(1, property.counter);
invalidationListener.check(property, 1);
}
@Test
public void testUnbindObservableValue() {
final long value1 = 9876543212345L;
final long value2 = -123456789098765L;
attachInvalidationListener();
final ObservableValueStub<Number> v = new ObservableValueStub<Number>(value1);
property.bind(v);
property.unbind();
assertEquals(value1, property.get());
assertFalse(property.isBound());
property.reset();
invalidationListener.reset();
v.set(value2);
assertEquals(value1, property.get());
assertEquals(0, property.counter);
invalidationListener.check(null, 0);
property.reset();
property.set(value2);
assertEquals(value2, property.get());
assertEquals(1, property.counter);
invalidationListener.check(property, 1);
}
@Test
public void testAddingListenerWillAlwaysReceiveInvalidationEvent() {
final LongProperty v = new SimpleLongProperty(VALUE_1);
final InvalidationListenerMock listener2 = new InvalidationListenerMock();
final InvalidationListenerMock listener3 = new InvalidationListenerMock();
property.set(VALUE_1);
property.addListener(listener2);
listener2.reset();
property.set(-VALUE_1);
listener2.check(property, 1);
property.bind(v);
v.set(VALUE_2);
property.addListener(listener3);
v.get();
listener3.reset();
v.set(-VALUE_2);
listener3.check(property, 1);
}
@Test
public void testToString() {
final long value1 = 1234567890987654321L;
final long value2 = -987654321012345678L;
final LongProperty v = new SimpleLongProperty(value2);
property.set(value1);
assertEquals("LongProperty [value: " + value1 + "]", property.toString());
property.bind(v);
assertEquals("LongProperty [bound, invalid]", property.toString());
property.get();
assertEquals("LongProperty [bound, value: " + value2 + "]", property.toString());
v.set(value1);
assertEquals("LongProperty [bound, invalid]", property.toString());
property.get();
assertEquals("LongProperty [bound, value: " + value1 + "]", property.toString());
final Object bean = new Object();
final String name = "My name";
final LongProperty v1 = new LongPropertyMock(bean, name);
assertEquals("LongProperty [bean: " + bean.toString() + ", name: My name, value: " + 0L + "]", v1.toString());
v1.set(value1);
assertEquals("LongProperty [bean: " + bean.toString() + ", name: My name, value: " + value1 + "]", v1.toString());
final LongProperty v2 = new LongPropertyMock(bean, NO_NAME_1);
assertEquals("LongProperty [bean: " + bean.toString() + ", value: " + 0L + "]", v2.toString());
v2.set(value1);
assertEquals("LongProperty [bean: " + bean.toString() + ", value: " + value1 + "]", v2.toString());
final LongProperty v3 = new LongPropertyMock(bean, NO_NAME_2);
assertEquals("LongProperty [bean: " + bean.toString() + ", value: " + 0L + "]", v3.toString());
v3.set(value1);
assertEquals("LongProperty [bean: " + bean.toString() + ", value: " + value1 + "]", v3.toString());
final LongProperty v4 = new LongPropertyMock(NO_BEAN, name);
assertEquals("LongProperty [name: My name, value: " + 0L + "]", v4.toString());
v4.set(value1);
assertEquals("LongProperty [name: My name, value: " + value1 + "]", v4.toString());
}
private static class LongPropertyMock extends LongPropertyBase {
private final Object bean;
private final String name;
private int counter;
private LongPropertyMock() {
this.bean = NO_BEAN;
this.name = NO_NAME_1;
}
private LongPropertyMock(Object bean, String name) {
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
