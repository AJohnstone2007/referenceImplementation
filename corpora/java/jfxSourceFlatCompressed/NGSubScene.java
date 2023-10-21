package com.sun.javafx.sg.prism;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.CompositeMode;
import com.sun.prism.Graphics;
import com.sun.prism.RTTexture;
import com.sun.prism.RenderTarget;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
import com.sun.prism.paint.Color;
import com.sun.prism.paint.Paint;
public class NGSubScene extends NGNode {
private float slWidth, slHeight;
private double lastScaledW, lastScaledH;
private RTTexture rtt;
private RTTexture resolveRTT = null;
private NGNode root = null;
private boolean renderSG = true;
private final boolean depthBuffer;
private final boolean msaa;
public NGSubScene(boolean depthBuffer, boolean msaa) {
this.depthBuffer = depthBuffer;
this.msaa = msaa;
}
private NGSubScene() {
this(false, false);
}
public void setRoot(NGNode root) {
this.root = root;
}
private Paint fillPaint;
public void setFillPaint(Object paint) {
fillPaint = (Paint)paint;
}
private NGCamera camera;
public void setCamera(NGCamera camera) {
this.camera = camera == null ? NGCamera.INSTANCE : camera;
}
public void setWidth(float width) {
if (this.slWidth != width) {
this.slWidth = width;
geometryChanged();
invalidateRTT();
}
}
public void setHeight(float height) {
if (this.slHeight != height) {
this.slHeight = height;
geometryChanged();
invalidateRTT();
}
}
private NGLightBase[] lights;
public NGLightBase[] getLights() { return lights; }
public void setLights(NGLightBase[] lights) {
this.lights = lights;
}
public void markContentDirty() {
visualsChanged();
}
@Override
protected void visualsChanged() {
renderSG = true;
super.visualsChanged();
}
@Override
protected void geometryChanged() {
renderSG = true;
super.geometryChanged();
}
private void invalidateRTT() {
if (rtt != null) {
rtt.dispose();
rtt = null;
}
}
@Override
protected boolean hasOverlappingContents() {
return false;
}
private boolean isOpaque = false;
private void applyBackgroundFillPaint(Graphics g) {
isOpaque = true;
if (fillPaint != null) {
if (fillPaint instanceof Color) {
Color fillColor = (Color)fillPaint;
isOpaque = (fillColor.getAlpha() >= 1.0);
g.clear(fillColor);
} else {
if (!fillPaint.isOpaque()) {
g.clear();
isOpaque = false;
}
g.setPaint(fillPaint);
g.fillRect(0, 0, rtt.getContentWidth(), rtt.getContentHeight());
}
} else {
isOpaque = false;
g.clear();
}
}
@Override
public void renderForcedContent(Graphics gOptional) {
root.renderForcedContent(gOptional);
}
private static double hypot(double x, double y, double z) {
return Math.sqrt(x * x + y * y + z * z);
}
static final double THRESHOLD = 1.0 / 256.0;
@Override
protected void renderContent(Graphics g) {
if (slWidth <= 0.0 || slHeight <= 0.0) { return; }
BaseTransform txform = g.getTransformNoClone();
double scaleX = hypot(txform.getMxx(), txform.getMyx(), txform.getMzx());
double scaleY = hypot(txform.getMxy(), txform.getMyy(), txform.getMzy());
double scaledW = slWidth * scaleX;
double scaledH = slHeight * scaleY;
int rtWidth = (int) Math.ceil(scaledW - THRESHOLD);
int rtHeight = (int) Math.ceil(scaledH - THRESHOLD);
if (Math.max(Math.abs(scaledW - lastScaledW), Math.abs(scaledH - lastScaledH)) > THRESHOLD) {
if (rtt != null &&
(rtWidth != rtt.getContentWidth() ||
rtHeight != rtt.getContentHeight()))
{
invalidateRTT();
}
renderSG = true;
lastScaledW = scaledW;
lastScaledH = scaledH;
}
if (rtt != null) {
rtt.lock();
if (rtt.isSurfaceLost()) {
renderSG = true;
rtt = null;
}
}
if (renderSG || !root.isClean()) {
if (rtt == null) {
ResourceFactory factory = g.getResourceFactory();
rtt = factory.createRTTexture(rtWidth, rtHeight,
Texture.WrapMode.CLAMP_TO_ZERO,
msaa);
}
Graphics rttGraphics = rtt.createGraphics();
rttGraphics.setPixelScaleFactors(g.getPixelScaleFactorX(), g.getPixelScaleFactorY());
rttGraphics.scale((float) scaleX, (float) scaleY);
rttGraphics.setLights(lights);
rttGraphics.setDepthBuffer(depthBuffer);
if (camera != null) {
rttGraphics.setCamera(camera);
}
applyBackgroundFillPaint(rttGraphics);
root.render(rttGraphics);
root.clearDirtyTree();
renderSG = false;
}
if (msaa) {
int x0 = rtt.getContentX();
int y0 = rtt.getContentY();
int x1 = x0 + rtWidth;
int y1 = y0 + rtHeight;
if ((isOpaque || g.getCompositeMode() == CompositeMode.SRC) &&
isDirectBlitTransform(txform, scaleX, scaleY) &&
!g.isDepthTest())
{
int tx = (int)(txform.getMxt() + 0.5);
int ty = (int)(txform.getMyt() + 0.5);
RenderTarget target = g.getRenderTarget();
int dstX0 = target.getContentX() + tx;
int dstY0 = target.getContentY() + ty;
int dstX1 = dstX0 + rtWidth;
int dstY1 = dstY0 + rtHeight;
int dstW = target.getContentWidth();
int dstH = target.getContentHeight();
int dX = dstX1 > dstW ? dstW - dstX1 : 0;
int dY = dstY1 > dstH ? dstH - dstY1 : 0;
g.blit(rtt, null, x0, y0, x1 + dX, y1 + dY,
dstX0, dstY0, dstX1 + dX, dstY1 + dY);
} else {
if (resolveRTT != null &&
(resolveRTT.getContentWidth() < rtWidth ||
(resolveRTT.getContentHeight() < rtHeight)))
{
resolveRTT.dispose();
resolveRTT = null;
}
if (resolveRTT != null) {
resolveRTT.lock();
if (resolveRTT.isSurfaceLost()) {
resolveRTT = null;
}
}
if (resolveRTT == null) {
resolveRTT = g.getResourceFactory().createRTTexture(rtWidth, rtHeight,
Texture.WrapMode.CLAMP_TO_ZERO, false);
}
resolveRTT.createGraphics().blit(rtt, resolveRTT, x0, y0, x1, y1,
x0, y0, x1, y1);
g.drawTexture(resolveRTT, 0, 0, (float) (rtWidth / scaleX), (float) (rtHeight / scaleY),
0, 0, rtWidth, rtHeight);
resolveRTT.unlock();
}
} else {
g.drawTexture(rtt, 0, 0, (float) (rtWidth / scaleX), (float) (rtHeight / scaleY),
0, 0, rtWidth, rtHeight);
}
rtt.unlock();
}
private static boolean isDirectBlitTransform(BaseTransform tx, double sx, double sy) {
if (sx == 1.0 && sy == 1.0) return tx.isTranslateOrIdentity();
if (!tx.is2D()) return false;
return (tx.getMxx() == sx &&
tx.getMxy() == 0.0 &&
tx.getMyx() == 0.0 &&
tx.getMyy() == sy);
}
public NGCamera getCamera() {
return camera;
}
}
