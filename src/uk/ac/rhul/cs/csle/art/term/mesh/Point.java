package uk.ac.rhul.cs.csle.art.term.mesh;

public class Point {
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(z);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Point other = (Point) obj;
    if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) return false;
    if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) return false;
    if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z)) return false;
    return true;
  }

  public final double x;
  public final double y;
  public final double z;

  @Override
  public String toString() {
    return x + "," + y + "," + z;
  }

  Point(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Point(Point v) {
    this.x = v.x;
    this.y = v.y;
    this.z = v.z;
  }

  @Override
  public Point clone() {
    return new Point(x, y, z);
  }

  public Point negated() {
    return new Point(-x, -y, -z);
  }

  public Point plus(Point a) {
    return new Point(x + a.x, y + a.y, z + a.z);
  }

  public Point minus(Point a) {
    return new Point(x - a.x, y - a.y, z - a.z);
  }

  public Point times(double a) {
    return new Point(x * a, y * a, z * a);
  }

  public Point dividedBy(double a) {
    return new Point(x / a, y / a, z / a);
  }

  public double dot(Point a) {
    return x * a.x + y * a.y + z * a.z;
  }

  public Point lerp(Point a, double t) {
    return plus(a.minus(this).times(t));
  }

  public double length() {
    return Math.sqrt(dot(this));
  }

  public Point unit() {
    return dividedBy(length());
  }

  public Point cross(Point a) {
    return new Point(y * a.z - z * a.y, z * a.x - x * a.z, x * a.y - y * a.x);
  }
}
