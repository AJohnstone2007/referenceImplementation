package com.sun.javafx.geom;
import com.sun.javafx.geom.transform.BaseTransform;
public abstract class RectangularShape extends Shape {
protected RectangularShape() { }
public abstract float getX();
public abstract float getY();
public abstract float getWidth();
public abstract float getHeight();
public float getMinX() {
return getX();
}
public float getMinY() {
return getY();
}
public float getMaxX() {
return getX() + getWidth();
}
public float getMaxY() {
return getY() + getHeight();
}
public float getCenterX() {
return getX() + getWidth() / 2f;
}
public float getCenterY() {
return getY() + getHeight() / 2f;
}
public abstract boolean isEmpty();
public abstract void setFrame(float x, float y, float w, float h);
public void setFrame(Point2D loc, Dimension2D size) {
setFrame(loc.x, loc.y, size.width, size.height);
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
public void setFrameFromDiagonal(Point2D p1, Point2D p2) {
setFrameFromDiagonal(p1.x, p1.y, p2.x, p2.y);
}
public void setFrameFromCenter(float centerX, float centerY,
float cornerX, float cornerY)
{
float halfW = Math.abs(cornerX - centerX);
float halfH = Math.abs(cornerY - centerY);
setFrame(centerX - halfW, centerY - halfH, halfW * 2f, halfH * 2f);
}
public void setFrameFromCenter(Point2D center, Point2D corner) {
setFrameFromCenter(center.x, center.y, corner.x, corner.y);
}
public boolean contains(Point2D p) {
return contains(p.x, p.y);
}
public RectBounds getBounds() {
float width = getWidth();
float height = getHeight();
if (width < 0 || height < 0) {
return new RectBounds();
}
float x = getX();
float y = getY();
float x1 = (float)Math.floor(x);
float y1 = (float)Math.floor(y);
float x2 = (float)Math.ceil(x + width);
float y2 = (float)Math.ceil(y + height);
return new RectBounds(x1, y1, x2, y2);
}
public PathIterator getPathIterator(BaseTransform tx, float flatness) {
return new FlatteningPathIterator(getPathIterator(tx), flatness);
}
@Override
public String toString() {
return getClass().getName() +
"[x=" + getX() +
",y=" + getY() +
",w=" + getWidth() +
",h=" + getHeight() + "]";
}
}
