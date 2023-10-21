package com.sun.javafx.geom;
import java.util.Vector;
final class Order2 extends Curve {
private double x0;
private double y0;
private double cx0;
private double cy0;
private double x1;
private double y1;
private double xmin;
private double xmax;
private double xcoeff0;
private double xcoeff1;
private double xcoeff2;
private double ycoeff0;
private double ycoeff1;
private double ycoeff2;
public static void insert(Vector curves, double tmp[],
double x0, double y0,
double cx0, double cy0,
double x1, double y1,
int direction)
{
int numparams = getHorizontalParams(y0, cy0, y1, tmp);
if (numparams == 0) {
addInstance(curves, x0, y0, cx0, cy0, x1, y1, direction);
return;
}
double t = tmp[0];
tmp[0] = x0; tmp[1] = y0;
tmp[2] = cx0; tmp[3] = cy0;
tmp[4] = x1; tmp[5] = y1;
split(tmp, 0, t);
int i0 = (direction == INCREASING)? 0 : 4;
int i1 = 4 - i0;
addInstance(curves, tmp[i0], tmp[i0 + 1], tmp[i0 + 2], tmp[i0 + 3],
tmp[i0 + 4], tmp[i0 + 5], direction);
addInstance(curves, tmp[i1], tmp[i1 + 1], tmp[i1 + 2], tmp[i1 + 3],
tmp[i1 + 4], tmp[i1 + 5], direction);
}
public static void addInstance(Vector curves,
double x0, double y0,
double cx0, double cy0,
double x1, double y1,
int direction) {
if (y0 > y1) {
curves.add(new Order2(x1, y1, cx0, cy0, x0, y0, -direction));
} else if (y1 > y0) {
curves.add(new Order2(x0, y0, cx0, cy0, x1, y1, direction));
}
}
public static int getHorizontalParams(double c0, double cp, double c1,
double ret[]) {
if (c0 <= cp && cp <= c1) {
return 0;
}
c0 -= cp;
c1 -= cp;
double denom = c0 + c1;
if (denom == 0) {
return 0;
}
double t = c0 / denom;
if (t <= 0 || t >= 1) {
return 0;
}
ret[0] = t;
return 1;
}
public static void split(double coords[], int pos, double t) {
double x0, y0, cx, cy, x1, y1;
coords[pos+8] = x1 = coords[pos+4];
coords[pos+9] = y1 = coords[pos+5];
cx = coords[pos+2];
cy = coords[pos+3];
x1 = cx + (x1 - cx) * t;
y1 = cy + (y1 - cy) * t;
x0 = coords[pos+0];
y0 = coords[pos+1];
x0 = x0 + (cx - x0) * t;
y0 = y0 + (cy - y0) * t;
cx = x0 + (x1 - x0) * t;
cy = y0 + (y1 - y0) * t;
coords[pos+2] = x0;
coords[pos+3] = y0;
coords[pos+4] = cx;
coords[pos+5] = cy;
coords[pos+6] = x1;
coords[pos+7] = y1;
}
public Order2(double x0, double y0,
double cx0, double cy0,
double x1, double y1,
int direction)
{
super(direction);
if (cy0 < y0) {
cy0 = y0;
} else if (cy0 > y1) {
cy0 = y1;
}
this.x0 = x0;
this.y0 = y0;
this.cx0 = cx0;
this.cy0 = cy0;
this.x1 = x1;
this.y1 = y1;
xmin = Math.min(Math.min(x0, x1), cx0);
xmax = Math.max(Math.max(x0, x1), cx0);
xcoeff0 = x0;
xcoeff1 = cx0 + cx0 - x0 - x0;
xcoeff2 = x0 - cx0 - cx0 + x1;
ycoeff0 = y0;
ycoeff1 = cy0 + cy0 - y0 - y0;
ycoeff2 = y0 - cy0 - cy0 + y1;
}
public int getOrder() {
return 2;
}
public double getXTop() {
return x0;
}
public double getYTop() {
return y0;
}
public double getXBot() {
return x1;
}
public double getYBot() {
return y1;
}
public double getXMin() {
return xmin;
}
public double getXMax() {
return xmax;
}
public double getX0() {
return (direction == INCREASING) ? x0 : x1;
}
public double getY0() {
return (direction == INCREASING) ? y0 : y1;
}
public double getCX0() {
return cx0;
}
public double getCY0() {
return cy0;
}
public double getX1() {
return (direction == DECREASING) ? x0 : x1;
}
public double getY1() {
return (direction == DECREASING) ? y0 : y1;
}
public double XforY(double y) {
if (y <= y0) {
return x0;
}
if (y >= y1) {
return x1;
}
return XforT(TforY(y));
}
public double TforY(double y) {
if (y <= y0) {
return 0;
}
if (y >= y1) {
return 1;
}
return TforY(y, ycoeff0, ycoeff1, ycoeff2);
}
public static double TforY(double y,
double ycoeff0, double ycoeff1, double ycoeff2)
{
ycoeff0 -= y;
if (ycoeff2 == 0.0) {
double root = -ycoeff0 / ycoeff1;
if (root >= 0 && root <= 1) {
return root;
}
} else {
double d = ycoeff1 * ycoeff1 - 4.0 * ycoeff2 * ycoeff0;
if (d >= 0.0) {
d = Math.sqrt(d);
if (ycoeff1 < 0.0) {
d = -d;
}
double q = (ycoeff1 + d) / -2.0;
double root = q / ycoeff2;
if (root >= 0 && root <= 1) {
return root;
}
if (q != 0.0) {
root = ycoeff0 / q;
if (root >= 0 && root <= 1) {
return root;
}
}
}
}
double y0 = ycoeff0;
double y1 = ycoeff0 + ycoeff1 + ycoeff2;
return (0 < (y0 + y1) / 2) ? 0.0 : 1.0;
}
public double XforT(double t) {
return (xcoeff2 * t + xcoeff1) * t + xcoeff0;
}
public double YforT(double t) {
return (ycoeff2 * t + ycoeff1) * t + ycoeff0;
}
public double dXforT(double t, int deriv) {
switch (deriv) {
case 0:
return (xcoeff2 * t + xcoeff1) * t + xcoeff0;
case 1:
return 2 * xcoeff2 * t + xcoeff1;
case 2:
return 2 * xcoeff2;
default:
return 0;
}
}
public double dYforT(double t, int deriv) {
switch (deriv) {
case 0:
return (ycoeff2 * t + ycoeff1) * t + ycoeff0;
case 1:
return 2 * ycoeff2 * t + ycoeff1;
case 2:
return 2 * ycoeff2;
default:
return 0;
}
}
public double nextVertical(double t0, double t1) {
double t = -xcoeff1 / (2 * xcoeff2);
if (t > t0 && t < t1) {
return t;
}
return t1;
}
public void enlarge(RectBounds r) {
r.add((float) x0, (float) y0);
double t = -xcoeff1 / (2 * xcoeff2);
if (t > 0 && t < 1) {
r.add((float) XforT(t), (float) YforT(t));
}
r.add((float) x1, (float) y1);
}
public Curve getSubCurve(double ystart, double yend, int dir) {
double t0, t1;
if (ystart <= y0) {
if (yend >= y1) {
return getWithDirection(dir);
}
t0 = 0;
} else {
t0 = TforY(ystart, ycoeff0, ycoeff1, ycoeff2);
}
if (yend >= y1) {
t1 = 1;
} else {
t1 = TforY(yend, ycoeff0, ycoeff1, ycoeff2);
}
double eqn[] = new double[10];
eqn[0] = x0;
eqn[1] = y0;
eqn[2] = cx0;
eqn[3] = cy0;
eqn[4] = x1;
eqn[5] = y1;
if (t1 < 1) {
split(eqn, 0, t1);
}
int i;
if (t0 <= 0) {
i = 0;
} else {
split(eqn, 0, t0 / t1);
i = 4;
}
return new Order2(eqn[i+0], ystart,
eqn[i+2], eqn[i+3],
eqn[i+4], yend,
dir);
}
public Curve getReversedCurve() {
return new Order2(x0, y0, cx0, cy0, x1, y1, -direction);
}
public int getSegment(float coords[]) {
coords[0] = (float) cx0;
coords[1] = (float) cy0;
if (direction == INCREASING) {
coords[2] = (float) x1;
coords[3] = (float) y1;
} else {
coords[2] = (float) x0;
coords[3] = (float) y0;
}
return PathIterator.SEG_QUADTO;
}
@Override
public String controlPointString() {
return ("("+round(cx0)+", "+round(cy0)+"), ");
}
}
