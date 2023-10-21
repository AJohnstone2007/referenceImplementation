package test.javafx.collections;
import com.sun.javafx.collections.NonIterableChange;
import com.sun.javafx.collections.ObservableListWrapper;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Collections;
import javafx.collections.ListChangeListener;
import javafx.collections.WeakListChangeListener;
import static org.junit.Assert.*;
public class WeakListChangeListenerTest {
@Test(expected=NullPointerException.class)
public void testConstructWithNull() {
new WeakListChangeListener<Object>(null);
}
@Test
public void testHandle() {
MockListObserver<Object> listener = new MockListObserver<Object>();
final WeakListChangeListener<Object> weakListener = new WeakListChangeListener<Object>(listener);
final ObservableListWrapper<Object> list = new ObservableListWrapper<Object>(new ArrayList<Object>());
final Object removedElement = new Object();
final ListChangeListener.Change<Object> change = new NonIterableChange.SimpleRemovedChange<Object>(0, 1, removedElement, list);
weakListener.onChanged(change);
listener.check1AddRemove(list, Collections.singletonList(removedElement), 0, 1);
assertFalse(weakListener.wasGarbageCollected());
listener = null;
System.gc();
assertTrue(weakListener.wasGarbageCollected());
weakListener.onChanged(change);
}
}
