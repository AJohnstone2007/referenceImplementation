package javafx.geometry;
import javafx.animation.Interpolatable;
import javafx.beans.NamedArg;
public class Point3D implements Interpolatable<Point3D> {
public static final Point3D ZERO = new Point3D(0.0, 0.0, 0.0);
private final double x;
public final double getX() {
return x;
}
private final double y;
public final double getY() {
return y;
}
private final double z;
public final double getZ() {
return z;
}
private int hash = 0;
public Point3D(@NamedArg("x") double x, @NamedArg("y") double y, @NamedArg("z") double z) {
this.x = x;
this.y = y;
this.z = z;
}
public double distance(double x1, double y1, double z1) {
double a = getX() - x1;
double b = getY() - y1;
double c = getZ() - z1;
return Math.sqrt(a * a + b * b + c * c);
}
public double distance(Point3D point) {
return distance(point.getX(), point.getY(), point.getZ());
}
public Point3D add(double x, double y, double z) {
return new Point3D(
getX() + x,
getY() + y,
getZ() + z);
}
public Point3D add(Point3D point) {
return add(point.getX(), point.getY(), point.getZ());
}
public Point3D subtract(double x, double y, double z) {
return new Point3D(
getX() - x,
getY() - y,
getZ() - z);
}
public Point3D subtract(Point3D point) {
return subtract(point.getX(), point.getY(), point.getZ());
}
public Point3D multiply(double factor) {
return new Point3D(getX() * factor, getY() * factor, getZ() * factor);
}
public Point3D normalize() {
final double mag = magnitude();
if (mag == 0.0) {
return new Point3D(0.0, 0.0, 0.0);
}
return new Point3D(
getX() / mag,
getY() / mag,
getZ() / mag);
}
public Point3D midpoint(double x, double y, double z) {
return new Point3D(
x + (getX() - x) / 2.0,
y + (getY() - y) / 2.0,
z + (getZ() - z) / 2.0);
}
public Point3D midpoint(Point3D point) {
return midpoint(point.getX(), point.getY(), point.getZ());
}
public double angle(double x, double y, double z) {
final double ax = getX();
final double ay = getY();
final double az = getZ();
final double delta = (ax * x + ay * y + az * z) / Math.sqrt(
(ax * ax + ay * ay + az * az) * (x * x + y * y + z * z));
if (delta > 1.0) {
return 0.0;
}
if (delta < -1.0) {
return 180.0;
}
return Math.toDegrees(Math.acos(delta));
}
public double angle(Point3D point) {
return angle(point.getX(), point.getY(), point.getZ());
}
public double angle(Point3D p1, Point3D p2) {
final double x = getX();
final double y = getY();
final double z = getZ();
final double ax = p1.getX() - x;
final double ay = p1.getY() - y;
final double az = p1.getZ() - z;
final double bx = p2.getX() - x;
final double by = p2.getY() - y;
final double bz = p2.getZ() - z;
final double delta = (ax * bx + ay * by + az * bz) / Math.sqrt(
(ax * ax + ay * ay + az * az) * (bx * bx + by * by + bz * bz));
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
final double z = getZ();
return Math.sqrt(x * x + y * y + z * z);
}
public double dotProduct(double x, double y, double z) {
return getX() * x + getY() * y + getZ() * z;
}
public double dotProduct(Point3D vector) {
return dotProduct(vector.getX(), vector.getY(), vector.getZ());
}
public Point3D crossProduct(double x, double y, double z) {
final double ax = getX();
final double ay = getY();
final double az = getZ();
return new Point3D(
ay * z - az * y,
az * x - ax * z,
ax * y - ay * x);
}
public Point3D crossProduct(Point3D vector) {
return crossProduct(vector.getX(), vector.getY(), vector.getZ());
}
@Override
public Point3D interpolate(Point3D endValue, double t) {
if (t <= 0.0) return this;
if (t >= 1.0) return endValue;
return new Point3D(
getX() + (endValue.getX() - getX()) * t,
getY() + (endValue.getY() - getY()) * t,
getZ() + (endValue.getZ() - getZ()) * t
);
}
@Override public boolean equals(Object obj) {
if (obj == this) return true;
if (obj instanceof Point3D) {
Point3D other = (Point3D) obj;
return getX() == other.getX() && getY() == other.getY() && getZ() == other.getZ();
} else return false;
}
@Override public int hashCode() {
if (hash == 0) {
long bits = 7L;
bits = 31L * bits + Double.doubleToLongBits(getX());
bits = 31L * bits + Double.doubleToLongBits(getY());
bits = 31L * bits + Double.doubleToLongBits(getZ());
hash = (int) (bits ^ (bits >> 32));
}
return hash;
}
@Override public String toString() {
return "Point3D [x = " + getX() + ", y = " + getY() + ", z = " + getZ() + "]";
}
}
