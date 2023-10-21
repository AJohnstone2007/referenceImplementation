package com.sun.javafx.sg.prism;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.BasicStroke;
import com.sun.prism.Graphics;
import com.sun.prism.PrinterGraphics;
import com.sun.prism.RTTexture;
import com.sun.prism.Texture;
import com.sun.prism.impl.PrismSettings;
import com.sun.prism.paint.Paint;
import com.sun.prism.shape.ShapeRep;
import static com.sun.prism.shape.ShapeRep.InvalidationType.LOCATION_AND_GEOMETRY;
public abstract class NGShape extends NGNode {
public enum Mode { EMPTY, FILL, STROKE, STROKE_FILL }
private RTTexture cached3D;
private double cachedW, cachedH;
protected Paint fillPaint;
protected Paint drawPaint;
protected BasicStroke drawStroke;
protected Mode mode = Mode.FILL;
protected ShapeRep shapeRep;
private boolean smooth;
public void setMode(Mode mode) {
if (mode != this.mode) {
this.mode = mode;
geometryChanged();
}
}
public Mode getMode() {
return mode;
}
public void setSmooth(boolean smooth) {
smooth = !PrismSettings.forceNonAntialiasedShape && smooth;
if (smooth != this.smooth) {
this.smooth = smooth;
visualsChanged();
}
}
public boolean isSmooth() {
return smooth;
}
public void setFillPaint(Object fillPaint) {
if (fillPaint != this.fillPaint ||
(this.fillPaint != null && this.fillPaint.isMutable()))
{
this.fillPaint = (Paint) fillPaint;
visualsChanged();
invalidateOpaqueRegion();
}
}
public Paint getFillPaint() {
return fillPaint;
}
public void setDrawPaint(Object drawPaint) {
if (drawPaint != this.drawPaint ||
(this.drawPaint != null && this.drawPaint.isMutable()))
{
this.drawPaint = (Paint) drawPaint;
visualsChanged();
}
}
public void setDrawStroke(BasicStroke drawStroke) {
if (this.drawStroke != drawStroke) {
this.drawStroke = drawStroke;
geometryChanged();
}
}
public void setDrawStroke(float strokeWidth,
StrokeType strokeType,
StrokeLineCap lineCap, StrokeLineJoin lineJoin,
float strokeMiterLimit,
float[] strokeDashArray, float strokeDashOffset)
{
int type;
if (strokeType == StrokeType.CENTERED) {
type = BasicStroke.TYPE_CENTERED;
} else if (strokeType == StrokeType.INSIDE) {
type = BasicStroke.TYPE_INNER;
} else {
type = BasicStroke.TYPE_OUTER;
}
int cap;
if (lineCap == StrokeLineCap.BUTT) {
cap = BasicStroke.CAP_BUTT;
} else if (lineCap == StrokeLineCap.SQUARE) {
cap = BasicStroke.CAP_SQUARE;
} else {
cap = BasicStroke.CAP_ROUND;
}
int join;
if (lineJoin == StrokeLineJoin.BEVEL) {
join = BasicStroke.JOIN_BEVEL;
} else if (lineJoin == StrokeLineJoin.MITER) {
join = BasicStroke.JOIN_MITER;
} else {
join = BasicStroke.JOIN_ROUND;
}
if (drawStroke == null) {
drawStroke = new BasicStroke(type, strokeWidth, cap, join, strokeMiterLimit);
} else {
drawStroke.set(type, strokeWidth, cap, join, strokeMiterLimit);
}
if (strokeDashArray.length > 0) {
drawStroke.set(strokeDashArray, strokeDashOffset);
} else {
drawStroke.set((float[])null, 0f);
}
geometryChanged();
}
public abstract Shape getShape();
protected ShapeRep createShapeRep(Graphics g) {
return g.getResourceFactory().createPathRep();
}
@Override
protected void visualsChanged() {
super.visualsChanged();
if (cached3D != null) {
cached3D.dispose();
cached3D = null;
}
}
private static double hypot(double x, double y, double z) {
return Math.sqrt(x * x + y * y + z * z);
}
static final double THRESHOLD = 1.0 / 256.0;
@Override
protected void renderContent(Graphics g) {
if (mode == Mode.EMPTY) {
return;
}
final boolean printing = g instanceof PrinterGraphics;
final BaseTransform tx = g.getTransformNoClone();
final boolean needs3D = !tx.is2D();
if (needs3D) {
final double scaleX = hypot(tx.getMxx(), tx.getMyx(), tx.getMzx());
final double scaleY = hypot(tx.getMxy(), tx.getMyy(), tx.getMzy());
final double scaledW = scaleX * contentBounds.getWidth();
final double scaledH = scaleY * contentBounds.getHeight();
if (cached3D != null) {
cached3D.lock();
if (cached3D.isSurfaceLost() ||
Math.max(Math.abs(scaledW - cachedW), Math.abs(scaledH - cachedH)) > THRESHOLD)
{
cached3D.unlock();
cached3D.dispose();
cached3D = null;
}
}
if (cached3D == null) {
final int w = (int) Math.ceil(scaledW);
final int h = (int) Math.ceil(scaledH);
cachedW = scaledW;
cachedH = scaledH;
if (w <= 0 || h <= 0) {
return;
}
cached3D = g.getResourceFactory().createRTTexture(w, h,
Texture.WrapMode.CLAMP_TO_ZERO,
false);
cached3D.setLinearFiltering(isSmooth());
cached3D.contentsUseful();
final Graphics textureGraphics = cached3D.createGraphics();
textureGraphics.scale((float) scaleX, (float) scaleY);
textureGraphics.translate(-contentBounds.getMinX(), -contentBounds.getMinY());
renderContent2D(textureGraphics, printing);
}
final int rtWidth = cached3D.getContentWidth();
final int rtHeight = cached3D.getContentHeight();
final float dx0 = contentBounds.getMinX();
final float dy0 = contentBounds.getMinY();
final float dx1 = dx0 + (float) (rtWidth / scaleX);
final float dy1 = dy0 + (float) (rtHeight / scaleY);
g.drawTexture(cached3D, dx0, dy0, dx1, dy1, 0, 0, rtWidth, rtHeight);
cached3D.unlock();
} else {
if (cached3D != null) {
cached3D.dispose();
cached3D = null;
}
renderContent2D(g, printing);
}
}
protected void renderContent2D(Graphics g, boolean printing) {
boolean saveAA = g.isAntialiasedShape();
boolean isAA = isSmooth();
if (isAA != saveAA) {
g.setAntialiasedShape(isAA);
}
ShapeRep localShapeRep = printing ? null : this.shapeRep;
if (localShapeRep == null) {
localShapeRep = createShapeRep(g);
}
Shape shape = getShape();
if (mode != Mode.STROKE) {
g.setPaint(fillPaint);
localShapeRep.fill(g, shape, contentBounds);
}
if (mode != Mode.FILL && drawStroke.getLineWidth() > 0) {
g.setPaint(drawPaint);
g.setStroke(drawStroke);
localShapeRep.draw(g, shape, contentBounds);
}
if (isAA != saveAA) {
g.setAntialiasedShape(saveAA);
}
if (!printing) {
this.shapeRep = localShapeRep;
}
}
@Override
protected boolean hasOverlappingContents() {
return mode == Mode.STROKE_FILL;
}
protected Shape getStrokeShape() {
return drawStroke.createStrokedShape(getShape());
}
@Override
protected void geometryChanged() {
super.geometryChanged();
if (shapeRep != null) {
shapeRep.invalidate(LOCATION_AND_GEOMETRY);
}
if (cached3D != null) {
cached3D.dispose();
cached3D = null;
}
}
@Override
protected boolean hasOpaqueRegion() {
final Mode mode = getMode();
final Paint fillPaint = getFillPaint();
return super.hasOpaqueRegion() &&
(mode == Mode.FILL || mode == Mode.STROKE_FILL) &&
(fillPaint != null && fillPaint.isOpaque());
}
}
