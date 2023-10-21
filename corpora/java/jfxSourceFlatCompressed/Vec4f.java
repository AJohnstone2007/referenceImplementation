package com.sun.javafx.geom;
public class Vec4f {
public float x;
public float y;
public float z;
public float w;
public Vec4f() { }
public Vec4f(Vec4f v) {
this.x = v.x;
this.y = v.y;
this.z = v.z;
this.w = v.w;
}
public Vec4f(float x, float y, float z, float w) {
this.x = x;
this.y = y;
this.z = z;
this.w = w;
}
public void set(Vec4f v) {
this.x = v.x;
this.y = v.y;
this.z = v.z;
this.w = v.w;
}
@Override
public String toString() {
return "(" + x + ", " + y + ", " + z + ", " + w + ")";
}
}
