package test.javafx.binding;
import test.javafx.beans.InvalidationListenerMock;
import javafx.beans.Observable;
import javafx.beans.binding.MapBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import test.javafx.beans.value.ChangeListenerMock;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import test.javafx.collections.MockMapObserver;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.junit.Before;
import org.junit.Test;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static test.javafx.collections.MockMapObserver.Call;
import static org.junit.Assert.*;
public class MapBindingTest {
private final static Object KEY_1 = new Object();
private final static Object KEY_2_0 = new Object();
private final static Object KEY_2_1 = new Object();
private final static Object DATA_1 = new Object();
private final static Object DATA_2_0 = new Object();
private final static Object DATA_2_1 = new Object();
private ObservableStub dependency1;
private ObservableStub dependency2;
private MapBindingImpl binding0;
private MapBindingImpl binding1;
private MapBindingImpl binding2;
private ObservableMap<Object, Object> emptyMap;
private ObservableMap<Object, Object> set1;
private ObservableMap<Object, Object> set2;
private MockMapObserver<Object, Object> listener;
@Before
public void setUp() {
dependency1 = new ObservableStub();
dependency2 = new ObservableStub();
binding0 = new MapBindingImpl();
binding1 = new MapBindingImpl(dependency1);
binding2 = new MapBindingImpl(dependency1, dependency2);
emptyMap = FXCollections.observableMap(Collections.emptyMap());
set1 = FXCollections.observableMap(Collections.singletonMap(KEY_1, DATA_1));
final Map<Object, Object> map = new HashMap<Object, Object>();
map.put(KEY_2_0, DATA_2_0);
map.put(KEY_2_1, DATA_2_1);
set2 = FXCollections.observableMap(map);
listener = new MockMapObserver<Object, Object>();
binding0.setValue(set2);
binding1.setValue(set2);
binding2.setValue(set2);
}
@Test
public void testSizeProperty() {
assertEquals(binding0, binding0.sizeProperty().getBean());
assertEquals(binding1, binding1.sizeProperty().getBean());
assertEquals(binding2, binding2.sizeProperty().getBean());
final ReadOnlyIntegerProperty size = binding1.sizeProperty();
assertEquals("size", size.getName());
assertEquals(2, size.get());
binding1.setValue(emptyMap);
dependency1.fireValueChangedEvent();
assertEquals(0, size.get());
binding1.setValue(null);
dependency1.fireValueChangedEvent();
assertEquals(0, size.get());
binding1.setValue(set1);
dependency1.fireValueChangedEvent();
assertEquals(1, size.get());
}
@Test
public void testEmptyProperty() {
assertEquals(binding0, binding0.emptyProperty().getBean());
assertEquals(binding1, binding1.emptyProperty().getBean());
assertEquals(binding2, binding2.emptyProperty().getBean());
final ReadOnlyBooleanProperty empty = binding1.emptyProperty();
assertEquals("empty", empty.getName());
assertFalse(empty.get());
binding1.setValue(emptyMap);
dependency1.fireValueChangedEvent();
assertTrue(empty.get());
binding1.setValue(null);
dependency1.fireValueChangedEvent();
assertTrue(empty.get());
binding1.setValue(set1);
dependency1.fireValueChangedEvent();
assertFalse(empty.get());
}
@Test
public void testNoDependency_MapChangeListener() {
binding0.getValue();
binding0.addListener(listener);
System.gc();
assertEquals(true, binding0.isValid());
binding0.reset();
binding0.getValue();
assertEquals(0, binding0.getComputeValueCounter());
assertEquals(0, listener.getCallsNumber());
assertEquals(true, binding0.isValid());
}
@Test
public void testSingleDependency_MapChangeListener() {
binding1.getValue();
binding1.addListener(listener);
System.gc();
assertEquals(true, binding1.isValid());
binding1.reset();
listener.clear();
binding1.setValue(set1);
dependency1.fireValueChangedEvent();
assertEquals(1, binding1.getComputeValueCounter());
listener.assertMultipleCalls(new Call[]{new Call<Object, Object>(KEY_2_0, DATA_2_0, null), new Call<Object, Object>(KEY_2_1, DATA_2_1, null), new Call<Object, Object>(KEY_1, null, DATA_1)});
assertEquals(true, binding1.isValid());
listener.clear();
binding1.getValue();
assertEquals(0, binding1.getComputeValueCounter());
assertEquals(0, listener.getCallsNumber());
assertEquals(true, binding1.isValid());
listener.clear();
binding1.setValue(set1);
dependency1.fireValueChangedEvent();
assertEquals(1, binding1.getComputeValueCounter());
assertEquals(0, listener.getCallsNumber());
assertEquals(true, binding1.isValid());
listener.clear();
binding1.getValue();
assertEquals(0, binding1.getComputeValueCounter());
assertEquals(0, listener.getCallsNumber());
assertEquals(true, binding1.isValid());
listener.clear();
binding1.setValue(set2);
dependency1.fireValueChangedEvent();
listener.clear();
binding1.setValue(set1);
dependency1.fireValueChangedEvent();
assertEquals(2, binding1.getComputeValueCounter());
listener.assertMultipleCalls(new Call[]{new Call<Object, Object>(KEY_2_0, DATA_2_0, null), new Call<Object, Object>(KEY_2_1, DATA_2_1, null), new Call<Object, Object>(KEY_1, null, DATA_1)});
assertEquals(true, binding1.isValid());
listener.clear();
binding1.getValue();
assertEquals(0, binding1.getComputeValueCounter());
assertEquals(0, listener.getCallsNumber());
assertEquals(true, binding1.isValid());
listener.clear();
binding1.setValue(set2);
dependency1.fireValueChangedEvent();
binding1.setValue(set2);
dependency1.fireValueChangedEvent();
assertEquals(2, binding1.getComputeValueCounter());
listener.assertMultipleCalls(new Call[] {new Call<Object, Object>(KEY_1, DATA_1, null), new Call<Object, Object>(KEY_2_0, null, DATA_2_0), new Call<Object, Object>(KEY_2_1, null, DATA_2_1)});
assertEquals(true, binding1.isValid());
listener.clear();
binding1.getValue();
assertEquals(0, binding1.getComputeValueCounter());
assertEquals(0, listener.getCallsNumber());
assertEquals(true, binding1.isValid());
}
@Test
public void testChangeContent_InvalidationListener() {
final InvalidationListenerMock listenerMock = new InvalidationListenerMock();
binding1.get();
binding1.addListener(listenerMock);
assertTrue(binding1.isValid());
binding1.reset();
listenerMock.reset();
set2.put(new Object(), new Object());
assertEquals(0, binding1.getComputeValueCounter());
listenerMock.check(binding1, 1);
assertTrue(binding1.isValid());
}
@Test
public void testChangeContent_ChangeListener() {
final ChangeListenerMock listenerMock = new ChangeListenerMock(null);
binding1.get();
binding1.addListener(listenerMock);
assertTrue(binding1.isValid());
binding1.reset();
listenerMock.reset();
set2.put(new Object(), new Object());
assertEquals(0, binding1.getComputeValueCounter());
listenerMock.check(binding1, set2, set2, 1);
assertTrue(binding1.isValid());
}
@Test
public void testChangeContent_MapChangeListener() {
binding1.get();
binding1.addListener(listener);
assertTrue(binding1.isValid());
final Object newKey = new Object();
final Object newData = new Object();
binding1.reset();
listener.clear();
set2.put(newKey, newData);
assertEquals(0, binding1.getComputeValueCounter());
listener.assertAdded(MockMapObserver.Tuple.tup(newKey, newData));
assertTrue(binding1.isValid());
}
public static class ObservableStub extends ObservableValueBase<Object> {
@Override public void fireValueChangedEvent() {super.fireValueChangedEvent();}
@Override
public Object getValue() {
return null;
}
}
private static class MapBindingImpl extends MapBinding<Object, Object> {
private int computeValueCounter = 0;
private ObservableMap<Object, Object> value;
public void setValue(ObservableMap<Object, Object> value) {
this.value = value;
}
public MapBindingImpl(Observable... dep) {
super.bind(dep);
}
public int getComputeValueCounter() {
final int result = computeValueCounter;
reset();
return result;
}
public void reset() {
computeValueCounter = 0;
}
@Override
public ObservableMap<Object, Object> computeValue() {
computeValueCounter++;
return value;
}
public ObservableList<?> getDependencies() {
fail("Should not reach here");
return null;
}
}
}
