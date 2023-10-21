package javafx.geometry;
import javafx.beans.NamedArg;
public class BoundingBox extends Bounds {
private int hash = 0;
public BoundingBox(@NamedArg("minX") double minX, @NamedArg("minY") double minY, @NamedArg("minZ") double minZ, @NamedArg("width") double width, @NamedArg("height") double height, @NamedArg("depth") double depth) {
super(minX, minY, minZ, width, height, depth);
}
public BoundingBox(@NamedArg("minX") double minX, @NamedArg("minY") double minY, @NamedArg("width") double width, @NamedArg("height") double height) {
super(minX, minY, 0, width, height, 0);
}
@Override
public boolean isEmpty() {
return getMaxX() < getMinX() || getMaxY() < getMinY() || getMaxZ() < getMinZ();
}
@Override public boolean contains(Point2D p) {
if (p == null) return false;
return contains(p.getX(), p.getY(), 0.0f);
}
@Override public boolean contains(Point3D p) {
if (p == null) return false;
return contains(p.getX(), p.getY(), p.getZ());
}
@Override public boolean contains(double x, double y) {
return contains(x, y, 0.0f);
}
@Override public boolean contains(double x, double y, double z) {
if (isEmpty()) return false;
return x >= getMinX() && x <= getMaxX() && y >= getMinY() && y <= getMaxY()
&& z >= getMinZ() && z <= getMaxZ();
}
@Override public boolean contains(Bounds b) {
if ((b == null) || b.isEmpty()) return false;
return contains(b.getMinX(), b.getMinY(), b.getMinZ(),
b.getWidth(), b.getHeight(), b.getDepth());
}
@Override public boolean contains(double x, double y, double w, double h) {
return contains(x, y) && contains(x + w, y + h);
}
@Override public boolean contains(double x, double y, double z,
double w, double h, double d) {
return contains(x, y, z) && contains(x + w, y + h, z + d);
}
@Override public boolean intersects(Bounds b) {
if ((b == null) || b.isEmpty()) return false;
return intersects(b.getMinX(), b.getMinY(), b.getMinZ(),
b.getWidth(), b.getHeight(), b.getDepth());
}
@Override public boolean intersects(double x, double y, double w, double h) {
return intersects(x, y, 0, w, h, 0);
}
@Override public boolean intersects(double x, double y, double z,
double w, double h, double d) {
if (isEmpty() || w < 0 || h < 0 || d < 0) return false;
return (x + w >= getMinX() &&
y + h >= getMinY() &&
z + d >= getMinZ() &&
x <= getMaxX() &&
y <= getMaxY() &&
z <= getMaxZ());
}
@Override public boolean equals(Object obj) {
if (obj == this) return true;
if (obj instanceof BoundingBox) {
BoundingBox other = (BoundingBox) obj;
return getMinX() == other.getMinX()
&& getMinY() == other.getMinY()
&& getMinZ() == other.getMinZ()
&& getWidth() == other.getWidth()
&& getHeight() == other.getHeight()
&& getDepth() == other.getDepth();
} else return false;
}
@Override public int hashCode() {
if (hash == 0) {
long bits = 7L;
bits = 31L * bits + Double.doubleToLongBits(getMinX());
bits = 31L * bits + Double.doubleToLongBits(getMinY());
bits = 31L * bits + Double.doubleToLongBits(getMinZ());
bits = 31L * bits + Double.doubleToLongBits(getWidth());
bits = 31L * bits + Double.doubleToLongBits(getHeight());
bits = 31L * bits + Double.doubleToLongBits(getDepth());
hash = (int) (bits ^ (bits >> 32));
}
return hash;
}
@Override public String toString() {
return "BoundingBox ["
+ "minX:" + getMinX()
+ ", minY:" + getMinY()
+ ", minZ:" + getMinZ()
+ ", width:" + getWidth()
+ ", height:" + getHeight()
+ ", depth:" + getDepth()
+ ", maxX:" + getMaxX()
+ ", maxY:" + getMaxY()
+ ", maxZ:" + getMaxZ()
+ "]";
}
}
