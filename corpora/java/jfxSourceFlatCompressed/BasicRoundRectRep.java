package com.sun.prism.impl.shape;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.RoundRectangle2D;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.Graphics;
import static com.sun.javafx.geom.transform.BaseTransform.*;
public class BasicRoundRectRep extends BasicShapeRep {
private static final float[] TMP_ARR = new float[4];
public BasicRoundRectRep() {
}
@Override
public void fill(Graphics g, Shape shape, BaseBounds bounds) {
fillRoundRect(g, (RoundRectangle2D)shape);
}
public static void fillRoundRect(Graphics g, RoundRectangle2D r) {
if ((r.width <= 0) || (r.height <= 0)) {
return;
}
float arcw = r.arcWidth;
float arch = r.arcHeight;
if (arcw > 0 && arch > 0) {
g.fillRoundRect(r.x, r.y, r.width, r.height, arcw, arch);
} else {
if (isAARequiredForFill(g, r)) {
g.fillRect(r.x, r.y, r.width, r.height);
} else {
g.fillQuad(r.x, r.y, r.x+r.width, r.y+r.height);
}
}
}
@Override
public void draw(Graphics g, Shape shape, BaseBounds bounds) {
drawRoundRect(g, (RoundRectangle2D)shape);
}
public static void drawRoundRect(Graphics g, RoundRectangle2D r) {
float arcw = r.arcWidth;
float arch = r.arcHeight;
if (arcw > 0 && arch > 0) {
g.drawRoundRect(r.x, r.y, r.width, r.height, arcw, arch);
} else {
g.drawRect(r.x, r.y, r.width, r.height);
}
}
private static boolean notIntEnough(float f) {
return Math.abs(f - Math.round(f)) > 0.06;
}
private static boolean notOnIntGrid(float x1, float y1, float x2, float y2) {
return
notIntEnough(x1) || notIntEnough(y1) ||
notIntEnough(x2) || notIntEnough(y2);
}
protected static boolean isAARequiredForFill(Graphics g,
RoundRectangle2D rrect)
{
BaseTransform xform = g.getTransformNoClone();
long t = xform.getType();
boolean aaRequiredForSure =
(t & ~(TYPE_TRANSLATION|TYPE_QUADRANT_ROTATION|TYPE_MASK_SCALE)) != 0;
if (aaRequiredForSure) {
return true;
} else {
if (xform == null || xform.isIdentity()) {
return notOnIntGrid(rrect.x, rrect.y,
rrect.x + rrect.width,
rrect.y + rrect.height);
}
TMP_ARR[0] = rrect.x;
TMP_ARR[1] = rrect.y;
TMP_ARR[2] = rrect.x + rrect.width;
TMP_ARR[3] = rrect.y + rrect.height;
xform.transform(TMP_ARR, 0, TMP_ARR, 0, 2);
return notOnIntGrid(TMP_ARR[0], TMP_ARR[1],
TMP_ARR[2], TMP_ARR[3]);
}
}
}
