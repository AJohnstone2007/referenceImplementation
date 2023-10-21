package com.sun.prism.impl.paint;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.paint.Color;
import com.sun.prism.paint.LinearGradient;
final class LinearGradientContext extends MultipleGradientContext {
private float dgdX, dgdY, gc;
LinearGradientContext(LinearGradient paint,
BaseTransform t,
float startx, float starty,
float endx, float endy,
float[] fractions,
Color[] colors,
int cycleMethod)
{
super(paint, t, fractions, colors, cycleMethod);
float dx = endx - startx;
float dy = endy - starty;
float dSq = dx*dx + dy*dy;
float constX = dx/dSq;
float constY = dy/dSq;
dgdX = a00*constX + a10*constY;
dgdY = a01*constX + a11*constY;
gc = (a02-startx)*constX + (a12-starty)*constY;
}
protected void fillRaster(int[] pixels, int off, int adjust,
int x, int y, int w, int h)
{
float g = 0;
int rowLimit = off + w;
float initConst = (dgdX*x) + gc;
for (int i = 0; i < h; i++) {
g = initConst + dgdY*(y+i);
while (off < rowLimit) {
pixels[off++] = indexIntoGradientsArrays(g);
g += dgdX;
}
off += adjust;
rowLimit = off + w;
}
}
}
