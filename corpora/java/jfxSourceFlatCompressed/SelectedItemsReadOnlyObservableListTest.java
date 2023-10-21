package test.com.sun.javafx.scene.control;
import com.sun.javafx.collections.ObservableListWrapper;
import com.sun.javafx.scene.control.SelectedItemsReadOnlyObservableList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
public class SelectedItemsReadOnlyObservableListTest {
ObservableList<String> items;
ObservableList<String> selectedItems;
TestObservableList<Integer> selectedIndices;
List<String> changes;
@Before
public void setup() {
changes = new ArrayList<>();
items = FXCollections.observableArrayList("foo", "bar", "baz", "qux", "quz");
selectedIndices = new TestObservableList<>();
selectedItems = new SelectedItemsReadOnlyObservableList<>(selectedIndices, () -> 0) {
@Override protected String getModelItem(int index) { return items.get(index); }
};
selectedItems.addListener((ListChangeListener<? super String>) c -> changes.add(c.toString()));
}
@Test
public void testAddAndRemoveEntireRange() {
selectedIndices.addAll(0, 1, 2, 3, 4);
assertEquals(1, changes.size());
assertEquals("{ [foo, bar, baz, qux, quz] added at 0 }", changes.get(0));
changes.clear();
selectedIndices.removeAll(0, 1, 2, 3, 4);
assertEquals(1, changes.size());
assertEquals("{ [foo, bar, baz, qux, quz] removed at 0 }", changes.get(0));
}
@Test
public void testRemoveInteriorRange() {
selectedIndices.addAll(0, 1, 2, 3, 4);
changes.clear();
selectedIndices.removeAll(1, 2, 3);
assertEquals(1, changes.size());
assertEquals("{ [bar, baz, qux] removed at 1 }", changes.get(0));
}
@Test
public void testRemoveDisjointRanges() {
selectedIndices.addAll(0, 1, 2, 3, 4);
changes.clear();
selectedIndices.removeAll(0, 1, 3, 4);
assertEquals(1, changes.size());
assertEquals("{ [foo, bar] removed at 0, [qux, quz] removed at 1 }", changes.get(0));
}
@Test
@Ignore("JDK-8267951")
public void testReplaceTwoItems() {
selectedIndices.addAll(0, 1);
changes.clear();
selectedIndices.replaceAll(i -> i == 0 ? 1 : 0);
assertEquals(2, changes.size());
assertEquals("{ [foo] replaced by [bar] at 0 }", changes.get(0));
assertEquals("{ [bar] replaced by [foo] at 1 }", changes.get(1));
}
@Test
@Ignore("JDK-8267951")
public void testReplaceDisjointRanges() {
selectedIndices.addAll(0, 1, 2, 3, 4);
changes.clear();
selectedIndices.replaceAll(i -> {
switch (i) {
case 0: return 1;
case 1: return 0;
case 3: return 4;
case 4: return 3;
default: return i;
}
});
assertEquals(4, changes.size());
assertEquals("{ [foo] replaced by [bar] at 0 }", changes.get(0));
assertEquals("{ [bar] replaced by [foo] at 1 }", changes.get(1));
assertEquals("{ [qux] replaced by [quz] at 3 }", changes.get(2));
assertEquals("{ [quz] replaced by [qux] at 4 }", changes.get(3));
}
@Test
public void testReplaceAndRemoveRanges() {
selectedIndices.addAll(0, 1, 2, 3, 4);
changes.clear();
selectedIndices._beginChange();
selectedIndices.set(0, 1);
selectedIndices.set(1, 0);
selectedIndices.remove(3, 5);
selectedIndices._endChange();
assertEquals(1, changes.size());
assertEquals("{ [foo, bar] replaced by [bar, foo] at 0, [qux, quz] removed at 3 }", changes.get(0));
}
@Test
public void testRemoveAndReplaceRanges() {
selectedIndices.addAll(0, 1, 2, 3, 4);
changes.clear();
selectedIndices._beginChange();
selectedIndices.remove(0, 2);
selectedIndices.set(1, 4);
selectedIndices.set(2, 3);
selectedIndices._endChange();
assertEquals(1, changes.size());
assertEquals("{ [foo, bar] removed at 0, [qux, quz] replaced by [quz, qux] at 1 }", changes.get(0));
}
private static class TestObservableList<T> extends ObservableListWrapper<T> {
public TestObservableList() { super(new ArrayList<>()); }
public void _beginChange() { beginChange(); }
public void _endChange() { endChange(); }
}
}
