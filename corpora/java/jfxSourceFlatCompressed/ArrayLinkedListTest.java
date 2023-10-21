package test.javafx.scene.control.skin;
import javafx.scene.control.skin.VirtualFlowShim.ArrayLinkedListShim;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
public class ArrayLinkedListTest {
private ArrayLinkedListShim<String> list;
private String a = "a";
private String b = "b";
private String c = "c";
@Before public void setUp() {
list = new ArrayLinkedListShim<String>();
}
@Test public void testArrayLinkedList_Empty_GetFirstReturnsNull() {
assertNull(list.getFirst());
}
@Test public void testArrayLinkedList_Empty_GetLastReturnsNull() {
assertNull(list.getLast());
}
@Test public void testArrayLinkedList_Empty_AddFirst() {
list.addFirst(a);
assertEquals(1, list.size());
assertEquals(a, list.getFirst());
assertEquals(a, list.getLast());
assertEquals(a, list.get(0));
assertFalse(list.isEmpty());
}
@Test public void testArrayLinkedList_Empty_AddLast() {
list.addLast(c);
assertEquals(1, list.size());
assertEquals(c, list.getFirst());
assertEquals(c, list.getLast());
assertEquals(c, list.get(0));
assertFalse(list.isEmpty());
}
@Test public void testArrayLinkedList_Empty_SizeIsZero() {
assertEquals(0, list.size());
}
@Test public void testArrayLinkedList_Empty_IsEmpty() {
assertTrue(list.isEmpty());
}
@Test public void testArrayLinkedList_Empty_ClearHasNoEffect() {
list.clear();
assertTrue(list.isEmpty());
assertEquals(0, list.size());
}
@Test public void testArrayLinkedList_Empty_GetResultsInArrayIndexOutOfBounds() {
try {
list.get(0);
assertTrue("get didn't return an IndexOutOfBoundsException", false);
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
}
}
@Test public void testArrayLinkedList_Empty_RemoveFirstIsNoOp() {
list.removeFirst();
}
@Test public void testArrayLinkedList_Empty_RemoveLastIsNoOp() {
list.removeLast();
}
@Test public void testArrayLinkedList_Empty_RemoveResultsInArrayIndexOutOfBounds() {
try {
list.remove(0);
assertTrue("remove didn't return an IndexOutOfBoundsException", false);
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
}
}
@Test public void testArrayLinkedList_GetFirst_AfterAddLast() {
list.addLast(a);
list.addLast(b);
list.addLast(c);
assertEquals(a, list.getFirst());
}
@Test public void testArrayLinkedList_GetFirst_AfterAddFirst() {
list.addFirst(c);
list.addFirst(b);
list.addFirst(a);
assertEquals(a, list.getFirst());
}
@Test public void testArrayLinkedList_GetFirst_AfterAddFirstAndAddLast() {
list.addFirst(b);
list.addLast(c);
list.addFirst(a);
assertEquals(a, list.getFirst());
}
@Test public void testArrayLinkedList_GetFirst_AfterRemoveFirst() {
list.addLast(a);
list.addLast(b);
list.addLast(c);
list.removeFirst();
assertEquals(b, list.getFirst());
list.removeFirst();
assertEquals(c, list.getFirst());
list.removeFirst();
assertNull(list.getFirst());
}
@Test public void testArrayLinkedList_GetFirst_AfterRemoveLast() {
list.addLast(a);
list.addLast(b);
list.addLast(c);
list.removeLast();
assertEquals(a, list.getFirst());
list.removeLast();
assertEquals(a, list.getFirst());
list.removeLast();
assertNull(list.getFirst());
}
@Test public void testArrayLinkedList_GetLast_AfterAddLast() {
list.addLast(a);
list.addLast(b);
list.addLast(c);
assertEquals(c, list.getLast());
}
@Test public void testArrayLinkedList_GetLast_AfterAddFirst() {
list.addFirst(c);
list.addFirst(b);
list.addFirst(a);
assertEquals(c, list.getLast());
}
@Test public void testArrayLinkedList_GetLast_AfterAddFirstAndAddLast() {
list.addFirst(b);
list.addLast(c);
list.addFirst(a);
assertEquals(c, list.getLast());
}
@Test public void testArrayLinkedList_GetLast_AfterRemoveFirst() {
list.addLast(a);
list.addLast(b);
list.addLast(c);
list.removeFirst();
assertEquals(c, list.getLast());
list.removeFirst();
assertEquals(c, list.getLast());
list.removeFirst();
assertNull(list.getLast());
}
@Test public void testArrayLinkedList_GetLast_AfterRemoveLast() {
list.addLast(a);
list.addLast(b);
list.addLast(c);
list.removeLast();
assertEquals(b, list.getLast());
list.removeLast();
assertEquals(a, list.getLast());
list.removeLast();
assertNull(list.getLast());
}
@Test public void testArrayLinkedList_Get_AfterAddLast() {
list.addLast(a);
list.addLast(b);
list.addLast(c);
assertEquals(a, list.get(0));
assertEquals(b, list.get(1));
assertEquals(c, list.get(2));
}
@Test public void testArrayLinkedList_Get_AfterAddFirst() {
list.addFirst(c);
list.addFirst(b);
list.addFirst(a);
assertEquals(a, list.get(0));
assertEquals(b, list.get(1));
assertEquals(c, list.get(2));
}
@Test public void testArrayLinkedList_Get_AfterAddFirstAndAddLast() {
list.addFirst(b);
list.addLast(c);
list.addFirst(a);
assertEquals(a, list.get(0));
assertEquals(b, list.get(1));
assertEquals(c, list.get(2));
}
@Test public void testArrayLinkedList_Get_AfterRemoveFirst() {
list.addLast(a);
list.addLast(b);
list.addLast(c);
list.removeFirst();
assertEquals(b, list.get(0));
assertEquals(c, list.get(1));
list.removeFirst();
assertEquals(c, list.get(0));
}
@Test public void testArrayLinkedList_Get_AfterRemoveLast() {
list.addLast(a);
list.addLast(b);
list.addLast(c);
list.removeLast();
assertEquals(a, list.get(0));
assertEquals(b, list.get(1));
list.removeLast();
assertEquals(a, list.get(0));
}
}
