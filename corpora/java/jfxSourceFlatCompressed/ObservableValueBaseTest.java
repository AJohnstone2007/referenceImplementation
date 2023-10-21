package test.javafx.beans.value;
import test.javafx.beans.value.ChangeListenerMock;
import javafx.beans.InvalidationListener;
import test.javafx.beans.InvalidationListenerMock;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValueStub;
import org.junit.Before;
import org.junit.Test;
public class ObservableValueBaseTest {
private static final Object UNDEFINED_VALUE = new Object();
private static final Object V1 = new Object();
private static final Object V2 = new Object();
private ObservableObjectValueStub<Object> valueModel;
private InvalidationListenerMock invalidationListener;
private ChangeListenerMock<Object> changeListener;
@Before
public void setUp() {
valueModel = new ObservableObjectValueStub<Object>();
invalidationListener = new InvalidationListenerMock();
changeListener = new ChangeListenerMock<Object>(UNDEFINED_VALUE);
}
@Test
public void testInitialState() {
valueModel.fireValueChangedEvent();
}
@Test
public void testOneInvalidationListener() {
valueModel.addListener(invalidationListener);
System.gc();
valueModel.set(V1);
invalidationListener.check(valueModel, 1);
valueModel.removeListener(invalidationListener);
valueModel.set(V2);
invalidationListener.check(null, 0);
valueModel.removeListener(invalidationListener);
valueModel.set(V1);
invalidationListener.check(null, 0);
}
@Test
public void testOneChangeListener() {
valueModel.addListener(changeListener);
System.gc();
valueModel.set(V1);
changeListener.check(valueModel, null, V1, 1);
valueModel.set(V1);
changeListener.check(null, UNDEFINED_VALUE, UNDEFINED_VALUE, 0);
valueModel.set(null);
changeListener.check(valueModel, V1, null, 1);
valueModel.set(null);
changeListener.check(null, UNDEFINED_VALUE, UNDEFINED_VALUE, 0);
valueModel.removeListener(changeListener);
valueModel.set(V2);
changeListener.check(null, UNDEFINED_VALUE, UNDEFINED_VALUE, 0);
valueModel.removeListener(changeListener);
valueModel.set(V1);
changeListener.check(null, UNDEFINED_VALUE, UNDEFINED_VALUE, 0);
}
@Test
public void testTwoObservers() {
final InvalidationListenerMock observer2 = new InvalidationListenerMock();
valueModel.addListener(invalidationListener);
valueModel.addListener(observer2);
System.gc();
valueModel.fireValueChangedEvent();
invalidationListener.check(valueModel, 1);
observer2.check(valueModel, 1);
invalidationListener.reset();
observer2.reset();
valueModel.removeListener(invalidationListener);
valueModel.fireValueChangedEvent();
invalidationListener.check(null, 0);
observer2.check(valueModel, 1);
observer2.reset();
valueModel.removeListener(observer2);
valueModel.fireValueChangedEvent();
invalidationListener.check(null, 0);
observer2.check(null, 0);
observer2.reset();
valueModel.removeListener(observer2);
valueModel.removeListener(invalidationListener);
valueModel.fireValueChangedEvent();
invalidationListener.check(null, 0);
observer2.check(null, 0);
}
@Test
public void testConcurrentAdd() {
final InvalidationListenerMock observer2 = new AddingListenerMock();
valueModel.addListener(observer2);
valueModel.fireValueChangedEvent();
observer2.check(valueModel, 1);
invalidationListener.reset();
observer2.reset();
valueModel.fireValueChangedEvent();
invalidationListener.check(valueModel, 1);
observer2.check(valueModel, 1);
}
@Test
public void testConcurrentRemove() {
final InvalidationListenerMock observer2 = new RemovingListenerMock();
valueModel.addListener(observer2);
valueModel.addListener(invalidationListener);
valueModel.fireValueChangedEvent();
observer2.check(valueModel, 1);
invalidationListener.reset();
observer2.reset();
valueModel.fireValueChangedEvent();
invalidationListener.check(null, 0);
observer2.check(valueModel, 1);
}
@Test(expected=NullPointerException.class)
public void testAddingNull_InvalidationListener() {
valueModel.addListener((InvalidationListener)null);
}
@Test(expected=NullPointerException.class)
public void testAddingNull_ChangeListener() {
valueModel.addListener((ChangeListener<Object>)null);
}
@Test(expected=NullPointerException.class)
public void testRemovingNull_InvalidationListener() {
valueModel.removeListener((InvalidationListener)null);
}
@Test(expected=NullPointerException.class)
public void testRemovingNull_ChangeListener() {
valueModel.removeListener((ChangeListener<Object>)null);
}
private class AddingListenerMock extends InvalidationListenerMock {
@Override public void invalidated(Observable valueModel) {
super.invalidated(valueModel);
valueModel.addListener(invalidationListener);
}
}
private class RemovingListenerMock extends InvalidationListenerMock {
@Override public void invalidated(Observable valueModel) {
super.invalidated(valueModel);
valueModel.removeListener(invalidationListener);
}
}
}
