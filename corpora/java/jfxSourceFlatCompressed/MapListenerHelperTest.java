package test.com.sun.javafx.collections;
import test.javafx.collections.MockMapObserver;
import com.sun.javafx.binding.MapExpressionHelper;
import com.sun.javafx.collections.MapListenerHelper;
import javafx.beans.InvalidationListener;
import test.javafx.beans.InvalidationListenerMock;
import javafx.beans.Observable;
import javafx.collections.*;
import org.junit.Before;
import org.junit.Test;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
public class MapListenerHelperTest {
private InvalidationListenerMock[] invalidationListenerMock;
private MockMapObserver<Object, Object>[] changeListenerMock;
private MapListenerHelper<Object, Object> helper;
private ObservableMap<Object, Object> map;
private MapChangeListener.Change<Object, Object> change;
@Before
public void setUp() {
invalidationListenerMock = new InvalidationListenerMock[] {
new InvalidationListenerMock(),
new InvalidationListenerMock(),
new InvalidationListenerMock(),
new InvalidationListenerMock()
};
changeListenerMock = new MockMapObserver[] {
new MockMapObserver<Object, Object>(),
new MockMapObserver<Object, Object>(),
new MockMapObserver<Object, Object>(),
new MockMapObserver<Object, Object>()
};
helper = null;
map = FXCollections.observableHashMap();
change = new MapExpressionHelper.SimpleChange<Object, Object>(map).setRemoved(new Object(), new Object());
}
private void resetAllListeners() {
for (final InvalidationListenerMock listener : invalidationListenerMock) {
listener.reset();
}
for (final MockMapObserver<Object, Object> listener : changeListenerMock) {
listener.clear();
}
}
@Test(expected = NullPointerException.class)
public void testAddInvalidationListener_Null() {
MapListenerHelper.addListener(helper, (InvalidationListener)null);
}
@Test(expected = NullPointerException.class)
public void testRemoveInvalidationListener_Null() {
MapListenerHelper.removeListener(helper, (InvalidationListener) null);
}
@Test(expected = NullPointerException.class)
public void testRemoveMapChangeListener_Null() {
MapListenerHelper.removeListener(helper, (MapChangeListener<Object, Object>) null);
}
@Test(expected = NullPointerException.class)
public void testAddMapChangeListener_Null() {
MapListenerHelper.addListener(helper, (MapChangeListener<Object, Object>) null);
}
@Test
public void testEmpty() {
assertFalse(MapListenerHelper.hasListeners(helper));
MapListenerHelper.fireValueChangedEvent(helper, change);
MapListenerHelper.removeListener(helper, invalidationListenerMock[0]);
MapListenerHelper.removeListener(helper, changeListenerMock[0]);
}
@Test
public void testInvalidation_Simple() {
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[0]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(map, 1);
helper = MapListenerHelper.removeListener(helper, invalidationListenerMock[1]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(map, 1);
invalidationListenerMock[1].check(null, 0);
helper = MapListenerHelper.removeListener(helper, changeListenerMock[0]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(map, 1);
assertEquals(0, changeListenerMock[0].getCallsNumber());
helper = MapListenerHelper.removeListener(helper, invalidationListenerMock[0]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(null, 0);
}
@Test
public void testInvalidation_AddInvalidation() {
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[0]);
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[1]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(map, 1);
invalidationListenerMock[1].check(map, 1);
}
@Test
public void testInvalidation_AddChange() {
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[0]);
helper = MapListenerHelper.addListener(helper, changeListenerMock[0]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(map, 1);
assertEquals(1, changeListenerMock[0].getCallsNumber());
}
@Test
public void testInvalidation_ChangeInPulse() {
final InvalidationListener listener = observable -> {
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[0]);
};
helper = MapListenerHelper.addListener(helper, listener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, listener);
invalidationListenerMock[0].reset();
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(map, 1);
}
@Test
public void testChange_Simple() {
helper = MapListenerHelper.addListener(helper, changeListenerMock[0]);
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(1, changeListenerMock[0].getCallsNumber());
changeListenerMock[0].clear();
helper = MapListenerHelper.removeListener(helper, changeListenerMock[1]);
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(1, changeListenerMock[0].getCallsNumber());
assertEquals(0, changeListenerMock[1].getCallsNumber());
changeListenerMock[0].clear();
helper = MapListenerHelper.removeListener(helper, invalidationListenerMock[0]);
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(1, changeListenerMock[0].getCallsNumber());
invalidationListenerMock[0].check(null, 0);
changeListenerMock[0].clear();
helper = MapListenerHelper.removeListener(helper, changeListenerMock[0]);
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(0, changeListenerMock[0].getCallsNumber());
changeListenerMock[0].clear();
}
@Test
public void testChange_AddInvalidation() {
helper = MapListenerHelper.addListener(helper, changeListenerMock[0]);
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[0]);
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(1, changeListenerMock[0].getCallsNumber());
invalidationListenerMock[0].check(map, 1);
}
@Test
public void testChange_AddChange() {
helper = MapListenerHelper.addListener(helper, changeListenerMock[0]);
helper = MapListenerHelper.addListener(helper, changeListenerMock[1]);
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(1, changeListenerMock[0].getCallsNumber());
assertEquals(1, changeListenerMock[1].getCallsNumber());
}
@Test
public void testChange_ChangeInPulse() {
final MapChangeListener<Object, Object> listener = change1 -> {
helper = MapListenerHelper.addListener(helper, changeListenerMock[0]);
};
helper = MapListenerHelper.addListener(helper, listener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, listener);
changeListenerMock[0].clear();
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(1, changeListenerMock[0].getCallsNumber());
}
@Test
public void testGeneric_AddInvalidation() {
helper = MapListenerHelper.addListener(helper, changeListenerMock[0]);
helper = MapListenerHelper.addListener(helper, changeListenerMock[1]);
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[0]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(map, 1);
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[1]);
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[2]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(map, 1);
invalidationListenerMock[1].check(map, 1);
invalidationListenerMock[2].check(map, 1);
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[3]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(map, 1);
invalidationListenerMock[1].check(map, 1);
invalidationListenerMock[2].check(map, 1);
invalidationListenerMock[3].check(map, 1);
}
@Test
public void testGeneric_AddInvalidationInPulse() {
final MapChangeListener<Object, Object> addListener = new MapChangeListener<Object, Object>() {
int counter;
@Override
public void onChanged(Change<? extends Object, ? extends Object> change) {
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[counter++]);
}
};
helper = MapListenerHelper.addListener(helper, changeListenerMock[0]);
helper = MapListenerHelper.addListener(helper, addListener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, addListener);
resetAllListeners();
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(map, 1);
invalidationListenerMock[1].check(null, 0);
invalidationListenerMock[2].check(null, 0);
invalidationListenerMock[3].check(null, 0);
helper = MapListenerHelper.addListener(helper, addListener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, addListener);
resetAllListeners();
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(map, 1);
invalidationListenerMock[1].check(map, 1);
invalidationListenerMock[2].check(null, 0);
invalidationListenerMock[3].check(null, 0);
helper = MapListenerHelper.addListener(helper, addListener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, addListener);
resetAllListeners();
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(map, 1);
invalidationListenerMock[1].check(map, 1);
invalidationListenerMock[2].check(map, 1);
invalidationListenerMock[3].check(null, 0);
helper = MapListenerHelper.addListener(helper, addListener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, addListener);
resetAllListeners();
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(map, 1);
invalidationListenerMock[1].check(map, 1);
invalidationListenerMock[2].check(map, 1);
invalidationListenerMock[3].check(map, 1);
}
@Test
public void testGeneric_RemoveInvalidation() {
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[0]);
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[1]);
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[2]);
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[3]);
helper = MapListenerHelper.removeListener(helper, invalidationListenerMock[0]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(null, 0);
invalidationListenerMock[1].check(map, 1);
invalidationListenerMock[2].check(map, 1);
invalidationListenerMock[3].check(map, 1);
helper = MapListenerHelper.removeListener(helper, invalidationListenerMock[2]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(null, 0);
invalidationListenerMock[1].check(map, 1);
invalidationListenerMock[2].check(null, 0);
invalidationListenerMock[3].check(map, 1);
helper = MapListenerHelper.removeListener(helper, invalidationListenerMock[3]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(null, 0);
invalidationListenerMock[1].check(map, 1);
invalidationListenerMock[2].check(null, 0);
invalidationListenerMock[3].check(null, 0);
helper = MapListenerHelper.addListener(helper, changeListenerMock[0]);
helper = MapListenerHelper.removeListener(helper, invalidationListenerMock[1]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[1].check(null, 0);
assertEquals(1, changeListenerMock[0].getCallsNumber());
changeListenerMock[0].clear();
helper = MapListenerHelper.addListener(helper, changeListenerMock[1]);
helper = MapListenerHelper.removeListener(helper, invalidationListenerMock[0]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(null, 0);
assertEquals(1, changeListenerMock[0].getCallsNumber());
assertEquals(1, changeListenerMock[1].getCallsNumber());
changeListenerMock[0].clear();
changeListenerMock[1].clear();
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[0]);
helper = MapListenerHelper.removeListener(helper, invalidationListenerMock[0]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(null, 0);
assertEquals(1, changeListenerMock[0].getCallsNumber());
assertEquals(1, changeListenerMock[1].getCallsNumber());
}
@Test
public void testGeneric_RemoveInvalidationInPulse() {
final MapChangeListener<Object, Object> removeListener = new MapChangeListener<Object, Object>() {
int counter;
@Override
public void onChanged(Change<? extends Object, ? extends Object> change) {
helper = MapListenerHelper.removeListener(helper, invalidationListenerMock[counter++]);
}
};
helper = MapListenerHelper.addListener(helper, changeListenerMock[0]);
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[0]);
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[3]);
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[1]);
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[2]);
helper = MapListenerHelper.addListener(helper, removeListener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, removeListener);
resetAllListeners();
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(null, 0);
invalidationListenerMock[3].check(map, 1);
invalidationListenerMock[1].check(map, 1);
invalidationListenerMock[2].check(map, 1);
helper = MapListenerHelper.addListener(helper, removeListener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, removeListener);
resetAllListeners();
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(null, 0);
invalidationListenerMock[3].check(map, 1);
invalidationListenerMock[1].check(null, 0);
invalidationListenerMock[2].check(map, 1);
helper = MapListenerHelper.addListener(helper, removeListener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, removeListener);
resetAllListeners();
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(null, 0);
invalidationListenerMock[3].check(map, 1);
invalidationListenerMock[1].check(null, 0);
invalidationListenerMock[2].check(null, 0);
helper = MapListenerHelper.addListener(helper, removeListener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, removeListener);
resetAllListeners();
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(null, 0);
invalidationListenerMock[3].check(null, 0);
invalidationListenerMock[1].check(null, 0);
invalidationListenerMock[2].check(null, 0);
}
@Test
public void testGeneric_AddChange() {
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[0]);
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[0]);
helper = MapListenerHelper.addListener(helper, changeListenerMock[0]);
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(1, changeListenerMock[0].getCallsNumber());
changeListenerMock[0].clear();
helper = MapListenerHelper.addListener(helper, changeListenerMock[1]);
helper = MapListenerHelper.addListener(helper, changeListenerMock[2]);
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(1, changeListenerMock[0].getCallsNumber());
assertEquals(1, changeListenerMock[1].getCallsNumber());
assertEquals(1, changeListenerMock[2].getCallsNumber());
resetAllListeners();
helper = MapListenerHelper.addListener(helper, changeListenerMock[3]);
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(1, changeListenerMock[0].getCallsNumber());
assertEquals(1, changeListenerMock[1].getCallsNumber());
assertEquals(1, changeListenerMock[2].getCallsNumber());
assertEquals(1, changeListenerMock[3].getCallsNumber());
}
@Test
public void testGeneric_AddChangeInPulse() {
final InvalidationListener addListener = new InvalidationListener() {
int counter;
@Override
public void invalidated(Observable observable) {
helper = MapListenerHelper.addListener(helper, changeListenerMock[counter++]);
}
};
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[0]);
helper = MapListenerHelper.addListener(helper, addListener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, addListener);
resetAllListeners();
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(1, changeListenerMock[0].getCallsNumber());
assertEquals(0, changeListenerMock[1].getCallsNumber());
assertEquals(0, changeListenerMock[2].getCallsNumber());
assertEquals(0, changeListenerMock[3].getCallsNumber());
helper = MapListenerHelper.addListener(helper, addListener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, addListener);
resetAllListeners();
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(1, changeListenerMock[0].getCallsNumber());
assertEquals(1, changeListenerMock[1].getCallsNumber());
assertEquals(0, changeListenerMock[2].getCallsNumber());
assertEquals(0, changeListenerMock[3].getCallsNumber());
helper = MapListenerHelper.addListener(helper, addListener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, addListener);
resetAllListeners();
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(1, changeListenerMock[0].getCallsNumber());
assertEquals(1, changeListenerMock[1].getCallsNumber());
assertEquals(1, changeListenerMock[2].getCallsNumber());
assertEquals(0, changeListenerMock[3].getCallsNumber());
helper = MapListenerHelper.addListener(helper, addListener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, addListener);
resetAllListeners();
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(1, changeListenerMock[0].getCallsNumber());
assertEquals(1, changeListenerMock[1].getCallsNumber());
assertEquals(1, changeListenerMock[2].getCallsNumber());
assertEquals(1, changeListenerMock[3].getCallsNumber());
}
@Test
public void testGeneric_RemoveChange() {
helper = MapListenerHelper.addListener(helper, changeListenerMock[0]);
helper = MapListenerHelper.addListener(helper, changeListenerMock[1]);
helper = MapListenerHelper.addListener(helper, changeListenerMock[2]);
helper = MapListenerHelper.addListener(helper, changeListenerMock[3]);
helper = MapListenerHelper.removeListener(helper, changeListenerMock[0]);
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(0, changeListenerMock[0].getCallsNumber());
assertEquals(1, changeListenerMock[1].getCallsNumber());
assertEquals(1, changeListenerMock[2].getCallsNumber());
assertEquals(1, changeListenerMock[3].getCallsNumber());
resetAllListeners();
helper = MapListenerHelper.removeListener(helper, changeListenerMock[2]);
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(0, changeListenerMock[0].getCallsNumber());
assertEquals(1, changeListenerMock[1].getCallsNumber());
assertEquals(0, changeListenerMock[2].getCallsNumber());
assertEquals(1, changeListenerMock[3].getCallsNumber());
resetAllListeners();
helper = MapListenerHelper.removeListener(helper, changeListenerMock[3]);
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(0, changeListenerMock[0].getCallsNumber());
assertEquals(1, changeListenerMock[1].getCallsNumber());
assertEquals(0, changeListenerMock[2].getCallsNumber());
assertEquals(0, changeListenerMock[3].getCallsNumber());
resetAllListeners();
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[0]);
helper = MapListenerHelper.removeListener(helper, changeListenerMock[1]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(map, 1);
assertEquals(0, changeListenerMock[1].getCallsNumber());
changeListenerMock[1].clear();
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[1]);
helper = MapListenerHelper.removeListener(helper, changeListenerMock[0]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(map, 1);
invalidationListenerMock[1].check(map, 1);
assertEquals(0, changeListenerMock[0].getCallsNumber());
changeListenerMock[0].clear();
helper = MapListenerHelper.addListener(helper, changeListenerMock[0]);
helper = MapListenerHelper.removeListener(helper, changeListenerMock[0]);
MapListenerHelper.fireValueChangedEvent(helper, change);
invalidationListenerMock[0].check(map, 1);
invalidationListenerMock[1].check(map, 1);
assertEquals(0, changeListenerMock[0].getCallsNumber());
changeListenerMock[0].clear();
}
@Test
public void testGeneric_RemoveChangeInPulse() {
final InvalidationListener removeListener = new InvalidationListener() {
int counter;
@Override
public void invalidated(Observable observable) {
helper = MapListenerHelper.removeListener(helper, changeListenerMock[counter++]);
}
};
helper = MapListenerHelper.addListener(helper, invalidationListenerMock[0]);
helper = MapListenerHelper.addListener(helper, changeListenerMock[0]);
helper = MapListenerHelper.addListener(helper, changeListenerMock[3]);
helper = MapListenerHelper.addListener(helper, changeListenerMock[1]);
helper = MapListenerHelper.addListener(helper, changeListenerMock[2]);
helper = MapListenerHelper.addListener(helper, removeListener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, removeListener);
resetAllListeners();
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(0, changeListenerMock[0].getCallsNumber());
assertEquals(1, changeListenerMock[3].getCallsNumber());
assertEquals(1, changeListenerMock[1].getCallsNumber());
assertEquals(1, changeListenerMock[2].getCallsNumber());
helper = MapListenerHelper.addListener(helper, removeListener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, removeListener);
resetAllListeners();
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(0, changeListenerMock[0].getCallsNumber());
assertEquals(1, changeListenerMock[3].getCallsNumber());
assertEquals(0, changeListenerMock[1].getCallsNumber());
assertEquals(1, changeListenerMock[2].getCallsNumber());
helper = MapListenerHelper.addListener(helper, removeListener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, removeListener);
resetAllListeners();
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(0, changeListenerMock[0].getCallsNumber());
assertEquals(1, changeListenerMock[3].getCallsNumber());
assertEquals(0, changeListenerMock[1].getCallsNumber());
assertEquals(0, changeListenerMock[2].getCallsNumber());
helper = MapListenerHelper.addListener(helper, removeListener);
MapListenerHelper.fireValueChangedEvent(helper, change);
helper = MapListenerHelper.removeListener(helper, removeListener);
resetAllListeners();
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(0, changeListenerMock[0].getCallsNumber());
assertEquals(0, changeListenerMock[3].getCallsNumber());
assertEquals(0, changeListenerMock[1].getCallsNumber());
assertEquals(0, changeListenerMock[2].getCallsNumber());
}
@Test
public void testExceptionNotPropagatedFromSingleInvalidation() {
helper = MapListenerHelper.addListener(helper,(Observable o) -> {throw new RuntimeException();});
MapListenerHelper.fireValueChangedEvent(helper, change);
}
@Test
public void testExceptionNotPropagatedFromMultipleInvalidation() {
BitSet called = new BitSet();
helper = MapListenerHelper.addListener(helper, (Observable o) -> {called.set(0); throw new RuntimeException();});
helper = MapListenerHelper.addListener(helper, (Observable o) -> {called.set(1); throw new RuntimeException();});
MapListenerHelper.fireValueChangedEvent(helper, change);
assertTrue(called.get(0));
assertTrue(called.get(1));
}
@Test
public void testExceptionNotPropagatedFromSingleChange() {
helper = MapListenerHelper.addListener(helper, (MapChangeListener.Change<? extends Object,? extends Object> c) -> {
throw new RuntimeException();
});
MapListenerHelper.fireValueChangedEvent(helper, change);
}
@Test
public void testExceptionNotPropagatedFromMultipleChange() {
BitSet called = new BitSet();
helper = MapListenerHelper.addListener(helper, (MapChangeListener.Change<? extends Object,? extends Object> c) -> {called.set(0); throw new RuntimeException();});
helper = MapListenerHelper.addListener(helper, (MapChangeListener.Change<? extends Object,? extends Object> c) -> {called.set(1); throw new RuntimeException();});
MapListenerHelper.fireValueChangedEvent(helper, change);
assertTrue(called.get(0));
assertTrue(called.get(1));
}
@Test
public void testExceptionNotPropagatedFromMultipleChangeAndInvalidation() {
BitSet called = new BitSet();
helper = MapListenerHelper.addListener(helper, (MapChangeListener.Change<? extends Object,? extends Object> c) -> {called.set(0); throw new RuntimeException();});
helper = MapListenerHelper.addListener(helper, (MapChangeListener.Change<? extends Object,? extends Object> c) -> {called.set(1); throw new RuntimeException();});
helper = MapListenerHelper.addListener(helper, (Observable o) -> {called.set(2); throw new RuntimeException();});
helper = MapListenerHelper.addListener(helper, (Observable o) -> {called.set(3); throw new RuntimeException();});
MapListenerHelper.fireValueChangedEvent(helper, change);
assertTrue(called.get(0));
assertTrue(called.get(1));
assertTrue(called.get(2));
assertTrue(called.get(3));
}
@Test
public void testExceptionHandledByThreadUncaughtHandlerInSingleInvalidation() {
AtomicBoolean called = new AtomicBoolean(false);
Thread.currentThread().setUncaughtExceptionHandler((t, e) -> called.set(true));
helper = MapListenerHelper.addListener(helper,(Observable o) -> {throw new RuntimeException();});
MapListenerHelper.fireValueChangedEvent(helper, change);
assertTrue(called.get());
}
@Test
public void testExceptionHandledByThreadUncaughtHandlerInMultipleInvalidation() {
AtomicInteger called = new AtomicInteger(0);
Thread.currentThread().setUncaughtExceptionHandler((t, e) -> called.incrementAndGet());
helper = MapListenerHelper.addListener(helper, (Observable o) -> {throw new RuntimeException();});
helper = MapListenerHelper.addListener(helper, (Observable o) -> {throw new RuntimeException();});
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(2, called.get());
}
@Test
public void testExceptionHandledByThreadUncaughtHandlerInSingleChange() {
AtomicBoolean called = new AtomicBoolean(false);
Thread.currentThread().setUncaughtExceptionHandler((t, e) -> called.set(true));
helper = MapListenerHelper.addListener(helper, (MapChangeListener.Change<? extends Object, ? extends Object> c) -> {throw new RuntimeException();});
MapListenerHelper.fireValueChangedEvent(helper, change);
assertTrue(called.get());
}
@Test
public void testExceptionHandledByThreadUncaughtHandlerInMultipleChange() {
AtomicInteger called = new AtomicInteger(0);
Thread.currentThread().setUncaughtExceptionHandler((t, e) -> called.incrementAndGet());
helper = MapListenerHelper.addListener(helper, (MapChangeListener.Change<? extends Object, ? extends Object> c) -> {throw new RuntimeException();});
helper = MapListenerHelper.addListener(helper, (MapChangeListener.Change<? extends Object, ? extends Object> c) -> {throw new RuntimeException();});
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(2, called.get());
}
@Test
public void testExceptionHandledByThreadUncaughtHandlerInMultipleChangeAndInvalidation() {
AtomicInteger called = new AtomicInteger(0);
Thread.currentThread().setUncaughtExceptionHandler((t, e) -> called.incrementAndGet());
helper = MapListenerHelper.addListener(helper, (MapChangeListener.Change<? extends Object, ? extends Object> c) -> { throw new RuntimeException();});
helper = MapListenerHelper.addListener(helper, (MapChangeListener.Change<? extends Object, ? extends Object> c) -> { throw new RuntimeException();});
helper = MapListenerHelper.addListener(helper, (Observable o) -> { throw new RuntimeException();});
helper = MapListenerHelper.addListener(helper, (Observable o) -> {throw new RuntimeException();});
MapListenerHelper.fireValueChangedEvent(helper, change);
assertEquals(4, called.get());
}
}
