package com.sun.javafx.sg.prism;
import com.sun.javafx.geom.Ellipse2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Shape;
import com.sun.prism.Graphics;
import com.sun.prism.shape.ShapeRep;
public class NGCircle extends NGShape {
static final float HALF_SQRT_HALF = .353f;
private Ellipse2D ellipse = new Ellipse2D();
private float cx, cy;
public void updateCircle(float cx, float cy, float r) {
ellipse.x = cx - r;
ellipse.y = cy - r;
ellipse.width = r * 2f;
ellipse.height = ellipse.width;
this.cx = cx;
this.cy = cy;
geometryChanged();
}
@Override
public Shape getShape() {
return ellipse;
}
@Override protected boolean supportsOpaqueRegions() { return true; }
@Override
protected boolean hasOpaqueRegion() {
return super.hasOpaqueRegion() && ellipse.width > 0;
}
@Override
protected RectBounds computeOpaqueRegion(RectBounds opaqueRegion) {
final float halfSquareLength = ellipse.width * HALF_SQRT_HALF;
return (RectBounds) opaqueRegion.deriveWithNewBounds(
cx - halfSquareLength,
cy - halfSquareLength, 0,
cx + halfSquareLength,
cy + halfSquareLength, 0);
}
@Override
protected ShapeRep createShapeRep(Graphics g) {
return g.getResourceFactory().createEllipseRep();
}
}
