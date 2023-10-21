package com.sun.javafx.geom;
import com.sun.javafx.geom.transform.BaseTransform;
public class Line2D extends Shape {
public float x1;
public float y1;
public float x2;
public float y2;
public Line2D() { }
public Line2D(float x1, float y1, float x2, float y2) {
setLine(x1, y1, x2, y2);
}
public Line2D(Point2D p1, Point2D p2) {
setLine(p1, p2);
}
public void setLine(float x1, float y1, float x2, float y2) {
this.x1 = x1;
this.y1 = y1;
this.x2 = x2;
this.y2 = y2;
}
public void setLine(Point2D p1, Point2D p2) {
setLine(p1.x, p1.y, p2.x, p2.y);
}
public void setLine(Line2D l) {
setLine(l.x1, l.y1, l.x2, l.y2);
}
public RectBounds getBounds() {
RectBounds b = new RectBounds();
b.setBoundsAndSort(x1, y1, x2, y2);
return b;
}
@Override
public boolean contains(float x, float y) { return false; }
@Override
public boolean contains(float x, float y, float w, float h) { return false; }
@Override
public boolean contains(Point2D p) { return false; }
@Override
public boolean intersects(float x, float y, float w, float h) {
int out1, out2;
if ((out2 = outcode(x, y, w, h, x2, y2)) == 0) {
return true;
}
float px = x1;
float py = y1;
while ((out1 = outcode(x, y, w, h, px, py)) != 0) {
if ((out1 & out2) != 0) {
return false;
}
if ((out1 & (OUT_LEFT | OUT_RIGHT)) != 0) {
px = x;
if ((out1 & OUT_RIGHT) != 0) {
px += w;
}
py = y1 + (px - x1) * (y2 - y1) / (x2 - x1);
} else {
py = y;
if ((out1 & OUT_BOTTOM) != 0) {
py += h;
}
px = x1 + (py - y1) * (x2 - x1) / (y2 - y1);
}
}
return true;
}
public static int relativeCCW(float x1, float y1,
float x2, float y2,
float px, float py)
{
x2 -= x1;
y2 -= y1;
px -= x1;
py -= y1;
float ccw = px * y2 - py * x2;
if (ccw == 0.0f) {
ccw = px * x2 + py * y2;
if (ccw > 0.0f) {
px -= x2;
py -= y2;
ccw = px * x2 + py * y2;
if (ccw < 0.0f) {
ccw = 0.0f;
}
}
}
return (ccw < 0.0f) ? -1 : ((ccw > 0.0f) ? 1 : 0);
}
public int relativeCCW(float px, float py) {
return relativeCCW(x1, y1, x2, y2, px, py);
}
public int relativeCCW(Point2D p) {
return relativeCCW(x1, y1, x2, y2, p.x, p.y);
}
public static boolean linesIntersect(float x1, float y1,
float x2, float y2,
float x3, float y3,
float x4, float y4)
{
return ((relativeCCW(x1, y1, x2, y2, x3, y3) *
relativeCCW(x1, y1, x2, y2, x4, y4) <= 0)
&& (relativeCCW(x3, y3, x4, y4, x1, y1) *
relativeCCW(x3, y3, x4, y4, x2, y2) <= 0));
}
public boolean intersectsLine(float x1, float y1, float x2, float y2) {
return linesIntersect(x1, y1, x2, y2, this.x1, this.y1, this.x2, this.y2);
}
public boolean intersectsLine(Line2D l) {
return linesIntersect(l.x1, l.y1, l.x2, l.y2, this.x1, this.y1, this.x2, this.y2);
}
public static float ptSegDistSq(float x1, float y1,
float x2, float y2,
float px, float py)
{
x2 -= x1;
y2 -= y1;
px -= x1;
py -= y1;
float dotprod = px * x2 + py * y2;
float projlenSq;
if (dotprod <= 0f) {
projlenSq = 0f;
} else {
px = x2 - px;
py = y2 - py;
dotprod = px * x2 + py * y2;
if (dotprod <= 0f) {
projlenSq = 0f;
} else {
projlenSq = dotprod * dotprod / (x2 * x2 + y2 * y2);
}
}
float lenSq = px * px + py * py - projlenSq;
if (lenSq < 0f) {
lenSq = 0f;
}
return lenSq;
}
public static float ptSegDist(float x1, float y1,
float x2, float y2,
float px, float py)
{
return (float) Math.sqrt(ptSegDistSq(x1, y1, x2, y2, px, py));
}
public float ptSegDistSq(float px, float py) {
return ptSegDistSq(x1, y1, x2, y2, px, py);
}
public float ptSegDistSq(Point2D pt) {
return ptSegDistSq(x1, y1, x2, y2, pt.x, pt.y);
}
public double ptSegDist(float px, float py) {
return ptSegDist(x1, y1, x2, y2, px, py);
}
public float ptSegDist(Point2D pt) {
return ptSegDist(x1, y1, x2, y2, pt.x, pt.y);
}
public static float ptLineDistSq(float x1, float y1,
float x2, float y2,
float px, float py)
{
x2 -= x1;
y2 -= y1;
px -= x1;
py -= y1;
float dotprod = px * x2 + py * y2;
float projlenSq = dotprod * dotprod / (x2 * x2 + y2 * y2);
float lenSq = px * px + py * py - projlenSq;
if (lenSq < 0f) {
lenSq = 0f;
}
return lenSq;
}
public static float ptLineDist(float x1, float y1,
float x2, float y2,
float px, float py)
{
return (float) Math.sqrt(ptLineDistSq(x1, y1, x2, y2, px, py));
}
public float ptLineDistSq(float px, float py) {
return ptLineDistSq(x1, y1, x2, y2, px, py);
}
public float ptLineDistSq(Point2D pt) {
return ptLineDistSq(x1, y1, x2, y2, pt.x, pt.y);
}
public float ptLineDist(float px, float py) {
return ptLineDist(x1, y1, x2, y2, px, py);
}
public float ptLineDist(Point2D pt) {
return ptLineDist(x1, y1, x2, y2, pt.x, pt.y);
}
public PathIterator getPathIterator(BaseTransform tx) {
return new LineIterator(this, tx);
}
public PathIterator getPathIterator(BaseTransform tx, float flatness) {
return new LineIterator(this, tx);
}
@Override
public Line2D copy() {
return new Line2D(x1, y1, x2, y2);
}
@Override
public int hashCode() {
int bits = java.lang.Float.floatToIntBits(x1);
bits += java.lang.Float.floatToIntBits(y1) * 37;
bits += java.lang.Float.floatToIntBits(x2) * 43;
bits += java.lang.Float.floatToIntBits(y2) * 47;
return bits;
}
@Override
public boolean equals(Object obj) {
if (obj == this) {
return true;
}
if (obj instanceof Line2D) {
Line2D line = (Line2D) obj;
return ((x1 == line.x1) && (y1 == line.y1) &&
(x2 == line.x2) && (y2 == line.y2));
}
return false;
}
}
