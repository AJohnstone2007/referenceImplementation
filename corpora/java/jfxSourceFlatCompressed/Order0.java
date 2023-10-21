package com.sun.javafx.geom;
final class Order0 extends Curve {
private double x;
private double y;
public Order0(double x, double y) {
super(INCREASING);
this.x = x;
this.y = y;
}
public int getOrder() {
return 0;
}
public double getXTop() {
return x;
}
public double getYTop() {
return y;
}
public double getXBot() {
return x;
}
public double getYBot() {
return y;
}
public double getXMin() {
return x;
}
public double getXMax() {
return x;
}
public double getX0() {
return x;
}
public double getY0() {
return y;
}
public double getX1() {
return x;
}
public double getY1() {
return y;
}
public double XforY(double y) {
return y;
}
public double TforY(double y) {
return 0;
}
public double XforT(double t) {
return x;
}
public double YforT(double t) {
return y;
}
public double dXforT(double t, int deriv) {
return 0;
}
public double dYforT(double t, int deriv) {
return 0;
}
public double nextVertical(double t0, double t1) {
return t1;
}
@Override
public int crossingsFor(double x, double y) {
return 0;
}
@Override
public boolean accumulateCrossings(Crossings c) {
return (x > c.getXLo() &&
x < c.getXHi() &&
y > c.getYLo() &&
y < c.getYHi());
}
public void enlarge(RectBounds r) {
r.add((float) x, (float) y);
}
public Curve getSubCurve(double ystart, double yend, int dir) {
return this;
}
public Curve getReversedCurve() {
return this;
}
public int getSegment(float coords[]) {
coords[0] = (float) x;
coords[1] = (float) y;
return PathIterator.SEG_MOVETO;
}
}
