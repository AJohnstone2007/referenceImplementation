package com.sun.javafx.geom;
public class Vec2f {
public float x;
public float y;
public Vec2f() { }
public Vec2f(float x, float y) {
this.x = x;
this.y = y;
}
public Vec2f(Vec2f v) {
this.x = v.x;
this.y = v.y;
}
public void set(Vec2f v) {
this.x = v.x;
this.y = v.y;
}
public void set(float x, float y) {
this.x = x;
this.y = y;
}
public static float distanceSq(float x1, float y1, float x2, float y2) {
x1 -= x2;
y1 -= y2;
return (x1 * x1 + y1 * y1);
}
public static float distance(float x1, float y1, float x2, float y2) {
x1 -= x2;
y1 -= y2;
return (float) Math.sqrt(x1 * x1 + y1 * y1);
}
public float distanceSq(float vx, float vy) {
vx -= x;
vy -= y;
return (vx * vx + vy * vy);
}
public float distanceSq(Vec2f v) {
float vx = v.x - this.x;
float vy = v.y - this.y;
return (vx * vx + vy * vy);
}
public float distance(float vx, float vy) {
vx -= x;
vy -= y;
return (float) Math.sqrt(vx * vx + vy * vy);
}
public float distance(Vec2f v) {
float vx = v.x - this.x;
float vy = v.y - this.y;
return (float) Math.sqrt(vx * vx + vy * vy);
}
@Override
public int hashCode() {
int bits = 7;
bits = 31 * bits + java.lang.Float.floatToIntBits(x);
bits = 31 * bits + java.lang.Float.floatToIntBits(y);
return bits;
}
@Override
public boolean equals(Object obj) {
if (obj == this) return true;
if (obj instanceof Vec2f) {
Vec2f v = (Vec2f) obj;
return (x == v.x) && (y == v.y);
}
return false;
}
@Override
public String toString() {
return "Vec2f[" + x + ", " + y + "]";
}
}
