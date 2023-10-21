package test.com.sun.javafx.geom;
import com.sun.javafx.geom.BoxBounds;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;
public class BaseBoundsTest {
public @Test
void testBounds_MakeEmptyTest() {
RectBounds rectBounds = new RectBounds(10, 20, 20, 30);
assertFalse(rectBounds.isEmpty());
rectBounds.makeEmpty();
assertTrue(rectBounds.isEmpty());
assertEquals(new RectBounds(), rectBounds);
BoxBounds boxBounds = new BoxBounds(10, 20, 10, 40, 50, 20);
assertFalse(boxBounds.isEmpty());
boxBounds.makeEmpty();
assertTrue(boxBounds.isEmpty());
assertEquals(new BoxBounds(), boxBounds);
}
public @Test
void testRectangle_ZeroArea() {
RectBounds rectBounds = new RectBounds(new Rectangle());
assertFalse(rectBounds.isEmpty());
RectBounds rectBounds2 = new RectBounds(0, 0, 0, 0);
assertEquals(rectBounds, rectBounds2);
}
public @Test
void testRectangle_Offset() {
Rectangle rect = new Rectangle(10, 20, 40, 50);
RectBounds rectBounds = new RectBounds(rect);
assertFalse(rectBounds.isEmpty());
RectBounds rectBounds2 = new RectBounds(10, 20, 50, 70);
assertEquals(rectBounds, rectBounds2);
}
public @Test
void testBounds_IntersectsTest1() {
RectBounds rectBounds = new RectBounds(10, 20, 40, 50);
assertTrue(rectBounds.is2D());
assertFalse(rectBounds.isEmpty());
BoxBounds boxBounds = new BoxBounds(10, 20, 0, 20, 30, 0);
assertTrue(boxBounds.is2D());
assertTrue(boxBounds.intersects(rectBounds));
boxBounds = new BoxBounds(10, 20, 2, 20, 30, 2);
assertFalse(boxBounds.is2D());
assertFalse(boxBounds.intersects(rectBounds));
}
public @Test
void testBounds_IntersectsTest2() {
RectBounds rectBounds = new RectBounds(10, 20, 40, 50);
assertFalse(rectBounds.isEmpty());
BoxBounds boxBounds = new BoxBounds(10, 20, 1, 20, 30, 1);
assertFalse(boxBounds.intersects(rectBounds));
}
public @Test
void testBounds_IntersectsTest3() {
BoxBounds boxBounds = new BoxBounds(10, 20, 10, 40, 50, 20);
assertFalse(boxBounds.isEmpty());
BoxBounds boxBounds2 = new BoxBounds(10, 20, 0, 20, 30, 5);
assertFalse(boxBounds2.intersects(boxBounds));
}
public @Test
void testBounds_IntersectsTest4() {
BoxBounds boxBounds = new BoxBounds(10, 20, 10, 40, 50, 20);
assertFalse(boxBounds.isEmpty());
BoxBounds boxBounds2 = new BoxBounds(10, 20, 0, 20, 30, 10);
assertTrue(boxBounds2.intersects(boxBounds));
}
public @Test
void testBounds_SetBoundsAndSortTest() {
RectBounds rectBounds = new RectBounds();
assertTrue(rectBounds.isEmpty());
rectBounds.setBoundsAndSort(20, 30, 10, 20);
assertFalse(rectBounds.isEmpty());
assertEquals(new RectBounds(10, 20, 20, 30), rectBounds);
BoxBounds boxBounds = new BoxBounds();
assertTrue(boxBounds.isEmpty());
boxBounds.setBoundsAndSort(40, 50, 20, 10, 20, 10);
assertFalse(boxBounds.isEmpty());
assertEquals(new BoxBounds(10, 20, 10, 40, 50, 20), boxBounds);
}
public @Test
void testBounds_UnionWithTest() {
RectBounds rectBounds = new RectBounds();
assertTrue(rectBounds.isEmpty());
RectBounds rectBounds2 = new RectBounds(0, 1, 2, 4);
rectBounds.unionWith(rectBounds2);
assertFalse(rectBounds.isEmpty());
assertEquals(new RectBounds(0, 1, 2, 4), rectBounds);
RectBounds rectBounds3 = new RectBounds(-1, -2, 2, 3);
rectBounds.unionWith(rectBounds3);
assertEquals(new RectBounds(-1, -2, 2, 4), rectBounds);
BoxBounds boxBounds = new BoxBounds();
assertTrue(boxBounds.isEmpty());
BoxBounds boxBounds2 = new BoxBounds(0, 1, 2, 2, 3, 4);
boxBounds.unionWith(boxBounds2);
assertFalse(boxBounds.isEmpty());
assertEquals(new BoxBounds(0, 1, 2, 2, 3, 4), boxBounds);
BoxBounds boxBounds3 = new BoxBounds(-1, -2, -3, 1, 2, 3);
boxBounds.unionWith(boxBounds3);
assertEquals(new BoxBounds(-1, -2, -3, 2, 3, 4), boxBounds);
}
}
