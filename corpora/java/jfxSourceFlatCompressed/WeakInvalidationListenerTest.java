package test.javafx.beans;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.junit.Test;
public class WeakInvalidationListenerTest {
@Test(expected=NullPointerException.class)
public void testConstructWithNull() {
new WeakInvalidationListener(null);
}
@Test
public void testHandle() {
InvalidationListenerMock listener = new InvalidationListenerMock();
final WeakInvalidationListener weakListener = new WeakInvalidationListener(listener);
final ObservableMock o = new ObservableMock();
weakListener.invalidated(o);
listener.check(o, 1);
assertFalse(weakListener.wasGarbageCollected());
o.reset();
listener = null;
System.gc();
assertTrue(weakListener.wasGarbageCollected());
weakListener.invalidated(o);
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
removeCounter++;
}
@Override
public void removeListener(ChangeListener<? super Object> listener) {
}
}
}
