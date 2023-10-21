package test.javafx.beans.property;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import test.javafx.beans.InvalidationListenerMock;
import test.javafx.beans.value.ChangeListenerMock;
import javafx.beans.value.ObservableDoubleValueStub;
import javafx.beans.value.ObservableObjectValueStub;
import org.junit.Before;
import org.junit.Test;
public class ReadOnlyDoubleWrapperTest {
private static final Double UNDEFINED = null;
private static final double DEFAULT = 0.0;
private static final double VALUE_1 = Math.PI;
private static final double VALUE_2 = Math.E;
private static final double EPSILON = 1e-12;
private ReadOnlyDoubleWrapperMock property;
private ReadOnlyDoubleProperty readOnlyProperty;
private InvalidationListenerMock internalInvalidationListener;
private InvalidationListenerMock publicInvalidationListener;
private ChangeListenerMock<Number> internalChangeListener;
private ChangeListenerMock<Number> publicChangeListener;
@Before
public void setUp() throws Exception {
property = new ReadOnlyDoubleWrapperMock();
readOnlyProperty = property.getReadOnlyProperty();
internalInvalidationListener = new InvalidationListenerMock();
publicInvalidationListener = new InvalidationListenerMock();
internalChangeListener = new ChangeListenerMock<Number>(UNDEFINED);
publicChangeListener = new ChangeListenerMock<Number>(UNDEFINED);
}
private void attachInvalidationListeners() {
property.addListener(internalInvalidationListener);
readOnlyProperty.addListener(publicInvalidationListener);
property.get();
readOnlyProperty.get();
internalInvalidationListener.reset();
publicInvalidationListener.reset();
}
private void attachInternalChangeListener() {
property.addListener(internalChangeListener);
property.get();
internalChangeListener.reset();
}
private void attachPublicChangeListener() {
readOnlyProperty.addListener(publicChangeListener);
readOnlyProperty.get();
publicChangeListener.reset();
}
@Test
public void testConstructor_NoArguments() {
final ReadOnlyDoubleWrapper p1 = new ReadOnlyDoubleWrapper();
assertEquals(DEFAULT, p1.get(), EPSILON);
assertEquals((Double)DEFAULT, p1.getValue(), EPSILON);
assertFalse(property.isBound());
assertEquals(null, p1.getBean());
assertEquals("", p1.getName());
final ReadOnlyDoubleProperty r1 = p1.getReadOnlyProperty();
assertEquals(DEFAULT, r1.get(), EPSILON);
assertEquals((Double)DEFAULT, r1.getValue(), EPSILON);
assertEquals(null, r1.getBean());
assertEquals("", r1.getName());
}
@Test
public void testConstructor_InitialValue() {
final ReadOnlyDoubleWrapper p1 = new ReadOnlyDoubleWrapper(VALUE_1);
assertEquals(VALUE_1, p1.get(), EPSILON);
assertEquals((Double)VALUE_1, p1.getValue(), EPSILON);
assertFalse(property.isBound());
assertEquals(null, p1.getBean());
assertEquals("", p1.getName());
final ReadOnlyDoubleProperty r1 = p1.getReadOnlyProperty();
assertEquals(VALUE_1, r1.get(), EPSILON);
assertEquals((Double)VALUE_1, r1.getValue(), EPSILON);
assertEquals(null, r1.getBean());
assertEquals("", r1.getName());
}
@Test
public void testConstructor_Bean_Name() {
final Object bean = new Object();
final String name = "My name";
final ReadOnlyDoubleWrapper p1 = new ReadOnlyDoubleWrapper(bean, name);
assertEquals(DEFAULT, p1.get(), EPSILON);
assertEquals((Double)DEFAULT, p1.getValue(), EPSILON);
assertFalse(property.isBound());
assertEquals(bean, p1.getBean());
assertEquals(name, p1.getName());
final ReadOnlyDoubleProperty r1 = p1.getReadOnlyProperty();
assertEquals(DEFAULT, r1.get(), EPSILON);
assertEquals((Double)DEFAULT, r1.getValue(), EPSILON);
assertEquals(bean, r1.getBean());
assertEquals(name, r1.getName());
}
@Test
public void testConstructor_Bean_Name_InitialValue() {
final Object bean = new Object();
final String name = "My name";
final ReadOnlyDoubleWrapper p1 = new ReadOnlyDoubleWrapper(bean, name, VALUE_1);
assertEquals(VALUE_1, p1.get(), EPSILON);
assertEquals((Double)VALUE_1, p1.getValue(), EPSILON);
assertFalse(property.isBound());
assertEquals(bean, p1.getBean());
assertEquals(name, p1.getName());
final ReadOnlyDoubleProperty r1 = p1.getReadOnlyProperty();
assertEquals(VALUE_1, r1.get(), EPSILON);
assertEquals((Double)VALUE_1, r1.getValue(), EPSILON);
assertEquals(bean, r1.getBean());
assertEquals(name, r1.getName());
}
@Test
public void testLazySet() {
attachInvalidationListeners();
property.set(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(readOnlyProperty, 1);
property.set(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(0);
internalInvalidationListener.check(null, 0);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(null, 0);
property.set(VALUE_2);
property.set(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(readOnlyProperty, 1);
}
@Test
public void testInternalEagerSet() {
attachInternalChangeListener();
property.set(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(1);
internalChangeListener.check(property, DEFAULT, VALUE_1, 1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
property.set(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(0);
internalChangeListener.check(null, UNDEFINED, UNDEFINED, 0);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
property.set(VALUE_2);
property.set(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(2);
internalChangeListener.check(property, VALUE_2, VALUE_1, 2);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
}
@Test
public void testPublicEagerSet() {
attachPublicChangeListener();
property.set(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicChangeListener.check(readOnlyProperty, DEFAULT, VALUE_1, 1);
property.set(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(0);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicChangeListener.check(null, UNDEFINED, UNDEFINED, 0);
property.set(VALUE_2);
property.set(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(2);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicChangeListener.check(readOnlyProperty, VALUE_2, VALUE_1, 2);
}
@Test
public void testLazySetValue() {
attachInvalidationListeners();
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(readOnlyProperty, 1);
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(0);
internalInvalidationListener.check(null, 0);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(null, 0);
property.setValue(VALUE_2);
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(readOnlyProperty, 1);
}
@Test
public void testInternalEagerSetValue() {
attachInternalChangeListener();
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(1);
internalChangeListener.check(property, DEFAULT, VALUE_1, 1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(0);
internalChangeListener.check(null, UNDEFINED, UNDEFINED, 0);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
property.setValue(VALUE_2);
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(2);
internalChangeListener.check(property, VALUE_2, VALUE_1, 2);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
}
@Test
public void testPublicEagerSetValue() {
attachPublicChangeListener();
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicChangeListener.check(readOnlyProperty, DEFAULT, VALUE_1, 1);
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(0);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicChangeListener.check(null, UNDEFINED, UNDEFINED, 0);
property.setValue(VALUE_2);
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(2);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicChangeListener.check(readOnlyProperty, VALUE_2, VALUE_1, 2);
}
@Test(expected=RuntimeException.class)
public void testSetBoundValue() {
final DoubleProperty v = new SimpleDoubleProperty(VALUE_1);
property.bind(v);
property.set(VALUE_1);
}
@Test
public void testLazyBind_primitive() {
attachInvalidationListeners();
final ObservableDoubleValueStub v = new ObservableDoubleValueStub(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get(), EPSILON);
assertTrue(property.isBound());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(readOnlyProperty, 1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get(), EPSILON);
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_2, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(readOnlyProperty, 1);
v.set(VALUE_1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get(), EPSILON);
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_2, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(readOnlyProperty, 1);
v.set(VALUE_1);
v.set(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(readOnlyProperty, 1);
}
@Test
public void testInternalEagerBind_primitive() {
attachInternalChangeListener();
final ObservableDoubleValueStub v = new ObservableDoubleValueStub(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get(), EPSILON);
assertTrue(property.isBound());
property.check(1);
internalChangeListener.check(property, DEFAULT, VALUE_1, 1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get(), EPSILON);
property.check(1);
internalChangeListener.check(property, VALUE_1, VALUE_2, 1);
assertEquals(VALUE_2, readOnlyProperty.get(), EPSILON);
v.set(VALUE_1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get(), EPSILON);
property.check(2);
internalChangeListener.check(property, VALUE_1, VALUE_2, 2);
assertEquals(VALUE_2, readOnlyProperty.get(), EPSILON);
v.set(VALUE_1);
v.set(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(2);
internalChangeListener.check(property, VALUE_2, VALUE_1, 1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
}
@Test
public void testPublicEagerBind_primitive() {
attachPublicChangeListener();
final ObservableDoubleValueStub v = new ObservableDoubleValueStub(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get(), EPSILON);
assertTrue(property.isBound());
property.check(1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicChangeListener.check(readOnlyProperty, DEFAULT, VALUE_1, 1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get(), EPSILON);
property.check(1);
assertEquals(VALUE_2, readOnlyProperty.get(), EPSILON);
publicChangeListener.check(readOnlyProperty, VALUE_1, VALUE_2, 1);
v.set(VALUE_1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get(), EPSILON);
property.check(2);
assertEquals(VALUE_2, readOnlyProperty.get(), EPSILON);
publicChangeListener.check(readOnlyProperty, VALUE_1, VALUE_2, 2);
v.set(VALUE_1);
v.set(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(2);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicChangeListener.check(readOnlyProperty, VALUE_2, VALUE_1, 1);
}
@Test
public void testLazyBind_generic() {
attachInvalidationListeners();
final ObservableObjectValueStub<Double> v = new ObservableObjectValueStub<Double>(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get(), EPSILON);
assertTrue(property.isBound());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(readOnlyProperty, 1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get(), EPSILON);
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_2, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(readOnlyProperty, 1);
v.set(VALUE_1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get(), EPSILON);
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_2, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(readOnlyProperty, 1);
v.set(VALUE_1);
v.set(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(readOnlyProperty, 1);
}
@Test
public void testInternalEagerBind_generic() {
attachInternalChangeListener();
final ObservableObjectValueStub<Double> v = new ObservableObjectValueStub<Double>(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get(), EPSILON);
assertTrue(property.isBound());
property.check(1);
internalChangeListener.check(property, DEFAULT, VALUE_1, 1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get(), EPSILON);
property.check(1);
internalChangeListener.check(property, VALUE_1, VALUE_2, 1);
assertEquals(VALUE_2, readOnlyProperty.get(), EPSILON);
v.set(VALUE_1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get(), EPSILON);
property.check(2);
internalChangeListener.check(property, VALUE_1, VALUE_2, 2);
assertEquals(VALUE_2, readOnlyProperty.get(), EPSILON);
v.set(VALUE_1);
v.set(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(2);
internalChangeListener.check(property, VALUE_2, VALUE_1, 1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
}
@Test
public void testPublicEagerBind_generic() {
attachPublicChangeListener();
final ObservableObjectValueStub<Double> v = new ObservableObjectValueStub<Double>(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get(), EPSILON);
assertTrue(property.isBound());
property.check(1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicChangeListener.check(readOnlyProperty, DEFAULT, VALUE_1, 1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get(), EPSILON);
property.check(1);
assertEquals(VALUE_2, readOnlyProperty.get(), EPSILON);
publicChangeListener.check(readOnlyProperty, VALUE_1, VALUE_2, 1);
v.set(VALUE_1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get(), EPSILON);
property.check(2);
assertEquals(VALUE_2, readOnlyProperty.get(), EPSILON);
publicChangeListener.check(readOnlyProperty, VALUE_1, VALUE_2, 2);
v.set(VALUE_1);
v.set(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(2);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicChangeListener.check(readOnlyProperty, VALUE_2, VALUE_1, 1);
}
@Test(expected=NullPointerException.class)
public void testBindToNull() {
property.bind(null);
}
@Test
public void testRebind() {
attachInvalidationListeners();
final DoubleProperty v1 = new SimpleDoubleProperty(VALUE_1);
final DoubleProperty v2 = new SimpleDoubleProperty(VALUE_2);
property.bind(v1);
property.get();
readOnlyProperty.get();
property.reset();
internalInvalidationListener.reset();
publicInvalidationListener.reset();
property.bind(v2);
assertEquals(VALUE_2, property.get(), EPSILON);
assertTrue(property.isBound());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_2, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(readOnlyProperty, 1);
v2.set(VALUE_1);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(readOnlyProperty, 1);
v1.set(VALUE_2);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(0);
internalInvalidationListener.check(null, 0);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(null, 0);
property.bind(v2);
assertEquals(VALUE_1, property.get(), EPSILON);
assertTrue(property.isBound());
property.check(0);
internalInvalidationListener.check(null, 0);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(null, 0);
}
@Test
public void testUnbind() {
attachInvalidationListeners();
final DoubleProperty v = new SimpleDoubleProperty(VALUE_1);
property.bind(v);
property.unbind();
assertEquals(VALUE_1, property.get(), EPSILON);
assertFalse(property.isBound());
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
property.reset();
internalInvalidationListener.reset();
publicInvalidationListener.reset();
v.set(VALUE_2);
assertEquals(VALUE_1, property.get(), EPSILON);
property.check(0);
internalInvalidationListener.check(null, 0);
assertEquals(VALUE_1, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(null, 0);
property.set(VALUE_2);
assertEquals(VALUE_2, property.get(), EPSILON);
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_2, readOnlyProperty.get(), EPSILON);
publicInvalidationListener.check(readOnlyProperty, 1);
}
@Test
public void testAddingListenerWillAlwaysReceiveInvalidationEvent() {
final DoubleProperty v = new SimpleDoubleProperty(VALUE_1);
final InvalidationListenerMock internalListener2 = new InvalidationListenerMock();
final InvalidationListenerMock internalListener3 = new InvalidationListenerMock();
final InvalidationListenerMock publicListener2 = new InvalidationListenerMock();
final InvalidationListenerMock publicListener3 = new InvalidationListenerMock();
property.set(VALUE_1);
property.addListener(internalListener2);
internalListener2.reset();
property.set(VALUE_2);
internalListener2.check(property, 1);
property.set(VALUE_1);
readOnlyProperty.addListener(publicListener2);
publicListener2.reset();
property.set(VALUE_2);
publicListener2.check(readOnlyProperty, 1);
property.bind(v);
v.set(VALUE_2);
property.addListener(internalListener3);
v.get();
internalListener3.reset();
v.set(VALUE_1);
internalListener3.check(property, 1);
property.bind(v);
v.set(VALUE_2);
readOnlyProperty.addListener(publicListener3);
v.get();
publicListener3.reset();
v.set(VALUE_1);
publicListener3.check(readOnlyProperty, 1);
}
@Test
public void testRemoveListeners() {
attachInvalidationListeners();
attachInternalChangeListener();
property.removeListener(internalInvalidationListener);
property.removeListener(internalChangeListener);
property.get();
internalInvalidationListener.reset();
internalChangeListener.reset();
property.set(VALUE_1);
internalInvalidationListener.check(null, 0);
internalChangeListener.check(null, UNDEFINED, UNDEFINED, 0);
final ReadOnlyDoubleWrapper v1 = new ReadOnlyDoubleWrapper();
v1.removeListener(internalInvalidationListener);
v1.removeListener(internalChangeListener);
}
@Test
public void testNoReadOnlyPropertyCreated() {
final DoubleProperty v1 = new SimpleDoubleProperty(VALUE_1);
final ReadOnlyDoubleWrapper p1 = new ReadOnlyDoubleWrapper();
p1.set(VALUE_1);
p1.bind(v1);
assertEquals(VALUE_1, p1.get(), EPSILON);
v1.set(VALUE_2);
assertEquals(VALUE_2, p1.get(), EPSILON);
}
@Test
public void testToString() {
final DoubleProperty v1 = new SimpleDoubleProperty(VALUE_1);
property.set(VALUE_1);
assertEquals("DoubleProperty [value: " + VALUE_1 + "]", property.toString());
assertEquals("ReadOnlyDoubleProperty [value: " + VALUE_1 + "]", readOnlyProperty.toString());
property.bind(v1);
assertEquals("DoubleProperty [bound, invalid]", property.toString());
assertEquals("ReadOnlyDoubleProperty [value: " + VALUE_1 + "]", readOnlyProperty.toString());
property.get();
assertEquals("DoubleProperty [bound, value: " + VALUE_1 + "]", property.toString());
assertEquals("ReadOnlyDoubleProperty [value: " + VALUE_1 + "]", readOnlyProperty.toString());
v1.set(VALUE_2);
assertEquals("DoubleProperty [bound, invalid]", property.toString());
assertEquals("ReadOnlyDoubleProperty [value: " + VALUE_2 + "]", readOnlyProperty.toString());
property.get();
assertEquals("DoubleProperty [bound, value: " + VALUE_2 + "]", property.toString());
assertEquals("ReadOnlyDoubleProperty [value: " + VALUE_2 + "]", readOnlyProperty.toString());
final Object bean = new Object();
final String name = "My name";
final ReadOnlyDoubleWrapper v2 = new ReadOnlyDoubleWrapper(bean, name);
assertEquals("DoubleProperty [bean: " + bean.toString() + ", name: My name, value: " + DEFAULT + "]", v2.toString());
assertEquals("ReadOnlyDoubleProperty [bean: " + bean.toString() + ", name: My name, value: " + DEFAULT + "]", v2.getReadOnlyProperty().toString());
final ReadOnlyDoubleWrapper v3 = new ReadOnlyDoubleWrapper(bean, "");
assertEquals("DoubleProperty [bean: " + bean.toString() + ", value: " + DEFAULT + "]", v3.toString());
assertEquals("ReadOnlyDoubleProperty [bean: " + bean.toString() + ", value: " + DEFAULT + "]", v3.getReadOnlyProperty().toString());
final ReadOnlyDoubleWrapper v4 = new ReadOnlyDoubleWrapper(null, name);
assertEquals("DoubleProperty [name: My name, value: " + DEFAULT + "]", v4.toString());
assertEquals("ReadOnlyDoubleProperty [name: My name, value: " + DEFAULT + "]", v4.getReadOnlyProperty().toString());
}
private static class ReadOnlyDoubleWrapperMock extends ReadOnlyDoubleWrapper {
private int counter;
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
}
}
