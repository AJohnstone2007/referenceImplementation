package com.sun.javafx.geom;
public abstract class BaseBounds {
public static enum BoundsType {
RECTANGLE,
BOX,
}
BaseBounds() { }
public abstract BaseBounds copy();
public abstract boolean is2D();
public abstract BoundsType getBoundsType();
public abstract float getWidth();
public abstract float getHeight();
public abstract float getDepth();
public abstract float getMinX();
public abstract float getMinY();
public abstract float getMinZ();
public abstract float getMaxX();
public abstract float getMaxY();
public abstract float getMaxZ();
public abstract void translate(float x, float y, float z);
public abstract Vec2f getMin(Vec2f min);
public abstract Vec2f getMax(Vec2f max);
public abstract Vec3f getMin(Vec3f min);
public abstract Vec3f getMax(Vec3f max);
public abstract BaseBounds deriveWithUnion(BaseBounds other);
public abstract BaseBounds deriveWithNewBounds(Rectangle other);
public abstract BaseBounds deriveWithNewBounds(BaseBounds other);
public abstract BaseBounds deriveWithNewBounds(float minX, float minY, float minZ,
float maxX, float maxY, float maxZ);
public abstract BaseBounds deriveWithNewBoundsAndSort(float minX, float minY, float minZ,
float maxX, float maxY, float maxZ);
public abstract BaseBounds deriveWithPadding(float h, float v, float d);
public abstract void intersectWith(Rectangle other);
public abstract void intersectWith(BaseBounds other);
public abstract void intersectWith(float minX, float minY, float minZ,
float maxX, float maxY, float maxZ);
public abstract void setBoundsAndSort(Point2D p1, Point2D p2);
public abstract void setBoundsAndSort(float minX, float minY, float minZ,
float maxX, float maxY, float maxZ);
public abstract void add(Point2D p);
public abstract void add(float x, float y, float z);
public abstract boolean contains(Point2D p);
public abstract boolean contains(float x, float y);
public abstract boolean intersects(float x, float y, float width, float height);
public abstract boolean isEmpty();
public abstract void roundOut();
public abstract RectBounds flattenInto(RectBounds bounds);
public abstract BaseBounds makeEmpty();
public abstract boolean disjoint(float x, float y, float width, float height);
protected abstract void sortMinMax();
public static BaseBounds getInstance(float minX, float minY, float minZ,
float maxX, float maxY, float maxZ) {
if (minZ == 0 && maxZ == 0) {
return getInstance(minX, minY, maxX, maxY);
} else {
return new BoxBounds(minX, minY, minZ, maxX, maxY, maxZ);
}
}
public static BaseBounds getInstance(float minX, float minY,
float maxX, float maxY) {
return new RectBounds(minX, minY, maxX, maxY);
}
}
