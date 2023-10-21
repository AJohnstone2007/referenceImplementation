package test.javafx.geometry;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import static org.junit.Assert.*;
import org.junit.Test;
public class BoundingBoxTest {
@Test
public void testIsEmpty() {
assertFalse(new BoundingBox(0, 0, 0, 0).isEmpty());
assertFalse(new BoundingBox(0, 0, 1, 0).isEmpty());
assertFalse(new BoundingBox(0, 0, 0, 1).isEmpty());
assertTrue(new BoundingBox(0, 0, -1, 0).isEmpty());
assertTrue(new BoundingBox(0, 0, 0, -1).isEmpty());
assertTrue(new BoundingBox(0, 0, -1, -1).isEmpty());
assertFalse(new BoundingBox(0, 0, 1, 1).isEmpty());
}
@Test
public void testIsEmpty3D() {
assertFalse(new BoundingBox(0, 0, 0, 0, 0, 0).isEmpty());
assertFalse(new BoundingBox(0, 0, 0, 1, 0, 0).isEmpty());
assertFalse(new BoundingBox(0, 0, 0, 0, 1, 0).isEmpty());
assertFalse(new BoundingBox(0, 0, 0, 0, 0, 1).isEmpty());
assertFalse(new BoundingBox(0, 0, 0, 1, 1, 1).isEmpty());
assertTrue(new BoundingBox(0, 0, 0, -1, 0, 0).isEmpty());
assertTrue(new BoundingBox(0, 0, 0, 0, -1, 0).isEmpty());
assertTrue(new BoundingBox(0, 0, 0, 0, 0, -1).isEmpty());
assertTrue(new BoundingBox(0, 0, 0, -1, -1, -1).isEmpty());
}
@Test
public void testCenter() {
BoundingBox bb = new BoundingBox(-2, -1, 0, 2, 2, 1);
assertEquals(bb.getCenterX(), -1, 0);
assertEquals(bb.getCenterY(), 0, 0);
assertEquals(bb.getCenterZ(), 0.5, 0);
}
@Test
public void testContains() {
BoundingBox bb = new BoundingBox(0, 0, 2, 2);
assertFalse(bb.contains((Point2D)null));
assertTrue(bb.contains(new Point2D(1, 1)));
assertTrue(bb.contains(1, 1));
assertFalse(bb.contains(3, 0));
assertFalse(bb.contains(1, -1));
assertFalse(bb.contains(1, 3));
assertFalse(bb.contains((Bounds) null));
assertTrue(bb.contains(new BoundingBox(1, 1, 1, 1)));
assertFalse(bb.contains(new BoundingBox(-1, 1, 1, 1)));
assertFalse(bb.contains(new BoundingBox(1, -1, 1, 1)));
assertFalse(bb.contains(new BoundingBox(1, 1, 2, 1)));
assertFalse(bb.contains(new BoundingBox(1, 1, 1, 2)));
assertTrue(bb.contains(1, 1, 1, 1));
assertFalse(bb.contains(-1, 1, 1, 1));
assertFalse(bb.contains(1, -1, 1, 1));
assertFalse(bb.contains(1, 1, 2, 1));
assertFalse(bb.contains(1, 1, 1, 2));
}
@Test
public void testContains3D() {
BoundingBox bb = new BoundingBox(0, 0, 0, 2, 2, 1);
assertFalse(bb.contains((Point2D)null));
assertTrue(bb.contains(new Point2D(1, 1)));
assertTrue(bb.contains(1, 1));
assertFalse(bb.contains(3, 0));
assertFalse(bb.contains(1, -1));
assertFalse(bb.contains(1, 3));
bb = new BoundingBox(0, 0, 0, 2, 2, 2);
assertFalse(bb.contains(1, 1, 3));
assertTrue(bb.contains(1, 1, 1));
assertFalse(bb.contains((Bounds) null));
assertTrue(bb.contains(new BoundingBox(1, 1, 1, 1, 1, 1)));
assertFalse(bb.contains(new BoundingBox(-1, 1, 1, 1, 1, 1)));
assertFalse(bb.contains(new BoundingBox(1, -1, 1, 1, 1, 1)));
assertFalse(bb.contains(new BoundingBox(1, 1, 1, 2, 1, 1)));
assertFalse(bb.contains(new BoundingBox(1, 1, 1, 1, 2, 1)));
assertTrue(bb.contains(1, 1, 0, 1, 1, 2));
assertFalse(bb.contains(-1, 1, 0, 1, 1, 2));
assertFalse(bb.contains(1, -1, 0, 1, 1, 2));
assertFalse(bb.contains(1, 1, 0, 2, 1, 2));
assertFalse(bb.contains(1, 1, 0, 1, 2, 2));
assertFalse(bb.contains(1, 1, 1, 1, 2, 3));
}
@Test
public void testIntersects() {
BoundingBox bb = new BoundingBox(0, 0, 2, 2);
assertFalse(bb.intersects((Bounds) null));
assertTrue(new BoundingBox(0, 0, 0, 0).intersects(bb));
assertTrue(new BoundingBox(0, 0, 0, 0).intersects(0, 0, 0, 0));
assertTrue(bb.intersects(1, 1, 1, 1));
assertTrue(bb.intersects(new BoundingBox(1, 1, 1, 1)));
assertFalse(bb.intersects(3, 3, 3, 3));
assertFalse(bb.intersects(new BoundingBox(-2, -2, 1, 1)));
}
@Test
public void testIntersects3D() {
BoundingBox bb = new BoundingBox(0, 0, 0, 2, 2, 1);
assertFalse(bb.intersects((Bounds) null));
assertTrue(new BoundingBox(0, 0, 0, 0, 0, 1).intersects(bb));
assertTrue(new BoundingBox(0, 0, 1, 0, 0, 1).intersects(0, 0, 1, 0, 0, 1));
assertTrue(bb.intersects(1, 1, 1, 1, 1, 1));
assertTrue(bb.intersects(new BoundingBox(1, 1, 1, 1, 1, 1)));
assertFalse(bb.intersects(3, 3, 0, 3, 3, 1));
assertFalse(bb.intersects(new BoundingBox(-2, -2, 0, 1, 1, 1)));
}
@Test
public void testEquals() {
BoundingBox p1 = new BoundingBox(0, 0, 0, 0);
BoundingBox p2 = new BoundingBox(0, 1, 1, 1);
BoundingBox p3 = new BoundingBox(1, 0, 1, 1);
assertTrue(p1.equals(p1));
assertTrue(p1.equals(new BoundingBox(0, 0, 0, 0)));
assertFalse(p1.equals(new Object()));
assertFalse(p1.equals(p2));
assertFalse(p1.equals(p3));
}
@Test
public void testEquals3D() {
BoundingBox p1 = new BoundingBox(0, 0, 0, 0, 0, 0);
BoundingBox p2 = new BoundingBox(0, 1, 1, 1, 1, 1);
BoundingBox p3 = new BoundingBox(1, 0, 1, 1, 1, 1);
BoundingBox p4 = new BoundingBox(0, 0, 0, 1, 1, 1);
BoundingBox p5 = new BoundingBox(0, 0, 0, 1, 1, 1);
assertTrue(p1.equals(p1));
assertTrue(p1.equals(new BoundingBox(0, 0, 0, 0, 0, 0)));
assertFalse(p1.equals(new Object()));
assertFalse(p1.equals(p2));
assertFalse(p1.equals(p3));
assertTrue(p4.equals(p5));
}
@Test
public void testToString() {
BoundingBox p1 = new BoundingBox(0, 0, 0, 0);
assertNotNull(p1.toString());
}
}
