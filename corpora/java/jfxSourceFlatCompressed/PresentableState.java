package com.sun.prism;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.Pixels;
import com.sun.glass.ui.Screen;
import com.sun.glass.ui.View;
import com.sun.glass.ui.Window;
public abstract class PresentableState {
protected Window window;
protected View view;
protected int nativeFrameBuffer;
protected int windowX, windowY;
protected float windowAlpha;
protected long nativeWindowHandle;
protected long nativeView;
protected int viewWidth, viewHeight;
protected float renderScaleX, renderScaleY;
protected int renderWidth, renderHeight;
protected float outputScaleX, outputScaleY;
protected int outputWidth, outputHeight;
protected int screenHeight;
protected int screenWidth;
protected boolean isWindowVisible;
protected boolean isWindowMinimized;
protected static final boolean hasWindowManager =
Application.GetApplication().hasWindowManager();
protected boolean isClosed;
protected final int pixelFormat = Pixels.getNativeFormat();
public PresentableState() {
}
public int getWindowX() {
return windowX;
}
public int getWindowY() {
return windowY;
}
public int getWidth() {
return viewWidth;
}
public int getHeight() {
return viewHeight;
}
public int getRenderWidth() {
return renderWidth;
}
public int getRenderHeight() {
return renderHeight;
}
public int getOutputWidth() {
return outputWidth;
}
public int getOutputHeight() {
return outputHeight;
}
public float getRenderScaleX() {
return renderScaleX;
}
public float getRenderScaleY() {
return renderScaleY;
}
public float getOutputScaleX() {
return outputScaleX;
}
public float getOutputScaleY() {
return outputScaleY;
}
public float getAlpha() {
return windowAlpha;
}
public long getNativeWindow() {
return nativeWindowHandle;
}
public long getNativeView() {
return nativeView;
}
public int getScreenHeight() {
return screenHeight;
}
public int getScreenWidth() {
return screenWidth;
}
public boolean isViewClosed() {
return isClosed;
}
public boolean isWindowMinimized() {
return isWindowMinimized;
}
public boolean isWindowVisible() {
return isWindowVisible;
}
public boolean hasWindowManager() {
return hasWindowManager;
}
public Window getWindow() {
return window;
}
public boolean isMSAA() { return false; }
public View getView() {
return view;
}
public int getPixelFormat() {
return pixelFormat;
}
public int getNativeFrameBuffer() {
return nativeFrameBuffer;
}
public void lock() {
if (view != null) {
view.lock();
nativeFrameBuffer = view.getNativeFrameBuffer();
}
}
public void unlock() {
if (view != null) view.unlock();
}
public void uploadPixels(PixelSource source) {
Pixels pixels = source.getLatestPixels();
if (pixels != null) {
try {
view.uploadPixels(pixels);
} finally {
source.doneWithPixels(pixels);
}
}
}
private int scale(int dim, float fromScale, float toScale) {
return (fromScale == toScale)
? dim
: (int) Math.ceil(dim * toScale / fromScale);
}
protected void update(float viewScaleX, float viewScaleY,
float renderScaleX, float renderScaleY,
float outputScaleX, float outputScaleY)
{
this.renderScaleX = renderScaleX;
this.renderScaleY = renderScaleY;
this.outputScaleX = outputScaleX;
this.outputScaleY = outputScaleY;
if (renderScaleX == viewScaleX && renderScaleY == viewScaleY) {
renderWidth = viewWidth;
renderHeight = viewHeight;
} else {
renderWidth = scale(viewWidth, viewScaleX, renderScaleX);
renderHeight = scale(viewHeight, viewScaleY, renderScaleY);
}
if (outputScaleX == viewScaleX && outputScaleY == viewScaleY) {
outputWidth = viewWidth;
outputHeight = viewHeight;
} else if (outputScaleX == renderScaleX && outputScaleY == renderScaleY) {
outputWidth = renderWidth;
outputHeight = renderHeight;
} else {
outputWidth = scale(viewWidth, viewScaleX, outputScaleX);
outputHeight = scale(viewHeight, viewScaleY, outputScaleY);
}
}
public void update() {
if (view != null) {
viewWidth = view.getWidth();
viewHeight = view.getHeight();
window = view.getWindow();
} else {
viewWidth = viewHeight = -1;
window = null;
}
if (window != null) {
windowX = window.getX();
windowY = window.getY();
windowAlpha = window.getAlpha();
nativeView = view.getNativeView();
nativeWindowHandle = window.getNativeWindow();
isClosed = view.isClosed();
isWindowVisible = window.isVisible();
isWindowMinimized = window.isMinimized();
update(window.getPlatformScaleX(), window.getPlatformScaleY(),
window.getRenderScaleX(), window.getRenderScaleY(),
window.getOutputScaleX(), window.getOutputScaleY());
Screen screen = window.getScreen();
if (screen != null) {
screenHeight = screen.getHeight();
screenWidth = screen.getWidth();
}
} else {
nativeView = -1;
nativeWindowHandle = -1;
isClosed = true;
}
}
}
