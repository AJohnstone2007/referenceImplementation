package com.sun.prism.impl.paint;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.NoninvertibleTransformException;
import com.sun.prism.paint.Color;
import com.sun.prism.paint.Gradient;
abstract class MultipleGradientContext {
protected int cycleMethod;
protected float a00, a01, a10, a11, a02, a12;
protected boolean isSimpleLookup;
protected int fastGradientArraySize;
protected int[] gradient;
private int[][] gradients;
private float[] normalizedIntervals;
private float[] fractions;
private int transparencyTest;
protected static final int GRADIENT_SIZE = 256;
protected static final int GRADIENT_SIZE_INDEX = GRADIENT_SIZE -1;
private static final int MAX_GRADIENT_ARRAY_SIZE = 5000;
protected MultipleGradientContext(Gradient mgp,
BaseTransform t,
float[] fractions,
Color[] colors,
int cycleMethod)
{
if (t == null) {
throw new NullPointerException("Transform cannot be null");
}
BaseTransform tInv;
try {
tInv = t.createInverse();
} catch (NoninvertibleTransformException e) {
tInv = BaseTransform.IDENTITY_TRANSFORM;
}
a00 = (float)tInv.getMxx();
a10 = (float)tInv.getMyx();
a01 = (float)tInv.getMxy();
a11 = (float)tInv.getMyy();
a02 = (float)tInv.getMxt();
a12 = (float)tInv.getMyt();
this.cycleMethod = cycleMethod;
this.fractions = fractions;
calculateLookupData(colors);
}
private void calculateLookupData(Color[] colors) {
Color[] normalizedColors = colors;
normalizedIntervals = new float[fractions.length-1];
for (int i = 0; i < normalizedIntervals.length; i++) {
normalizedIntervals[i] = this.fractions[i+1] - this.fractions[i];
}
transparencyTest = 0xff000000;
gradients = new int[normalizedIntervals.length][];
float Imin = 1;
for (int i = 0; i < normalizedIntervals.length; i++) {
Imin = (Imin > normalizedIntervals[i]) ?
normalizedIntervals[i] : Imin;
}
float estimatedSize = 0.0f;
for (int i = 0; i < normalizedIntervals.length
&& Float.isFinite(estimatedSize); i++) {
estimatedSize += (normalizedIntervals[i]/Imin) * GRADIENT_SIZE;
}
if (estimatedSize <= MAX_GRADIENT_ARRAY_SIZE) {
calculateSingleArrayGradient(normalizedColors, Imin);
} else {
calculateMultipleArrayGradient(normalizedColors);
}
}
private void calculateSingleArrayGradient(Color[] colors, float Imin) {
isSimpleLookup = true;
int rgb1, rgb2;
int gradientsTot = 1;
for (int i = 0; i < gradients.length; i++) {
int nGradients = (int)((normalizedIntervals[i]/Imin)*255f);
gradientsTot += nGradients;
gradients[i] = new int[nGradients];
rgb1 = colors[i].getIntArgbPre();
rgb2 = colors[i+1].getIntArgbPre();
interpolate(rgb1, rgb2, gradients[i]);
transparencyTest &= rgb1;
transparencyTest &= rgb2;
}
gradient = new int[gradientsTot];
int curOffset = 0;
for (int i = 0; i < gradients.length; i++){
System.arraycopy(gradients[i], 0, gradient,
curOffset, gradients[i].length);
curOffset += gradients[i].length;
}
gradient[gradient.length-1] = colors[colors.length-1].getIntArgbPre();
fastGradientArraySize = gradient.length - 1;
}
private void calculateMultipleArrayGradient(Color[] colors) {
isSimpleLookup = false;
int rgb1, rgb2;
for (int i = 0; i < gradients.length; i++){
gradients[i] = new int[GRADIENT_SIZE];
rgb1 = colors[i].getIntArgbPre();
rgb2 = colors[i+1].getIntArgbPre();
interpolate(rgb1, rgb2, gradients[i]);
transparencyTest &= rgb1;
transparencyTest &= rgb2;
}
}
private void interpolate(int rgb1, int rgb2, int[] output) {
int a1, r1, g1, b1, da, dr, dg, db;
float stepSize = 1.0f / output.length;
a1 = (rgb1 >> 24) & 0xff;
r1 = (rgb1 >> 16) & 0xff;
g1 = (rgb1 >> 8) & 0xff;
b1 = (rgb1 ) & 0xff;
da = ((rgb2 >> 24) & 0xff) - a1;
dr = ((rgb2 >> 16) & 0xff) - r1;
dg = ((rgb2 >> 8) & 0xff) - g1;
db = ((rgb2 ) & 0xff) - b1;
for (int i = 0; i < output.length; i++) {
output[i] =
(((int) ((a1 + i * da * stepSize) + 0.5) << 24)) |
(((int) ((r1 + i * dr * stepSize) + 0.5) << 16)) |
(((int) ((g1 + i * dg * stepSize) + 0.5) << 8)) |
(((int) ((b1 + i * db * stepSize) + 0.5) ));
}
}
protected final int indexIntoGradientsArrays(float position) {
if (cycleMethod == Gradient.PAD) {
if (position > 1) {
position = 1;
} else if (position < 0) {
position = 0;
}
} else if (cycleMethod == Gradient.REPEAT) {
position = position - (int)position;
if (position < 0) {
position = position + 1;
}
} else {
if (position < 0) {
position = -position;
}
int part = (int)position;
position = position - part;
if ((part & 1) == 1) {
position = 1 - position;
}
}
if (isSimpleLookup) {
return gradient[(int)(position * fastGradientArraySize)];
} else {
if (position < fractions[0]) {
return gradients[0][0];
}
for (int i = 0; i < gradients.length; i++) {
if (position < fractions[i+1]) {
float delta = position - fractions[i];
int index = (int)((delta / normalizedIntervals[i])
* (GRADIENT_SIZE_INDEX));
return gradients[i][index];
}
}
}
return gradients[gradients.length - 1][GRADIENT_SIZE_INDEX];
}
protected abstract void fillRaster(int pixels[], int off, int adjust,
int x, int y, int w, int h);
}
