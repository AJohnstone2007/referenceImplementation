package com.sun.scenario.effect.impl.sw.java;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.impl.Renderer;
public class JSWLinearConvolveShadowPeer extends JSWLinearConvolvePeer {
public JSWLinearConvolveShadowPeer(FilterContext fctx, Renderer r, String uniqueName) {
super(fctx, r, uniqueName);
}
private float[] getShadowColor() {
return getRenderState().getPassShadowColorComponents();
}
@Override
protected void filterVector(int dstPixels[], int dstw, int dsth, int dstscan,
int srcPixels[], int srcw, int srch, int srcscan,
float weights[], int count,
float srcx0, float srcy0,
float offsetx, float offsety,
float deltax, float deltay,
float dxcol, float dycol, float dxrow, float dyrow)
{
float shadowColor[] = getShadowColor();
int dstrow = 0;
srcx0 += (dxrow + dxcol) * 0.5f;
srcy0 += (dyrow + dycol) * 0.5f;
for (int dy = 0; dy < dsth; dy++) {
float srcx = srcx0;
float srcy = srcy0;
for (int dx = 0; dx < dstw; dx++) {
float sum = 0.0f;
float sampx = srcx + offsetx;
float sampy = srcy + offsety;
for (int i = 0; i < count; ++i) {
if (sampx >= 0 && sampy >= 0) {
int ix = (int) sampx;
int iy = (int) sampy;
if (ix < srcw && iy < srch) {
int argb = srcPixels[iy * srcscan + ix];
sum += (argb >>> 24) * weights[i];
}
}
sampx += deltax;
sampy += deltay;
}
sum = (sum < 0f) ? 0f : ((sum > 255f) ? 255f : sum);
dstPixels[dstrow + dx] = ((int) (shadowColor[0] * sum) << 16) |
((int) (shadowColor[1] * sum) << 8) |
((int) (shadowColor[2] * sum) ) |
((int) (shadowColor[3] * sum) << 24);
srcx += dxcol;
srcy += dycol;
}
srcx0 += dxrow;
srcy0 += dyrow;
dstrow += dstscan;
}
}
@Override
protected void filterHV(int dstPixels[], int dstcols, int dstrows, int dcolinc, int drowinc,
int srcPixels[], int srccols, int srcrows, int scolinc, int srowinc,
float weights[])
{
float shadowColor[] = getShadowColor();
int kernelSize = weights.length / 2;
float avals[] = new float[kernelSize];
int dstrow = 0;
int srcrow = 0;
int shadowRGBs[] = new int[256];
for (int i = 0; i < shadowRGBs.length; i++) {
shadowRGBs[i] = ((int) (shadowColor[0] * i) << 16) |
((int) (shadowColor[1] * i) << 8) |
((int) (shadowColor[2] * i) ) |
((int) (shadowColor[3] * i) << 24);
}
for (int r = 0; r < dstrows; r++) {
int dstoff = dstrow;
int srcoff = srcrow;
for (int i = 0; i < avals.length; i++) {
avals[i] = 0f;
}
int koff = kernelSize;
for (int c = 0; c < dstcols; c++) {
avals[kernelSize - koff] =
((c < srccols) ? srcPixels[srcoff] : 0) >>> 24;
if (--koff <= 0) {
koff += kernelSize;
}
float sum = -0.5f;
for (int i = 0; i < avals.length; i++) {
sum += avals[i] * weights[koff + i];
}
dstPixels[dstoff] =
((sum < 0f) ? 0
: ((sum >= 254f) ? shadowRGBs[255]
: shadowRGBs[((int) sum) + 1]));
dstoff += dcolinc;
srcoff += scolinc;
}
dstrow += drowinc;
srcrow += srowinc;
}
}
}
