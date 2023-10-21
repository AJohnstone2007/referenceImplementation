package test.com.sun.prism.impl;
public class Vec3f {
public float x;
public float y;
public float z;
public Vec3f() { }
public Vec3f(float x, float y, float z) {
this.x = x;
this.y = y;
this.z = z;
}
public Vec3f(Vec3f v) {
this.x = v.x;
this.y = v.y;
this.z = v.z;
}
public void set(Vec3f v) {
this.x = v.x;
this.y = v.y;
this.z = v.z;
}
public void set(float x, float y, float z) {
this.x = x;
this.y = y;
this.z = z;
}
public final void mul(float s) {
this.x *= s;
this.y *= s;
this.z *= s;
}
public void sub(Vec3f t1, Vec3f t2) {
this.x = t1.x - t2.x;
this.y = t1.y - t2.y;
this.z = t1.z - t2.z;
}
public void sub(Vec3f t1) {
this.x -= t1.x;
this.y -= t1.y;
this.z -= t1.z;
}
public void add(Vec3f t1, Vec3f t2) {
this.x = t1.x + t2.x;
this.y = t1.y + t2.y;
this.z = t1.z + t2.z;
}
public void add(Vec3f t1) {
this.x += t1.x;
this.y += t1.y;
this.z += t1.z;
}
public float length() {
return (float) Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z);
}
public void normalize() {
float norm = 1.0f / length();
this.x = this.x * norm;
this.y = this.y * norm;
this.z = this.z * norm;
}
public void cross(Vec3f v1, Vec3f v2) {
float tmpX;
float tmpY;
tmpX = v1.y * v2.z - v1.z * v2.y;
tmpY = v2.x * v1.z - v2.z * v1.x;
this.z = v1.x * v2.y - v1.y * v2.x;
this.x = tmpX;
this.y = tmpY;
}
public float dot(Vec3f v1) {
return this.x * v1.x + this.y * v1.y + this.z * v1.z;
}
@Override
public int hashCode() {
int bits = 7;
bits = 31 * bits + Float.floatToIntBits(x);
bits = 31 * bits + Float.floatToIntBits(y);
bits = 31 * bits + Float.floatToIntBits(z);
return bits;
}
@Override
public boolean equals(Object obj) {
if (obj == this) {
return true;
}
if (obj instanceof Vec3f) {
Vec3f v = (Vec3f) obj;
return (x == v.x) && (y == v.y) && (z == v.z);
}
return false;
}
@Override
public String toString() {
return "Vec3f[" + x + ", " + y + ", " + z + "]";
}
}
