package test.javafx.beans.property;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import test.javafx.beans.InvalidationListenerMock;
import test.javafx.beans.value.ChangeListenerMock;
import javafx.beans.value.ObservableStringValueStub;
import javafx.beans.value.ObservableObjectValueStub;
import org.junit.Before;
import org.junit.Test;
public class ReadOnlyStringWrapperTest {
private static final String UNDEFINED = null;
private static final String DEFAULT = null;
private static final String VALUE_1 = "Hello World!";
private static final String VALUE_2 = "Goodbye World!";
private ReadOnlyStringWrapperMock property;
private ReadOnlyStringProperty readOnlyProperty;
private InvalidationListenerMock internalInvalidationListener;
private InvalidationListenerMock publicInvalidationListener;
private ChangeListenerMock<String> internalChangeListener;
private ChangeListenerMock<String> publicChangeListener;
@Before
public void setUp() throws Exception {
property = new ReadOnlyStringWrapperMock();
readOnlyProperty = property.getReadOnlyProperty();
internalInvalidationListener = new InvalidationListenerMock();
publicInvalidationListener = new InvalidationListenerMock();
internalChangeListener = new ChangeListenerMock<String>(UNDEFINED);
publicChangeListener = new ChangeListenerMock<String>(UNDEFINED);
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
final ReadOnlyStringWrapper p1 = new ReadOnlyStringWrapper();
assertEquals(DEFAULT, p1.get());
assertEquals((String)DEFAULT, p1.getValue());
assertFalse(property.isBound());
assertEquals(null, p1.getBean());
assertEquals("", p1.getName());
final ReadOnlyStringProperty r1 = p1.getReadOnlyProperty();
assertEquals(DEFAULT, r1.get());
assertEquals((String)DEFAULT, r1.getValue());
assertEquals(null, r1.getBean());
assertEquals("", r1.getName());
}
@Test
public void testConstructor_InitialValue() {
final ReadOnlyStringWrapper p1 = new ReadOnlyStringWrapper(VALUE_1);
assertEquals(VALUE_1, p1.get());
assertEquals((String)VALUE_1, p1.getValue());
assertFalse(property.isBound());
assertEquals(null, p1.getBean());
assertEquals("", p1.getName());
final ReadOnlyStringProperty r1 = p1.getReadOnlyProperty();
assertEquals(VALUE_1, r1.get());
assertEquals((String)VALUE_1, r1.getValue());
assertEquals(null, r1.getBean());
assertEquals("", r1.getName());
}
@Test
public void testConstructor_Bean_Name() {
final Object bean = new Object();
final String name = "My name";
final ReadOnlyStringWrapper p1 = new ReadOnlyStringWrapper(bean, name);
assertEquals(DEFAULT, p1.get());
assertEquals((String)DEFAULT, p1.getValue());
assertFalse(property.isBound());
assertEquals(bean, p1.getBean());
assertEquals(name, p1.getName());
final ReadOnlyStringProperty r1 = p1.getReadOnlyProperty();
assertEquals(DEFAULT, r1.get());
assertEquals((String)DEFAULT, r1.getValue());
assertEquals(bean, r1.getBean());
assertEquals(name, r1.getName());
}
@Test
public void testConstructor_Bean_Name_InitialValue() {
final Object bean = new Object();
final String name = "My name";
final ReadOnlyStringWrapper p1 = new ReadOnlyStringWrapper(bean, name, VALUE_1);
assertEquals(VALUE_1, p1.get());
assertEquals((String)VALUE_1, p1.getValue());
assertFalse(property.isBound());
assertEquals(bean, p1.getBean());
assertEquals(name, p1.getName());
final ReadOnlyStringProperty r1 = p1.getReadOnlyProperty();
assertEquals(VALUE_1, r1.get());
assertEquals((String)VALUE_1, r1.getValue());
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
publicChangeListener.check(readOnlyProperty, DEFAULT, VALUE_1, 1);
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
internalChangeListener.check(property, DEFAULT, VALUE_1, 1);
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
publicChangeListener.check(readOnlyProperty, DEFAULT, VALUE_1, 1);
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
final StringProperty v = new SimpleStringProperty(VALUE_1);
property.bind(v);
property.set(VALUE_1);
}
@Test
public void testLazyBind_primitive() {
attachInvalidationListeners();
final ObservableStringValueStub v = new ObservableStringValueStub(VALUE_1);
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
final ObservableStringValueStub v = new ObservableStringValueStub(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get());
assertTrue(property.isBound());
property.check(1);
internalChangeListener.check(property, DEFAULT, VALUE_1, 1);
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
final ObservableStringValueStub v = new ObservableStringValueStub(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get());
assertTrue(property.isBound());
property.check(1);
assertEquals(VALUE_1, readOnlyProperty.get());
publicChangeListener.check(readOnlyProperty, DEFAULT, VALUE_1, 1);
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
final ObservableObjectValueStub<String> v = new ObservableObjectValueStub<String>(VALUE_1);
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
final ObservableObjectValueStub<String> v = new ObservableObjectValueStub<String>(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get());
assertTrue(property.isBound());
property.check(1);
internalChangeListener.check(property, DEFAULT, VALUE_1, 1);
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
final ObservableObjectValueStub<String> v = new ObservableObjectValueStub<String>(VALUE_1);
property.bind(v);
assertEquals(VALUE_1, property.get());
assertTrue(property.isBound());
property.check(1);
assertEquals(VALUE_1, readOnlyProperty.get());
publicChangeListener.check(readOnlyProperty, DEFAULT, VALUE_1, 1);
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
final StringProperty v1 = new SimpleStringProperty(VALUE_1);
final StringProperty v2 = new SimpleStringProperty(VALUE_2);
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
final StringProperty v = new SimpleStringProperty(VALUE_1);
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
final StringProperty v = new SimpleStringProperty(VALUE_1);
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
final ReadOnlyStringWrapper v1 = new ReadOnlyStringWrapper();
v1.removeListener(internalInvalidationListener);
v1.removeListener(internalChangeListener);
}
@Test
public void testNoReadOnlyPropertyCreated() {
final StringProperty v1 = new SimpleStringProperty(VALUE_1);
final ReadOnlyStringWrapper p1 = new ReadOnlyStringWrapper();
p1.set(VALUE_1);
p1.bind(v1);
assertEquals(VALUE_1, p1.get());
v1.set(VALUE_2);
assertEquals(VALUE_2, p1.get());
}
@Test
public void testToString() {
final StringProperty v1 = new SimpleStringProperty(VALUE_1);
property.set(VALUE_1);
assertEquals("StringProperty [value: " + VALUE_1 + "]", property.toString());
assertEquals("ReadOnlyStringProperty [value: " + VALUE_1 + "]", readOnlyProperty.toString());
property.bind(v1);
assertEquals("StringProperty [bound, invalid]", property.toString());
assertEquals("ReadOnlyStringProperty [value: " + VALUE_1 + "]", readOnlyProperty.toString());
property.get();
assertEquals("StringProperty [bound, value: " + VALUE_1 + "]", property.toString());
assertEquals("ReadOnlyStringProperty [value: " + VALUE_1 + "]", readOnlyProperty.toString());
v1.set(VALUE_2);
assertEquals("StringProperty [bound, invalid]", property.toString());
assertEquals("ReadOnlyStringProperty [value: " + VALUE_2 + "]", readOnlyProperty.toString());
property.get();
assertEquals("StringProperty [bound, value: " + VALUE_2 + "]", property.toString());
assertEquals("ReadOnlyStringProperty [value: " + VALUE_2 + "]", readOnlyProperty.toString());
final Object bean = new Object();
final String name = "My name";
final ReadOnlyStringWrapper v2 = new ReadOnlyStringWrapper(bean, name);
assertEquals("StringProperty [bean: " + bean.toString() + ", name: My name, value: " + DEFAULT + "]", v2.toString());
assertEquals("ReadOnlyStringProperty [bean: " + bean.toString() + ", name: My name, value: " + DEFAULT + "]", v2.getReadOnlyProperty().toString());
final ReadOnlyStringWrapper v3 = new ReadOnlyStringWrapper(bean, "");
assertEquals("StringProperty [bean: " + bean.toString() + ", value: " + DEFAULT + "]", v3.toString());
assertEquals("ReadOnlyStringProperty [bean: " + bean.toString() + ", value: " + DEFAULT + "]", v3.getReadOnlyProperty().toString());
final ReadOnlyStringWrapper v4 = new ReadOnlyStringWrapper(null, name);
assertEquals("StringProperty [name: My name, value: " + DEFAULT + "]", v4.toString());
assertEquals("ReadOnlyStringProperty [name: My name, value: " + DEFAULT + "]", v4.getReadOnlyProperty().toString());
}
private static class ReadOnlyStringWrapperMock extends ReadOnlyStringWrapper {
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
