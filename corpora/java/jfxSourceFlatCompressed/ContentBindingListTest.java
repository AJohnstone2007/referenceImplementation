package test.com.sun.javafx.binding;
import com.sun.javafx.binding.ContentBinding;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;
public class ContentBindingListTest {
private List<Integer> op1;
private ObservableList<Integer> op2;
private ObservableList<Integer> op3;
private List<Integer> list0;
private List<Integer> list1;
private List<Integer> list2;
@Before
public void setUp() {
list0 = new ArrayList<Integer>();
list1 = new ArrayList<Integer>(Arrays.asList(0));
list2 = new ArrayList<Integer>(Arrays.asList(2, 1));
op1 = new ArrayList<Integer>(list1);
op2 = FXCollections.observableArrayList(list2);
op3 = FXCollections.observableArrayList(list0);
}
@Test
public void testBind() {
List<Integer> list2_sorted = new ArrayList<Integer>(Arrays.asList(1, 2));
Bindings.bindContent(op1, op2);
System.gc();
assertEquals(list2, op1);
assertEquals(list2, op2);
op2.setAll(list1);
assertEquals(list1, op1);
assertEquals(list1, op2);
op2.setAll(list0);
assertEquals(list0, op1);
assertEquals(list0, op2);
op2.setAll(list2);
assertEquals(list2, op1);
assertEquals(list2, op2);
FXCollections.sort(op2);
assertEquals(list2_sorted, op1);
assertEquals(list2_sorted, op2);
}
@Test(expected = NullPointerException.class)
public void testBind_Null_X() {
Bindings.bindContent(null, op2);
}
@Test(expected = NullPointerException.class)
public void testBind_X_Null() {
Bindings.bindContent(op1, null);
}
@Test(expected = IllegalArgumentException.class)
public void testBind_X_Self() {
Bindings.bindContent(op2, op2);
}
@Test
public void testUnbind() {
Bindings.unbindContent(op1, op2);
Bindings.bindContent(op1, op2);
System.gc();
assertEquals(list2, op1);
assertEquals(list2, op2);
Bindings.unbindContent(op1, op2);
System.gc();
assertEquals(list2, op1);
assertEquals(list2, op2);
op1.clear();
assertEquals(list0, op1);
assertEquals(list2, op2);
op2.setAll(list1);
assertEquals(list0, op1);
assertEquals(list1, op2);
}
@Test(expected = NullPointerException.class)
public void testUnbind_Null_X() {
Bindings.unbindContent(null, op2);
}
@Test(expected = NullPointerException.class)
public void testUnbind_X_Null() {
Bindings.unbindContent(op1, null);
}
@Test(expected = IllegalArgumentException.class)
public void testUnbind_X_Self() {
Bindings.unbindContent(op2, op2);
}
@Test
public void testChaining() {
Bindings.bindContent(op1, op2);
Bindings.bindContent(op2, op3);
System.gc();
assertEquals(list0, op1);
assertEquals(list0, op2);
assertEquals(list0, op3);
op3.setAll(list1);
assertEquals(list1, op1);
assertEquals(list1, op2);
assertEquals(list1, op3);
Bindings.unbindContent(op1, op2);
System.gc();
assertEquals(list1, op1);
assertEquals(list1, op2);
assertEquals(list1, op3);
op3.setAll(list2);
assertEquals(list1, op1);
assertEquals(list2, op2);
assertEquals(list2, op3);
}
@Test
public void testHashCode() {
final int hc1 = ContentBinding.bind(op1, op2).hashCode();
ContentBinding.unbind(op1, op2);
final int hc2 = ContentBinding.bind(op1, op2).hashCode();
assertEquals(hc1, hc2);
}
@Test
public void testEquals() {
final Object golden = ContentBinding.bind(op1, op2);
ContentBinding.unbind(op1, op2);
assertTrue(golden.equals(golden));
assertFalse(golden.equals(null));
assertFalse(golden.equals(op1));
assertTrue(golden.equals(ContentBinding.bind(op1, op2)));
ContentBinding.unbind(op1, op2);
assertFalse(golden.equals(ContentBinding.bind(op3, op2)));
ContentBinding.unbind(op2, op3);
assertFalse(golden.equals(ContentBinding.bind(op2, op3)));
ContentBinding.unbind(op2, op3);
}
@Test
public void testEqualsWithGCedProperty() {
final Object binding1 = ContentBinding.bind(op1, op2);
ContentBinding.unbind(op1, op2);
final Object binding2 = ContentBinding.bind(op1, op2);
ContentBinding.unbind(op1, op2);
op1 = null;
System.gc();
assertTrue(binding1.equals(binding1));
assertFalse(binding1.equals(binding2));
}
@Test
public void testAlreadyBound() {
final Thread.UncaughtExceptionHandler exceptionHandler =
Thread.currentThread().getUncaughtExceptionHandler();
Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
throw new AssertionError("We don't expect any exceptions in this test!", e);
}
);
ContentBinding.bind(op1, op2);
ContentBinding.bind(op1, op2);
op2.remove(1);
Thread.currentThread().setUncaughtExceptionHandler(exceptionHandler);
}
@Test
public void testChangeValue() {
Bindings.bindContent(op1, op2);
assertEquals(2, op1.get(0).intValue());
op2.set(0, 1);
assertEquals(1, op1.get(0).intValue());
}
}
