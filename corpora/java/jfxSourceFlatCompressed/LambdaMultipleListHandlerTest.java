package test.com.sun.javafx.scene.control;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;
import com.sun.javafx.scene.control.LambdaMultiplePropertyChangeListenerHandler;
import static org.junit.Assert.*;
import static test.com.sun.javafx.scene.control.infrastructure.ControlSkinFactory.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
public class LambdaMultipleListHandlerTest {
private LambdaMultiplePropertyChangeListenerHandler handler;
private ObservableList<String> items;
@Test
public void testUnregistersSingleConsumerMultipleLists() {
List<Change<?>> changes = new ArrayList<>();
Consumer<Change<?>> consumer = change -> changes.add(change);
ObservableList<String> otherList = FXCollections.observableArrayList("other");
handler.registerListChangeListener(items, consumer);
handler.registerListChangeListener(otherList, consumer);
handler.unregisterListChangeListeners(otherList);
items.add("added");
otherList.add("added other");
assertEquals(1, changes.size());
assertEquals(items, changes.get(0).getList());
}
@Test
public void testUnregistersMultipleConsumers() {
List<Change<?>> changes = new ArrayList<>();
Consumer<Change<?>> consumer = change -> changes.add(change);
List<Change<?>> secondChanges = new ArrayList<>();
Consumer<Change<?>> secondConsumer = change -> secondChanges.addAll(changes);
handler.registerListChangeListener(items, consumer);
handler.registerListChangeListener(items, secondConsumer);
Consumer<Change<?>> removedChain = handler.unregisterListChangeListeners(items);
items.add("added after removed");
assertEquals("none of the removed listeners must be notified",
0, changes.size() + secondChanges.size());
items.addListener((ListChangeListener)(c -> removedChain.accept(c)));
items.add("added");
assertEquals(1, changes.size());
assertEquals(changes, secondChanges);
}
@Test
public void testUnregistersSingleConsumer() {
List<Change<?>> changes = new ArrayList<>();
Consumer<Change<?>> consumer = change -> changes.add(change);
ObservableList<String> otherList = FXCollections.observableArrayList("other");
handler.registerListChangeListener(items, consumer);
Consumer<Change<?>> removed = handler.unregisterListChangeListeners(items);
items.add("added");
assertEquals(0, changes.size());
assertSame(consumer, removed);
}
@Test
public void testUnregistersNotRegistered() {
assertNull(handler.unregisterListChangeListeners(items));
}
@Test
public void testUnregistersNull() {
assertNull(handler.unregisterListChangeListeners(null));
}
@Test
public void testRegisterConsumerToMultipleLists() {
List<Change<?>> changes = new ArrayList<>();
Consumer<Change<?>> consumer = change -> changes.add(change);
ObservableList<String> otherList = FXCollections.observableArrayList("other");
handler.registerListChangeListener(items, consumer);
handler.registerListChangeListener(otherList, consumer);
items.add("added");
otherList.add("added other");
assertEquals(2, changes.size());
assertEquals(items, changes.get(0).getList());
assertEquals(otherList, changes.get(1).getList());
}
@Test
public void testRegisterMultipleConsumerToSingleList() {
List<Change<?>> changes = new ArrayList<>();
Consumer<Change<?>> consumer = change -> changes.add(change);
List<Change<?>> secondChanges = new ArrayList<>();
Consumer<Change<?>> secondConsumer = change -> secondChanges.addAll(changes);
handler.registerListChangeListener(items, consumer);
handler.registerListChangeListener(items, secondConsumer);
items.add("added");
assertEquals(1, changes.size());
assertEquals(changes, secondChanges);
}
@Test
public void testRegister() {
List<Change<?>> changes = new ArrayList<>();
Consumer<Change<?>> consumer = change -> changes.add(change);
handler.registerListChangeListener(items, consumer);
String added = "added";
items.add(added);
assertEquals(1, changes.size());
Change<?> change = changes.get(0);
change.next();
assertTrue(change.wasAdded());
assertTrue(change.getAddedSubList().contains(added));
}
@Test
public void testRegisterNullConsumer() {
handler.registerListChangeListener(items, null);
}
@Test
public void testRegisterNullList() {
handler.registerListChangeListener(null, c -> {});
}
@Test
public void testDispose() {
List<Change<?>> changes = new ArrayList<>();
Consumer<Change<?>> consumer = change -> changes.add(change);
handler.registerListChangeListener(items, consumer);
handler.dispose();
items.add("added");
assertEquals("listener must not be invoked after dispose", 0, changes.size());
handler.registerListChangeListener(items, consumer);
items.add("added");
assertEquals("listener must be invoked when re-registered after dispose", 1, changes.size());
}
@Test
public void testRegisterMemoryLeak() {
List<Change<?>> changes = new ArrayList<>();
Consumer<Change<?>> consumer = change -> changes.add(change);
LambdaMultiplePropertyChangeListenerHandler handler = new LambdaMultiplePropertyChangeListenerHandler();
WeakReference<LambdaMultiplePropertyChangeListenerHandler> ref = new WeakReference<>(handler);
handler.registerListChangeListener(items, consumer);
items.add("added");
assertEquals(1, changes.size());
handler = null;
attemptGC(ref);
assertNull("handler must be gc'ed", ref.get());
items.add("another");
assertEquals("listener must not be invoked after gc", 1, changes.size());
}
@Before
public void setup() {
handler = new LambdaMultiplePropertyChangeListenerHandler();
items = FXCollections.observableArrayList("one", "two", "four");
}
@Test
public void testInvalidationOfListValuedObservable() {
String[] data = {"one", "two", "other"};
ObservableList<String> first = FXCollections.observableArrayList(data);
ObjectProperty<ObservableList<String>> itemsProperty = new SimpleObjectProperty<>(first);
assertSame(first, itemsProperty.get());
int[] invalidations = new int[] {0};
int[] changes = new int[] {0};
itemsProperty.addListener(obs -> invalidations[0]++);
itemsProperty.addListener((obs, ov, nv) -> changes[0]++);
itemsProperty.set(FXCollections.observableArrayList(data));
assertEquals("changeListener not notified", 0, changes[0]);
assertEquals("invalidationListener notified", 1, invalidations[0]);
itemsProperty.get().add("added");
assertEquals(0, changes[0]);
assertEquals(1, invalidations[0]);
itemsProperty.set(first);
assertEquals(1, changes[0]);
assertEquals(2, invalidations[0]);
}
}
