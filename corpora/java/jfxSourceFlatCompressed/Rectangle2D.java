package javafx.geometry;
import javafx.beans.NamedArg;
public class Rectangle2D {
public static final Rectangle2D EMPTY = new Rectangle2D(0, 0, 0, 0);
public double getMinX() { return minX; }
private double minX;
public double getMinY() { return minY; }
private double minY;
public double getWidth() { return width; }
private double width;
public double getHeight() { return height; }
private double height;
public double getMaxX() { return maxX; }
private double maxX;
public double getMaxY() { return maxY; }
private double maxY;
private int hash = 0;
public Rectangle2D(@NamedArg("minX") double minX, @NamedArg("minY") double minY, @NamedArg("width") double width, @NamedArg("height") double height) {
if (width < 0 || height < 0) {
throw new IllegalArgumentException("Both width and height must be >= 0");
}
this.minX = minX;
this.minY = minY;
this.width = width;
this.height = height;
this.maxX = minX + width;
this.maxY = minY + height;
}
public boolean contains(Point2D p) {
if (p == null) return false;
return contains(p.getX(), p.getY());
}
public boolean contains(double x, double y) {
return x >= minX && x <= maxX && y >= minY && y <= maxY;
}
public boolean contains(Rectangle2D r) {
if (r == null) return false;
return r.minX >= minX && r.minY >= minY && r.maxX <= maxX && r.maxY <= maxY;
}
public boolean contains(double x, double y, double w, double h) {
return x >= minX && y >= minY && w <= maxX - x && h <= maxY - y;
}
public boolean intersects(Rectangle2D r) {
if (r == null) return false;
return r.maxX > minX && r.maxY > minY && r.minX < maxX && r.minY < maxY;
}
public boolean intersects(double x, double y, double w, double h) {
return x < maxX && y < maxY && x + w > minX && y + h > minY;
}
@Override public boolean equals(Object obj) {
if (obj == this) return true;
if (obj instanceof Rectangle2D) {
Rectangle2D other = (Rectangle2D) obj;
return minX == other.minX
&& minY == other.minY
&& width == other.width
&& height == other.height;
} else return false;
}
@Override public int hashCode() {
if (hash == 0) {
long bits = 7L;
bits = 31L * bits + Double.doubleToLongBits(minX);
bits = 31L * bits + Double.doubleToLongBits(minY);
bits = 31L * bits + Double.doubleToLongBits(width);
bits = 31L * bits + Double.doubleToLongBits(height);
hash = (int) (bits ^ (bits >> 32));
}
return hash;
}
@Override public String toString() {
return "Rectangle2D [minX = " + minX
+ ", minY=" + minY
+ ", maxX=" + maxX
+ ", maxY=" + maxY
+ ", width=" + width
+ ", height=" + height
+ "]";
}
}
