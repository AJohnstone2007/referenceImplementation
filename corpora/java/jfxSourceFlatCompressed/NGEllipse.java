package com.sun.javafx.sg.prism;
import com.sun.javafx.geom.Ellipse2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Shape;
import com.sun.prism.Graphics;
import com.sun.prism.shape.ShapeRep;
public class NGEllipse extends NGShape {
private Ellipse2D ellipse = new Ellipse2D();
private float cx, cy;
public void updateEllipse(float cx, float cy, float rx, float ry) {
ellipse.x = cx - rx;
ellipse.width = rx * 2f;
ellipse.y = cy - ry;
ellipse.height = ry * 2f;
this.cx = cx;
this.cy = cy;
geometryChanged();
}
@Override
public final Shape getShape() {
return ellipse;
}
@Override
protected ShapeRep createShapeRep(Graphics g) {
return g.getResourceFactory().createEllipseRep();
}
@Override
protected boolean supportsOpaqueRegions() { return true; }
@Override
protected boolean hasOpaqueRegion() {
return super.hasOpaqueRegion() && ellipse.width > 0 && ellipse.height > 0;
}
@Override
protected RectBounds computeOpaqueRegion(RectBounds opaqueRegion) {
final float halfWidth = ellipse.width * NGCircle.HALF_SQRT_HALF;
final float halfHeight = ellipse.height * NGCircle.HALF_SQRT_HALF;
return (RectBounds) opaqueRegion.deriveWithNewBounds(
cx - halfWidth,
cy - halfHeight, 0,
cx + halfWidth,
cy + halfHeight, 0);
}
}
