package test.javafx.beans.property;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
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
public class ReadOnlyBooleanWrapperTest {
private static final Boolean UNDEFINED = null;
private static final boolean DEFAULT = false;
private static final boolean VALUE_1 = true;
private static final boolean VALUE_2 = false;
private ReadOnlyBooleanWrapperMock property;
private ReadOnlyBooleanProperty readOnlyProperty;
private InvalidationListenerMock internalInvalidationListener;
private InvalidationListenerMock publicInvalidationListener;
private ChangeListenerMock<Boolean> internalChangeListener;
private ChangeListenerMock<Boolean> publicChangeListener;
@Before
public void setUp() throws Exception {
property = new ReadOnlyBooleanWrapperMock();
readOnlyProperty = property.getReadOnlyProperty();
internalInvalidationListener = new InvalidationListenerMock();
publicInvalidationListener = new InvalidationListenerMock();
internalChangeListener = new ChangeListenerMock<Boolean>(UNDEFINED);
publicChangeListener = new ChangeListenerMock<Boolean>(UNDEFINED);
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
final ReadOnlyBooleanWrapper p1 = new ReadOnlyBooleanWrapper();
assertEquals(DEFAULT, p1.get());
assertEquals((Boolean)DEFAULT, p1.getValue());
assertFalse(property.isBound());
assertEquals(null, p1.getBean());
assertEquals("", p1.getName());
final ReadOnlyBooleanProperty r1 = p1.getReadOnlyProperty();
assertEquals(DEFAULT, r1.get());
assertEquals((Boolean)DEFAULT, r1.getValue());
assertEquals(null, r1.getBean());
assertEquals("", r1.getName());
}
@Test
public void testConstructor_InitialValue() {
final ReadOnlyBooleanWrapper p1 = new ReadOnlyBooleanWrapper(VALUE_1);
assertEquals(VALUE_1, p1.get());
assertEquals((Boolean)VALUE_1, p1.getValue());
assertFalse(property.isBound());
assertEquals(null, p1.getBean());
assertEquals("", p1.getName());
final ReadOnlyBooleanProperty r1 = p1.getReadOnlyProperty();
assertEquals(VALUE_1, r1.get());
assertEquals((Boolean)VALUE_1, r1.getValue());
assertEquals(null, r1.getBean());
assertEquals("", r1.getName());
}
@Test
public void testConstructor_Bean_Name() {
final Object bean = new Object();
final String name = "My name";
final ReadOnlyBooleanWrapper p1 = new ReadOnlyBooleanWrapper(bean, name);
assertEquals(DEFAULT, p1.get());
assertEquals((Boolean)DEFAULT, p1.getValue());
assertFalse(property.isBound());
assertEquals(bean, p1.getBean());
assertEquals(name, p1.getName());
final ReadOnlyBooleanProperty r1 = p1.getReadOnlyProperty();
assertEquals(DEFAULT, r1.get());
assertEquals((Boolean)DEFAULT, r1.getValue());
assertEquals(bean, r1.getBean());
assertEquals(name, r1.getName());
}
@Test
public void testConstructor_Bean_Name_InitialValue() {
final Object bean = new Object();
final String name = "My name";
final ReadOnlyBooleanWrapper p1 = new ReadOnlyBooleanWrapper(bean, name, VALUE_1);
assertEquals(VALUE_1, p1.get());
assertEquals((Boolean)VALUE_1, p1.getValue());
assertFalse(property.isBound());
assertEquals(bean, p1.getBean());
assertEquals(name, p1.getName());
final ReadOnlyBooleanProperty r1 = p1.getReadOnlyProperty();
assertEquals(VALUE_1, r1.get());
assertEquals((Boolean)VALUE_1, r1.getValue());
assertEquals(bean, r1.getBean());
assertEquals(name, r1.getName());
}
@Test
public void testLazySet() {
attachInvalidationListeners();
property.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get());
publicInvalidationListener.check(readOnlyProperty, 1);
property.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(0);
internalInvalidationListener.check(null, 0);
assertEquals(VALUE_1, readOnlyProperty.get());
publicInvalidationListener.check(null, 0);
property.set(VALUE_2);
property.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get());
publicInvalidationListener.check(readOnlyProperty, 1);
}
@Test
public void testInternalEagerSet() {
attachInternalChangeListener();
property.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(1);
internalChangeListener.check(property, DEFAULT, VALUE_1, 1);
assertEquals(VALUE_1, readOnlyProperty.get());
property.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(0);
internalChangeListener.check(null, UNDEFINED, UNDEFINED, 0);
assertEquals(VALUE_1, readOnlyProperty.get());
property.set(VALUE_2);
property.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(2);
internalChangeListener.check(property, VALUE_2, VALUE_1, 2);
assertEquals(VALUE_1, readOnlyProperty.get());
}
@Test
public void testPublicEagerSet() {
attachPublicChangeListener();
property.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(1);
assertEquals(VALUE_1, readOnlyProperty.get());
publicChangeListener.check(readOnlyProperty, VALUE_2, VALUE_1, 1);
property.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(0);
assertEquals(VALUE_1, readOnlyProperty.get());
publicChangeListener.check(null, UNDEFINED, UNDEFINED, 0);
property.set(VALUE_2);
property.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(2);
assertEquals(VALUE_1, readOnlyProperty.get());
publicChangeListener.check(readOnlyProperty, VALUE_2, VALUE_1, 2);
}
@Test
public void testLazySetValue() {
attachInvalidationListeners();
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get());
publicInvalidationListener.check(readOnlyProperty, 1);
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(0);
internalInvalidationListener.check(null, 0);
assertEquals(VALUE_1, readOnlyProperty.get());
publicInvalidationListener.check(null, 0);
property.setValue(VALUE_2);
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get());
publicInvalidationListener.check(readOnlyProperty, 1);
}
@Test
public void testInternalEagerSetValue() {
attachInternalChangeListener();
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(1);
internalChangeListener.check(property, VALUE_2, VALUE_1, 1);
assertEquals(VALUE_1, readOnlyProperty.get());
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(0);
internalChangeListener.check(null, UNDEFINED, UNDEFINED, 0);
assertEquals(VALUE_1, readOnlyProperty.get());
property.setValue(VALUE_2);
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(2);
internalChangeListener.check(property, VALUE_2, VALUE_1, 2);
assertEquals(VALUE_1, readOnlyProperty.get());
}
@Test
public void testPublicEagerSetValue() {
attachPublicChangeListener();
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(1);
assertEquals(VALUE_1, readOnlyProperty.get());
publicChangeListener.check(readOnlyProperty, VALUE_2, VALUE_1, 1);
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(0);
assertEquals(VALUE_1, readOnlyProperty.get());
publicChangeListener.check(null, UNDEFINED, UNDEFINED, 0);
property.setValue(VALUE_2);
property.setValue(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(2);
assertEquals(VALUE_1, readOnlyProperty.get());
publicChangeListener.check(readOnlyProperty, VALUE_2, VALUE_1, 2);
}
@Test(expected=RuntimeException.class)
public void testSetBoundValue() {
final BooleanProperty v = new SimpleBooleanProperty(VALUE_1);
property.bind(v);
property.set(VALUE_1);
}
@Test
public void testLazyBind_primitive() {
attachInvalidationListeners();
final ObservableBooleanValueStub v = new ObservableBooleanValueStub(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get());
assertTrue(property.isBound());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get());
publicInvalidationListener.check(readOnlyProperty, 1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_2, readOnlyProperty.get());
publicInvalidationListener.check(readOnlyProperty, 1);
v.set(VALUE_1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_2, readOnlyProperty.get());
publicInvalidationListener.check(readOnlyProperty, 1);
v.set(VALUE_1);
v.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get());
publicInvalidationListener.check(readOnlyProperty, 1);
}
@Test
public void testInternalEagerBind_primitive() {
attachInternalChangeListener();
final ObservableBooleanValueStub v = new ObservableBooleanValueStub(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get());
assertTrue(property.isBound());
property.check(1);
internalChangeListener.check(property, VALUE_2, VALUE_1, 1);
assertEquals(VALUE_1, readOnlyProperty.get());
v.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(1);
internalChangeListener.check(property, VALUE_1, VALUE_2, 1);
assertEquals(VALUE_2, readOnlyProperty.get());
v.set(VALUE_1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(2);
internalChangeListener.check(property, VALUE_1, VALUE_2, 2);
assertEquals(VALUE_2, readOnlyProperty.get());
v.set(VALUE_1);
v.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(2);
internalChangeListener.check(property, VALUE_2, VALUE_1, 1);
assertEquals(VALUE_1, readOnlyProperty.get());
}
@Test
public void testPublicEagerBind_primitive() {
attachPublicChangeListener();
final ObservableBooleanValueStub v = new ObservableBooleanValueStub(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get());
assertTrue(property.isBound());
property.check(1);
assertEquals(VALUE_1, readOnlyProperty.get());
publicChangeListener.check(readOnlyProperty, VALUE_2, VALUE_1, 1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(1);
assertEquals(VALUE_2, readOnlyProperty.get());
publicChangeListener.check(readOnlyProperty, VALUE_1, VALUE_2, 1);
v.set(VALUE_1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(2);
assertEquals(VALUE_2, readOnlyProperty.get());
publicChangeListener.check(readOnlyProperty, VALUE_1, VALUE_2, 2);
v.set(VALUE_1);
v.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(2);
assertEquals(VALUE_1, readOnlyProperty.get());
publicChangeListener.check(readOnlyProperty, VALUE_2, VALUE_1, 1);
}
@Test
public void testLazyBind_generic() {
attachInvalidationListeners();
final ObservableObjectValueStub<Boolean> v = new ObservableObjectValueStub<Boolean>(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get());
assertTrue(property.isBound());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get());
publicInvalidationListener.check(readOnlyProperty, 1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_2, readOnlyProperty.get());
publicInvalidationListener.check(readOnlyProperty, 1);
v.set(VALUE_1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_2, readOnlyProperty.get());
publicInvalidationListener.check(readOnlyProperty, 1);
v.set(VALUE_1);
v.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get());
publicInvalidationListener.check(readOnlyProperty, 1);
}
@Test
public void testInternalEagerBind_generic() {
attachInternalChangeListener();
final ObservableObjectValueStub<Boolean> v = new ObservableObjectValueStub<Boolean>(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get());
assertTrue(property.isBound());
property.check(1);
internalChangeListener.check(property, VALUE_2, VALUE_1, 1);
assertEquals(VALUE_1, readOnlyProperty.get());
v.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(1);
internalChangeListener.check(property, VALUE_1, VALUE_2, 1);
assertEquals(VALUE_2, readOnlyProperty.get());
v.set(VALUE_1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(2);
internalChangeListener.check(property, VALUE_1, VALUE_2, 2);
assertEquals(VALUE_2, readOnlyProperty.get());
v.set(VALUE_1);
v.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(2);
internalChangeListener.check(property, VALUE_2, VALUE_1, 1);
assertEquals(VALUE_1, readOnlyProperty.get());
}
@Test
public void testPublicEagerBind_generic() {
attachPublicChangeListener();
final ObservableObjectValueStub<Boolean> v = new ObservableObjectValueStub<Boolean>(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get());
assertTrue(property.isBound());
property.check(1);
assertEquals(VALUE_1, readOnlyProperty.get());
publicChangeListener.check(readOnlyProperty, VALUE_2, VALUE_1, 1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(1);
assertEquals(VALUE_2, readOnlyProperty.get());
publicChangeListener.check(readOnlyProperty, VALUE_1, VALUE_2, 1);
v.set(VALUE_1);
v.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(2);
assertEquals(VALUE_2, readOnlyProperty.get());
publicChangeListener.check(readOnlyProperty, VALUE_1, VALUE_2, 2);
v.set(VALUE_1);
v.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(2);
assertEquals(VALUE_1, readOnlyProperty.get());
publicChangeListener.check(readOnlyProperty, VALUE_2, VALUE_1, 1);
}
@Test(expected=NullPointerException.class)
public void testBindToNull() {
property.bind(null);
}
@Test
public void testRebind() {
attachInvalidationListeners();
final BooleanProperty v1 = new SimpleBooleanProperty(VALUE_1);
final BooleanProperty v2 = new SimpleBooleanProperty(VALUE_2);
property.bind(v1);
property.get();
readOnlyProperty.get();
property.reset();
internalInvalidationListener.reset();
publicInvalidationListener.reset();
property.bind(v2);
assertEquals(VALUE_2, property.get());
assertTrue(property.isBound());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_2, readOnlyProperty.get());
publicInvalidationListener.check(readOnlyProperty, 1);
v2.set(VALUE_1);
assertEquals(VALUE_1, property.get());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_1, readOnlyProperty.get());
publicInvalidationListener.check(readOnlyProperty, 1);
v1.set(VALUE_2);
assertEquals(VALUE_1, property.get());
property.check(0);
internalInvalidationListener.check(null, 0);
assertEquals(VALUE_1, readOnlyProperty.get());
publicInvalidationListener.check(null, 0);
property.bind(v2);
assertEquals(VALUE_1, property.get());
assertTrue(property.isBound());
property.check(0);
internalInvalidationListener.check(null, 0);
assertEquals(VALUE_1, readOnlyProperty.get());
publicInvalidationListener.check(null, 0);
}
@Test
public void testUnbind() {
attachInvalidationListeners();
final BooleanProperty v = new SimpleBooleanProperty(VALUE_1);
property.bind(v);
property.unbind();
assertEquals(VALUE_1, property.get());
assertFalse(property.isBound());
assertEquals(VALUE_1, readOnlyProperty.get());
property.reset();
internalInvalidationListener.reset();
publicInvalidationListener.reset();
v.set(VALUE_2);
assertEquals(VALUE_1, property.get());
property.check(0);
internalInvalidationListener.check(null, 0);
assertEquals(VALUE_1, readOnlyProperty.get());
publicInvalidationListener.check(null, 0);
property.set(VALUE_2);
assertEquals(VALUE_2, property.get());
property.check(1);
internalInvalidationListener.check(property, 1);
assertEquals(VALUE_2, readOnlyProperty.get());
publicInvalidationListener.check(readOnlyProperty, 1);
}
@Test
public void testAddingListenerWillAlwaysReceiveInvalidationEvent() {
final BooleanProperty v = new SimpleBooleanProperty(VALUE_1);
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
final ReadOnlyBooleanWrapper v1 = new ReadOnlyBooleanWrapper();
v1.removeListener(internalInvalidationListener);
v1.removeListener(internalChangeListener);
}
@Test
public void testNoReadOnlyPropertyCreated() {
final BooleanProperty v1 = new SimpleBooleanProperty(VALUE_1);
final ReadOnlyBooleanWrapper p1 = new ReadOnlyBooleanWrapper();
p1.set(VALUE_1);
p1.bind(v1);
assertEquals(VALUE_1, p1.get());
v1.set(VALUE_2);
assertEquals(VALUE_2, p1.get());
}
@Test
public void testToString() {
final BooleanProperty v1 = new SimpleBooleanProperty(VALUE_1);
property.set(VALUE_1);
assertEquals("BooleanProperty [value: " + VALUE_1 + "]", property.toString());
assertEquals("ReadOnlyBooleanProperty [value: " + VALUE_1 + "]", readOnlyProperty.toString());
property.bind(v1);
assertEquals("BooleanProperty [bound, invalid]", property.toString());
assertEquals("ReadOnlyBooleanProperty [value: " + VALUE_1 + "]", readOnlyProperty.toString());
property.get();
assertEquals("BooleanProperty [bound, value: " + VALUE_1 + "]", property.toString());
assertEquals("ReadOnlyBooleanProperty [value: " + VALUE_1 + "]", readOnlyProperty.toString());
v1.set(VALUE_2);
assertEquals("BooleanProperty [bound, invalid]", property.toString());
assertEquals("ReadOnlyBooleanProperty [value: " + VALUE_2 + "]", readOnlyProperty.toString());
property.get();
assertEquals("BooleanProperty [bound, value: " + VALUE_2 + "]", property.toString());
assertEquals("ReadOnlyBooleanProperty [value: " + VALUE_2 + "]", readOnlyProperty.toString());
final Object bean = new Object();
final String name = "My name";
final ReadOnlyBooleanWrapper v2 = new ReadOnlyBooleanWrapper(bean, name);
assertEquals("BooleanProperty [bean: " + bean.toString() + ", name: My name, value: " + DEFAULT + "]", v2.toString());
assertEquals("ReadOnlyBooleanProperty [bean: " + bean.toString() + ", name: My name, value: " + DEFAULT + "]", v2.getReadOnlyProperty().toString());
final ReadOnlyBooleanWrapper v3 = new ReadOnlyBooleanWrapper(bean, "");
assertEquals("BooleanProperty [bean: " + bean.toString() + ", value: " + DEFAULT + "]", v3.toString());
assertEquals("ReadOnlyBooleanProperty [bean: " + bean.toString() + ", value: " + DEFAULT + "]", v3.getReadOnlyProperty().toString());
final ReadOnlyBooleanWrapper v4 = new ReadOnlyBooleanWrapper(null, name);
assertEquals("BooleanProperty [name: My name, value: " + DEFAULT + "]", v4.toString());
assertEquals("ReadOnlyBooleanProperty [name: My name, value: " + DEFAULT + "]", v4.getReadOnlyProperty().toString());
}
private static class ReadOnlyBooleanWrapperMock extends ReadOnlyBooleanWrapper {
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
