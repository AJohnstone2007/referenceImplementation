package javafx.geometry;
import javafx.animation.Interpolatable;
import javafx.beans.NamedArg;
public class Point2D implements Interpolatable<Point2D> {
public static final Point2D ZERO = new Point2D(0.0, 0.0);
private final double x;
public final double getX() {
return x;
}
private final double y;
public final double getY() {
return y;
}
private int hash = 0;
public Point2D(@NamedArg("x") double x, @NamedArg("y") double y) {
this.x = x;
this.y = y;
}
public double distance(double x1, double y1) {
double a = getX() - x1;
double b = getY() - y1;
return Math.sqrt(a * a + b * b);
}
public double distance(Point2D point) {
return distance(point.getX(), point.getY());
}
public Point2D add(double x, double y) {
return new Point2D(
getX() + x,
getY() + y);
}
public Point2D add(Point2D point) {
return add(point.getX(), point.getY());
}
public Point2D subtract(double x, double y) {
return new Point2D(
getX() - x,
getY() - y);
}
public Point2D multiply(double factor) {
return new Point2D(getX() * factor, getY() * factor);
}
public Point2D subtract(Point2D point) {
return subtract(point.getX(), point.getY());
}
public Point2D normalize() {
final double mag = magnitude();
if (mag == 0.0) {
return new Point2D(0.0, 0.0);
}
return new Point2D(
getX() / mag,
getY() / mag);
}
public Point2D midpoint(double x, double y) {
return new Point2D(
x + (getX() - x) / 2.0,
y + (getY() - y) / 2.0);
}
public Point2D midpoint(Point2D point) {
return midpoint(point.getX(), point.getY());
}
public double angle(double x, double y) {
final double ax = getX();
final double ay = getY();
final double delta = (ax * x + ay * y) / Math.sqrt(
(ax * ax + ay * ay) * (x * x + y * y));
if (delta > 1.0) {
return 0.0;
}
if (delta < -1.0) {
return 180.0;
}
return Math.toDegrees(Math.acos(delta));
}
public double angle(Point2D point) {
return angle(point.getX(), point.getY());
}
public double angle(Point2D p1, Point2D p2) {
final double x = getX();
final double y = getY();
final double ax = p1.getX() - x;
final double ay = p1.getY() - y;
final double bx = p2.getX() - x;
final double by = p2.getY() - y;
final double delta = (ax * bx + ay * by) / Math.sqrt(
(ax * ax + ay * ay) * (bx * bx + by * by));
if (delta > 1.0) {
return 0.0;
}
if (delta < -1.0) {
return 180.0;
}
return Math.toDegrees(Math.acos(delta));
}
public double magnitude() {
final double x = getX();
final double y = getY();
return Math.sqrt(x * x + y * y);
}
public double dotProduct(double x, double y) {
return getX() * x + getY() * y;
}
public double dotProduct(Point2D vector) {
return dotProduct(vector.getX(), vector.getY());
}
public Point3D crossProduct(double x, double y) {
final double ax = getX();
final double ay = getY();
return new Point3D(
0, 0, ax * y - ay * x);
}
public Point3D crossProduct(Point2D vector) {
return crossProduct(vector.getX(), vector.getY());
}
@Override
public Point2D interpolate(Point2D endValue, double t) {
if (t <= 0.0) return this;
if (t >= 1.0) return endValue;
return new Point2D(
getX() + (endValue.getX() - getX()) * t,
getY() + (endValue.getY() - getY()) * t
);
}
@Override public boolean equals(Object obj) {
if (obj == this) return true;
if (obj instanceof Point2D) {
Point2D other = (Point2D) obj;
return getX() == other.getX() && getY() == other.getY();
} else return false;
}
@Override public int hashCode() {
if (hash == 0) {
long bits = 7L;
bits = 31L * bits + Double.doubleToLongBits(getX());
bits = 31L * bits + Double.doubleToLongBits(getY());
hash = (int) (bits ^ (bits >> 32));
}
return hash;
}
@Override public String toString() {
return "Point2D [x = " + getX() + ", y = " + getY() + "]";
}
}
