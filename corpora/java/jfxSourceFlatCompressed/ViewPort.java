package com.sun.prism.image;
public class ViewPort {
public float u0, v0, u1, v1;
public ViewPort(float u, float v, float du, float dv) {
u0 = u; u1 = u + du;
v0 = v; v1 = v + dv;
}
public ViewPort getScaledVersion(float pixelScale) {
if (pixelScale == 1.0f) {
return this;
}
float newu0 = u0 * pixelScale;
float newv0 = v0 * pixelScale;
float newu1 = u1 * pixelScale;
float newv1 = v1 * pixelScale;
return new ViewPort(newu0, newv0, newu1 - newu0, newv1 - newv0);
}
public float getRelX(float u) { return (u - u0) / (u1 - u0); }
public float getRelY(float v) { return (v - v0) / (v1 - v0); }
public Coords getClippedCoords(float iw, float ih, float w, float h) {
Coords cr = new Coords(w, h, this);
if (u1 > iw || u0 < 0) {
if (u0 >= iw || u1 <= 0) return null;
if (u1 > iw) {
cr.x1 = w * getRelX(iw);
cr.u1 = iw;
}
if (u0 < 0) {
cr.x0 = w * getRelX(0);
cr.u0 = 0;
}
}
if (v1 > ih || v0 < 0) {
if (v0 >= ih || v1 <= 0) return null;
if (v1 > ih) {
cr.y1 = h * getRelY(ih);
cr.v1 = ih;
}
if (v0 < 0) {
cr.y0 = h * getRelY(0);
cr.v0 = 0;
}
}
return cr;
}
}
