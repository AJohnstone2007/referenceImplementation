package test.com.sun.javafx.sg.prism;
import java.util.Arrays;
import com.sun.javafx.geom.Ellipse2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGNodeShim;
import com.sun.javafx.sg.prism.NGShape;
import com.sun.prism.paint.Color;
import com.sun.prism.paint.LinearGradient;
import com.sun.prism.paint.Stop;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
public class NGShapeTest extends NGTestBase {
private NGShape shape;
@Before
public void setup() {
shape = new NGShape() {
@Override
public Shape getShape() {
return new Ellipse2D(10, 10, 10, 10);
}
@Override
protected boolean supportsOpaqueRegions() {
return true;
}
@Override
protected RectBounds computeOpaqueRegion(RectBounds opaqueRegion) {
opaqueRegion.setBounds(0, 0, 20, 20);
return opaqueRegion;
}
};
shape.setDrawPaint(Color.WHITE);
shape.setFillPaint(Color.BLACK);
}
@Test
public void hasOpaqueRegionReturnsFalseIfModeIsStroke() {
shape.setMode(NGShape.Mode.STROKE);
assertFalse(NGNodeShim.hasOpaqueRegion(shape));
}
@Test
public void hasOpaqueRegionReturnsFalseIfModeIsEmpty() {
shape.setMode(NGShape.Mode.EMPTY);
assertFalse(NGNodeShim.hasOpaqueRegion(shape));
}
@Test
public void hasOpaqueRegionReturnsFalseIfFillPaintIsNull() {
shape.setFillPaint(null);
assertFalse(NGNodeShim.hasOpaqueRegion(shape));
}
@Test
public void hasOpaqueRegionReturnsFalseIfFillPaintIsNotOpaque() {
shape.setFillPaint(new LinearGradient(0, 0, 1, 1, BaseTransform.IDENTITY_TRANSFORM, true, 0, Arrays.asList(
new Stop(Color.BLACK, 0), new Stop(Color.TRANSPARENT, 1))));
assertFalse(NGNodeShim.hasOpaqueRegion(shape));
}
@Test
public void hasOpaqueRegionReturnsTrueIfModeIsSTROKE_FILE() {
shape.setMode(NGShape.Mode.STROKE_FILL);
assertTrue(NGNodeShim.hasOpaqueRegion(shape));
}
@Test
public void hasOpaqueRegionReturnsTrueIfModeIsFILL() {
assertTrue(NGNodeShim.hasOpaqueRegion(shape));
}
@Test
public void getOpaqueRegionChangesWhenFillChanged() {
RectBounds or = shape.getOpaqueRegion();
assertNotNull(or);
shape.setFillPaint(null);
assertNull(shape.getOpaqueRegion());
shape.setFillPaint(Color.BLACK);
assertNotNull(shape.getOpaqueRegion());
assertEquals(or, shape.getOpaqueRegion());
}
@Test
public void getOpaqueRegionChangesWhenModeChanged() {
RectBounds or = shape.getOpaqueRegion();
assertNotNull(or);
shape.setMode(NGShape.Mode.EMPTY);
assertNull(shape.getOpaqueRegion());
shape.setMode(NGShape.Mode.FILL);
assertNotNull(shape.getOpaqueRegion());
assertEquals(or, shape.getOpaqueRegion());
shape.setMode(NGShape.Mode.STROKE);
assertNull(shape.getOpaqueRegion());
shape.setMode(NGShape.Mode.STROKE_FILL);
assertNotNull(shape.getOpaqueRegion());
assertEquals(or, shape.getOpaqueRegion());
}
}
