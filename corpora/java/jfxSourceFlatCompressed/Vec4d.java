package com.sun.javafx.geom;
public class Vec4d {
public double x;
public double y;
public double z;
public double w;
public Vec4d() { }
public Vec4d(Vec4d v) {
this.x = v.x;
this.y = v.y;
this.z = v.z;
this.w = v.w;
}
public Vec4d(double x, double y, double z, double w) {
this.x = x;
this.y = y;
this.z = z;
this.w = w;
}
public void set(Vec4d v) {
this.x = v.x;
this.y = v.y;
this.z = v.z;
this.w = v.w;
}
}
