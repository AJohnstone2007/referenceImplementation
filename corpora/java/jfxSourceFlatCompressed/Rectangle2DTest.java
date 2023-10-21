package test.javafx.geometry;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
public class Rectangle2DTest {
@Test(expected=IllegalArgumentException.class)
public void testConstruction1() {
new Rectangle2D(0, 0, -1, 0);
}
@Test(expected=IllegalArgumentException.class)
public void testConstruction2() {
new Rectangle2D(0, 0, 0, -1);
}
@Test
public void testContains() {
Rectangle2D r = new Rectangle2D(0, 0, 1, 1);
assertFalse(r.contains((Point2D)null));
assertFalse(r.contains((Rectangle2D)null));
assertTrue(r.contains(new Point2D(.5f, .5f)));
assertTrue(r.contains(new Rectangle2D(.1f, .1f, .5f, .5f)));
assertFalse(r.contains(-1, -1));
assertFalse(r.contains(2, 1));
assertFalse(r.contains(.5f, -1));
assertFalse(r.contains(.5f, 2));
assertTrue(r.contains(.1f, .1f, .1f, .1f));
assertFalse(r.contains(.1f, -1, 1, 1));
assertFalse(r.contains(.1f, .1f, 2, 2));
assertFalse(r.contains(.1f, .1f, .1f, 2));
}
@Test
public void testIntersects() {
Rectangle2D r = new Rectangle2D(0, 0, 1, 1);
assertFalse(r.intersects((Rectangle2D)null));
assertTrue(r.intersects(new Rectangle2D(.1f, .1f, .1f, .1f)));
assertFalse(r.intersects(new Rectangle2D(-1, 1, 1, 1)));
assertFalse(r.intersects(new Rectangle2D(.1f, 1, 1, 1)));
assertFalse(r.intersects(-1, 1, 1, 1));
assertFalse(r.intersects(.1f, 1, 1, 1));
}
@Test
public void testEquals() {
Rectangle2D r1 = new Rectangle2D(0, 0, 1, 1);
Rectangle2D r2 = new Rectangle2D(1, 1, 1, 1);
Rectangle2D r3 = new Rectangle2D(0, 1, 1, 1);
Rectangle2D r4 = new Rectangle2D(0, 0, 2, 1);
Rectangle2D r5 = new Rectangle2D(0, 0, 1, 2);
assertTrue(r1.equals(r1));
assertTrue(r1.equals(new Rectangle2D(0, 0, 1, 1)));
assertFalse(r1.equals(new Object()));
assertFalse(r1.equals(r2));
assertFalse(r1.equals(r3));
assertFalse(r1.equals(r4));
assertFalse(r1.equals(r5));
}
@Test
public void testHash() {
Rectangle2D p1 = new Rectangle2D(0, 0, 0, 0);
Rectangle2D p2 = new Rectangle2D(0, 1, 0, 0);
Rectangle2D p3 = new Rectangle2D(0, 1, 0, 0);
assertEquals(p3.hashCode(), p2.hashCode());
assertFalse(p1.hashCode() == p2.hashCode());
}
@Test
public void testToString() {
Rectangle2D p1 = new Rectangle2D(0, 0, 0, 0);
assertNotNull(p1.toString());
}
}
