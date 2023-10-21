package com.sun.prism.image;
import com.sun.prism.Graphics;
import com.sun.prism.Texture;
public class Coords {
float x0, y0, x1, y1;
float u0, v0, u1, v1;
public Coords(float w, float h, ViewPort v) {
x0 = 0; x1 = w;
y0 = 0; y1 = h;
u0 = v.u0; u1 = v.u1;
v0 = v.v0; v1 = v.v1;
}
public Coords() {
}
public void draw(Texture t, Graphics g, float x, float y) {
g.drawTexture(t,
x + x0, y + y0, x + x1, y + y1,
u0, v0, u1, v1);
}
public float getX(float u) {
return (x0 * (u1 - u) + x1 * (u - u0)) / (u1 - u0);
}
public float getY(float v) {
return (y0 * (v1 - v) + y1 * (v - v0)) / (v1 - v0);
}
}
