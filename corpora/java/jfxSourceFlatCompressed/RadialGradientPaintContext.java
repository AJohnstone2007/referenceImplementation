package com.sun.prism.j2d.paint;
import com.sun.prism.j2d.paint.MultipleGradientPaint.CycleMethod;
import com.sun.prism.j2d.paint.MultipleGradientPaint.ColorSpaceType;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
final class RadialGradientPaintContext extends MultipleGradientPaintContext {
private boolean isSimpleFocus = false;
private boolean isNonCyclic = false;
private float radius;
private float centerX, centerY, focusX, focusY;
private float radiusSq;
private float constA, constB;
private float gDeltaDelta;
private float trivial;
private static final float SCALEBACK = .99f;
RadialGradientPaintContext(RadialGradientPaint paint,
ColorModel cm,
Rectangle deviceBounds,
Rectangle2D userBounds,
AffineTransform t,
RenderingHints hints,
float cx, float cy,
float r,
float fx, float fy,
float[] fractions,
Color[] colors,
CycleMethod cycleMethod,
ColorSpaceType colorSpace)
{
super(paint, cm, deviceBounds, userBounds, t, hints,
fractions, colors, cycleMethod, colorSpace);
centerX = cx;
centerY = cy;
focusX = fx;
focusY = fy;
radius = r;
this.isSimpleFocus = (focusX == centerX) && (focusY == centerY);
this.isNonCyclic = (cycleMethod == CycleMethod.NO_CYCLE);
radiusSq = radius * radius;
float dX = focusX - centerX;
float dY = focusY - centerY;
double distSq = (dX * dX) + (dY * dY);
if (distSq > radiusSq * SCALEBACK) {
float scalefactor = (float)Math.sqrt(radiusSq * SCALEBACK / distSq);
dX = dX * scalefactor;
dY = dY * scalefactor;
focusX = centerX + dX;
focusY = centerY + dY;
}
trivial = (float)Math.sqrt(radiusSq - (dX * dX));
constA = a02 - centerX;
constB = a12 - centerY;
gDeltaDelta = 2 * ( a00 * a00 + a10 * a10) / radiusSq;
}
protected void fillRaster(int pixels[], int off, int adjust,
int x, int y, int w, int h)
{
if (isSimpleFocus && isNonCyclic && isSimpleLookup) {
simpleNonCyclicFillRaster(pixels, off, adjust, x, y, w, h);
} else {
cyclicCircularGradientFillRaster(pixels, off, adjust, x, y, w, h);
}
}
private void simpleNonCyclicFillRaster(int pixels[], int off, int adjust,
int x, int y, int w, int h)
{
float rowX = (a00*x) + (a01*y) + constA;
float rowY = (a10*x) + (a11*y) + constB;
float gDeltaDelta = this.gDeltaDelta;
adjust += w;
int rgbclip = gradient[fastGradientArraySize];
for (int j = 0; j < h; j++) {
float gRel = (rowX * rowX + rowY * rowY) / radiusSq;
float gDelta = (2 * ( a00 * rowX + a10 * rowY) / radiusSq +
gDeltaDelta/2);
int i = 0;
while (i < w && gRel >= 1.0f) {
pixels[off + i] = rgbclip;
gRel += gDelta;
gDelta += gDeltaDelta;
i++;
}
while (i < w && gRel < 1.0f) {
int gIndex;
if (gRel <= 0) {
gIndex = 0;
} else {
float fIndex = gRel * SQRT_LUT_SIZE;
int iIndex = (int) (fIndex);
float s0 = sqrtLut[iIndex];
float s1 = sqrtLut[iIndex+1] - s0;
fIndex = s0 + (fIndex - iIndex) * s1;
gIndex = (int) (fIndex * fastGradientArraySize);
}
pixels[off + i] = gradient[gIndex];
gRel += gDelta;
gDelta += gDeltaDelta;
i++;
}
while (i < w) {
pixels[off + i] = rgbclip;
i++;
}
off += adjust;
rowX += a01;
rowY += a11;
}
}
private static final int SQRT_LUT_SIZE = (1 << 11);
private static float sqrtLut[] = new float[SQRT_LUT_SIZE+1];
static {
for (int i = 0; i < sqrtLut.length; i++) {
sqrtLut[i] = (float) Math.sqrt(i / ((float) SQRT_LUT_SIZE));
}
}
private void cyclicCircularGradientFillRaster(int pixels[], int off,
int adjust,
int x, int y,
int w, int h)
{
final double constC =
-radiusSq + (centerX * centerX) + (centerY * centerY);
double A, B, C;
double slope, yintcpt;
double solutionX, solutionY;
final float constX = (a00*x) + (a01*y) + a02;
final float constY = (a10*x) + (a11*y) + a12;
final float precalc2 = 2 * centerY;
final float precalc3 = -2 * centerX;
float g;
float det;
float currentToFocusSq;
float intersectToFocusSq;
float deltaXSq, deltaYSq;
int indexer = off;
int pixInc = w+adjust;
if (trivial == 0) {
int rgb0 = indexIntoGradientsArrays(0f);
for (int j = 0; j < h; j++) {
for (int i = 0; i < w; i++) {
pixels[indexer + i] = rgb0;
}
indexer += pixInc;
}
return;
}
for (int j = 0; j < h; j++) {
float X = (a01*j) + constX;
float Y = (a11*j) + constY;
for (int i = 0; i < w; i++) {
if (X == focusX) {
solutionX = focusX;
solutionY = centerY;
solutionY += (Y > focusY) ? trivial : -trivial;
} else {
slope = (Y - focusY) / (X - focusX);
yintcpt = Y - (slope * X);
A = (slope * slope) + 1;
B = precalc3 + (-2 * slope * (centerY - yintcpt));
C = constC + (yintcpt* (yintcpt - precalc2));
det = (float)Math.sqrt((B * B) - (4 * A * C));
solutionX = -B;
solutionX += (X < focusX)? -det : det;
solutionX = solutionX / (2 * A);
solutionY = (slope * solutionX) + yintcpt;
}
deltaXSq = X - focusX;
deltaXSq = deltaXSq * deltaXSq;
deltaYSq = Y - focusY;
deltaYSq = deltaYSq * deltaYSq;
currentToFocusSq = deltaXSq + deltaYSq;
deltaXSq = (float)solutionX - focusX;
deltaXSq = deltaXSq * deltaXSq;
deltaYSq = (float)solutionY - focusY;
deltaYSq = deltaYSq * deltaYSq;
intersectToFocusSq = deltaXSq + deltaYSq;
if (intersectToFocusSq == 0) {
intersectToFocusSq =
(solutionY >= focusY) ? trivial : -trivial;
}
g = (float)Math.sqrt(currentToFocusSq / intersectToFocusSq);
pixels[indexer + i] = indexIntoGradientsArrays(g);
X += a00;
Y += a10;
}
indexer += pixInc;
}
}
}
