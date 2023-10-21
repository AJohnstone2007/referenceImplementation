package test.javafx.collections;
import com.sun.javafx.binding.SetExpressionHelper;
import com.sun.javafx.collections.ObservableSetWrapper;
import org.junit.Test;
import java.util.HashSet;
import javafx.collections.SetChangeListener;
import javafx.collections.WeakSetChangeListener;
import static org.junit.Assert.*;
public class WeakSetChangeListenerTest {
@Test(expected=NullPointerException.class)
public void testConstructWithNull() {
new WeakSetChangeListener<Object>(null);
}
@Test
public void testHandle() {
MockSetObserver<Object> listener = new MockSetObserver<Object>();
final WeakSetChangeListener<Object> weakListener = new WeakSetChangeListener<Object>(listener);
final ObservableSetMock set = new ObservableSetMock();
final Object removedElement = new Object();
final SetChangeListener.Change<Object> change = new SetExpressionHelper.SimpleChange<Object>(set).setRemoved(removedElement);
weakListener.onChanged(change);
listener.assertRemoved(MockSetObserver.Tuple.tup(removedElement));
assertFalse(weakListener.wasGarbageCollected());
set.reset();
listener = null;
System.gc();
assertTrue(weakListener.wasGarbageCollected());
weakListener.onChanged(change);
assertEquals(1, set.removeCounter);
}
private static class ObservableSetMock extends ObservableSetWrapper<Object> {
private int removeCounter;
public ObservableSetMock() {
super(new HashSet<Object>());
}
private void reset() {
removeCounter = 0;
}
@Override
public void removeListener(SetChangeListener<? super Object> listener) {
removeCounter++;
}
}
}
