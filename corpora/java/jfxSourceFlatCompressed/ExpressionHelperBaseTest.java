package test.com.sun.javafx.binding;
import com.sun.javafx.binding.ExpressionHelperBaseShim;
import static org.junit.Assert.*;
import javafx.beans.WeakListener;
import org.junit.Test;
public class ExpressionHelperBaseTest {
private static final Object listener = new Object();
private static final Object listener2 = new Object();
private static final WeakListener validWeakListener = () -> false;
private static final WeakListener gcedWeakListener = () -> true;
@Test
public void testEmptyArray() {
Object[] array = new Object[0];
assertEquals(0, ExpressionHelperBaseShim.trim(0, array));
assertArrayEquals(new Object[0], array);
array = new Object[1];
assertEquals(0, ExpressionHelperBaseShim.trim(0, array));
assertArrayEquals(new Object[1], array);
}
@Test
public void testSingleElement() {
Object[] array = new Object[] {listener};
assertEquals(1, ExpressionHelperBaseShim.trim(1, array));
assertArrayEquals(new Object[] {listener}, array);
array = new Object[] {validWeakListener};
assertEquals(1, ExpressionHelperBaseShim.trim(1, array));
assertArrayEquals(new Object[] {validWeakListener}, array);
array = new Object[] {gcedWeakListener};
assertEquals(0, ExpressionHelperBaseShim.trim(1, array));
assertArrayEquals(new Object[] {null}, array);
array = new Object[] {listener, null};
assertEquals(1, ExpressionHelperBaseShim.trim(1, array));
assertArrayEquals(new Object[] {listener, null}, array);
array = new Object[] {validWeakListener, null};
assertEquals(1, ExpressionHelperBaseShim.trim(1, array));
assertArrayEquals(new Object[] {validWeakListener, null}, array);
array = new Object[] {gcedWeakListener, null};
assertEquals(0, ExpressionHelperBaseShim.trim(1, array));
assertArrayEquals(new Object[] {null, null}, array);
}
@Test
public void testMultipleElements() {
Object[] array = new Object[] {validWeakListener, listener, listener2};
assertEquals(3, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {validWeakListener, listener, listener2}, array);
array = new Object[] {listener, validWeakListener, listener2};
assertEquals(3, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {listener, validWeakListener, listener2}, array);
array = new Object[] {listener, listener2, validWeakListener};
assertEquals(3, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {listener, listener2, validWeakListener}, array);
array = new Object[] {validWeakListener, listener, listener2, null};
assertEquals(3, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {validWeakListener, listener, listener2, null}, array);
array = new Object[] {listener, validWeakListener, listener2, null};
assertEquals(3, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {listener, validWeakListener, listener2, null}, array);
array = new Object[] {listener, listener2, validWeakListener, null};
assertEquals(3, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {listener, listener2, validWeakListener, null}, array);
array = new Object[] {gcedWeakListener, validWeakListener, listener};
assertEquals(2, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {validWeakListener, listener, null}, array);
array = new Object[] {gcedWeakListener, listener, validWeakListener};
assertEquals(2, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {listener, validWeakListener, null}, array);
array = new Object[] {gcedWeakListener, validWeakListener, listener, null};
assertEquals(2, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {validWeakListener, listener, null, null}, array);
array = new Object[] {gcedWeakListener, listener, validWeakListener, null};
assertEquals(2, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {listener, validWeakListener, null, null}, array);
array = new Object[] {validWeakListener, gcedWeakListener, listener};
assertEquals(2, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {validWeakListener, listener, null}, array);
array = new Object[] {listener, gcedWeakListener, validWeakListener};
assertEquals(2, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {listener, validWeakListener, null}, array);
array = new Object[] {validWeakListener, gcedWeakListener, listener, null};
assertEquals(2, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {validWeakListener, listener, null, null}, array);
array = new Object[] {listener, gcedWeakListener, validWeakListener, null};
assertEquals(2, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {listener, validWeakListener, null, null}, array);
array = new Object[] {validWeakListener, listener, gcedWeakListener};
assertEquals(2, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {validWeakListener, listener, null}, array);
array = new Object[] {listener, validWeakListener, gcedWeakListener};
assertEquals(2, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {listener, validWeakListener, null}, array);
array = new Object[] {validWeakListener, listener, gcedWeakListener, null};
assertEquals(2, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {validWeakListener, listener, null, null}, array);
array = new Object[] {listener, validWeakListener, gcedWeakListener, null};
assertEquals(2, ExpressionHelperBaseShim.trim(3, array));
assertArrayEquals(new Object[] {listener, validWeakListener, null, null}, array);
array = new Object[] {gcedWeakListener, gcedWeakListener};
assertEquals(0, ExpressionHelperBaseShim.trim(2, array));
assertArrayEquals(new Object[] {null, null}, array);
array = new Object[] {gcedWeakListener, gcedWeakListener, null};
assertEquals(0, ExpressionHelperBaseShim.trim(2, array));
assertArrayEquals(new Object[] {null, null, null}, array);
}
}
