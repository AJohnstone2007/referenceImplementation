package com.sun.javafx.geom;
import com.sun.javafx.geom.transform.BaseTransform;
public class Arc2D extends RectangularShape {
public final static int OPEN = 0;
public final static int CHORD = 1;
public final static int PIE = 2;
private int type;
public float x;
public float y;
public float width;
public float height;
public float start;
public float extent;
public Arc2D() {
this(OPEN);
}
public Arc2D(int type) {
setArcType(type);
}
public Arc2D(float x, float y, float w, float h,
float start, float extent, int type) {
this(type);
this.x = x;
this.y = y;
this.width = w;
this.height = h;
this.start = start;
this.extent = extent;
}
@Override
public float getX() {
return x;
}
@Override
public float getY() {
return y;
}
@Override
public float getWidth() {
return width;
}
@Override
public float getHeight() {
return height;
}
public boolean isEmpty() {
return (width <= 0f || height <= 0f);
}
public void setArc(float x, float y, float w, float h,
float angSt, float angExt, int closure) {
this.setArcType(closure);
this.x = x;
this.y = y;
this.width = w;
this.height = h;
this.start = angSt;
this.extent = angExt;
}
public int getArcType() {
return type;
}
public Point2D getStartPoint() {
double angle = Math.toRadians(-start);
double x = this.x + (Math.cos(angle) * 0.5 + 0.5) * width;
double y = this.y + (Math.sin(angle) * 0.5 + 0.5) * height;
return new Point2D((float)x, (float)y);
}
public Point2D getEndPoint() {
double angle = Math.toRadians(-start - extent);
double x = this.x + (Math.cos(angle) * 0.5 + 0.5) * width;
double y = this.y + (Math.sin(angle) * 0.5 + 0.5) * height;
return new Point2D((float)x, (float)y);
}
public void setArc(Point2D loc, Dimension2D size,
float angSt, float angExt, int closure) {
setArc(loc.x, loc.y, size.width, size.height, angSt, angExt, closure);
}
public void setArc(Arc2D a) {
setArc(a.x, a.y, a.width, a.height, a.start, a.extent, a.type);
}
public void setArcByCenter(float x, float y, float radius,
float angSt, float angExt, int closure) {
setArc(x - radius, y - radius, radius * 2f, radius * 2f,
angSt, angExt, closure);
}
public void setArcByTangent(Point2D p1, Point2D p2, Point2D p3, float radius) {
double ang1 = Math.atan2(p1.y - p2.y,
p1.x - p2.x);
double ang2 = Math.atan2(p3.y - p2.y,
p3.x - p2.x);
double diff = ang2 - ang1;
if (diff > Math.PI) {
ang2 -= Math.PI * 2.0;
} else if (diff < -Math.PI) {
ang2 += Math.PI * 2.0;
}
double bisect = (ang1 + ang2) / 2.0;
double theta = Math.abs(ang2 - bisect);
double dist = radius / Math.sin(theta);
double x = p2.x + dist * Math.cos(bisect);
double y = p2.y + dist * Math.sin(bisect);
if (ang1 < ang2) {
ang1 -= Math.PI / 2.0;
ang2 += Math.PI / 2.0;
} else {
ang1 += Math.PI / 2.0;
ang2 -= Math.PI / 2.0;
}
ang1 = Math.toDegrees(-ang1);
ang2 = Math.toDegrees(-ang2);
diff = ang2 - ang1;
if (diff < 0) {
diff += 360;
} else {
diff -= 360;
}
setArcByCenter((float)x, (float)y, (float)radius, (float)ang1, (float)diff, type);
}
public void setAngleStart(Point2D p) {
double dx = this.height * (p.x - getCenterX());
double dy = this.width * (p.y - getCenterY());
start = (float)-Math.toDegrees(Math.atan2(dy, dx));
}
public void setAngles(float x1, float y1, float x2, float y2) {
double x = getCenterX();
double y = getCenterY();
double w = this.width;
double h = this.height;;
double ang1 = Math.atan2(w * (y - y1), h * (x1 - x));
double ang2 = Math.atan2(w * (y - y2), h * (x2 - x));
ang2 -= ang1;
if (ang2 <= 0.0) {
ang2 += Math.PI * 2.0;
}
start = (float)Math.toDegrees(ang1);
extent = (float)Math.toDegrees(ang2);
}
public void setAngles(Point2D p1, Point2D p2) {
setAngles(p1.x, p1.y, p2.x, p2.y);
}
public void setArcType(int type) {
if (type < OPEN || type > PIE) {
throw new IllegalArgumentException("invalid type for Arc: "+type);
}
this.type = type;
}
public void setFrame(float x, float y, float w, float h) {
setArc(x, y, w, h, start, extent, type);
}
public RectBounds getBounds() {
if (isEmpty()) {
return new RectBounds(x, y, x + width, y + height);
}
double x1, y1, x2, y2;
if (getArcType() == PIE) {
x1 = y1 = x2 = y2 = 0.0;
} else {
x1 = y1 = 1.0;
x2 = y2 = -1.0;
}
double angle = 0.0;
for (int i = 0; i < 6; i++) {
if (i < 4) {
angle += 90.0;
if (!containsAngle((float)angle)) {
continue;
}
} else if (i == 4) {
angle = start;
} else {
angle += extent;
}
double rads = Math.toRadians(-angle);
double xe = Math.cos(rads);
double ye = Math.sin(rads);
x1 = Math.min(x1, xe);
y1 = Math.min(y1, ye);
x2 = Math.max(x2, xe);
y2 = Math.max(y2, ye);
}
double w = this.width;
double h = this.height;
x2 = this.x + (x2 * 0.5 + 0.5) * w;
y2 = this.y + (y2 * 0.5 + 0.5) * h;
x1 = this.x + (x1 * 0.5 + 0.5) * w;
y1 = this.y + (y1 * 0.5 + 0.5) * h;
return new RectBounds((float)x1, (float)y1, (float)x2, (float)y2);
}
static float normalizeDegrees(double angle) {
if (angle > 180.0) {
if (angle <= (180.0 + 360.0)) {
angle = angle - 360.0;
} else {
angle = Math.IEEEremainder(angle, 360.0);
if (angle == -180.0) {
angle = 180.0;
}
}
} else if (angle <= -180.0) {
if (angle > (-180.0 - 360.0)) {
angle = angle + 360.0;
} else {
angle = Math.IEEEremainder(angle, 360.0);
if (angle == -180.0) {
angle = 180.0;
}
}
}
return (float)angle;
}
public boolean containsAngle(float angle) {
double angExt = extent;
boolean backwards = (angExt < 0.0);
if (backwards) {
angExt = -angExt;
}
if (angExt >= 360.0) {
return true;
}
angle = normalizeDegrees(angle) - normalizeDegrees(start);
if (backwards) {
angle = -angle;
}
if (angle < 0.0) {
angle += 360.0;
}
return (angle >= 0.0) && (angle < angExt);
}
public boolean contains(float x, float y) {
double ellw = this.width;
if (ellw <= 0.0) {
return false;
}
double normx = (x - this.x) / ellw - 0.5;
double ellh = this.height;
if (ellh <= 0.0) {
return false;
}
double normy = (y - this.y) / ellh - 0.5;
double distSq = (normx * normx + normy * normy);
if (distSq >= 0.25) {
return false;
}
double angExt = Math.abs(extent);
if (angExt >= 360.0) {
return true;
}
boolean inarc = containsAngle((float)-Math.toDegrees(Math.atan2(normy,
normx)));
if (type == PIE) {
return inarc;
}
if (inarc) {
if (angExt >= 180.0) {
return true;
}
} else {
if (angExt <= 180.0) {
return false;
}
}
double angle = Math.toRadians(-start);
double x1 = Math.cos(angle);
double y1 = Math.sin(angle);
angle += Math.toRadians(-extent);
double x2 = Math.cos(angle);
double y2 = Math.sin(angle);
boolean inside = (Line2D.relativeCCW((float)x1, (float)y1, (float)x2, (float)y2, (float)(2*normx), (float)(2*normy)) *
Line2D.relativeCCW((float)x1, (float)y1, (float)x2, (float)y2, 0, 0) >= 0);
return inarc ? !inside : inside;
}
public boolean intersects(float x, float y, float w, float h) {
float aw = this.width;
float ah = this.height;
if ( w <= 0 || h <= 0 || aw <= 0 || ah <= 0 ) {
return false;
}
float ext = extent;
if (ext == 0) {
return false;
}
float ax = this.x;
float ay = this.y;
float axw = ax + aw;
float ayh = ay + ah;
float xw = x + w;
float yh = y + h;
if (x >= axw || y >= ayh || xw <= ax || yh <= ay) {
return false;
}
float axc = getCenterX();
float ayc = getCenterY();
double sangle = Math.toRadians(-start);
float sx = (float) (this.x + (Math.cos(sangle) * 0.5 + 0.5) * width);
float sy = (float) (this.y + (Math.sin(sangle) * 0.5 + 0.5) * height);
double eangle = Math.toRadians(-start - extent);
float ex = (float) (this.x + (Math.cos(eangle) * 0.5 + 0.5) * width);
float ey = (float) (this.y + (Math.sin(eangle) * 0.5 + 0.5) * height);
if (ayc >= y && ayc <= yh) {
if ((sx < xw && ex < xw && axc < xw &&
axw > x && containsAngle(0)) ||
(sx > x && ex > x && axc > x &&
ax < xw && containsAngle(180))) {
return true;
}
}
if (axc >= x && axc <= xw) {
if ((sy > y && ey > y && ayc > y &&
ay < yh && containsAngle(90)) ||
(sy < yh && ey < yh && ayc < yh &&
ayh > y && containsAngle(270))) {
return true;
}
}
if (type == PIE || Math.abs(ext) > 180) {
if (Shape.intersectsLine(x, y, w, h, axc, ayc, sx, sy) ||
Shape.intersectsLine(x, y, w, h, axc, ayc, ex, ey))
{
return true;
}
} else {
if (Shape.intersectsLine(x, y, w, h, sx, sy, ex, ey)) {
return true;
}
}
if (contains(x, y) || contains(x + w, y) ||
contains(x, y + h) || contains(x + w, y + h)) {
return true;
}
return false;
}
public boolean contains(float x, float y, float w, float h) {
if (!(contains(x, y) &&
contains(x + w, y) &&
contains(x, y + h) &&
contains(x + w, y + h))) {
return false;
}
if (type != PIE || Math.abs(extent) <= 180.0) {
return true;
}
float halfW = getWidth() / 2f;
float halfH = getHeight() / 2f;
float xc = x + halfW;
float yc = y + halfH;
float angle = (float) Math.toRadians(-start);
float xe = (float) (xc + halfW * Math.cos(angle));
float ye = (float) (yc + halfH * Math.sin(angle));
if (Shape.intersectsLine(x, y, w, h, xc, yc, xe, ye)) {
return false;
}
angle += (float) Math.toRadians(-extent);
xe = (float) (xc + halfW * Math.cos(angle));
ye = (float) (yc + halfH * Math.sin(angle));
return !Shape.intersectsLine(x, y, w, h, xc, yc, xe, ye);
}
public PathIterator getPathIterator(BaseTransform tx) {
return new ArcIterator(this, tx);
}
@Override
public Arc2D copy() {
return new Arc2D(x, y, width, height, start, extent, type);
}
@Override
public int hashCode() {
int bits = java.lang.Float.floatToIntBits(x);
bits += java.lang.Float.floatToIntBits(y) * 37;
bits += java.lang.Float.floatToIntBits(width) * 43;
bits += java.lang.Float.floatToIntBits(height) * 47;
bits += java.lang.Float.floatToIntBits(start) * 53;
bits += java.lang.Float.floatToIntBits(extent) * 59;
bits += getArcType() * 61;
return bits;
}
@Override
public boolean equals(Object obj) {
if (obj == this) return true;
if (obj instanceof Arc2D) {
Arc2D a2d = (Arc2D) obj;
return ((x == a2d.x) &&
(y == a2d.y) &&
(width == a2d.width) &&
(height == a2d.height) &&
(start == a2d.start) &&
(extent == a2d.extent) &&
(type == a2d.type));
}
return false;
}
}
