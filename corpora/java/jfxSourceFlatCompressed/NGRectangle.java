package com.sun.javafx.sg.prism;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.RectangularShape;
import com.sun.javafx.geom.RoundRectangle2D;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.BasicStroke;
import com.sun.prism.Graphics;
import com.sun.prism.RectShadowGraphics;
import com.sun.prism.paint.Color;
import com.sun.prism.shape.ShapeRep;
import com.sun.scenario.effect.Effect;
import static com.sun.javafx.geom.transform.BaseTransform.TYPE_MASK_SCALE;
import static com.sun.javafx.geom.transform.BaseTransform.TYPE_QUADRANT_ROTATION;
import static com.sun.javafx.geom.transform.BaseTransform.TYPE_TRANSLATION;
public class NGRectangle extends NGShape {
private RoundRectangle2D rrect = new RoundRectangle2D();
public void updateRectangle(float x, float y, float width, float height,
float arcWidth, float arcHeight) {
rrect.x = x;
rrect.y = y;
rrect.width = width;
rrect.height = height;
rrect.arcWidth = arcWidth;
rrect.arcHeight = arcHeight;
geometryChanged();
}
@Override
protected boolean supportsOpaqueRegions() { return true; }
@Override
protected boolean hasOpaqueRegion() {
return super.hasOpaqueRegion() && rrect.width > 1 && rrect.height > 1;
}
static final float HALF_MINUS_HALF_SQRT_HALF = 0.5f - NGCircle.HALF_SQRT_HALF;
@Override
protected RectBounds computeOpaqueRegion(RectBounds opaqueRegion) {
final float x = rrect.x;
final float y = rrect.y;
final float w = rrect.width;
final float h = rrect.height;
final float aw = rrect.arcWidth;
final float ah = rrect.arcHeight;
if (aw <= 0 || ah <= 0) {
return (RectBounds) opaqueRegion.deriveWithNewBounds(x, y, 0, x + w, y + h, 0);
} else {
final float arcInsetWidth = Math.min(w, aw) * HALF_MINUS_HALF_SQRT_HALF;
final float arcInsetHeight = Math.min(h, ah) * HALF_MINUS_HALF_SQRT_HALF;
return (RectBounds) opaqueRegion.deriveWithNewBounds(
x + arcInsetWidth, y + arcInsetHeight, 0,
x + w - arcInsetWidth, y + h - arcInsetHeight, 0);
}
}
boolean isRounded() {
return rrect.arcWidth > 0f && rrect.arcHeight > 0f;
}
@Override
protected void renderEffect(Graphics g) {
if (!(g instanceof RectShadowGraphics) || !renderEffectDirectly(g)) {
super.renderEffect(g);
}
}
private boolean renderEffectDirectly(Graphics g) {
if (mode != Mode.FILL || isRounded()) {
return false;
}
float alpha = g.getExtraAlpha();
if (fillPaint instanceof Color) {
alpha *= ((Color) fillPaint).getAlpha();
} else {
return false;
}
Effect effect = getEffect();
if (EffectUtil.renderEffectForRectangularNode(this, g, effect,
alpha, true ,
rrect.x, rrect.y,
rrect.width, rrect.height))
{
return true;
}
return false;
}
@Override
public final Shape getShape() {
return rrect;
}
@Override
protected ShapeRep createShapeRep(Graphics g) {
return g.getResourceFactory().createRoundRectRep();
}
private static final double SQRT_2 = Math.sqrt(2.0);
private static boolean hasRightAngleMiterAndNoDashes(BasicStroke bs) {
return (bs.getLineJoin() == BasicStroke.JOIN_MITER &&
bs.getMiterLimit() >= SQRT_2 &&
bs.getDashArray() == null);
}
static boolean rectContains(float x, float y,
NGShape node,
RectangularShape r)
{
double rw = r.getWidth();
double rh = r.getHeight();
if (rw < 0 || rh < 0) {
return false;
}
Mode mode = node.mode;
if (mode == Mode.EMPTY) {
return false;
}
double rx = r.getX();
double ry = r.getY();
if (mode == Mode.FILL) {
return (x >= rx && y >= ry && x < rx+rw && y < ry+rh);
}
float outerpad = -1.0f;
float innerpad = -1.0f;
boolean checkstroke = false;
BasicStroke drawstroke = node.drawStroke;
int type = drawstroke.getType();
if (type == BasicStroke.TYPE_INNER) {
if (mode == Mode.STROKE_FILL) {
outerpad = 0.0f;
} else {
if (drawstroke.getDashArray() == null) {
outerpad = 0.0f;
innerpad = drawstroke.getLineWidth();
} else {
checkstroke = true;
}
}
} else if (type == BasicStroke.TYPE_OUTER) {
if (hasRightAngleMiterAndNoDashes(drawstroke)) {
outerpad = drawstroke.getLineWidth();
if (mode == Mode.STROKE) {
innerpad = 0.0f;
}
} else {
if (mode == Mode.STROKE_FILL) {
outerpad = 0.0f;
}
checkstroke = true;
}
} else if (type == BasicStroke.TYPE_CENTERED) {
if (hasRightAngleMiterAndNoDashes(drawstroke)) {
outerpad = drawstroke.getLineWidth() / 2.0f;
if (mode == Mode.STROKE) {
innerpad = outerpad;
}
} else {
if (mode == Mode.STROKE_FILL) {
outerpad = 0.0f;
}
checkstroke = true;
}
} else {
if (mode == Mode.STROKE_FILL) {
outerpad = 0.0f;
}
checkstroke = true;
}
if (outerpad >= 0.0f) {
if (x >= rx -outerpad && y >= ry -outerpad &&
x < rx+rw+outerpad && y < ry+rh+outerpad) {
if (innerpad >= 0.0f &&
innerpad < rw/2.0f && innerpad < rh/2.0f &&
x >= rx +innerpad && y >= ry +innerpad &&
x < rx+rw-innerpad && y < ry+rh-innerpad)
{
return false;
}
return true;
}
}
if (checkstroke) {
return node.getStrokeShape().contains(x, y);
}
return false;
}
@Override protected final boolean isRectClip(BaseTransform xform, boolean permitRoundedRectangle) {
if (mode != NGShape.Mode.FILL || getClipNode() != null || (getEffect() != null && getEffect().reducesOpaquePixels()) ||
getOpacity() < 1f || (!permitRoundedRectangle && isRounded()) || !fillPaint.isOpaque())
{
return false;
}
BaseTransform nodeXform = getTransform();
if (!nodeXform.isIdentity()) {
if (!xform.isIdentity()) {
TEMP_TRANSFORM.setTransform(xform);
TEMP_TRANSFORM.concatenate(nodeXform);
xform = TEMP_TRANSFORM;
} else {
xform = nodeXform;
}
}
long t = xform.getType();
return
(t & ~(TYPE_TRANSLATION|TYPE_QUADRANT_ROTATION|TYPE_MASK_SCALE)) == 0;
}
}
