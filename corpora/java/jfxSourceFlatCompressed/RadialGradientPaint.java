package com.sun.prism.j2d.paint;
import java.awt.Color;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
public final class RadialGradientPaint extends MultipleGradientPaint {
private final Point2D focus;
private final Point2D center;
private final float radius;
public RadialGradientPaint(float cx, float cy, float radius,
float[] fractions, Color[] colors)
{
this(cx, cy,
radius,
cx, cy,
fractions,
colors,
CycleMethod.NO_CYCLE);
}
public RadialGradientPaint(Point2D center, float radius,
float[] fractions, Color[] colors)
{
this(center,
radius,
center,
fractions,
colors,
CycleMethod.NO_CYCLE);
}
public RadialGradientPaint(float cx, float cy, float radius,
float[] fractions, Color[] colors,
CycleMethod cycleMethod)
{
this(cx, cy,
radius,
cx, cy,
fractions,
colors,
cycleMethod);
}
public RadialGradientPaint(Point2D center, float radius,
float[] fractions, Color[] colors,
CycleMethod cycleMethod)
{
this(center,
radius,
center,
fractions,
colors,
cycleMethod);
}
public RadialGradientPaint(float cx, float cy, float radius,
float fx, float fy,
float[] fractions, Color[] colors,
CycleMethod cycleMethod)
{
this(new Point2D.Float(cx, cy),
radius,
new Point2D.Float(fx, fy),
fractions,
colors,
cycleMethod);
}
public RadialGradientPaint(Point2D center, float radius,
Point2D focus,
float[] fractions, Color[] colors,
CycleMethod cycleMethod)
{
this(center,
radius,
focus,
fractions,
colors,
cycleMethod,
ColorSpaceType.SRGB,
new AffineTransform());
}
public RadialGradientPaint(Point2D center,
float radius,
Point2D focus,
float[] fractions, Color[] colors,
CycleMethod cycleMethod,
ColorSpaceType colorSpace,
AffineTransform gradientTransform)
{
super(fractions, colors, cycleMethod, colorSpace, gradientTransform);
if (center == null) {
throw new NullPointerException("Center point must be non-null");
}
if (focus == null) {
throw new NullPointerException("Focus point must be non-null");
}
if (radius < 0) {
throw new IllegalArgumentException("Radius must be non-negative");
}
this.center = new Point2D.Double(center.getX(), center.getY());
this.focus = new Point2D.Double(focus.getX(), focus.getY());
this.radius = radius;
}
public RadialGradientPaint(Rectangle2D gradientBounds,
float[] fractions, Color[] colors,
CycleMethod cycleMethod)
{
this(new Point2D.Double(gradientBounds.getCenterX(),
gradientBounds.getCenterY()),
1.0f,
new Point2D.Double(gradientBounds.getCenterX(),
gradientBounds.getCenterY()),
fractions,
colors,
cycleMethod,
ColorSpaceType.SRGB,
createGradientTransform(gradientBounds));
if (gradientBounds.isEmpty()) {
throw new IllegalArgumentException("Gradient bounds must be " +
"non-empty");
}
}
private static AffineTransform createGradientTransform(Rectangle2D r) {
double cx = r.getCenterX();
double cy = r.getCenterY();
AffineTransform xform = AffineTransform.getTranslateInstance(cx, cy);
xform.scale(r.getWidth()/2, r.getHeight()/2);
xform.translate(-cx, -cy);
return xform;
}
public PaintContext createContext(ColorModel cm,
Rectangle deviceBounds,
Rectangle2D userBounds,
AffineTransform transform,
RenderingHints hints)
{
transform = new AffineTransform(transform);
transform.concatenate(gradientTransform);
return new RadialGradientPaintContext(this, cm,
deviceBounds, userBounds,
transform, hints,
(float)center.getX(),
(float)center.getY(),
radius,
(float)focus.getX(),
(float)focus.getY(),
fractions, colors,
cycleMethod, colorSpace);
}
public Point2D getCenterPoint() {
return new Point2D.Double(center.getX(), center.getY());
}
public Point2D getFocusPoint() {
return new Point2D.Double(focus.getX(), focus.getY());
}
public float getRadius() {
return radius;
}
}
