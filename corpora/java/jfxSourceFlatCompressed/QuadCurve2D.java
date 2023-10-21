package com.sun.javafx.geom;
import com.sun.javafx.geom.transform.BaseTransform;
public class QuadCurve2D extends Shape {
public float x1;
public float y1;
public float ctrlx;
public float ctrly;
public float x2;
public float y2;
public QuadCurve2D() { }
public QuadCurve2D(float x1, float y1,
float ctrlx, float ctrly,
float x2, float y2)
{
setCurve(x1, y1, ctrlx, ctrly, x2, y2);
}
public void setCurve(float x1, float y1,
float ctrlx, float ctrly,
float x2, float y2)
{
this.x1 = x1;
this.y1 = y1;
this.ctrlx = ctrlx;
this.ctrly = ctrly;
this.x2 = x2;
this.y2 = y2;
}
public RectBounds getBounds() {
float left = Math.min(Math.min(x1, x2), ctrlx);
float top = Math.min(Math.min(y1, y2), ctrly);
float right = Math.max(Math.max(x1, x2), ctrlx);
float bottom = Math.max(Math.max(y1, y2), ctrly);
return new RectBounds(left, top, right, bottom);
}
public CubicCurve2D toCubic() {
return new CubicCurve2D(x1, y1,
(x1 + 2 * ctrlx) / 3, (y1 + 2 * ctrly) / 3,
(2 * ctrlx + x2) / 3, (2 * ctrly + y2) / 3,
x2, y2);
}
public void setCurve(float[] coords, int offset) {
setCurve(coords[offset + 0], coords[offset + 1],
coords[offset + 2], coords[offset + 3],
coords[offset + 4], coords[offset + 5]);
}
public void setCurve(Point2D p1, Point2D cp, Point2D p2) {
setCurve(p1.x, p1.y, cp.x, cp.y, p2.x, p2.y);
}
public void setCurve(Point2D[] pts, int offset) {
setCurve(pts[offset + 0].x, pts[offset + 0].y,
pts[offset + 1].x, pts[offset + 1].y,
pts[offset + 2].x, pts[offset + 2].y);
}
public void setCurve(QuadCurve2D c) {
setCurve(c.x1, c.y1, c.ctrlx, c.ctrly, c.x2, c.y2);
}
public static float getFlatnessSq(float x1, float y1,
float ctrlx, float ctrly,
float x2, float y2) {
return Line2D.ptSegDistSq(x1, y1, x2, y2, ctrlx, ctrly);
}
public static float getFlatness(float x1, float y1,
float ctrlx, float ctrly,
float x2, float y2) {
return Line2D.ptSegDist(x1, y1, x2, y2, ctrlx, ctrly);
}
public static float getFlatnessSq(float coords[], int offset) {
return Line2D.ptSegDistSq(coords[offset + 0], coords[offset + 1],
coords[offset + 4], coords[offset + 5],
coords[offset + 2], coords[offset + 3]);
}
public static float getFlatness(float coords[], int offset) {
return Line2D.ptSegDist(coords[offset + 0], coords[offset + 1],
coords[offset + 4], coords[offset + 5],
coords[offset + 2], coords[offset + 3]);
}
public float getFlatnessSq() {
return Line2D.ptSegDistSq(x1, y1, x2, y2, ctrlx, ctrly);
}
public float getFlatness() {
return Line2D.ptSegDist(x1, y1, x2, y2, ctrlx, ctrly);
}
public void subdivide(QuadCurve2D left, QuadCurve2D right) {
subdivide(this, left, right);
}
public static void subdivide(QuadCurve2D src,
QuadCurve2D left,
QuadCurve2D right)
{
float x1 = src.x1;
float y1 = src.y1;
float ctrlx = src.ctrlx;
float ctrly = src.ctrly;
float x2 = src.x2;
float y2 = src.y2;
float ctrlx1 = (x1 + ctrlx) / 2f;
float ctrly1 = (y1 + ctrly) / 2f;
float ctrlx2 = (x2 + ctrlx) / 2f;
float ctrly2 = (y2 + ctrly) / 2f;
ctrlx = (ctrlx1 + ctrlx2) / 2f;
ctrly = (ctrly1 + ctrly2) / 2f;
if (left != null) {
left.setCurve(x1, y1, ctrlx1, ctrly1, ctrlx, ctrly);
}
if (right != null) {
right.setCurve(ctrlx, ctrly, ctrlx2, ctrly2, x2, y2);
}
}
public static void subdivide(float src[], int srcoff,
float left[], int leftoff,
float right[], int rightoff)
{
float x1 = src[srcoff + 0];
float y1 = src[srcoff + 1];
float ctrlx = src[srcoff + 2];
float ctrly = src[srcoff + 3];
float x2 = src[srcoff + 4];
float y2 = src[srcoff + 5];
if (left != null) {
left[leftoff + 0] = x1;
left[leftoff + 1] = y1;
}
if (right != null) {
right[rightoff + 4] = x2;
right[rightoff + 5] = y2;
}
x1 = (x1 + ctrlx) / 2f;
y1 = (y1 + ctrly) / 2f;
x2 = (x2 + ctrlx) / 2f;
y2 = (y2 + ctrly) / 2f;
ctrlx = (x1 + x2) / 2f;
ctrly = (y1 + y2) / 2f;
if (left != null) {
left[leftoff + 2] = x1;
left[leftoff + 3] = y1;
left[leftoff + 4] = ctrlx;
left[leftoff + 5] = ctrly;
}
if (right != null) {
right[rightoff + 0] = ctrlx;
right[rightoff + 1] = ctrly;
right[rightoff + 2] = x2;
right[rightoff + 3] = y2;
}
}
public static int solveQuadratic(float eqn[]) {
return solveQuadratic(eqn, eqn);
}
public static int solveQuadratic(float eqn[], float res[]) {
float a = eqn[2];
float b = eqn[1];
float c = eqn[0];
int roots = 0;
if (a == 0f) {
if (b == 0f) {
return -1;
}
res[roots++] = -c / b;
} else {
float d = b * b - 4f * a * c;
if (d < 0f) {
return 0;
}
d = (float) Math.sqrt(d);
if (b < 0f) {
d = -d;
}
float q = (b + d) / -2f;
res[roots++] = q / a;
if (q != 0f) {
res[roots++] = c / q;
}
}
return roots;
}
public boolean contains(float x, float y) {
float x1 = this.x1;
float y1 = this.y1;
float xc = this.ctrlx;
float yc = this.ctrly;
float x2 = this.x2;
float y2 = this.y2;
float kx = x1 - 2 * xc + x2;
float ky = y1 - 2 * yc + y2;
float dx = x - x1;
float dy = y - y1;
float dxl = x2 - x1;
float dyl = y2 - y1;
float t0 = (dx * ky - dy * kx) / (dxl * ky - dyl * kx);
if (t0 < 0 || t0 > 1 || t0 != t0) {
return false;
}
float xb = kx * t0 * t0 + 2 * (xc - x1) * t0 + x1;
float yb = ky * t0 * t0 + 2 * (yc - y1) * t0 + y1;
float xl = dxl * t0 + x1;
float yl = dyl * t0 + y1;
return (x >= xb && x < xl) ||
(x >= xl && x < xb) ||
(y >= yb && y < yl) ||
(y >= yl && y < yb);
}
public boolean contains(Point2D p) {
return contains(p.x, p.y);
}
private static void fillEqn(float eqn[], float val,
float c1, float cp, float c2) {
eqn[0] = c1 - val;
eqn[1] = cp + cp - c1 - c1;
eqn[2] = c1 - cp - cp + c2;
}
private static int evalQuadratic(float vals[], int num,
boolean include0,
boolean include1,
float inflect[],
float c1, float ctrl, float c2) {
int j = 0;
for (int i = 0; i < num; i++) {
float t = vals[i];
if ((include0 ? t >= 0 : t > 0) &&
(include1 ? t <= 1 : t < 1) &&
(inflect == null ||
inflect[1] + 2*inflect[2]*t != 0))
{
float u = 1 - t;
vals[j++] = c1*u*u + 2*ctrl*t*u + c2*t*t;
}
}
return j;
}
private static final int BELOW = -2;
private static final int LOWEDGE = -1;
private static final int INSIDE = 0;
private static final int HIGHEDGE = 1;
private static final int ABOVE = 2;
private static int getTag(float coord, float low, float high) {
if (coord <= low) {
return (coord < low ? BELOW : LOWEDGE);
}
if (coord >= high) {
return (coord > high ? ABOVE : HIGHEDGE);
}
return INSIDE;
}
private static boolean inwards(int pttag, int opt1tag, int opt2tag) {
switch (pttag) {
case BELOW:
case ABOVE:
default:
return false;
case LOWEDGE:
return (opt1tag >= INSIDE || opt2tag >= INSIDE);
case INSIDE:
return true;
case HIGHEDGE:
return (opt1tag <= INSIDE || opt2tag <= INSIDE);
}
}
public boolean intersects(float x, float y, float w, float h) {
if (w <= 0 || h <= 0) {
return false;
}
float x1 = this.x1;
float y1 = this.y1;
int x1tag = getTag(x1, x, x + w);
int y1tag = getTag(y1, y, y + h);
if (x1tag == INSIDE && y1tag == INSIDE) {
return true;
}
float x2 = this.x2;
float y2 = this.y2;
int x2tag = getTag(x2, x, x + w);
int y2tag = getTag(y2, y, y + h);
if (x2tag == INSIDE && y2tag == INSIDE) {
return true;
}
float ctrlx = this.ctrlx;
float ctrly = this.ctrly;
int ctrlxtag = getTag(ctrlx, x, x + w);
int ctrlytag = getTag(ctrly, y, y + h);
if (x1tag < INSIDE && x2tag < INSIDE && ctrlxtag < INSIDE) {
return false;
}
if (y1tag < INSIDE && y2tag < INSIDE && ctrlytag < INSIDE) {
return false;
}
if (x1tag > INSIDE && x2tag > INSIDE && ctrlxtag > INSIDE) {
return false;
}
if (y1tag > INSIDE && y2tag > INSIDE && ctrlytag > INSIDE) {
return false;
}
if (inwards(x1tag, x2tag, ctrlxtag) &&
inwards(y1tag, y2tag, ctrlytag))
{
return true;
}
if (inwards(x2tag, x1tag, ctrlxtag) &&
inwards(y2tag, y1tag, ctrlytag))
{
return true;
}
boolean xoverlap = (x1tag * x2tag <= 0);
boolean yoverlap = (y1tag * y2tag <= 0);
if (x1tag == INSIDE && x2tag == INSIDE && yoverlap) {
return true;
}
if (y1tag == INSIDE && y2tag == INSIDE && xoverlap) {
return true;
}
float[] eqn = new float[3];
float[] res = new float[3];
if (!yoverlap) {
fillEqn(eqn, (y1tag < INSIDE ? y : y+h), y1, ctrly, y2);
return (solveQuadratic(eqn, res) == 2 &&
evalQuadratic(res, 2, true, true, null,
x1, ctrlx, x2) == 2 &&
getTag(res[0], x, x+w) * getTag(res[1], x, x+w) <= 0);
}
if (!xoverlap) {
fillEqn(eqn, (x1tag < INSIDE ? x : x+w), x1, ctrlx, x2);
return (solveQuadratic(eqn, res) == 2 &&
evalQuadratic(res, 2, true, true, null,
y1, ctrly, y2) == 2 &&
getTag(res[0], y, y+h) * getTag(res[1], y, y+h) <= 0);
}
float dx = x2 - x1;
float dy = y2 - y1;
float k = y2 * x1 - x2 * y1;
int c1tag, c2tag;
if (y1tag == INSIDE) {
c1tag = x1tag;
} else {
c1tag = getTag((k + dx * (y1tag < INSIDE ? y : y+h)) / dy, x, x+w);
}
if (y2tag == INSIDE) {
c2tag = x2tag;
} else {
c2tag = getTag((k + dx * (y2tag < INSIDE ? y : y+h)) / dy, x, x+w);
}
if (c1tag * c2tag <= 0) {
return true;
}
c1tag = ((c1tag * x1tag <= 0) ? y1tag : y2tag);
fillEqn(eqn, (c2tag < INSIDE ? x : x+w), x1, ctrlx, x2);
int num = solveQuadratic(eqn, res);
evalQuadratic(res, num, true, true, null, y1, ctrly, y2);
c2tag = getTag(res[0], y, y+h);
return (c1tag * c2tag <= 0);
}
public boolean contains(float x, float y, float w, float h) {
if (w <= 0 || h <= 0) {
return false;
}
return (contains(x, y) &&
contains(x + w, y) &&
contains(x + w, y + h) &&
contains(x, y + h));
}
public PathIterator getPathIterator(BaseTransform tx) {
return new QuadIterator(this, tx);
}
public PathIterator getPathIterator(BaseTransform tx, float flatness) {
return new FlatteningPathIterator(getPathIterator(tx), flatness);
}
@Override
public QuadCurve2D copy() {
return new QuadCurve2D(x1, y1, ctrlx, ctrly, x2, y2);
}
@Override
public int hashCode() {
int bits = java.lang.Float.floatToIntBits(x1);
bits += java.lang.Float.floatToIntBits(y1) * 37;
bits += java.lang.Float.floatToIntBits(x2) * 43;
bits += java.lang.Float.floatToIntBits(y2) * 47;
bits += java.lang.Float.floatToIntBits(ctrlx) * 53;
bits += java.lang.Float.floatToIntBits(ctrly) * 59;
return bits;
}
@Override
public boolean equals(Object obj) {
if (obj == this) {
return true;
}
if (obj instanceof QuadCurve2D) {
QuadCurve2D curve = (QuadCurve2D) obj;
return ((x1 == curve.x1) && (y1 == curve.y1) &&
(x2 == curve.x2) && (y2 == curve.y2) &&
(ctrlx == curve.ctrlx) && (ctrly == curve.ctrly));
}
return false;
}
}
