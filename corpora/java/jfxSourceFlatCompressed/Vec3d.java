package com.sun.javafx.geom;
public class Vec3d {
public double x;
public double y;
public double z;
public Vec3d() { }
public Vec3d(double x, double y, double z) {
this.x = x;
this.y = y;
this.z = z;
}
public Vec3d(Vec3d v) {
set(v);
}
public Vec3d(Vec3f v) {
set(v);
}
public void set(Vec3f v) {
this.x = v.x;
this.y = v.y;
this.z = v.z;
}
public void set(Vec3d v) {
this.x = v.x;
this.y = v.y;
this.z = v.z;
}
public void set(double x, double y, double z) {
this.x = x;
this.y = y;
this.z = z;
}
public void mul(double scale) {
x *= scale;
y *= scale;
z *= scale;
}
public void sub(Vec3f t1, Vec3f t2) {
this.x = t1.x - t2.x;
this.y = t1.y - t2.y;
this.z = t1.z - t2.z;
}
public void sub(Vec3d t1, Vec3d t2) {
this.x = t1.x - t2.x;
this.y = t1.y - t2.y;
this.z = t1.z - t2.z;
}
public void sub(Vec3d t1) {
this.x -= t1.x;
this.y -= t1.y;
this.z -= t1.z;
}
public void add(Vec3d t1, Vec3d t2) {
this.x = t1.x + t2.x;
this.y = t1.y + t2.y;
this.z = t1.z + t2.z;
}
public void add(Vec3d t1) {
this.x += t1.x;
this.y += t1.y;
this.z += t1.z;
}
public double length() {
return Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z);
}
public void normalize() {
double norm = 1.0 / length();
this.x = this.x * norm;
this.y = this.y * norm;
this.z = this.z * norm;
}
public void cross(Vec3d v1, Vec3d v2) {
double tmpX;
double tmpY;
tmpX = v1.y * v2.z - v1.z * v2.y;
tmpY = v2.x * v1.z - v2.z * v1.x;
this.z = v1.x * v2.y - v1.y * v2.x;
this.x = tmpX;
this.y = tmpY;
}
public double dot(Vec3d v1) {
return this.x * v1.x + this.y * v1.y + this.z * v1.z;
}
@Override
public int hashCode() {
long bits = 7L;
bits = 31L * bits + Double.doubleToLongBits(x);
bits = 31L * bits + Double.doubleToLongBits(y);
bits = 31L * bits + Double.doubleToLongBits(z);
return (int) (bits ^ (bits >> 32));
}
@Override
public boolean equals(Object obj) {
if (obj == this) {
return true;
}
if (obj instanceof Vec3d) {
Vec3d v = (Vec3d) obj;
return (x == v.x) && (y == v.y) && (z == v.z);
}
return false;
}
@Override
public String toString() {
return "Vec3d[" + x + ", " + y + ", " + z + "]";
}
}
