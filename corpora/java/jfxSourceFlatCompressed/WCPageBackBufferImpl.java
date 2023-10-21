package com.sun.javafx.webkit.prism;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.prism.Graphics;
import com.sun.prism.GraphicsPipeline;
import com.sun.prism.Image;
import com.sun.prism.RTTexture;
import com.sun.prism.ResourceFactory;
import com.sun.prism.ResourceFactoryListener;
import com.sun.prism.Texture;
import com.sun.prism.paint.Color;
import com.sun.webkit.graphics.WCCamera;
import com.sun.webkit.graphics.WCGraphicsContext;
import com.sun.webkit.graphics.WCGraphicsManager;
import com.sun.webkit.graphics.WCPageBackBuffer;
import java.lang.ref.WeakReference;
import javafx.scene.transform.Transform;
final class WCPageBackBufferImpl extends WCPageBackBuffer implements ResourceFactoryListener {
private RTTexture texture;
private WeakReference<ResourceFactory> registeredWithFactory = null;
private boolean firstValidate = true;
private float pixelScale;
private final static PlatformLogger log =
PlatformLogger.getLogger(WCPageBackBufferImpl.class.getName());
WCPageBackBufferImpl(float pixelScale) {
this.pixelScale = pixelScale;
}
private static RTTexture createTexture(int w, int h) {
return GraphicsPipeline.getDefaultResourceFactory()
.createRTTexture(w, h, Texture.WrapMode.CLAMP_NOT_NEEDED);
}
public WCGraphicsContext createGraphics() {
Graphics g = texture.createGraphics();
g.setCamera(WCCamera.INSTANCE);
g.scale(pixelScale, pixelScale);
return WCGraphicsManager.getGraphicsManager().createGraphicsContext(g);
}
public void disposeGraphics(WCGraphicsContext gc) {
gc.dispose();
}
public void flush(final WCGraphicsContext gc, int x, int y, final int w, final int h) {
int x2 = x + w;
int y2 = y + h;
((Graphics) gc.getPlatformGraphics()).drawTexture(texture, x, y, x2, y2,
x * pixelScale, y * pixelScale, x2 * pixelScale, y2 * pixelScale);
texture.unlock();
}
protected void copyArea(int x, int y, int w, int h, int dx, int dy) {
x *= pixelScale;
y *= pixelScale;
w = (int) Math.ceil(w * pixelScale);
h = (int) Math.ceil(h * pixelScale);
dx *= pixelScale;
dy *= pixelScale;
RTTexture aux = createTexture(w, h);
aux.createGraphics().drawTexture(texture, 0, 0, w, h, x, y, x + w, y + h);
texture.createGraphics().drawTexture(aux, x + dx, y + dy, x + w + dx, y + h + dy,
0, 0, w, h);
aux.dispose();
}
public boolean validate(int width, int height) {
ResourceFactory factory = GraphicsPipeline.getDefaultResourceFactory();
if (factory == null || factory.isDisposed()) {
log.fine("WCPageBackBufferImpl::validate : device disposed or not ready");
return false;
}
width = (int) Math.ceil(width * pixelScale);
height = (int) Math.ceil(height * pixelScale);
if (texture != null) {
texture.lock();
if (texture.isSurfaceLost()) {
texture.dispose();
texture = null;
}
}
if (texture == null) {
texture = createTexture(width, height);
texture.contentsUseful();
if (registeredWithFactory == null || registeredWithFactory.get() != factory) {
factory.addFactoryListener(this);
registeredWithFactory = new WeakReference<>(factory);
}
if (firstValidate) {
firstValidate = false;
} else {
texture.unlock();
return false;
}
} else {
int tw = texture.getContentWidth();
int th = texture.getContentHeight();
if (tw != width || th != height) {
RTTexture newTexture = createTexture(width, height);
newTexture.contentsUseful();
newTexture.createGraphics().drawTexture(texture, 0, 0,
Math.min(width, tw), Math.min(height, th));
texture.dispose();
texture = newTexture;
}
}
return true;
}
@Override public void factoryReset() {
if (texture != null) {
texture.dispose();
texture = null;
}
}
@Override public void factoryReleased() {
log.fine("WCPageBackBufferImpl: resource factory released");
if (texture != null) {
texture.dispose();
texture = null;
}
}
}
