package com.sun.scenario.effect.impl.sw.java;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.impl.HeapImage;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.impl.state.BoxRenderState;
public class JSWBoxBlurPeer extends JSWEffectPeer<BoxRenderState> {
public JSWBoxBlurPeer(FilterContext fctx, Renderer r, String uniqueName) {
super(fctx, r, uniqueName);
}
@Override
public ImageData filter(Effect effect,
BoxRenderState brstate,
BaseTransform transform,
Rectangle outputClip,
ImageData... inputs)
{
setRenderState(brstate);
boolean horizontal = (getPass() == 0);
int hinc = horizontal ? brstate.getBoxPixelSize(0) - 1 : 0;
int vinc = horizontal ? 0 : brstate.getBoxPixelSize(1) - 1;
int iterations = brstate.getBlurPasses();
if (iterations < 1 || (hinc < 1 && vinc < 1)) {
inputs[0].addref();
return inputs[0];
}
int growx = (hinc * iterations + 1) & (~0x1);
int growy = (vinc * iterations + 1) & (~0x1);
HeapImage src = (HeapImage)inputs[0].getUntransformedImage();
Rectangle srcr = inputs[0].getUntransformedBounds();
HeapImage cur = src;
int curw = srcr.width;
int curh = srcr.height;
int curscan = cur.getScanlineStride();
int[] curPixels = cur.getPixelArray();
int finalw = curw + growx;
int finalh = curh + growy;
while (curw < finalw || curh < finalh) {
int neww = curw + hinc;
int newh = curh + vinc;
if (neww > finalw) neww = finalw;
if (newh > finalh) newh = finalh;
HeapImage dst = (HeapImage)getRenderer().getCompatibleImage(neww, newh);
int newscan = dst.getScanlineStride();
int[] newPixels = dst.getPixelArray();
if (horizontal) {
filterHorizontal(newPixels, neww, newh, newscan,
curPixels, curw, curh, curscan);
} else {
filterVertical(newPixels, neww, newh, newscan,
curPixels, curw, curh, curscan);
}
if (cur != src) {
getRenderer().releaseCompatibleImage(cur);
}
cur = dst;
curw = neww;
curh = newh;
curPixels = newPixels;
curscan = newscan;
}
Rectangle resBounds =
new Rectangle(srcr.x - growx/2, srcr.y - growy/2, curw, curh);
return new ImageData(getFilterContext(), cur, resBounds);
}
protected void filterHorizontal(int dstPixels[], int dstw, int dsth, int dstscan,
int srcPixels[], int srcw, int srch, int srcscan)
{
int hsize = dstw - srcw + 1;
int kscale = 0x7fffffff / (hsize * 255);
int srcoff = 0;
int dstoff = 0;
for (int y = 0; y < dsth; y++) {
int suma = 0;
int sumr = 0;
int sumg = 0;
int sumb = 0;
for (int x = 0; x < dstw; x++) {
int rgb;
rgb = (x >= hsize) ? srcPixels[srcoff + x - hsize] : 0;
suma -= (rgb >>> 24);
sumr -= (rgb >> 16) & 0xff;
sumg -= (rgb >> 8) & 0xff;
sumb -= (rgb ) & 0xff;
rgb = (x < srcw) ? srcPixels[srcoff + x] : 0;
suma += (rgb >>> 24);
sumr += (rgb >> 16) & 0xff;
sumg += (rgb >> 8) & 0xff;
sumb += (rgb ) & 0xff;
dstPixels[dstoff + x] =
(((suma * kscale) >> 23) << 24) +
(((sumr * kscale) >> 23) << 16) +
(((sumg * kscale) >> 23) << 8) +
(((sumb * kscale) >> 23) );
}
srcoff += srcscan;
dstoff += dstscan;
}
}
protected void filterVertical(int dstPixels[], int dstw, int dsth, int dstscan,
int srcPixels[], int srcw, int srch, int srcscan)
{
int vsize = dsth - srch + 1;
int kscale = 0x7fffffff / (vsize * 255);
int voff = vsize * srcscan;
for (int x = 0; x < dstw; x++) {
int suma = 0;
int sumr = 0;
int sumg = 0;
int sumb = 0;
int srcoff = x;
int dstoff = x;
for (int y = 0; y < dsth; y++) {
int rgb;
rgb = (srcoff >= voff) ? srcPixels[srcoff - voff] : 0;
suma -= (rgb >>> 24);
sumr -= (rgb >> 16) & 0xff;
sumg -= (rgb >> 8) & 0xff;
sumb -= (rgb ) & 0xff;
rgb = (y < srch) ? srcPixels[srcoff] : 0;
suma += (rgb >>> 24);
sumr += (rgb >> 16) & 0xff;
sumg += (rgb >> 8) & 0xff;
sumb += (rgb ) & 0xff;
dstPixels[dstoff] =
(((suma * kscale) >> 23) << 24) +
(((sumr * kscale) >> 23) << 16) +
(((sumg * kscale) >> 23) << 8) +
(((sumb * kscale) >> 23) );
srcoff += srcscan;
dstoff += dstscan;
}
}
}
}
