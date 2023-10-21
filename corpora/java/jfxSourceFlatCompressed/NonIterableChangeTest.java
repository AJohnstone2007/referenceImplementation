package test.com.sun.javafx.collections;
import com.sun.javafx.collections.NonIterableChange;
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class NonIterableChangeTest {
ObservableList<String> list;
@Before
public void setUp() {
list = FXCollections.observableArrayList("a", "b", "c", "d", "e");
}
@Test
public void testSimpleAdd() {
Change<String> change = new NonIterableChange.SimpleAddChange<String>(0, 1, list);
assertTrue(change.next());
assertTrue(change.wasAdded());
assertFalse(change.wasRemoved());
assertFalse(change.wasReplaced());
assertFalse(change.wasUpdated());
assertFalse(change.wasPermutated());
assertEquals(0, change.getFrom());
assertEquals(1, change.getTo());
assertEquals(1, change.getAddedSize());
assertEquals(Arrays.asList("a"), change.getAddedSubList());
assertEquals(0, change.getRemovedSize());
assertEquals(Arrays.asList(), change.getRemoved());
assertNotNull(change.toString());
assertFalse(change.next());
}
@Test
public void testSimpleRemove() {
Change<String> change = new NonIterableChange.SimpleRemovedChange<String>(0, 0, "a0", list);
assertTrue(change.next());
assertFalse(change.wasAdded());
assertTrue(change.wasRemoved());
assertFalse(change.wasReplaced());
assertFalse(change.wasUpdated());
assertFalse(change.wasPermutated());
assertEquals(0, change.getFrom());
assertEquals(0, change.getTo());
assertEquals(0, change.getAddedSize());
assertEquals(Arrays.asList(), change.getAddedSubList());
assertEquals(1, change.getRemovedSize());
assertEquals(Arrays.asList("a0"), change.getRemoved());
assertNotNull(change.toString());
assertFalse(change.next());
}
@Test
public void testSimpleUpdate() {
Change<String> change = new NonIterableChange.SimpleUpdateChange<String>(0, 1, list);
assertTrue(change.next());
assertFalse(change.wasAdded());
assertFalse(change.wasRemoved());
assertFalse(change.wasReplaced());
assertTrue(change.wasUpdated());
assertFalse(change.wasPermutated());
assertEquals(0, change.getFrom());
assertEquals(1, change.getTo());
assertEquals(0, change.getAddedSize());
assertEquals(Arrays.asList(), change.getAddedSubList());
assertEquals(0, change.getRemovedSize());
assertEquals(Arrays.asList(), change.getRemoved());
assertNotNull(change.toString());
assertFalse(change.next());
}
@Test
public void testSimplePermutation() {
Change<String> change = new NonIterableChange.SimplePermutationChange<String>(0, 2, new int[] {1, 0}, list);
assertTrue(change.next());
assertFalse(change.wasAdded());
assertFalse(change.wasRemoved());
assertFalse(change.wasReplaced());
assertFalse(change.wasUpdated());
assertTrue(change.wasPermutated());
assertEquals(0, change.getFrom());
assertEquals(1, change.getPermutation(0));
assertEquals(0, change.getPermutation(1));
assertEquals(2, change.getTo());
assertEquals(0, change.getAddedSize());
assertEquals(Arrays.asList(), change.getAddedSubList());
assertEquals(0, change.getRemovedSize());
assertEquals(Arrays.asList(), change.getRemoved());
assertNotNull(change.toString());
assertFalse(change.next());
}
}
