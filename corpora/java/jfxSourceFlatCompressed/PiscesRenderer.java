package com.sun.pisces;
import com.sun.prism.impl.Disposer;
public final class PiscesRenderer {
public static final int ARC_OPEN = 0;
public static final int ARC_CHORD = 1;
public static final int ARC_PIE = 2;
private long nativePtr = 0L;
private AbstractSurface surface;
public PiscesRenderer(AbstractSurface surface) {
this.surface = surface;
initialize();
Disposer.addRecord(this, new PiscesRendererDisposerRecord(nativePtr));
}
private native void initialize();
public void setColor(int red, int green, int blue, int alpha) {
checkColorRange(red, "RED");
checkColorRange(green, "GREEN");
checkColorRange(blue, "BLUE");
checkColorRange(alpha, "ALPHA");
this.setColorImpl(red, green, blue, alpha);
}
private native void setColorImpl(int red, int green, int blue, int alpha);
private void checkColorRange(int v, String componentName) {
if (v < 0 || v > 255) {
throw new IllegalArgumentException(componentName + " color component is out of range");
}
}
public void setColor(int red, int green, int blue) {
setColor(red, green, blue, 255);
}
public void setCompositeRule(int compositeRule) {
if (compositeRule != RendererBase.COMPOSITE_CLEAR &&
compositeRule != RendererBase.COMPOSITE_SRC &&
compositeRule != RendererBase.COMPOSITE_SRC_OVER)
{
throw new IllegalArgumentException("Invalid value for Composite-Rule");
}
this.setCompositeRuleImpl(compositeRule);
}
private native void setCompositeRuleImpl(int compositeRule);
private native void setLinearGradientImpl(int x0, int y0, int x1, int y1,
int[] colors,
int cycleMethod,
Transform6 gradientTransform);
public void setLinearGradient(int x0, int y0, int x1, int y1,
int[] fractions, int[] rgba,
int cycleMethod,
Transform6 gradientTransform)
{
final GradientColorMap gradientColorMap = new GradientColorMap(fractions, rgba, cycleMethod);
setLinearGradientImpl(x0, y0, x1, y1,
gradientColorMap.colors, cycleMethod,
gradientTransform == null ? new Transform6(1 << 16, 0, 0, 1 << 16, 0, 0) : gradientTransform);
}
public void setLinearGradient(int x0, int y0, int x1, int y1,
GradientColorMap gradientColorMap,
Transform6 gradientTransform)
{
setLinearGradientImpl(x0, y0, x1, y1,
gradientColorMap.colors,
gradientColorMap.cycleMethod,
gradientTransform == null ? new Transform6(1 << 16, 0, 0, 1 << 16, 0, 0) : gradientTransform);
}
public void setLinearGradient(int x0, int y0, int color0,
int x1, int y1, int color1,
int cycleMethod) {
int[] fractions = {0x0000, 0x10000};
int[] rgba = {color0, color1};
Transform6 ident = new Transform6(1 << 16, 0, 0, 1 << 16, 0, 0);
setLinearGradient(x0, y0, x1, y1, fractions, rgba, cycleMethod, ident);
}
private native void setRadialGradientImpl(int cx, int cy, int fx, int fy,
int radius,
int[] colors,
int cycleMethod,
Transform6 gradientTransform);
public void setRadialGradient(int cx, int cy, int fx, int fy,
int radius,
int[] fractions, int[] rgba,
int cycleMethod,
Transform6 gradientTransform)
{
final GradientColorMap gradientColorMap = new GradientColorMap(fractions, rgba, cycleMethod);
setRadialGradientImpl(cx, cy, fx, fy, radius,
gradientColorMap.colors, cycleMethod,
gradientTransform == null ? new Transform6(1 << 16, 0, 0, 1 << 16, 0, 0) : gradientTransform);
}
public void setRadialGradient(int cx, int cy, int fx, int fy,
int radius,
GradientColorMap gradientColorMap,
Transform6 gradientTransform) {
setRadialGradientImpl(cx, cy, fx, fy, radius,
gradientColorMap.colors,
gradientColorMap.cycleMethod,
gradientTransform == null ? new Transform6(1 << 16, 0, 0, 1 << 16, 0, 0) : gradientTransform);
}
public void setTexture(int imageType, int data[], int width, int height, int stride,
Transform6 textureTransform, boolean repeat, boolean linearFiltering, boolean hasAlpha)
{
this.inputImageCheck(width, height, 0, stride, data.length);
this.setTextureImpl(imageType, data, width, height, stride, textureTransform, repeat, linearFiltering, hasAlpha);
}
private native void setTextureImpl(int imageType, int data[], int width, int height, int stride,
Transform6 textureTransform, boolean repeat, boolean linearFiltering, boolean hasAlpha);
public void setClip(int minX, int minY, int width, int height) {
final int x1 = Math.max(minX, 0);
final int y1 = Math.max(minY, 0);
final int x2 = Math.min(minX + width, surface.getWidth());
final int y2 = Math.min(minY + height, surface.getHeight());
this.setClipImpl(x1, y1, x2 - x1, y2 - y1);
}
private native void setClipImpl(int minX, int minY, int width, int height);
public void resetClip() {
this.setClipImpl(0, 0, surface.getWidth(), surface.getHeight());
}
public void clearRect(int x, int y, int w, int h) {
final int x1 = Math.max(x, 0);
final int y1 = Math.max(y, 0);
final int x2 = Math.min(x + w, surface.getWidth());
final int y2 = Math.min(y + h, surface.getHeight());
this.clearRectImpl(x1, y1, x2 - x1, y2 - y1);
}
private native void clearRectImpl(int x, int y, int w, int h);
public void fillRect(int x, int y, int w, int h) {
final int x1 = Math.max(x, 0);
final int y1 = Math.max(y, 0);
final int x2 = Math.min(x + w, surface.getWidth() << 16);
final int y2 = Math.min(y + h, surface.getHeight() << 16);
final int w2 = x2 - x1;
final int h2 = y2 - y1;
if (w2 > 0 && h2 > 0) {
this.fillRectImpl(x1, y1, w2, h2);
}
}
private native void fillRectImpl(int x, int y, int w, int h);
public void emitAndClearAlphaRow(byte[] alphaMap, int[] alphaDeltas, int pix_y, int pix_x_from, int pix_x_to,
int rowNum)
{
this.emitAndClearAlphaRow(alphaMap, alphaDeltas, pix_y, pix_x_from, pix_x_to, 0, rowNum);
}
public void emitAndClearAlphaRow(byte[] alphaMap, int[] alphaDeltas, int pix_y, int pix_x_from, int pix_x_to,
int pix_x_off, int rowNum)
{
if (pix_x_off < 0 || (pix_x_off + (pix_x_to - pix_x_from)) > alphaDeltas.length) {
throw new IllegalArgumentException("rendering range exceeds length of data");
}
this.emitAndClearAlphaRowImpl(alphaMap, alphaDeltas, pix_y, pix_x_from, pix_x_to, pix_x_off, rowNum);
}
private native void emitAndClearAlphaRowImpl(byte[] alphaMap, int[] alphaDeltas, int pix_y, int pix_x_from, int pix_x_to,
int pix_x_off, int rowNum);
public void fillAlphaMask(byte[] mask, int x, int y, int width, int height, int offset, int stride) {
if (mask == null) {
throw new NullPointerException("Mask is NULL");
}
this.inputImageCheck(width, height, offset, stride, mask.length);
this.fillAlphaMaskImpl(mask, x, y, width, height, offset, stride);
}
private native void fillAlphaMaskImpl(byte[] mask, int x, int y, int width, int height, int offset, int stride);
public void setLCDGammaCorrection(float gamma) {
if (gamma <= 0) {
throw new IllegalArgumentException("Gamma must be greater than zero");
}
this.setLCDGammaCorrectionImpl(gamma);
}
private native void setLCDGammaCorrectionImpl(float gamma);
public void fillLCDAlphaMask(byte[] mask, int x, int y, int width, int height, int offset, int stride)
{
if (mask == null) {
throw new NullPointerException("Mask is NULL");
}
this.inputImageCheck(width, height, offset, stride, mask.length);
this.fillLCDAlphaMaskImpl(mask, x, y, width, height, offset, stride);
}
private native void fillLCDAlphaMaskImpl(byte[] mask, int x, int y, int width, int height, int offset, int stride);
public void drawImage(int imageType, int imageMode, int data[], int width, int height, int offset, int stride,
Transform6 textureTransform, boolean repeat, boolean linearFiltering,
int bboxX, int bboxY, int bboxW, int bboxH,
int lEdge, int rEdge, int tEdge, int bEdge,
int txMin, int tyMin, int txMax, int tyMax,
boolean hasAlpha)
{
this.inputImageCheck(width, height, offset, stride, data.length);
this.drawImageImpl(imageType, imageMode, data, width, height, offset, stride,
textureTransform, repeat, linearFiltering,
bboxX, bboxY, bboxW, bboxH,
lEdge, rEdge, tEdge, bEdge,
txMin, tyMin, txMax, tyMax,
hasAlpha);
}
private native void drawImageImpl(int imageType, int imageMode, int data[], int width, int height, int offset, int stride,
Transform6 textureTransform, boolean repeat, boolean linearFiltering,
int bboxX, int bboxY, int bboxW, int bboxH,
int lEdge, int rEdge, int tEdge, int bEdge,
int txMin, int tyMin, int txMax, int tyMax,
boolean hasAlpha);
private void inputImageCheck(int width, int height, int offset, int stride, int data_length) {
if (width < 0) {
throw new IllegalArgumentException("WIDTH must be positive");
}
if (height < 0) {
throw new IllegalArgumentException("HEIGHT must be positive");
}
if (offset < 0) {
throw new IllegalArgumentException("OFFSET must be positive");
}
if (stride < 0) {
throw new IllegalArgumentException("STRIDE must be positive");
}
if (stride < width) {
throw new IllegalArgumentException("STRIDE must be >= WIDTH");
}
final int nbits = 32-Integer.numberOfLeadingZeros(stride) + 32-Integer.numberOfLeadingZeros(height);
if (nbits > 31) {
throw new IllegalArgumentException("STRIDE * HEIGHT is too large");
}
if ((offset + stride*(height-1) + width) > data_length) {
throw new IllegalArgumentException("STRIDE * HEIGHT exceeds length of data");
}
}
private static native void disposeNative(long nativeHandle);
private static class PiscesRendererDisposerRecord implements Disposer.Record {
private long nativeHandle;
PiscesRendererDisposerRecord(long nh) {
nativeHandle = nh;
}
@Override
public void dispose() {
if (nativeHandle != 0L) {
disposeNative(nativeHandle);
nativeHandle = 0L;
}
}
}
}
