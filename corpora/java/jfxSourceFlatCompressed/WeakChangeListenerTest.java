package test.javafx.beans.value;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import org.junit.Test;
import static org.junit.Assert.*;
public class WeakChangeListenerTest {
@Test(expected=NullPointerException.class)
public void testConstructWithNull() {
new WeakChangeListener<Object>(null);
}
@Test
public void testHandle() {
ChangeListenerMock<Object> listener = new ChangeListenerMock<Object>(new Object());
final WeakChangeListener<Object> weakListener = new WeakChangeListener<Object>(listener);
final ObservableMock o = new ObservableMock();
final Object obj1 = new Object();
final Object obj2 = new Object();
weakListener.changed(o, obj1, obj2);
listener.check(o, obj1, obj2, 1);
assertFalse(weakListener.wasGarbageCollected());
o.reset();
listener = null;
System.gc();
assertTrue(weakListener.wasGarbageCollected());
weakListener.changed(o, obj2, obj1);
assertEquals(1, o.removeCounter);
}
private static class ObservableMock implements ObservableValue<Object> {
private int removeCounter;
private void reset() {
removeCounter = 0;
}
@Override
public Object getValue() {
return null;
}
@Override
public void addListener(InvalidationListener listener) {
}
@Override
public void addListener(ChangeListener<? super Object> listener) {
}
@Override
public void removeListener(InvalidationListener listener) {
}
@Override
public void removeListener(ChangeListener<? super Object> listener) {
removeCounter++;
}
}
}
