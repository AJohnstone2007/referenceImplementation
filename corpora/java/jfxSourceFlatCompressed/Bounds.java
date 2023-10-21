package javafx.geometry;
public abstract class Bounds {
public final double getMinX() { return minX; }
private double minX;
public final double getMinY() { return minY; }
private double minY;
public final double getMinZ() { return minZ; }
private double minZ;
public final double getWidth() { return width; }
private double width;
public final double getHeight() { return height; }
private double height;
public final double getDepth() { return depth; }
private double depth;
public final double getMaxX() { return maxX; }
private double maxX;
public final double getMaxY() { return maxY; }
private double maxY;
public final double getMaxZ() { return maxZ; }
private double maxZ;
public final double getCenterX() {
return (getMaxX() + getMinX()) * 0.5;
}
public final double getCenterY() {
return (getMaxY() + getMinY()) * 0.5;
}
public final double getCenterZ() {
return (getMaxZ() + getMinZ()) * 0.5;
}
public abstract boolean isEmpty();
public abstract boolean contains(Point2D p);
public abstract boolean contains(Point3D p);
public abstract boolean contains(double x, double y);
public abstract boolean contains(double x, double y, double z);
public abstract boolean contains(Bounds b);
public abstract boolean contains(double x, double y, double w, double h);
public abstract boolean contains(double x, double y, double z,
double w, double h, double d);
public abstract boolean intersects(Bounds b);
public abstract boolean intersects(double x, double y, double w, double h);
public abstract boolean intersects(double x, double y, double z,
double w, double h, double d);
protected Bounds(double minX, double minY, double minZ, double width, double height, double depth) {
this.minX = minX;
this.minY = minY;
this.minZ = minZ;
this.width = width;
this.height = height;
this.depth = depth;
this.maxX = minX + width;
this.maxY = minY + height;
this.maxZ = minZ + depth;
}
}
