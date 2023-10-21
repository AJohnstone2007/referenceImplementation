package com.sun.prism.j2d.paint;
import com.sun.prism.j2d.paint.MultipleGradientPaint.ColorSpaceType;
import com.sun.prism.j2d.paint.MultipleGradientPaint.CycleMethod;
import java.awt.Color;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
abstract class MultipleGradientPaintContext implements PaintContext {
protected ColorModel model;
private static ColorModel xrgbmodel =
new DirectColorModel(24, 0x00ff0000, 0x0000ff00, 0x000000ff);
protected static ColorModel cachedModel;
protected static WeakReference<Raster> cached;
protected Raster saved;
protected CycleMethod cycleMethod;
protected ColorSpaceType colorSpace;
protected float a00, a01, a10, a11, a02, a12;
protected boolean isSimpleLookup;
protected int fastGradientArraySize;
protected int[] gradient;
private int[][] gradients;
private float[] normalizedIntervals;
private float[] fractions;
private int transparencyTest;
private static final int SRGBtoLinearRGB[] = new int[256];
private static final int LinearRGBtoSRGB[] = new int[256];
static {
for (int k = 0; k < 256; k++) {
SRGBtoLinearRGB[k] = convertSRGBtoLinearRGB(k);
LinearRGBtoSRGB[k] = convertLinearRGBtoSRGB(k);
}
}
protected static final int GRADIENT_SIZE = 256;
protected static final int GRADIENT_SIZE_INDEX = GRADIENT_SIZE -1;
private static final int MAX_GRADIENT_ARRAY_SIZE = 5000;
protected MultipleGradientPaintContext(MultipleGradientPaint mgp,
ColorModel cm,
Rectangle deviceBounds,
Rectangle2D userBounds,
AffineTransform t,
RenderingHints hints,
float[] fractions,
Color[] colors,
CycleMethod cycleMethod,
ColorSpaceType colorSpace)
{
if (deviceBounds == null) {
throw new NullPointerException("Device bounds cannot be null");
}
if (userBounds == null) {
throw new NullPointerException("User bounds cannot be null");
}
if (t == null) {
throw new NullPointerException("Transform cannot be null");
}
AffineTransform tInv;
try {
tInv = t.createInverse();
} catch (NoninvertibleTransformException e) {
tInv = new AffineTransform();
}
double m[] = new double[6];
tInv.getMatrix(m);
a00 = (float)m[0];
a10 = (float)m[1];
a01 = (float)m[2];
a11 = (float)m[3];
a02 = (float)m[4];
a12 = (float)m[5];
this.cycleMethod = cycleMethod;
this.colorSpace = colorSpace;
this.fractions = fractions;
this.gradient =
(mgp.gradient != null) ? mgp.gradient.get() : null;
this.gradients =
(mgp.gradients != null) ? mgp.gradients.get() : null;
if (gradient == null && gradients == null) {
calculateLookupData(colors);
mgp.model = this.model;
mgp.normalizedIntervals = this.normalizedIntervals;
mgp.isSimpleLookup = this.isSimpleLookup;
if (isSimpleLookup) {
mgp.fastGradientArraySize = this.fastGradientArraySize;
mgp.gradient = new SoftReference<int[]>(this.gradient);
} else {
mgp.gradients = new SoftReference<int[][]>(this.gradients);
}
} else {
this.model = mgp.model;
this.normalizedIntervals = mgp.normalizedIntervals;
this.isSimpleLookup = mgp.isSimpleLookup;
this.fastGradientArraySize = mgp.fastGradientArraySize;
}
}
private void calculateLookupData(Color[] colors) {
Color[] normalizedColors;
if (colorSpace == ColorSpaceType.LINEAR_RGB) {
normalizedColors = new Color[colors.length];
for (int i = 0; i < colors.length; i++) {
int argb = colors[i].getRGB();
int a = argb >>> 24;
int r = SRGBtoLinearRGB[(argb >> 16) & 0xff];
int g = SRGBtoLinearRGB[(argb >> 8) & 0xff];
int b = SRGBtoLinearRGB[(argb ) & 0xff];
normalizedColors[i] = new Color(r, g, b, a);
}
} else {
normalizedColors = colors;
}
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
int estimatedSize = 0;
for (int i = 0; i < normalizedIntervals.length; i++) {
estimatedSize += (normalizedIntervals[i]/Imin) * GRADIENT_SIZE;
}
if (estimatedSize > MAX_GRADIENT_ARRAY_SIZE) {
calculateMultipleArrayGradient(normalizedColors);
} else {
calculateSingleArrayGradient(normalizedColors, Imin);
}
if ((transparencyTest >>> 24) == 0xff) {
model = xrgbmodel;
} else {
model = ColorModel.getRGBdefault();
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
rgb1 = colors[i].getRGB();
rgb2 = colors[i+1].getRGB();
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
gradient[gradient.length-1] = colors[colors.length-1].getRGB();
if (colorSpace == ColorSpaceType.LINEAR_RGB) {
for (int i = 0; i < gradient.length; i++) {
gradient[i] = convertEntireColorLinearRGBtoSRGB(gradient[i]);
}
}
fastGradientArraySize = gradient.length - 1;
}
private void calculateMultipleArrayGradient(Color[] colors) {
isSimpleLookup = false;
int rgb1, rgb2;
for (int i = 0; i < gradients.length; i++){
gradients[i] = new int[GRADIENT_SIZE];
rgb1 = colors[i].getRGB();
rgb2 = colors[i+1].getRGB();
interpolate(rgb1, rgb2, gradients[i]);
transparencyTest &= rgb1;
transparencyTest &= rgb2;
}
if (colorSpace == ColorSpaceType.LINEAR_RGB) {
for (int j = 0; j < gradients.length; j++) {
for (int i = 0; i < gradients[j].length; i++) {
gradients[j][i] =
convertEntireColorLinearRGBtoSRGB(gradients[j][i]);
}
}
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
private int convertEntireColorLinearRGBtoSRGB(int rgb) {
int a1, r1, g1, b1;
a1 = (rgb >> 24) & 0xff;
r1 = (rgb >> 16) & 0xff;
g1 = (rgb >> 8) & 0xff;
b1 = (rgb ) & 0xff;
r1 = LinearRGBtoSRGB[r1];
g1 = LinearRGBtoSRGB[g1];
b1 = LinearRGBtoSRGB[b1];
return ((a1 << 24) |
(r1 << 16) |
(g1 << 8) |
(b1 ));
}
protected final int indexIntoGradientsArrays(float position) {
if (cycleMethod == CycleMethod.NO_CYCLE) {
if (position > 1) {
position = 1;
} else if (position < 0) {
position = 0;
}
} else if (cycleMethod == CycleMethod.REPEAT) {
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
private static int convertSRGBtoLinearRGB(int color) {
float input, output;
input = color / 255.0f;
if (input <= 0.04045f) {
output = input / 12.92f;
} else {
output = (float)Math.pow((input + 0.055) / 1.055, 2.4);
}
return Math.round(output * 255.0f);
}
private static int convertLinearRGBtoSRGB(int color) {
float input, output;
input = color/255.0f;
if (input <= 0.0031308) {
output = input * 12.92f;
} else {
output = (1.055f *
((float) Math.pow(input, (1.0 / 2.4)))) - 0.055f;
}
return Math.round(output * 255.0f);
}
public final Raster getRaster(int x, int y, int w, int h) {
Raster raster = saved;
if (raster == null ||
raster.getWidth() < w || raster.getHeight() < h)
{
raster = getCachedRaster(model, w, h);
saved = raster;
}
DataBufferInt rasterDB = (DataBufferInt)raster.getDataBuffer();
int[] pixels = rasterDB.getData(0);
int off = rasterDB.getOffset();
int scanlineStride = ((SinglePixelPackedSampleModel)
raster.getSampleModel()).getScanlineStride();
int adjust = scanlineStride - w;
fillRaster(pixels, off, adjust, x, y, w, h);
return raster;
}
protected abstract void fillRaster(int pixels[], int off, int adjust,
int x, int y, int w, int h);
private static synchronized Raster getCachedRaster(ColorModel cm,
int w, int h)
{
if (cm == cachedModel) {
if (cached != null) {
Raster ras = (Raster) cached.get();
if (ras != null &&
ras.getWidth() >= w &&
ras.getHeight() >= h)
{
cached = null;
return ras;
}
}
}
return cm.createCompatibleWritableRaster(w, h);
}
private static synchronized void putCachedRaster(ColorModel cm,
Raster ras)
{
if (cached != null) {
Raster cras = (Raster) cached.get();
if (cras != null) {
int cw = cras.getWidth();
int ch = cras.getHeight();
int iw = ras.getWidth();
int ih = ras.getHeight();
if (cw >= iw && ch >= ih) {
return;
}
if (cw * ch >= iw * ih) {
return;
}
}
}
cachedModel = cm;
cached = new WeakReference<Raster>(ras);
}
public final void dispose() {
if (saved != null) {
putCachedRaster(model, saved);
saved = null;
}
}
public final ColorModel getColorModel() {
return model;
}
}
