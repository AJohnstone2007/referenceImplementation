package com.sun.javafx.geom;
public class Quat4f {
final static double EPS2 = 1.0e-30;
public float x;
public float y;
public float z;
public float w;
public Quat4f()
{
this.x = 0.0f;
this.y = 0.0f;
this.z = 0.0f;
this.w = 0.0f;
}
public Quat4f(float x, float y, float z, float w) {
float mag;
mag = (float) (1.0 / Math.sqrt(x * x + y * y + z * z + w * w));
this.x = x * mag;
this.y = y * mag;
this.z = z * mag;
this.w = w * mag;
}
public Quat4f(float[] q) {
float mag;
mag = (float) (1.0 / Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]));
x = q[0] * mag;
y = q[1] * mag;
z = q[2] * mag;
w = q[3] * mag;
}
public Quat4f(Quat4f q1) {
this.x = q1.x;
this.y = q1.y;
this.z = q1.z;
this.w = q1.w;
}
public final void normalize() {
float norm;
norm = (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
if (norm > 0.0f) {
norm = 1.0f / (float) Math.sqrt(norm);
this.x *= norm;
this.y *= norm;
this.z *= norm;
this.w *= norm;
} else {
this.x = (float) 0.0;
this.y = (float) 0.0;
this.z = (float) 0.0;
this.w = (float) 0.0;
}
}
public final void set(Matrix3f m1) {
float ww = 0.25f * (m1.m00 + m1.m11 + m1.m22 + 1.0f);
if (ww >= 0) {
if (ww >= EPS2) {
this.w = (float) Math.sqrt((double) ww);
ww = 0.25f / this.w;
this.x = (m1.m21 - m1.m12) * ww;
this.y = (m1.m02 - m1.m20) * ww;
this.z = (m1.m10 - m1.m01) * ww;
return;
}
} else {
this.w = 0;
this.x = 0;
this.y = 0;
this.z = 1;
return;
}
this.w = 0;
ww = -0.5f * (m1.m11 + m1.m22);
if (ww >= 0) {
if (ww >= EPS2) {
this.x = (float) Math.sqrt((double) ww);
ww = 0.5f / this.x;
this.y = m1.m10 * ww;
this.z = m1.m20 * ww;
return;
}
} else {
this.x = 0;
this.y = 0;
this.z = 1;
return;
}
this.x = 0;
ww = 0.5f * (1.0f - m1.m22);
if (ww >= EPS2) {
this.y = (float) Math.sqrt((double) ww);
this.z = m1.m21 / (2.0f * this.y);
return;
}
this.y = 0;
this.z = 1;
}
public final void set(float m1[][]) {
float ww = 0.25f * (m1[0][0] + m1[1][1] + m1[2][2] + 1.0f);
if (ww >= 0) {
if (ww >= EPS2) {
this.w = (float) Math.sqrt((double) ww);
ww = 0.25f / this.w;
this.x = (m1[2][1] - m1[1][2]) * ww;
this.y = (m1[0][2] - m1[2][0]) * ww;
this.z = (m1[1][0] - m1[0][1]) * ww;
return;
}
} else {
this.w = 0;
this.x = 0;
this.y = 0;
this.z = 1;
return;
}
this.w = 0;
ww = -0.5f * (m1[1][1] + m1[2][2]);
if (ww >= 0) {
if (ww >= EPS2) {
this.x = (float) Math.sqrt((double) ww);
ww = 0.5f / this.x;
this.y = m1[1][0] * ww;
this.z = m1[2][0] * ww;
return;
}
} else {
this.x = 0;
this.y = 0;
this.z = 1;
return;
}
this.x = 0;
ww = 0.5f * (1.0f - m1[2][2]);
if (ww >= EPS2) {
this.y = (float) Math.sqrt((double) ww);
this.z = m1[2][1] / (2.0f * this.y);
return;
}
this.y = 0;
this.z = 1;
}
public final void scale(float s)
{
this.x *= s;
this.y *= s;
this.z *= s;
this.w *= s;
}
@Override
public String toString() {
return "Quat4f[" + x + ", " + y + ", " + z + ", " + w + "]";
}
}
