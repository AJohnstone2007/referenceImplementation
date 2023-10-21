package test.javafx.geometry;
import javafx.geometry.Dimension2D;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
public class Dimension2DTest {
@Test
public void testEquals() {
Dimension2D p1 = new Dimension2D(1, 1);
Dimension2D p2 = new Dimension2D(0, 0);
Dimension2D p3 = new Dimension2D(0, 0);
assertTrue(p1.equals(p1));
assertTrue(p1.equals(new Dimension2D(1, 1)));
assertFalse(p1.equals(new Object()));
assertFalse(p1.equals(p2));
assertFalse(p1.equals(p3));
assertTrue(p2.equals(p3));
}
@Test
public void testHashCode() {
Dimension2D d = new Dimension2D(1, 2);
Dimension2D d2 = new Dimension2D(1, 1);
int h = d.hashCode();
assertEquals(h, d.hashCode());
assertFalse(d.hashCode() == d2.hashCode());
}
@Test
public void testToString() {
assertNotNull(new Dimension2D(0,0).toString());
}
}
