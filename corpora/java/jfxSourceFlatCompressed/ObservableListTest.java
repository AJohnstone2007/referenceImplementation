package test.javafx.collections;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
@RunWith(Parameterized.class)
public class ObservableListTest {
static final List<String> EMPTY = Collections.emptyList();
final Callable<ObservableList<String>> listFactory;
ObservableList<String> list;
MockListObserver<String> mlo;
public ObservableListTest(final Callable<ObservableList<String>> listFactory) {
this.listFactory = listFactory;
}
@Parameterized.Parameters
public static Collection createParameters() {
Object[][] data = new Object[][] {
{ TestedObservableLists.ARRAY_LIST },
{ TestedObservableLists.LINKED_LIST },
{ TestedObservableLists.VETOABLE_LIST },
{ TestedObservableLists.CHECKED_OBSERVABLE_ARRAY_LIST },
{ TestedObservableLists.SYNCHRONIZED_OBSERVABLE_ARRAY_LIST },
{ TestedObservableLists.OBSERVABLE_LIST_PROPERTY }
};
return Arrays.asList(data);
}
@Before
public void setUp() throws Exception {
list = listFactory.call();
mlo = new MockListObserver<String>();
list.addListener(mlo);
useListData("one", "two", "three");
}
void useListData(String... strings) {
list.clear();
list.addAll(Arrays.asList(strings));
mlo.clear();
}
@Test
public void testObserverAddRemove() {
MockListObserver<String> mlo2 = new MockListObserver<String>();
list.addListener(mlo2);
list.removeListener(mlo);
list.add("xyzzy");
mlo.check0();
mlo2.check1AddRemove(list, EMPTY, 3, 4);
}
@Test
@Ignore
public void testObserverAddTwice() {
list.addListener(mlo);
list.add("plugh");
mlo.check1AddRemove(list, EMPTY, 3, 4);
}
@Test
public void testObserverRemoveTwice() {
list.removeListener(mlo);
list.removeListener(mlo);
list.add("plugh");
mlo.check0();
}
@Test
public void testAddToEmpty() {
useListData();
list.add("asdf");
mlo.check1AddRemove(list, EMPTY, 0, 1);
}
@Test
public void testAddAtEnd() {
list.add("four");
mlo.check1AddRemove(list, EMPTY, 3, 4);
}
@Test
public void testAddInMiddle() {
list.add(1, "xyz");
mlo.check1AddRemove(list, EMPTY, 1, 2);
}
@Test
public void testAddSeveralToEmpty() {
useListData();
list.addAll(Arrays.asList("alpha", "bravo", "charlie"));
mlo.check1AddRemove(list, EMPTY, 0, 3);
}
@Test
public void testAddSeveralAtEnd() {
list.addAll(Arrays.asList("four", "five"));
mlo.check1AddRemove(list, EMPTY, 3, 5);
}
@Test
public void testAddSeveralInMiddle() {
list.addAll(1, Arrays.asList("a", "b"));
mlo.check1AddRemove(list, EMPTY, 1, 3);
}
@Test
public void testClearNonempty() {
list.clear();
mlo.check1AddRemove(list, Arrays.asList("one", "two", "three"), 0, 0);
}
@Test
public void testRemoveByIndex() {
String r = list.remove(1);
mlo.check1AddRemove(list, Arrays.asList("two"), 1, 1);
assertEquals("two", r);
}
@Test
public void testRemoveObject() {
useListData("one", "x", "two", "three");
boolean b = list.remove("two");
mlo.check1AddRemove(list, Arrays.asList("two"), 2, 2);
assertTrue(b);
}
@Test
public void testRemoveNull() {
useListData("one", "two", null, "three");
boolean b = list.remove(null);
mlo.check1AddRemove(list, Arrays.asList((String)null), 2, 2);
assertTrue(b);
}
@Test
public void testRemoveAll() {
useListData("one", "two", "three", "four", "five");
list.removeAll(Arrays.asList("one", "two", "four", "six"));
assertEquals(2, mlo.calls.size());
mlo.checkAddRemove(0, list, Arrays.asList("one", "two"), 0, 0);
mlo.checkAddRemove(1, list, Arrays.asList("four"), 1, 1);
}
@Test
public void testRemoveAll_1() {
useListData("a", "c", "d", "c");
list.removeAll(Arrays.asList("c"));
assertEquals(2, mlo.calls.size());
mlo.checkAddRemove(0, list, Arrays.asList("c"), 1, 1);
mlo.checkAddRemove(1, list, Arrays.asList("c"), 2, 2);
}
@Test
public void testRemoveAll_2() {
useListData("one", "two");
list.removeAll(Arrays.asList("three", "four"));
mlo.check0();
}
@Test
public void testRemoveAll_3() {
useListData("a", "c", "d", "c");
list.removeAll(Arrays.asList("d"));
assertEquals(1, mlo.calls.size());
mlo.checkAddRemove(0, list, Arrays.asList("d"), 2, 2);
}
@Test
public void testRemoveAll_4() {
useListData("a", "c", "d", "c");
list.removeAll(Arrays.asList("d", "c"));
assertEquals(1, mlo.calls.size());
mlo.checkAddRemove(0, list, Arrays.asList("c", "d", "c"), 1, 1);
}
@Test
public void testRetainAll() {
useListData("one", "two", "three", "four", "five");
list.retainAll(Arrays.asList("two", "five", "six"));
assertEquals(2, mlo.calls.size());
mlo.checkAddRemove(0, list, Arrays.asList("one"), 0, 0);
mlo.checkAddRemove(1, list, Arrays.asList("three", "four"), 1, 1);
}
@Test
public void testRetainAllEmptySource() {
List<String> data = new ArrayList<>(list);
list.retainAll();
assertTrue(list.isEmpty());
mlo.check1AddRemove(list, data, 0, 0);
}
@Test
public void testRemoveNonexistent() {
useListData("one", "two", "x", "three");
boolean b = list.remove("four");
mlo.check0();
assertFalse(b);
}
@Test
public void testSet() {
String r = list.set(1, "fnord");
mlo.check1AddRemove(list, Arrays.asList("two"), 1, 2);
assertEquals("two", r);
}
@Test
public void testSetAll() {
useListData("one", "two", "three");
boolean r = list.setAll("one");
assertTrue(r);
r = list.setAll("one", "four", "five");
assertTrue(r);
r = list.setAll();
assertTrue(r);
r = list.setAll("one");
assertTrue(r);
}
@Test
public void testSetAllNoUpdate() {
useListData();
boolean r = list.setAll();
assertFalse(r);
}
@Test
public void testObserverCanRemoveObservers() {
final ListChangeListener<String> listObserver = change -> {
change.getList().removeListener(mlo);
};
list.addListener(listObserver);
list.add("x");
mlo.clear();
list.add("y");
mlo.check0();
list.removeListener(listObserver);
final StringListChangeListener listener = new StringListChangeListener();
list.addListener(listener);
list.add("z");
assertEquals(listener.counter, 1);
list.add("zz");
assertEquals(listener.counter, 1);
}
@Test
public void testEqualsAndHashCode() {
final List<String> other = Arrays.asList("one", "two", "three");
assertTrue(list.equals(other));
assertEquals(list.hashCode(), other.hashCode());
}
private static class StringListChangeListener implements ListChangeListener<String> {
private int counter;
@Override
public void onChanged(final Change<? extends String> change) {
change.getList().removeListener(this);
++counter;
}
}
}
