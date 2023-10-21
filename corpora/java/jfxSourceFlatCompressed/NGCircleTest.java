package test.com.sun.javafx.sg.prism;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.sg.prism.NGCircle;
import com.sun.javafx.sg.prism.NGNodeShim;
import com.sun.prism.paint.Color;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
public class NGCircleTest extends NGTestBase {
NGCircle circle;
@Before public void setup() {
circle = new NGCircle();
circle.setFillPaint(Color.RED);
circle.updateCircle(10, 10, 5);
}
@Test
public void testSupportsOpaqueRegion() {
assertTrue(NGNodeShim.supportsOpaqueRegions(circle));
}
@Test
public void hasOpaqueRegionIfRadiusIsGreaterThanZero() {
assertTrue(NGNodeShim.hasOpaqueRegion(circle));
circle.updateCircle(10, 10, 0);
assertFalse(NGNodeShim.hasOpaqueRegion(circle));
circle.updateCircle(10, 10, .0001f);
assertTrue(NGNodeShim.hasOpaqueRegion(circle));
}
@Test
public void opaqueRegionLiesWithinCircle() {
RectBounds or = new RectBounds();
final float[] radiusValues = new float[] {
.001f,
1f/3f,
(float) Math.E,
(float) Math.PI,
10f,
13.321f
};
for (float r : radiusValues) {
circle.updateCircle(10, 10, r);
or = NGNodeShim.computeOpaqueRegion(circle, or);
assertNotNull(or);
assertTrue(circle.getShape().contains(or.getMinX(), or.getMinY(), or.getWidth(), or.getHeight()));
}
}
@Test
public void testComputeOpaqueRegion() {
RectBounds or = NGNodeShim.computeOpaqueRegion(circle, new RectBounds());
float r = 5;
float side = 2*r / (float) Math.sqrt(2);
float halfSide = side / 2f;
float x1 = 10 - halfSide;
float y1 = 10 - halfSide;
float x2 = 10 + halfSide;
float y2 = 10 + halfSide;
assertTrue(x1 < or.getMinX());
assertTrue(y1 < or.getMinY());
assertTrue(x2 > or.getMaxX());
assertTrue(y2 > or.getMaxY());
assertEquals(x1, or.getMinX(), .1f);
assertEquals(y1, or.getMinY(), .1f);
assertEquals(x2, or.getMaxX(), .1f);
assertEquals(y2, or.getMaxY(), .1f);
}
}
