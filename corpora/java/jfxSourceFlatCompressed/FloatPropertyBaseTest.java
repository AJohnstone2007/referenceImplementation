package test.javafx.beans.property;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.FloatPropertyBase;
import javafx.beans.property.SimpleFloatProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import test.javafx.beans.InvalidationListenerMock;
import test.javafx.beans.value.ChangeListenerMock;
import javafx.beans.value.ObservableFloatValueStub;
import javafx.beans.value.ObservableValueStub;
import org.junit.Before;
import org.junit.Test;
public class FloatPropertyBaseTest {
private static final Object NO_BEAN = null;
private static final String NO_NAME_1 = null;
private static final String NO_NAME_2 = "";
private static final Float UNDEFINED = Float.MAX_VALUE;
private static final float EPSILON = 1e-6f;
private static final float PI = (float)Math.PI;
private static final float E = (float)Math.E;
private FloatPropertyMock property;
private InvalidationListenerMock invalidationListener;
private ChangeListenerMock<Number> changeListener;
@Before
public void setUp() throws Exception {
property = new FloatPropertyMock();
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
final FloatProperty p1 = new SimpleFloatProperty();
assertEquals(0.0f, p1.get(), EPSILON);
assertEquals(Float.valueOf(0.0f), p1.getValue(), EPSILON);
assertFalse(property.isBound());
final FloatProperty p2 = new SimpleFloatProperty(-PI);
assertEquals(-PI, p2.get(), EPSILON);
assertEquals(Float.valueOf(-PI), p2.getValue(), EPSILON);
assertFalse(property.isBound());
}
@Test
public void testInvalidationListener() {
attachInvalidationListener();
property.set(E);
invalidationListener.check(property, 1);
property.removeListener(invalidationListener);
invalidationListener.reset();
property.set(PI);
invalidationListener.check(null, 0);
}
@Test
public void testChangeListener() {
attachChangeListener();
property.set(E);
changeListener.check(property, 0.0f, E, 1);
property.removeListener(changeListener);
changeListener.reset();
property.set(PI);
changeListener.check(null, UNDEFINED, UNDEFINED, 0);
}
@Test
public void testLazySet() {
attachInvalidationListener();
property.set(E);
assertEquals(E, property.get(), EPSILON);
property.check(1);
invalidationListener.check(property, 1);
property.set(E);
assertEquals(E, property.get(), EPSILON);
property.check(0);
invalidationListener.check(null, 0);
property.set(PI);
property.set(-PI);
assertEquals(-PI, property.get(), EPSILON);
property.check(1);
invalidationListener.check(property, 1);
}
@Test
public void testEagerSet() {
attachChangeListener();
property.set(E);
assertEquals(E, property.get(), EPSILON);
property.check(1);
changeListener.check(property, 0.0f, E, 1);
property.set(E);
assertEquals(E, property.get(), EPSILON);
property.check(0);
changeListener.check(null, UNDEFINED, UNDEFINED, 0);
property.set(PI);
property.set(-PI);
assertEquals(-PI, property.get(), EPSILON);
property.check(2);
changeListener.check(property, PI, -PI, 2);
}
@Test
public void testLazySetValue() {
attachInvalidationListener();
property.setValue(E);
assertEquals(E, property.get(), EPSILON);
property.check(1);
invalidationListener.check(property, 1);
property.setValue(E);
assertEquals(E, property.get(), EPSILON);
property.check(0);
invalidationListener.check(null, 0);
property.setValue(PI);
property.setValue(-PI);
assertEquals(-PI, property.get(), EPSILON);
property.check(1);
invalidationListener.check(property, 1);
}
@Test
public void testEagerSetValue() {
attachChangeListener();
property.setValue(E);
assertEquals(E, property.get(), EPSILON);
property.check(1);
changeListener.check(property, 0.0f, E, 1);
property.setValue(E);
assertEquals(E, property.get(), EPSILON);
property.check(0);
changeListener.check(null, UNDEFINED, UNDEFINED, 0);
property.setValue(PI);
property.setValue(-PI);
assertEquals(-PI, property.get(), EPSILON);
property.check(2);
changeListener.check(property, PI, -PI, 2);
}
@Test(expected=RuntimeException.class)
public void testSetBoundValue() {
final FloatProperty v = new SimpleFloatProperty(PI);
property.bind(v);
property.set(PI);
}
@Test
public void testLazyBind() {
attachInvalidationListener();
final FloatProperty v = new SimpleFloatProperty(PI);
property.reset();
property.bind(v);
assertEquals(PI, property.get(), EPSILON);
assertTrue(property.isBound());
property.check(1);
invalidationListener.check(property, 1);
v.set(E);
assertEquals(E, property.get(), EPSILON);
property.check(1);
invalidationListener.check(property, 1);
v.set(PI);
v.set(-PI);
assertEquals(-PI, property.get(), EPSILON);
property.check(1);
invalidationListener.check(property, 1);
v.set(PI);
v.set(PI);
assertEquals(PI, property.get(), EPSILON);
property.check(1);
invalidationListener.check(property, 1);
}
@Test
public void testEagerBind() {
attachChangeListener();
final ObservableFloatValueStub v = new ObservableFloatValueStub(PI);
property.reset();
property.bind(v);
assertEquals(PI, property.get(), EPSILON);
assertTrue(property.isBound());
property.check(1);
changeListener.check(property, 0.0f, PI, 1);
v.set(E);
assertEquals(E, property.get(), EPSILON);
property.check(1);
changeListener.check(property, PI, E, 1);
v.set(PI);
v.set(-PI);
assertEquals(-PI, property.get(), EPSILON);
property.check(2);
changeListener.check(property, PI, -PI, 2);
v.set(PI);
v.set(PI);
assertEquals(PI, property.get(), EPSILON);
property.check(2);
changeListener.check(property, -PI, PI, 1);
}
@Test
public void testLazyBindObservableValue() {
final float value1 = (float)Math.PI;
final float value2 = (float)Math.E;
attachInvalidationListener();
final ObservableValueStub<Number> v = new ObservableValueStub<Number>(value1);
property.bind(v);
assertEquals(value1, property.get(), EPSILON);
assertTrue(property.isBound());
property.check(1);
invalidationListener.check(property, 1);
v.set(value2);
assertEquals(value2, property.get(), EPSILON);
property.check(1);
invalidationListener.check(property, 1);
v.set(value1);
v.set(value2);
assertEquals(value2, property.get(), EPSILON);
property.check(1);
invalidationListener.check(property, 1);
v.set(value1);
v.set(value1);
assertEquals(value1, property.get(), EPSILON);
property.check(1);
invalidationListener.check(property, 1);
v.set(null);
assertEquals(0.0f, property.get(), EPSILON);
property.check(1);
invalidationListener.check(property, 1);
}
@Test
public void testEagerBindObservableValue() {
final float value1 = (float)Math.PI;
final float value2 = (float)Math.E;
attachChangeListener();
final ObservableValueStub<Number> v = new ObservableValueStub<Number>(value1);
property.bind(v);
assertEquals(value1, property.get(), EPSILON);
assertTrue(property.isBound());
property.check(1);
changeListener.check(property, 0.0f, value1, 1);
v.set(value2);
assertEquals(value2, property.get(), EPSILON);
property.check(1);
changeListener.check(property, value1, value2, 1);
v.set(value1);
v.set(value2);
assertEquals(value2, property.get(), EPSILON);
property.check(2);
changeListener.check(property, value1, value2, 2);
v.set(value1);
v.set(value1);
assertEquals(value1, property.get(), EPSILON);
property.check(2);
changeListener.check(property, value2, value1, 1);
v.set(null);
assertEquals(0.0f, property.get(), EPSILON);
property.check(1);
changeListener.check(property, value1, 0.0f, 1);
}
@Test(expected=NullPointerException.class)
public void testBindToNull() {
property.bind(null);
}
@Test
public void testRebind() {
attachInvalidationListener();
final FloatProperty v1 = new SimpleFloatProperty(PI);
final FloatProperty v2 = new SimpleFloatProperty(E);
property.bind(v1);
property.get();
property.reset();
invalidationListener.reset();
property.bind(v2);
assertEquals(E, property.get(), EPSILON);
assertTrue(property.isBound());
assertEquals(1, property.counter);
invalidationListener.check(property, 1);
property.reset();
v1.set(-PI);
assertEquals(E, property.get(), EPSILON);
assertEquals(0, property.counter);
invalidationListener.check(null, 0);
property.reset();
v2.set(-E);
assertEquals(-E, property.get(), EPSILON);
assertEquals(1, property.counter);
invalidationListener.check(property, 1);
property.reset();
property.bind(v2);
assertEquals(-E, property.get(), EPSILON);
assertTrue(property.isBound());
assertEquals(0, property.counter);
invalidationListener.check(null, 0);
}
@Test
public void testUnbind() {
attachInvalidationListener();
final FloatProperty v = new SimpleFloatProperty(PI);
property.bind(v);
property.unbind();
assertEquals(PI, property.get(), EPSILON);
assertFalse(property.isBound());
property.reset();
invalidationListener.reset();
v.set(E);
assertEquals(PI, property.get(), EPSILON);
assertEquals(0, property.counter);
invalidationListener.check(null, 0);
property.reset();
property.set(-PI);
assertEquals(-PI, property.get(), EPSILON);
assertEquals(1, property.counter);
invalidationListener.check(property, 1);
}
@Test
public void testUnbindObservableValue() {
final float value1 = (float)Math.PI;
final float value2 = (float)Math.E;
attachInvalidationListener();
final ObservableValueStub<Number> v = new ObservableValueStub<Number>(value1);
property.bind(v);
property.unbind();
assertEquals(value1, property.get(), EPSILON);
assertFalse(property.isBound());
property.reset();
invalidationListener.reset();
v.set(value2);
assertEquals(value1, property.get(), EPSILON);
assertEquals(0, property.counter);
invalidationListener.check(null, 0);
property.reset();
property.set(value2);
assertEquals(value2, property.get(), EPSILON);
assertEquals(1, property.counter);
invalidationListener.check(property, 1);
}
@Test
public void testAddingListenerWillAlwaysReceiveInvalidationEvent() {
final FloatProperty v = new SimpleFloatProperty(PI);
final InvalidationListenerMock listener2 = new InvalidationListenerMock();
final InvalidationListenerMock listener3 = new InvalidationListenerMock();
property.set(PI);
property.addListener(listener2);
listener2.reset();
property.set(-PI);
listener2.check(property, 1);
property.bind(v);
v.set(E);
property.addListener(listener3);
v.get();
listener3.reset();
v.set(-E);
listener3.check(property, 1);
}
@Test
public void testToString() {
final float value1 = (float)Math.E;
final float value2 = (float)-Math.PI;
final FloatProperty v = new SimpleFloatProperty(value2);
property.set(value1);
assertEquals("FloatProperty [value: " + value1 + "]", property.toString());
property.bind(v);
assertEquals("FloatProperty [bound, invalid]", property.toString());
property.get();
assertEquals("FloatProperty [bound, value: " + value2 + "]", property.toString());
v.set(value1);
assertEquals("FloatProperty [bound, invalid]", property.toString());
property.get();
assertEquals("FloatProperty [bound, value: " + value1 + "]", property.toString());
final Object bean = new Object();
final String name = "My name";
final FloatProperty v1 = new FloatPropertyMock(bean, name);
assertEquals("FloatProperty [bean: " + bean.toString() + ", name: My name, value: " + 0.0f + "]", v1.toString());
v1.set(value1);
assertEquals("FloatProperty [bean: " + bean.toString() + ", name: My name, value: " + value1 + "]", v1.toString());
final FloatProperty v2 = new FloatPropertyMock(bean, NO_NAME_1);
assertEquals("FloatProperty [bean: " + bean.toString() + ", value: " + 0.0f + "]", v2.toString());
v2.set(value1);
assertEquals("FloatProperty [bean: " + bean.toString() + ", value: " + value1 + "]", v2.toString());
final FloatProperty v3 = new FloatPropertyMock(bean, NO_NAME_2);
assertEquals("FloatProperty [bean: " + bean.toString() + ", value: " + 0.0f + "]", v3.toString());
v3.set(value1);
assertEquals("FloatProperty [bean: " + bean.toString() + ", value: " + value1 + "]", v3.toString());
final FloatProperty v4 = new FloatPropertyMock(NO_BEAN, name);
assertEquals("FloatProperty [name: My name, value: " + 0.0f + "]", v4.toString());
v4.set(value1);
assertEquals("FloatProperty [name: My name, value: " + value1 + "]", v4.toString());
}
private static class FloatPropertyMock extends FloatPropertyBase {
private final Object bean;
private final String name;
private int counter;
private FloatPropertyMock() {
this.bean = NO_BEAN;
this.name = NO_NAME_1;
}
private FloatPropertyMock(Object bean, String name) {
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
