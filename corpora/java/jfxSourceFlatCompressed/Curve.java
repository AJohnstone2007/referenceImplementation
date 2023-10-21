package com.sun.marlin;
final class Curve {
double ax, ay, bx, by, cx, cy, dx, dy;
double dax, day, dbx, dby;
Curve() {
}
void set(final double[] points, final int type) {
if (type == 8) {
set(points[0], points[1],
points[2], points[3],
points[4], points[5],
points[6], points[7]);
} else if (type == 4) {
set(points[0], points[1],
points[2], points[3]);
} else {
set(points[0], points[1],
points[2], points[3],
points[4], points[5]);
}
}
void set(final double x1, final double y1,
final double x2, final double y2,
final double x3, final double y3,
final double x4, final double y4)
{
final double dx32 = 3.0d * (x3 - x2);
final double dy32 = 3.0d * (y3 - y2);
final double dx21 = 3.0d * (x2 - x1);
final double dy21 = 3.0d * (y2 - y1);
ax = (x4 - x1) - dx32;
ay = (y4 - y1) - dy32;
bx = (dx32 - dx21);
by = (dy32 - dy21);
cx = dx21;
cy = dy21;
dx = x1;
dy = y1;
dax = 3.0d * ax;
day = 3.0d * ay;
dbx = 2.0d * bx;
dby = 2.0d * by;
}
void set(final double x1, final double y1,
final double x2, final double y2,
final double x3, final double y3)
{
final double dx21 = (x2 - x1);
final double dy21 = (y2 - y1);
ax = 0.0d;
ay = 0.0d;
bx = (x3 - x2) - dx21;
by = (y3 - y2) - dy21;
cx = 2.0d * dx21;
cy = 2.0d * dy21;
dx = x1;
dy = y1;
dax = 0.0d;
day = 0.0d;
dbx = 2.0d * bx;
dby = 2.0d * by;
}
void set(final double x1, final double y1,
final double x2, final double y2)
{
final double dx21 = (x2 - x1);
final double dy21 = (y2 - y1);
ax = 0.0d;
ay = 0.0d;
bx = 0.0d;
by = 0.0d;
cx = dx21;
cy = dy21;
dx = x1;
dy = y1;
dax = 0.0d;
day = 0.0d;
dbx = 0.0d;
dby = 0.0d;
}
int dxRoots(final double[] roots, final int off) {
return Helpers.quadraticRoots(dax, dbx, cx, roots, off);
}
int dyRoots(final double[] roots, final int off) {
return Helpers.quadraticRoots(day, dby, cy, roots, off);
}
int infPoints(final double[] pts, final int off) {
final double a = dax * dby - dbx * day;
final double b = 2.0d * (cy * dax - day * cx);
final double c = cy * dbx - cx * dby;
return Helpers.quadraticRoots(a, b, c, pts, off);
}
int xPoints(final double[] ts, final int off, final double x)
{
return Helpers.cubicRootsInAB(ax, bx, cx, dx - x, ts, off, 0.0d, 1.0d);
}
int yPoints(final double[] ts, final int off, final double y)
{
return Helpers.cubicRootsInAB(ay, by, cy, dy - y, ts, off, 0.0d, 1.0d);
}
private int perpendiculardfddf(final double[] pts, final int off) {
assert pts.length >= off + 4;
final double a = 2.0d * (dax * dax + day * day);
final double b = 3.0d * (dax * dbx + day * dby);
final double c = 2.0d * (dax * cx + day * cy) + dbx * dbx + dby * dby;
final double d = dbx * cx + dby * cy;
return Helpers.cubicRootsInAB(a, b, c, d, pts, off, 0.0d, 1.0d);
}
int rootsOfROCMinusW(final double[] roots, final int off, final double w2, final double err) {
assert off <= 6 && roots.length >= 10;
int ret = off;
final int end = off + perpendiculardfddf(roots, off);
roots[end] = 1.0d;
double t0 = 0.0d, ft0 = ROCsq(t0) - w2;
for (int i = off; i <= end; i++) {
double t1 = roots[i], ft1 = ROCsq(t1) - w2;
if (ft0 == 0.0d) {
roots[ret++] = t0;
} else if (ft1 * ft0 < 0.0d) {
roots[ret++] = falsePositionROCsqMinusX(t0, t1, w2, err);
}
t0 = t1;
ft0 = ft1;
}
return ret - off;
}
private static double eliminateInf(final double x) {
return (x == Double.POSITIVE_INFINITY ? Double.MAX_VALUE :
(x == Double.NEGATIVE_INFINITY ? Double.MIN_VALUE : x));
}
private double falsePositionROCsqMinusX(final double t0, final double t1,
final double w2, final double err)
{
final int iterLimit = 100;
int side = 0;
double t = t1, ft = eliminateInf(ROCsq(t) - w2);
double s = t0, fs = eliminateInf(ROCsq(s) - w2);
double r = s, fr;
for (int i = 0; i < iterLimit && Math.abs(t - s) > err * Math.abs(t + s); i++) {
r = (fs * t - ft * s) / (fs - ft);
fr = ROCsq(r) - w2;
if (sameSign(fr, ft)) {
ft = fr; t = r;
if (side < 0) {
fs /= (1 << (-side));
side--;
} else {
side = -1;
}
} else if (fr * fs > 0.0d) {
fs = fr; s = r;
if (side > 0) {
ft /= (1 << side);
side++;
} else {
side = 1;
}
} else {
break;
}
}
return r;
}
private static boolean sameSign(final double x, final double y) {
return (x < 0.0d && y < 0.0d) || (x > 0.0d && y > 0.0d);
}
private double ROCsq(final double t) {
final double dx = t * (t * dax + dbx) + cx;
final double dy = t * (t * day + dby) + cy;
final double ddx = 2.0d * dax * t + dbx;
final double ddy = 2.0d * day * t + dby;
final double dx2dy2 = dx * dx + dy * dy;
final double ddx2ddy2 = ddx * ddx + ddy * ddy;
final double ddxdxddydy = ddx * dx + ddy * dy;
return dx2dy2 * ((dx2dy2 * dx2dy2) / (dx2dy2 * ddx2ddy2 - ddxdxddydy * ddxdxddydy));
}
}
