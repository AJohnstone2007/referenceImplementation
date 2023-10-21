package test.javafx.binding;
import test.javafx.beans.InvalidationListenerMock;
import javafx.beans.Observable;
import javafx.beans.binding.SetBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import test.javafx.beans.value.ChangeListenerMock;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import test.javafx.collections.MockSetObserver;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import org.junit.Before;
import org.junit.Test;
import static test.javafx.collections.MockSetObserver.Call;
import static org.junit.Assert.*;
public class SetBindingTest {
private final static Object DATA_1 = new Object();
private final static Object DATA_2_0 = new Object();
private final static Object DATA_2_1 = new Object();
private ObservableStub dependency1;
private ObservableStub dependency2;
private SetBindingImpl binding0;
private SetBindingImpl binding1;
private SetBindingImpl binding2;
private ObservableSet<Object> emptySet;
private ObservableSet<Object> set1;
private ObservableSet<Object> set2;
private MockSetObserver<Object> listener;
@Before
public void setUp() {
dependency1 = new ObservableStub();
dependency2 = new ObservableStub();
binding0 = new SetBindingImpl();
binding1 = new SetBindingImpl(dependency1);
binding2 = new SetBindingImpl(dependency1, dependency2);
emptySet = FXCollections.observableSet();
set1 = FXCollections.observableSet(DATA_1);
set2 = FXCollections.observableSet(DATA_2_0, DATA_2_1);
listener = new MockSetObserver<Object>();
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
binding1.setValue(emptySet);
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
binding1.setValue(emptySet);
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
public void testNoDependency_SetChangeListener() {
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
public void testSingleDependency_SetChangeListener() {
binding1.getValue();
binding1.addListener(listener);
System.gc();
assertEquals(true, binding1.isValid());
binding1.reset();
listener.clear();
binding1.setValue(set1);
dependency1.fireValueChangedEvent();
assertEquals(1, binding1.getComputeValueCounter());
listener.assertMultipleCalls(new Call[]{new Call<Object>(DATA_2_0, null), new Call<Object>(DATA_2_1, null), new Call<Object>(null, DATA_1)});
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
listener.assertMultipleCalls(new Call[]{new Call<Object>(DATA_2_0, null), new Call<Object>(DATA_2_1, null), new Call<Object>(null, DATA_1)});
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
listener.assertMultipleCalls(new Call[] {new Call<Object>(DATA_1, null), new Call<Object>(null, DATA_2_0), new Call<Object>(null, DATA_2_1)});
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
set2.add(new Object());
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
set2.add(new Object());
assertEquals(0, binding1.getComputeValueCounter());
listenerMock.check(binding1, set2, set2, 1);
assertTrue(binding1.isValid());
}
@Test
public void testChangeContent_SetChangeListener() {
binding1.get();
binding1.addListener(listener);
assertTrue(binding1.isValid());
final int oldSize = set2.size();
final Object newObject = new Object();
binding1.reset();
listener.clear();
set2.add(newObject);
assertEquals(0, binding1.getComputeValueCounter());
listener.assertAdded(MockSetObserver.Tuple.tup(newObject));
assertTrue(binding1.isValid());
}
public static class ObservableStub extends ObservableValueBase<Object> {
@Override public void fireValueChangedEvent() {super.fireValueChangedEvent();}
@Override
public Object getValue() {
return null;
}
}
private static class SetBindingImpl extends SetBinding<Object> {
private int computeValueCounter = 0;
private ObservableSet<Object> value;
public void setValue(ObservableSet<Object> value) {
this.value = value;
}
public SetBindingImpl(Observable... dep) {
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
public ObservableSet<Object> computeValue() {
computeValueCounter++;
return value;
}
public ObservableList<?> getDependencies() {
fail("Should not reach here");
return null;
}
}
}
