package com.sun.prism.es2;
import com.sun.glass.ui.Screen;
import com.sun.javafx.geom.Rectangle;
import com.sun.prism.GraphicsResource;
import com.sun.prism.Presentable;
import com.sun.prism.PresentableState;
import com.sun.prism.RTTexture;
import com.sun.prism.CompositeMode;
import com.sun.prism.impl.PrismSettings;
import com.sun.javafx.PlatformUtil;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture.WrapMode;
class ES2SwapChain implements ES2RenderTarget, Presentable, GraphicsResource {
private final ES2Context context;
private final PresentableState pState;
private GLDrawable drawable;
private boolean needsResize;
private boolean opaque = false;
private int w, h;
private float pixelScaleFactorX;
private float pixelScaleFactorY;
int nativeDestHandle = 0;
private final boolean msaa;
private RTTexture stableBackbuffer;
private boolean copyFullBuffer;
@Override
public boolean isOpaque() {
if (stableBackbuffer != null) {
return stableBackbuffer.isOpaque();
} else {
return opaque;
}
}
@Override
public void setOpaque(boolean isOpaque) {
if (stableBackbuffer != null) {
stableBackbuffer.setOpaque(isOpaque);
} else {
this.opaque = isOpaque;
}
}
ES2SwapChain(ES2Context context, PresentableState pState) {
this.context = context;
this.pState = pState;
this.pixelScaleFactorX = pState.getRenderScaleX();
this.pixelScaleFactorY = pState.getRenderScaleY();
this.msaa = pState.isMSAA();
long nativeWindow = pState.getNativeWindow();
drawable = ES2Pipeline.glFactory.createGLDrawable(
nativeWindow, context.getPixelFormat());
}
@Override
public boolean lockResources(PresentableState pState) {
if (this.pState != pState ||
pixelScaleFactorX != pState.getRenderScaleX() ||
pixelScaleFactorY != pState.getRenderScaleY())
{
return true;
}
needsResize = (w != pState.getRenderWidth() || h != pState.getRenderHeight());
if (stableBackbuffer != null && !needsResize) {
stableBackbuffer.lock();
if (stableBackbuffer.isSurfaceLost()) {
stableBackbuffer = null;
return true;
}
}
return false;
}
@Override
public boolean prepare(Rectangle clip) {
try {
ES2Graphics g = ES2Graphics.create(context, this);
if (stableBackbuffer != null) {
if (needsResize) {
g.forceRenderTarget();
needsResize = false;
}
w = pState.getRenderWidth();
h = pState.getRenderHeight();
int sw = w;
int sh = h;
int dw = pState.getOutputWidth();
int dh = pState.getOutputHeight();
copyFullBuffer = false;
if (isMSAA()) {
context.flushVertexBuffer();
g.blit(stableBackbuffer, null,
0, 0, sw, sh, 0, dh, dw, 0);
} else {
drawTexture(g, stableBackbuffer,
0, 0, dw, dh, 0, 0, sw, sh);
}
stableBackbuffer.unlock();
}
return drawable != null;
} catch (Throwable th) {
if (PrismSettings.verbose) {
th.printStackTrace();
}
return false;
}
}
private void drawTexture(ES2Graphics g, RTTexture src,
float dx1, float dy1, float dx2, float dy2,
float sx1, float sy1, float sx2, float sy2) {
CompositeMode savedMode = g.getCompositeMode();
if (!pState.hasWindowManager()) {
g.setExtraAlpha(pState.getAlpha());
g.setCompositeMode(CompositeMode.SRC_OVER);
} else {
g.setCompositeMode(CompositeMode.SRC);
}
g.drawTexture(src, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
context.flushVertexBuffer();
g.setCompositeMode(savedMode);
}
@Override
public boolean present() {
boolean presented = drawable.swapBuffers(context.getGLContext());
context.makeCurrent(null);
return presented;
}
@Override
public ES2Graphics createGraphics() {
if (drawable.getNativeWindow() != pState.getNativeWindow()) {
drawable = ES2Pipeline.glFactory.createGLDrawable(
pState.getNativeWindow(), context.getPixelFormat());
}
context.makeCurrent(drawable);
nativeDestHandle = pState.getNativeFrameBuffer();
if (nativeDestHandle == 0) {
GLContext glContext = context.getGLContext();
nativeDestHandle = glContext.getBoundFBO();
}
needsResize = (w != pState.getRenderWidth() || h != pState.getRenderHeight());
if (stableBackbuffer == null || needsResize) {
if (stableBackbuffer != null) {
stableBackbuffer.dispose();
stableBackbuffer = null;
} else {
ES2Graphics.create(context, this);
}
w = pState.getRenderWidth();
h = pState.getRenderHeight();
ResourceFactory factory = context.getResourceFactory();
stableBackbuffer = factory.createRTTexture(w, h,
WrapMode.CLAMP_NOT_NEEDED,
msaa);
if (PrismSettings.dirtyOptsEnabled) {
stableBackbuffer.contentsUseful();
}
copyFullBuffer = true;
}
ES2Graphics g = ES2Graphics.create(context, stableBackbuffer);
g.scale(pixelScaleFactorX, pixelScaleFactorY);
return g;
}
@Override
public int getFboID() {
return nativeDestHandle;
}
@Override
public Screen getAssociatedScreen() {
return context.getAssociatedScreen();
}
@Override
public int getPhysicalWidth() {
return pState.getOutputWidth();
}
@Override
public int getPhysicalHeight() {
return pState.getOutputHeight();
}
@Override
public int getContentX() {
if (PlatformUtil.useEGL()) {
return (int) (pState.getWindowX() * pState.getOutputScaleX());
} else {
return 0;
}
}
@Override
public int getContentY() {
if (PlatformUtil.useEGL()) {
return ((int) (pState.getScreenHeight() * pState.getOutputScaleY())) -
pState.getOutputHeight() - ((int) (pState.getWindowY() * pState.getOutputScaleY()));
} else {
return 0;
}
}
@Override
public int getContentWidth() {
return pState.getOutputWidth();
}
@Override
public int getContentHeight() {
return pState.getOutputHeight();
}
@Override
public float getPixelScaleFactorX() {
return pixelScaleFactorX;
}
@Override
public float getPixelScaleFactorY() {
return pixelScaleFactorY;
}
@Override
public void dispose() {
if (stableBackbuffer != null) {
stableBackbuffer.dispose();
stableBackbuffer = null;
}
}
@Override
public boolean isMSAA() {
return stableBackbuffer != null ? stableBackbuffer.isMSAA() :
msaa;
}
}
