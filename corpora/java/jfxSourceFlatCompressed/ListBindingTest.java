package test.javafx.binding;
import test.javafx.beans.InvalidationListenerMock;
import javafx.beans.Observable;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import test.javafx.beans.value.ChangeListenerMock;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.junit.Before;
import org.junit.Test;
import java.util.Collections;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
public class ListBindingTest {
private ObservableStub dependency1;
private ObservableStub dependency2;
private ListBindingImpl binding0;
private ListBindingImpl binding1;
private ListBindingImpl binding2;
private ObservableList<Object> emptyList;
private ObservableList<Object> list1;
private ObservableList<Object> list2;
private ListChangeListenerMock listener;
@Before
public void setUp() {
dependency1 = new ObservableStub();
dependency2 = new ObservableStub();
binding0 = new ListBindingImpl();
binding1 = new ListBindingImpl(dependency1);
binding2 = new ListBindingImpl(dependency1, dependency2);
emptyList = FXCollections.observableArrayList();
list1 = FXCollections.observableArrayList(new Object());
list2 = FXCollections.observableArrayList(new Object(), new Object());
listener = new ListChangeListenerMock();
binding0.setValue(list2);
binding1.setValue(list2);
binding2.setValue(list2);
}
@Test
public void testSizeProperty() {
assertEquals(binding0, binding0.sizeProperty().getBean());
assertEquals(binding1, binding1.sizeProperty().getBean());
assertEquals(binding2, binding2.sizeProperty().getBean());
final ReadOnlyIntegerProperty size = binding1.sizeProperty();
assertEquals("size", size.getName());
assertEquals(2, size.get());
binding1.setValue(emptyList);
dependency1.fireValueChangedEvent();
assertEquals(0, size.get());
binding1.setValue(null);
dependency1.fireValueChangedEvent();
assertEquals(0, size.get());
binding1.setValue(list1);
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
binding1.setValue(emptyList);
dependency1.fireValueChangedEvent();
assertTrue(empty.get());
binding1.setValue(null);
dependency1.fireValueChangedEvent();
assertTrue(empty.get());
binding1.setValue(list1);
dependency1.fireValueChangedEvent();
assertFalse(empty.get());
}
@Test
public void testNoDependency_ListChangeListener() {
binding0.getValue();
binding0.addListener(listener);
System.gc();
assertEquals(true, binding0.isValid());
binding0.reset();
binding0.getValue();
assertEquals(0, binding0.getComputeValueCounter());
listener.checkNotCalled();
assertEquals(true, binding0.isValid());
}
@Test
public void testSingleDependency_ListChangeListener() {
binding1.getValue();
binding1.addListener(listener);
System.gc();
assertEquals(true, binding1.isValid());
binding1.reset();
listener.reset();
binding1.setValue(list1);
dependency1.fireValueChangedEvent();
assertEquals(1, binding1.getComputeValueCounter());
listener.check(list2, list1, 1);
assertEquals(true, binding1.isValid());
binding1.getValue();
assertEquals(0, binding1.getComputeValueCounter());
listener.checkNotCalled();
assertEquals(true, binding1.isValid());
binding1.setValue(list1);
dependency1.fireValueChangedEvent();
assertEquals(1, binding1.getComputeValueCounter());
listener.checkNotCalled();
assertEquals(true, binding1.isValid());
binding1.getValue();
assertEquals(0, binding1.getComputeValueCounter());
listener.checkNotCalled();
assertEquals(true, binding1.isValid());
binding1.setValue(list2);
dependency1.fireValueChangedEvent();
binding1.setValue(list1);
dependency1.fireValueChangedEvent();
assertEquals(2, binding1.getComputeValueCounter());
listener.check(list2, list1, 2);
assertEquals(true, binding1.isValid());
binding1.getValue();
assertEquals(0, binding1.getComputeValueCounter());
listener.checkNotCalled();
assertEquals(true, binding1.isValid());
binding1.setValue(list2);
dependency1.fireValueChangedEvent();
binding1.setValue(list2);
dependency1.fireValueChangedEvent();
assertEquals(2, binding1.getComputeValueCounter());
listener.check(list1, list2, 1);
assertEquals(true, binding1.isValid());
binding1.getValue();
assertEquals(0, binding1.getComputeValueCounter());
listener.checkNotCalled();
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
list2.add(new Object());
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
list2.add(new Object());
assertEquals(0, binding1.getComputeValueCounter());
listenerMock.check(binding1, list2, list2, 1);
assertTrue(binding1.isValid());
}
@Test
public void testChangeContent_ListChangeListener() {
binding1.get();
binding1.addListener(listener);
assertTrue(binding1.isValid());
final int oldSize = list2.size();
final Object newObject = new Object();
binding1.reset();
listener.reset();
list2.add(newObject);
assertEquals(0, binding1.getComputeValueCounter());
listener.check(oldSize, newObject, 1);
assertTrue(binding1.isValid());
}
public static class ObservableStub extends ObservableValueBase<Object> {
@Override public void fireValueChangedEvent() {super.fireValueChangedEvent();}
@Override
public Object getValue() {
return null;
}
}
private static class ListBindingImpl extends ListBinding<Object> {
private int computeValueCounter = 0;
private ObservableList<Object> value;
public void setValue(ObservableList<Object> value) {
this.value = value;
}
public ListBindingImpl(Observable... dep) {
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
public ObservableList<Object> computeValue() {
computeValueCounter++;
return value;
}
public ObservableList<?> getDependencies() {
fail("Should not reach here");
return null;
}
}
private class ListChangeListenerMock implements ListChangeListener<Object> {
private Change<? extends Object> change;
private int counter;
@Override
public void onChanged(Change<? extends Object> change) {
this.change = change;
counter++;
}
private void reset() {
change = null;
counter = 0;
}
private void checkNotCalled() {
assertEquals(null, change);
assertEquals(0, counter);
reset();
}
private void check(ObservableList<Object> oldList, ObservableList<Object> newList, int counter) {
assertTrue(change.next());
assertTrue(change.wasReplaced());
assertEquals(oldList, change.getRemoved());
assertEquals(newList, change.getList());
assertFalse(change.next());
assertEquals(counter, this.counter);
reset();
}
private void check(int pos, Object newObject, int counter) {
assertTrue(change.next());
assertTrue(change.wasAdded());
assertEquals(pos, change.getFrom());
assertEquals(Collections.singletonList(newObject), change.getAddedSubList());
assertFalse(change.next());
assertEquals(counter, this.counter);
reset();
}
}
}
