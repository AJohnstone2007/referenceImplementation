package com.sun.javafx.tk.quantum;
import java.nio.IntBuffer;
import com.sun.glass.ui.Pixels;
import com.sun.prism.Graphics;
import com.sun.prism.GraphicsPipeline;
import com.sun.prism.RTTexture;
import com.sun.prism.Texture.WrapMode;
import com.sun.prism.impl.Disposer;
import com.sun.prism.impl.QueuedPixelSource;
final class UploadingPainter extends ViewPainter implements Runnable {
private RTTexture rttexture;
private RTTexture resolveRTT = null;
private QueuedPixelSource pixelSource = new QueuedPixelSource(true);
private float penScaleX, penScaleY;
UploadingPainter(GlassScene view) {
super(view);
}
void disposeRTTexture() {
if (rttexture != null) {
rttexture.dispose();
rttexture = null;
}
if (resolveRTT != null) {
resolveRTT.dispose();
resolveRTT = null;
}
}
@Override
public float getPixelScaleFactorX() {
return sceneState.getRenderScaleX();
}
@Override
public float getPixelScaleFactorY() {
return sceneState.getRenderScaleY();
}
@Override public void run() {
renderLock.lock();
boolean errored = false;
try {
if (!validateStageGraphics()) {
if (QuantumToolkit.verbose) {
System.err.println("UploadingPainter: validateStageGraphics failed");
}
paintImpl(null);
return;
}
if (factory == null) {
factory = GraphicsPipeline.getDefaultResourceFactory();
}
if (factory == null || !factory.isDeviceReady()) {
factory = null;
return;
}
float scalex = getPixelScaleFactorX();
float scaley = getPixelScaleFactorY();
int bufWidth = sceneState.getRenderWidth();
int bufHeight = sceneState.getRenderHeight();
boolean needsReset = (penScaleX != scalex ||
penScaleY != scaley ||
penWidth != viewWidth ||
penHeight != viewHeight ||
rttexture == null ||
rttexture.getContentWidth() != bufWidth ||
rttexture.getContentHeight() != bufHeight);
if (!needsReset) {
rttexture.lock();
if (rttexture.isSurfaceLost()) {
rttexture.unlock();
sceneState.getScene().entireSceneNeedsRepaint();
needsReset = true;
}
}
if (needsReset) {
disposeRTTexture();
rttexture = factory.createRTTexture(bufWidth, bufHeight, WrapMode.CLAMP_NOT_NEEDED,
sceneState.isMSAA());
if (rttexture == null) {
return;
}
penScaleX = scalex;
penScaleY = scaley;
penWidth = viewWidth;
penHeight = viewHeight;
freshBackBuffer = true;
}
Graphics g = rttexture.createGraphics();
if (g == null) {
disposeRTTexture();
sceneState.getScene().entireSceneNeedsRepaint();
return;
}
g.scale(scalex, scaley);
paintImpl(g);
freshBackBuffer = false;
int outWidth = sceneState.getOutputWidth();
int outHeight = sceneState.getOutputHeight();
float outScaleX = sceneState.getOutputScaleX();
float outScaleY = sceneState.getOutputScaleY();
RTTexture rtt;
if (rttexture.isMSAA() || outWidth != bufWidth || outHeight != bufHeight) {
rtt = resolveRenderTarget(g, outWidth, outHeight);
} else {
rtt = rttexture;
}
Pixels pix = pixelSource.getUnusedPixels(outWidth, outHeight, outScaleX, outScaleY);
IntBuffer bits = (IntBuffer) pix.getPixels();
int rawbits[] = rtt.getPixels();
if (rawbits != null) {
bits.put(rawbits, 0, outWidth * outHeight);
} else {
if (!rtt.readPixels(bits)) {
sceneState.getScene().entireSceneNeedsRepaint();
disposeRTTexture();
pix = null;
}
}
if (rttexture != null) {
rttexture.unlock();
}
if (pix != null) {
pixelSource.enqueuePixels(pix);
sceneState.uploadPixels(pixelSource);
}
} catch (Throwable th) {
errored = true;
th.printStackTrace(System.err);
} finally {
if (rttexture != null && rttexture.isLocked()) {
rttexture.unlock();
}
if (resolveRTT != null && resolveRTT.isLocked()) {
resolveRTT.unlock();
}
Disposer.cleanUp();
sceneState.getScene().setPainting(false);
if (factory != null) {
factory.getTextureResourcePool().freeDisposalRequestedAndCheckResources(errored);
}
renderLock.unlock();
}
}
private RTTexture resolveRenderTarget(Graphics g, int width, int height) {
if (resolveRTT != null) {
resolveRTT.lock();
if (resolveRTT.isSurfaceLost() ||
resolveRTT.getContentWidth() != width ||
resolveRTT.getContentHeight() != height)
{
resolveRTT.unlock();
resolveRTT.dispose();
resolveRTT = null;
}
}
if (resolveRTT == null) {
resolveRTT = g.getResourceFactory().createRTTexture(
width, height,
WrapMode.CLAMP_NOT_NEEDED, false);
}
int srcw = rttexture.getContentWidth();
int srch = rttexture.getContentHeight();
g.blit(rttexture, resolveRTT, 0, 0, srcw, srch, 0, 0, width, height);
return resolveRTT;
}
}
