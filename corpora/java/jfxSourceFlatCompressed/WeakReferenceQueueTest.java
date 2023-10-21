package test.com.sun.javafx.util;
import com.sun.javafx.util.WeakReferenceQueue;
import com.sun.javafx.util.WeakReferenceQueueShim;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Iterator;
import org.junit.Test;
public class WeakReferenceQueueTest {
@Test
public void testAdd() {
WeakReferenceQueue q = new WeakReferenceQueue();
String s = new String("Wow!");
q.add(s);
assertEquals(1, WeakReferenceQueueShim.size(q));
}
@Test
public void testRemove() {
WeakReferenceQueue q = new WeakReferenceQueue();
String a = new String("a");
q.add(a);
String b = new String("b");
q.add(b);
String c = new String("c");
q.add(c);
assertEquals(3, WeakReferenceQueueShim.size(q));
q.remove(a);
q.remove(c);
assertEquals(1, WeakReferenceQueueShim.size(q));
}
@Test
public void testCleanup() {
WeakReferenceQueue q = new WeakReferenceQueue();
String a = new String("a");
q.add(a);
String b = new String("b");
q.add(b);
String c = new String("c");
q.add(c);
assertEquals(3, WeakReferenceQueueShim.size(q));
a = null;
c = null;
tryGCReallyHard();
q.cleanup();
assertEquals(1, WeakReferenceQueueShim.size(q));
}
@Test
public void testIterator() {
WeakReferenceQueue q = new WeakReferenceQueue();
String a = new String("a");
q.add(a);
String b = new String("b");
q.add(b);
String c = new String("c");
q.add(c);
Iterator itr = q.iterator();
assertTrue(itr.hasNext());
assertEquals(c, itr.next());
assertTrue(itr.hasNext());
assertEquals(b, itr.next());
assertTrue(itr.hasNext());
assertEquals(a, itr.next());
assertFalse(itr.hasNext());
itr = q.iterator();
assertEquals(c, itr.next());
assertEquals(b, itr.next());
assertEquals(a, itr.next());
}
@Test
public void testEmptyIterator() {
WeakReferenceQueue q = new WeakReferenceQueue();
Iterator itr = q.iterator();
assertFalse(itr.hasNext());
}
@Test
public void testIteratorRemove() {
WeakReferenceQueue q = new WeakReferenceQueue();
String a = new String("a");
q.add(a);
String b = new String("b");
q.add(b);
String c = new String("c");
q.add(c);
Iterator itr = q.iterator();
itr.next();
itr.remove();
assertEquals(2, WeakReferenceQueueShim.size(q));
itr.next();
itr.remove();
assertEquals(1, WeakReferenceQueueShim.size(q));
itr.next();
itr.remove();
assertEquals(0, WeakReferenceQueueShim.size(q));
q.add(a);
q.add(b);
q.add(c);
itr = q.iterator();
itr.next();
itr.next();
itr.remove();
itr = q.iterator();
assertEquals(c, itr.next());
assertEquals(a, itr.next());
}
@Test
public void testIteratingOverSparseQueue() {
WeakReferenceQueue q = new WeakReferenceQueue();
String a = new String("a");
q.add(a);
String b = new String("b");
q.add(b);
String c = new String("c");
q.add(c);
assertEquals(3, WeakReferenceQueueShim.size(q));
a = null;
c = null;
tryGCReallyHard();
q.cleanup();
Iterator itr = q.iterator();
assertEquals(b, itr.next());
assertFalse(itr.hasNext());
}
@Test
public void testIteratingOverSparseQueueWithoutCleanup() {
WeakReferenceQueue q = new WeakReferenceQueue();
String a = new String("a");
q.add(a);
String b = new String("b");
q.add(b);
String c = new String("c");
q.add(c);
assertEquals(3, WeakReferenceQueueShim.size(q));
a = null;
c = null;
tryGCReallyHard();
Iterator itr = q.iterator();
assertEquals(b, itr.next());
assertFalse(itr.hasNext());
}
private void tryGCReallyHard() {
for (int i = 0; i < 100000; i++) {
String s = new String("GARBAGE");
}
for (int i = 0; i < 10; i++) {
System.gc();
System.gc();
System.gc();
}
try { Thread.sleep(100); } catch (InterruptedException e) {}
}
}
