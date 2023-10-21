package test.com.sun.javafx.sg.prism;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.sg.prism.NGEllipse;
import com.sun.javafx.sg.prism.NGNodeShim;
import com.sun.prism.paint.Color;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
public class NGEllipseTest extends NGTestBase {
NGEllipse ellipse;
@Before
public void setup() {
ellipse = new NGEllipse();
ellipse.setFillPaint(Color.RED);
ellipse.updateEllipse(10, 10, 3, 4);
}
@Test
public void testSupportsOpaqueRegion() {
assertTrue(NGNodeShim.supportsOpaqueRegions(ellipse));
}
@Test
public void hasOpaqueRegionIfRadiusIsGreaterThanZero() {
assertTrue(NGNodeShim.hasOpaqueRegion(ellipse));
ellipse.updateEllipse(10, 10, 0, 10);
assertFalse(NGNodeShim.hasOpaqueRegion(ellipse));
ellipse.updateEllipse(10, 10, 10, 0);
assertFalse(NGNodeShim.hasOpaqueRegion(ellipse));
ellipse.updateEllipse(10, 10, .0001f, .0001f);
assertTrue(NGNodeShim.hasOpaqueRegion(ellipse));
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
for (int i=0; i<radiusValues.length; i++) {
ellipse.updateEllipse(10, 10, radiusValues[i], radiusValues[radiusValues.length-i-1]);
or = NGNodeShim.computeOpaqueRegion(ellipse, or);
assertNotNull(or);
assertTrue(ellipse.getShape().contains(or.getMinX(), or.getMinY(), or.getWidth(), or.getHeight()));
}
}
@Test
public void testComputeOpaqueRegion() {
RectBounds or = NGNodeShim.computeOpaqueRegion(ellipse, new RectBounds());
float rx = 3;
float ry = 4;
float width = 2*rx / (float) Math.sqrt(2);
float halfWidth = width / 2f;
float height = 2*ry / (float) Math.sqrt(2);
float halfHeight = height / 2f;
float x1 = 10 - halfWidth;
float y1 = 10 - halfHeight;
float x2 = 10 + halfWidth;
float y2 = 10 + halfHeight;
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
