package com.sun.javafx.geom;
import com.sun.javafx.geom.transform.BaseTransform;
public class Ellipse2D extends RectangularShape {
public float x;
public float y;
public float width;
public float height;
public Ellipse2D() { }
public Ellipse2D(float x, float y, float w, float h) {
setFrame(x, y, w, h);
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
@Override
public boolean isEmpty() {
return (width <= 0f || height <= 0f);
}
public void setFrame(float x, float y, float w, float h) {
this.x = x;
this.y = y;
this.width = w;
this.height = h;
}
public RectBounds getBounds() {
return new RectBounds(x, y, x + width, y + height);
}
public boolean contains(float x, float y) {
float ellw = this.width;
if (ellw <= 0f) {
return false;
}
float normx = (x - this.x) / ellw - 0.5f;
float ellh = this.height;
if (ellh <= 0f) {
return false;
}
float normy = (y - this.y) / ellh - 0.5f;
return (normx * normx + normy * normy) < 0.25f;
}
public boolean intersects(float x, float y, float w, float h) {
if (w <= 0f || h <= 0f) {
return false;
}
float ellw = this.width;
if (ellw <= 0f) {
return false;
}
float normx0 = (x - this.x) / ellw - 0.5f;
float normx1 = normx0 + w / ellw;
float ellh = this.height;
if (ellh <= 0f) {
return false;
}
float normy0 = (y - this.y) / ellh - 0.5f;
float normy1 = normy0 + h / ellh;
float nearx, neary;
if (normx0 > 0f) {
nearx = normx0;
} else if (normx1 < 0f) {
nearx = normx1;
} else {
nearx = 0f;
}
if (normy0 > 0f) {
neary = normy0;
} else if (normy1 < 0f) {
neary = normy1;
} else {
neary = 0f;
}
return (nearx * nearx + neary * neary) < 0.25f;
}
public boolean contains(float x, float y, float w, float h) {
return (contains(x, y) &&
contains(x + w, y) &&
contains(x, y + h) &&
contains(x + w, y + h));
}
public PathIterator getPathIterator(BaseTransform tx) {
return new EllipseIterator(this, tx);
}
@Override
public Ellipse2D copy() {
return new Ellipse2D(x, y, width, height);
}
@Override
public int hashCode() {
int bits = java.lang.Float.floatToIntBits(x);
bits += java.lang.Float.floatToIntBits(y) * 37;
bits += java.lang.Float.floatToIntBits(width) * 43;
bits += java.lang.Float.floatToIntBits(height) * 47;
return bits;
}
@Override
public boolean equals(Object obj) {
if (obj == this) {
return true;
}
if (obj instanceof Ellipse2D) {
Ellipse2D e2d = (Ellipse2D) obj;
return ((x == e2d.x) &&
(y == e2d.y) &&
(width == e2d.width) &&
(height == e2d.height));
}
return false;
}
}
