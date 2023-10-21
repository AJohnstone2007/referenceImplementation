package test.com.sun.javafx.sg.prism;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.sg.prism.NGNodeShim;
import com.sun.javafx.sg.prism.NGRectangle;
import com.sun.prism.paint.Color;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
public class NGRectangleTest extends NGTestBase {
NGRectangleMock r;
@Before
public void setup() {
r = new NGRectangleMock();
r.updateRectangle(0, 0, 100, 100, 0, 0);
r.setFillPaint(Color.BLACK);
}
@Test
public void testSupportsOpaqueRegions() {
assertTrue(NGNodeShim.supportsOpaqueRegions(r));
}
@Test
public void testHasOpaqueRegion() {
assertTrue(r.hasOpaqueRegion());
}
@Test
public void testHasOpaqueRegion_NoFill() {
r.setFillPaint(null);
assertFalse(r.hasOpaqueRegion());
}
@Test
public void testHasOpaqueRegion_NoWidth() {
r.updateRectangle(0, 0, 0, 100, 0, 0);
assertFalse(r.hasOpaqueRegion());
}
@Test
public void testHasOpaqueRegion_NoHeight() {
r.updateRectangle(0, 0, 100, 0, 0, 0);
assertFalse(r.hasOpaqueRegion());
}
@Test
public void testHasOpaqueRegion_ArcWidthSoBig() {
r.updateRectangle(0, 0, 100, 100, 100, 100);
assertTrue(r.hasOpaqueRegion());
}
@Test
public void computeOpaqueRegion_NoArc() {
assertEquals(new RectBounds(0, 0, 100, 100), r.computeOpaqueRegion(new RectBounds()));
}
class NGRectangleMock extends NGRectangle {
boolean opaqueRegionRecomputed = false;
@Override
public boolean hasOpaqueRegion() {
opaqueRegionRecomputed = true;
return super.hasOpaqueRegion();
}
@Override
protected RectBounds computeOpaqueRegion(RectBounds opaqueRegion) {
opaqueRegionRecomputed = true;
return super.computeOpaqueRegion(opaqueRegion);
}
}
}
