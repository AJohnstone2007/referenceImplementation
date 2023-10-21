package com.sun.javafx.geom;
import java.util.Arrays;
import com.sun.javafx.geom.transform.BaseTransform;
public class CubicCurve2D extends Shape {
public float x1;
public float y1;
public float ctrlx1;
public float ctrly1;
public float ctrlx2;
public float ctrly2;
public float x2;
public float y2;
public CubicCurve2D() { }
public CubicCurve2D(float x1, float y1,
float ctrlx1, float ctrly1,
float ctrlx2, float ctrly2,
float x2, float y2)
{
setCurve(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
}
public void setCurve(float x1, float y1,
float ctrlx1, float ctrly1,
float ctrlx2, float ctrly2,
float x2, float y2)
{
this.x1 = x1;
this.y1 = y1;
this.ctrlx1 = ctrlx1;
this.ctrly1 = ctrly1;
this.ctrlx2 = ctrlx2;
this.ctrly2 = ctrly2;
this.x2 = x2;
this.y2 = y2;
}
public RectBounds getBounds() {
float left = Math.min(Math.min(x1, x2),
Math.min(ctrlx1, ctrlx2));
float top = Math.min(Math.min(y1, y2),
Math.min(ctrly1, ctrly2));
float right = Math.max(Math.max(x1, x2),
Math.max(ctrlx1, ctrlx2));
float bottom = Math.max(Math.max(y1, y2),
Math.max(ctrly1, ctrly2));
return new RectBounds(left, top, right, bottom);
}
public Point2D eval(float t) {
Point2D result = new Point2D();
eval(t, result);
return result;
}
public void eval(float td, Point2D result) {
result.setLocation(calcX(td), calcY(td));
}
public Point2D evalDt(float t) {
Point2D result = new Point2D();
evalDt(t, result);
return result;
}
public void evalDt(float td, Point2D result) {
float t = td;
float u = 1 - t;
float x = 3*((ctrlx1-x1)*u*u +
2*(ctrlx2-ctrlx1)*u*t +
(x2-ctrlx2)*t*t);
float y = 3*((ctrly1-y1)*u*u +
2*(ctrly2-ctrly1)*u*t +
(y2-ctrly2)*t*t);
result.setLocation(x, y);
}
public void setCurve(float[] coords, int offset) {
setCurve(coords[offset + 0], coords[offset + 1],
coords[offset + 2], coords[offset + 3],
coords[offset + 4], coords[offset + 5],
coords[offset + 6], coords[offset + 7]);
}
public void setCurve(Point2D p1, Point2D cp1, Point2D cp2, Point2D p2) {
setCurve(p1.x, p1.y, cp1.x, cp1.y, cp2.x, cp2.y, p2.x, p2.y);
}
public void setCurve(Point2D[] pts, int offset) {
setCurve(pts[offset + 0].x, pts[offset + 0].y,
pts[offset + 1].x, pts[offset + 1].y,
pts[offset + 2].x, pts[offset + 2].y,
pts[offset + 3].x, pts[offset + 3].y);
}
public void setCurve(CubicCurve2D c) {
setCurve(c.x1, c.y1, c.ctrlx1, c.ctrly1, c.ctrlx2, c.ctrly2, c.x2, c.y2);
}
public static float getFlatnessSq(float x1, float y1,
float ctrlx1, float ctrly1,
float ctrlx2, float ctrly2,
float x2, float y2) {
return Math.max(Line2D.ptSegDistSq(x1, y1, x2, y2, ctrlx1, ctrly1),
Line2D.ptSegDistSq(x1, y1, x2, y2, ctrlx2, ctrly2));
}
public static float getFlatness(float x1, float y1,
float ctrlx1, float ctrly1,
float ctrlx2, float ctrly2,
float x2, float y2) {
return (float) Math.sqrt(getFlatnessSq(x1, y1, ctrlx1, ctrly1,
ctrlx2, ctrly2, x2, y2));
}
public static float getFlatnessSq(float coords[], int offset) {
return getFlatnessSq(coords[offset + 0], coords[offset + 1],
coords[offset + 2], coords[offset + 3],
coords[offset + 4], coords[offset + 5],
coords[offset + 6], coords[offset + 7]);
}
public static float getFlatness(float coords[], int offset) {
return getFlatness(coords[offset + 0], coords[offset + 1],
coords[offset + 2], coords[offset + 3],
coords[offset + 4], coords[offset + 5],
coords[offset + 6], coords[offset + 7]);
}
public float getFlatnessSq() {
return getFlatnessSq(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
}
public float getFlatness() {
return getFlatness(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
}
public void subdivide(float t, CubicCurve2D left, CubicCurve2D right) {
if ((left == null) && (right == null)) return;
float npx = calcX(t);
float npy = calcY(t);
float x1 = this.x1;
float y1 = this.y1;
float c1x = this.ctrlx1;
float c1y = this.ctrly1;
float c2x = this.ctrlx2;
float c2y = this.ctrly2;
float x2 = this.x2;
float y2 = this.y2;
float u = 1-t;
float hx = u*c1x+t*c2x;
float hy = u*c1y+t*c2y;
if (left != null) {
float lx1 = x1;
float ly1 = y1;
float lc1x = u*x1+t*c1x;
float lc1y = u*y1+t*c1y;
float lc2x = u*lc1x+t*hx;
float lc2y = u*lc1y+t*hy;
float lx2 = npx;
float ly2 = npy;
left.setCurve(lx1, ly1,
lc1x, lc1y,
lc2x, lc2y,
lx2, ly2);
}
if (right != null) {
float rx1 = npx;
float ry1 = npy;
float rc2x = u*c2x+t*x2;
float rc2y = u*c2y+t*y2;
float rc1x = u*hx+t*rc2x;
float rc1y = u*hy+t*rc2y;
float rx2 = x2;
float ry2 = y2;
right.setCurve(rx1, ry1,
rc1x, rc1y,
rc2x, rc2y,
rx2, ry2);
}
}
public void subdivide(CubicCurve2D left, CubicCurve2D right) {
subdivide(this, left, right);
}
public static void subdivide(CubicCurve2D src,
CubicCurve2D left,
CubicCurve2D right) {
float x1 = src.x1;
float y1 = src.y1;
float ctrlx1 = src.ctrlx1;
float ctrly1 = src.ctrly1;
float ctrlx2 = src.ctrlx2;
float ctrly2 = src.ctrly2;
float x2 = src.x2;
float y2 = src.y2;
float centerx = (ctrlx1 + ctrlx2) / 2f;
float centery = (ctrly1 + ctrly2) / 2f;
ctrlx1 = (x1 + ctrlx1) / 2f;
ctrly1 = (y1 + ctrly1) / 2f;
ctrlx2 = (x2 + ctrlx2) / 2f;
ctrly2 = (y2 + ctrly2) / 2f;
float ctrlx12 = (ctrlx1 + centerx) / 2f;
float ctrly12 = (ctrly1 + centery) / 2f;
float ctrlx21 = (ctrlx2 + centerx) / 2f;
float ctrly21 = (ctrly2 + centery) / 2f;
centerx = (ctrlx12 + ctrlx21) / 2f;
centery = (ctrly12 + ctrly21) / 2f;
if (left != null) {
left.setCurve(x1, y1, ctrlx1, ctrly1,
ctrlx12, ctrly12, centerx, centery);
}
if (right != null) {
right.setCurve(centerx, centery, ctrlx21, ctrly21,
ctrlx2, ctrly2, x2, y2);
}
}
public static void subdivide(float src[], int srcoff,
float left[], int leftoff,
float right[], int rightoff) {
float x1 = src[srcoff + 0];
float y1 = src[srcoff + 1];
float ctrlx1 = src[srcoff + 2];
float ctrly1 = src[srcoff + 3];
float ctrlx2 = src[srcoff + 4];
float ctrly2 = src[srcoff + 5];
float x2 = src[srcoff + 6];
float y2 = src[srcoff + 7];
if (left != null) {
left[leftoff + 0] = x1;
left[leftoff + 1] = y1;
}
if (right != null) {
right[rightoff + 6] = x2;
right[rightoff + 7] = y2;
}
x1 = (x1 + ctrlx1) / 2f;
y1 = (y1 + ctrly1) / 2f;
x2 = (x2 + ctrlx2) / 2f;
y2 = (y2 + ctrly2) / 2f;
float centerx = (ctrlx1 + ctrlx2) / 2f;
float centery = (ctrly1 + ctrly2) / 2f;
ctrlx1 = (x1 + centerx) / 2f;
ctrly1 = (y1 + centery) / 2f;
ctrlx2 = (x2 + centerx) / 2f;
ctrly2 = (y2 + centery) / 2f;
centerx = (ctrlx1 + ctrlx2) / 2f;
centery = (ctrly1 + ctrly2) / 2f;
if (left != null) {
left[leftoff + 2] = x1;
left[leftoff + 3] = y1;
left[leftoff + 4] = ctrlx1;
left[leftoff + 5] = ctrly1;
left[leftoff + 6] = centerx;
left[leftoff + 7] = centery;
}
if (right != null) {
right[rightoff + 0] = centerx;
right[rightoff + 1] = centery;
right[rightoff + 2] = ctrlx2;
right[rightoff + 3] = ctrly2;
right[rightoff + 4] = x2;
right[rightoff + 5] = y2;
}
}
public static int solveCubic(float eqn[]) {
return solveCubic(eqn, eqn);
}
public static int solveCubic(float eqn[], float res[]) {
float d = eqn[3];
if (d == 0f) {
return QuadCurve2D.solveQuadratic(eqn, res);
}
float a = eqn[2] / d;
float b = eqn[1] / d;
float c = eqn[0] / d;
int roots = 0;
float Q = (a * a - 3f * b) / 9f;
float R = (2f * a * a * a - 9f * a * b + 27f * c) / 54f;
float R2 = R * R;
float Q3 = Q * Q * Q;
a = a / 3f;
if (R2 < Q3) {
float theta = (float) Math.acos(R / Math.sqrt(Q3));
Q = (float) (-2f * Math.sqrt(Q));
if (res == eqn) {
eqn = new float[4];
System.arraycopy(res, 0, eqn, 0, 4);
}
res[roots++] = (float) (Q * Math.cos(theta / 3f) - a);
res[roots++] = (float) (Q * Math.cos((theta + Math.PI * 2f)/ 3f) - a);
res[roots++] = (float) (Q * Math.cos((theta - Math.PI * 2f)/ 3f) - a);
fixRoots(res, eqn);
} else {
boolean neg = (R < 0f);
float S = (float) Math.sqrt(R2 - Q3);
if (neg) {
R = -R;
}
float A = (float) Math.pow(R + S, 1f / 3f);
if (!neg) {
A = -A;
}
float B = (A == 0f) ? 0f : (Q / A);
res[roots++] = (A + B) - a;
}
return roots;
}
private static void fixRoots(float res[], float eqn[]) {
final float EPSILON = (float) 1E-5;
for (int i = 0; i < 3; i++) {
float t = res[i];
if (Math.abs(t) < EPSILON) {
res[i] = findZero(t, 0, eqn);
} else if (Math.abs(t - 1) < EPSILON) {
res[i] = findZero(t, 1, eqn);
}
}
}
private static float solveEqn(float eqn[], int order, float t) {
float v = eqn[order];
while (--order >= 0) {
v = v * t + eqn[order];
}
return v;
}
private static float findZero(float t, float target, float eqn[]) {
float slopeqn[] = {eqn[1], 2*eqn[2], 3*eqn[3]};
float slope;
float origdelta = 0f;
float origt = t;
while (true) {
slope = solveEqn(slopeqn, 2, t);
if (slope == 0f) {
return t;
}
float y = solveEqn(eqn, 3, t);
if (y == 0f) {
return t;
}
float delta = - (y / slope);
if (origdelta == 0f) {
origdelta = delta;
}
if (t < target) {
if (delta < 0f) return t;
} else if (t > target) {
if (delta > 0f) return t;
} else {
return (delta > 0f
? (target + java.lang.Float.MIN_VALUE)
: (target - java.lang.Float.MIN_VALUE));
}
float newt = t + delta;
if (t == newt) {
return t;
}
if (delta * origdelta < 0) {
int tag = (origt < t
? getTag(target, origt, t)
: getTag(target, t, origt));
if (tag != INSIDE) {
return (origt + t) / 2;
}
t = target;
} else {
t = newt;
}
}
}
public boolean contains(float x, float y) {
if (!(x * 0f + y * 0f == 0f)) {
return false;
}
int crossings =
(Shape.pointCrossingsForLine(x, y, x1, y1, x2, y2) +
Shape.pointCrossingsForCubic(x, y,
x1, y1,
ctrlx1, ctrly1,
ctrlx2, ctrly2,
x2, y2, 0));
return ((crossings & 1) == 1);
}
public boolean contains(Point2D p) {
return contains(p.x, p.y);
}
private static void fillEqn(float eqn[], float val,
float c1, float cp1, float cp2, float c2) {
eqn[0] = c1 - val;
eqn[1] = (cp1 - c1) * 3f;
eqn[2] = (cp2 - cp1 - cp1 + c1) * 3f;
eqn[3] = c2 + (cp1 - cp2) * 3f - c1;
}
private static int evalCubic(float vals[], int num,
boolean include0,
boolean include1,
float inflect[],
float c1, float cp1,
float cp2, float c2) {
int j = 0;
for (int i = 0; i < num; i++) {
float t = vals[i];
if ((include0 ? t >= 0 : t > 0) &&
(include1 ? t <= 1 : t < 1) &&
(inflect == null ||
inflect[1] + (2*inflect[2] + 3*inflect[3]*t)*t != 0))
{
float u = 1 - t;
vals[j++] = c1*u*u*u + 3*cp1*t*u*u + 3*cp2*t*t*u + c2*t*t*t;
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
float ctrlx1 = this.ctrlx1;
float ctrly1 = this.ctrly1;
float ctrlx2 = this.ctrlx2;
float ctrly2 = this.ctrly2;
int ctrlx1tag = getTag(ctrlx1, x, x + w);
int ctrly1tag = getTag(ctrly1, y, y + h);
int ctrlx2tag = getTag(ctrlx2, x, x + w);
int ctrly2tag = getTag(ctrly2, y, y + h);
if (x1tag < INSIDE && x2tag < INSIDE &&
ctrlx1tag < INSIDE && ctrlx2tag < INSIDE)
{
return false;
}
if (y1tag < INSIDE && y2tag < INSIDE &&
ctrly1tag < INSIDE && ctrly2tag < INSIDE)
{
return false;
}
if (x1tag > INSIDE && x2tag > INSIDE &&
ctrlx1tag > INSIDE && ctrlx2tag > INSIDE)
{
return false;
}
if (y1tag > INSIDE && y2tag > INSIDE &&
ctrly1tag > INSIDE && ctrly2tag > INSIDE)
{
return false;
}
if (inwards(x1tag, x2tag, ctrlx1tag) &&
inwards(y1tag, y2tag, ctrly1tag))
{
return true;
}
if (inwards(x2tag, x1tag, ctrlx2tag) &&
inwards(y2tag, y1tag, ctrly2tag))
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
float[] eqn = new float[4];
float[] res = new float[4];
if (!yoverlap) {
fillEqn(eqn, (y1tag < INSIDE ? y : y+h), y1, ctrly1, ctrly2, y2);
int num = solveCubic(eqn, res);
num = evalCubic(res, num, true, true, null,
x1, ctrlx1, ctrlx2, x2);
return (num == 2 &&
getTag(res[0], x, x+w) * getTag(res[1], x, x+w) <= 0);
}
if (!xoverlap) {
fillEqn(eqn, (x1tag < INSIDE ? x : x+w), x1, ctrlx1, ctrlx2, x2);
int num = solveCubic(eqn, res);
num = evalCubic(res, num, true, true, null,
y1, ctrly1, ctrly2, y2);
return (num == 2 &&
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
fillEqn(eqn, (c2tag < INSIDE ? x : x+w), x1, ctrlx1, ctrlx2, x2);
int num = solveCubic(eqn, res);
num = evalCubic(res, num, true, true, null, y1, ctrly1, ctrly2, y2);
int tags[] = new int[num+1];
for (int i = 0; i < num; i++) {
tags[i] = getTag(res[i], y, y+h);
}
tags[num] = c1tag;
Arrays.sort(tags);
return ((num >= 1 && tags[0] * tags[1] <= 0) ||
(num >= 3 && tags[2] * tags[3] <= 0));
}
public boolean contains(float x, float y, float w, float h) {
if (w <= 0 || h <= 0) {
return false;
}
if (!(contains(x, y) &&
contains(x + w, y) &&
contains(x + w, y + h) &&
contains(x, y + h))) {
return false;
}
return !Shape.intersectsLine(x, y, w, h, x1, y1, x2, y2);
}
public PathIterator getPathIterator(BaseTransform tx) {
return new CubicIterator(this, tx);
}
public PathIterator getPathIterator(BaseTransform tx, float flatness) {
return new FlatteningPathIterator(getPathIterator(tx), flatness);
}
@Override
public CubicCurve2D copy() {
return new CubicCurve2D(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
}
@Override
public int hashCode() {
int bits = java.lang.Float.floatToIntBits(x1);
bits += java.lang.Float.floatToIntBits(y1) * 37;
bits += java.lang.Float.floatToIntBits(x2) * 43;
bits += java.lang.Float.floatToIntBits(y2) * 47;
bits += java.lang.Float.floatToIntBits(ctrlx1) * 53;
bits += java.lang.Float.floatToIntBits(ctrly1) * 59;
bits += java.lang.Float.floatToIntBits(ctrlx2) * 61;
bits += java.lang.Float.floatToIntBits(ctrly2) * 101;
return bits;
}
@Override
public boolean equals(Object obj) {
if (obj == this) {
return true;
}
if (obj instanceof CubicCurve2D) {
CubicCurve2D curve = (CubicCurve2D) obj;
return ((x1 == curve.x1) && (y1 == curve.y1) &&
(x2 == curve.x2) && (y2 == curve.y2) &&
(ctrlx1 == curve.ctrlx1) && (ctrly1 == curve.ctrly1) &&
(ctrlx2 == curve.ctrlx2) && (ctrly2 == curve.ctrly2));
}
return false;
}
private float calcX(final float t) {
final float u = 1 - t;
return (u*u*u*x1 +
3*(t*u*u*ctrlx1 +
t*t*u*ctrlx2) +
t*t*t*x2);
}
private float calcY(final float t) {
final float u = 1 - t;
return (u*u*u*y1 +
3*(t*u*u*ctrly1 +
t*t*u*ctrly2) +
t*t*t*y2);
}
}
