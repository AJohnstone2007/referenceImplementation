package com.sun.prism.image;
import com.sun.prism.Graphics;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
public class CompoundCoords {
private int xImg0, xImg1;
private int yImg0, yImg1;
private Coords tileCoords[];
public CompoundCoords(CompoundImage t, Coords crd) {
int xImg0 = find1(fastFloor(crd.u0), t.uSubdivision);
int xImg1 = find2(fastCeil(crd.u1), t.uSubdivision);
int yImg0 = find1(fastFloor(crd.v0), t.vSubdivision);
int yImg1 = find2(fastCeil(crd.v1), t.vSubdivision);
if (xImg0 < 0 || xImg1 < 0 || yImg0 < 0 || yImg1 < 0) return;
this.xImg0 = xImg0; this.xImg1 = xImg1;
this.yImg0 = yImg0; this.yImg1 = yImg1;
tileCoords = new Coords[(xImg1 - xImg0 + 1) * (yImg1 - yImg0 + 1)];
float xMedian[] = new float[xImg1-xImg0];
float yMedian[] = new float[yImg1-yImg0];
for (int x = xImg0; x < xImg1; ++x) {
xMedian[x - xImg0] = crd.getX(t.uSubdivision[x + 1]);
}
for (int y = yImg0; y < yImg1; ++y) {
yMedian[y - yImg0] = crd.getY(t.vSubdivision[y + 1]);
}
int idx = 0;
for (int y = yImg0; y <= yImg1; ++y) {
float v0 = (y == yImg0 ? crd.v0 : t.vSubdivision[y]) - t.v0[y];
float v1 = (y == yImg1 ? crd.v1 : t.vSubdivision[y + 1]) - t.v0[y];
float y0 = y == yImg0 ? crd.y0 : yMedian[y - yImg0 - 1];
float y1 = y == yImg1 ? crd.y1 : yMedian[y - yImg0];
for (int x = xImg0; x <= xImg1; ++x) {
Coords segment = new Coords();
segment.v0 = v0;
segment.v1 = v1;
segment.y0 = y0;
segment.y1 = y1;
segment.u0 = (x == xImg0 ? crd.u0 : t.uSubdivision[x]) - t.u0[x];
segment.u1 = (x == xImg1 ? crd.u1 : t.uSubdivision[x + 1]) - t.u0[x];
segment.x0 = x == xImg0 ? crd.x0 : xMedian[x - xImg0-1];
segment.x1 = x == xImg1 ? crd.x1 : xMedian[x - xImg0];
tileCoords[idx++] = segment;
}
}
}
public void draw(Graphics g, CompoundImage t, float xS, float yS) {
if (tileCoords == null) return;
ResourceFactory factory = g.getResourceFactory();
int idx = 0;
for (int y = yImg0; y <= yImg1; ++y) {
for (int x = xImg0; x <= xImg1; ++x) {
Texture tex = t.getTile(x, y, factory);
tileCoords[idx++].draw(tex, g, xS, yS);
tex.unlock();
}
}
}
private static int find1(int x, int array[]) {
for (int i = 0; i < array.length - 1; ++i) {
if (array[i] <= x && x < array[i + 1]) {
return i;
}
}
return -1;
}
private static int find2(int x, int array[]) {
for (int i = 0; i < array.length - 1; ++i) {
if (array[i] < x && x <= array[i + 1]) {
return i;
}
}
return -1;
}
private static int fastFloor(float x) {
int ix = (int) x;
return (ix <= x) ? ix : ix - 1;
}
private static int fastCeil(float x) {
int ix = (int) x;
return (ix >= x) ? ix : ix + 1;
}
}
