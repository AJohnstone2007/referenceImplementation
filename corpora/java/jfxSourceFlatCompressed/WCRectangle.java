package com.sun.webkit.graphics;
public final class WCRectangle {
float x;
float y;
float w;
float h;
public WCRectangle(float x, float y, float w, float h) {
this.x = x;
this.y = y;
this.w = w;
this.h = h;
}
public WCRectangle(WCRectangle r) {
this.x = r.x;
this.y = r.y;
this.w = r.w;
this.h = r.h;
}
public WCRectangle() {
}
public float getX() {
return x;
}
public int getIntX() {
return (int)x;
}
public float getY() {
return y;
}
public int getIntY() {
return (int)y;
}
public float getWidth() {
return w;
}
public int getIntWidth() {
return (int)w;
}
public float getHeight() {
return h;
}
public int getIntHeight() {
return (int)h;
}
public boolean contains(WCRectangle r) {
return x <= r.x && x + w >= r.x + r.w && y <= r.y && y + h >= r.y + r.h;
}
public WCRectangle intersection(WCRectangle r) {
float tx1 = this.x;
float ty1 = this.y;
float rx1 = r.x;
float ry1 = r.y;
float tx2 = tx1; tx2 += this.w;
float ty2 = ty1; ty2 += this.h;
float rx2 = rx1; rx2 += r.w;
float ry2 = ry1; ry2 += r.h;
if (tx1 < rx1) tx1 = rx1;
if (ty1 < ry1) ty1 = ry1;
if (tx2 > rx2) tx2 = rx2;
if (ty2 > ry2) ty2 = ry2;
tx2 -= tx1;
ty2 -= ty1;
if (tx2 < Float.MIN_VALUE) tx2 = Float.MIN_VALUE;
if (ty2 < Float.MIN_VALUE) ty2 = Float.MIN_VALUE;
return new WCRectangle(tx1, ty1, tx2, ty2);
}
public void translate(float dx, float dy) {
float oldv = this.x;
float newv = oldv + dx;
if (dx < 0) {
if (newv > oldv) {
if (w >= 0) {
w += newv - Float.MIN_VALUE;
}
newv = Float.MIN_VALUE;
}
} else {
if (newv < oldv) {
if (w >= 0) {
w += newv - Float.MAX_VALUE;
if (w < 0) w = Float.MAX_VALUE;
}
newv = Float.MAX_VALUE;
}
}
this.x = newv;
oldv = this.y;
newv = oldv + dy;
if (dy < 0) {
if (newv > oldv) {
if (h >= 0) {
h += newv - Float.MIN_VALUE;
}
newv = Float.MIN_VALUE;
}
} else {
if (newv < oldv) {
if (h >= 0) {
h += newv - Float.MAX_VALUE;
if (h < 0) h = Float.MAX_VALUE;
}
newv = Float.MAX_VALUE;
}
}
this.y = newv;
}
public WCRectangle createUnion(WCRectangle r) {
WCRectangle dest = new WCRectangle();
WCRectangle.union(this, r, dest);
return dest;
}
public static void union(WCRectangle src1,
WCRectangle src2,
WCRectangle dest)
{
float x1 = Math.min(src1.getMinX(), src2.getMinX());
float y1 = Math.min(src1.getMinY(), src2.getMinY());
float x2 = Math.max(src1.getMaxX(), src2.getMaxX());
float y2 = Math.max(src1.getMaxY(), src2.getMaxY());
dest.setFrameFromDiagonal(x1, y1, x2, y2);
}
public void setFrameFromDiagonal(float x1, float y1, float x2, float y2) {
if (x2 < x1) {
float t = x1;
x1 = x2;
x2 = t;
}
if (y2 < y1) {
float t = y1;
y1 = y2;
y2 = t;
}
setFrame(x1, y1, x2 - x1, y2 - y1);
}
public void setFrame(float x, float y, float w, float h) {
this.x = x;
this.y = y;
this.w = w;
this.h = h;
}
public float getMinX() {
return getX();
}
public float getMaxX() {
return getX() + getWidth();
}
public float getMinY() {
return getY();
}
public float getMaxY() {
return getY() + getHeight();
}
public boolean isEmpty() {
return (w <= 0) || (h <= 0);
}
@Override
public boolean equals(Object obj) {
if (obj instanceof WCRectangle) {
WCRectangle rc = (WCRectangle)obj;
return (x == rc.x) && (y == rc.y) && (w == rc.w) && (h == rc.h);
}
return super.equals(obj);
}
@Override
public String toString() {
return "WCRectangle{x:" + x + " y:" + y + " w:" + w + " h:" + h + "}";
}
}
