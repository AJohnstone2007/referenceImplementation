package com.sun.glass.ui.monocle;
import java.nio.Buffer;
import java.nio.ByteBuffer;
public class EGLScreen implements NativeScreen {
final int depth;
final int nativeFormat;
final int width, height;
final int offsetX, offsetY;
final int dpi;
final long handle;
final float scale;
public EGLScreen(int idx) {
this.handle = nGetHandle(idx);
this.depth = nGetDepth(idx);
this.nativeFormat = nGetNativeFormat(idx);
this.width = nGetWidth(idx);
this.height = nGetHeight(idx);
this.offsetX = nGetOffsetX(idx);
this.offsetY = nGetOffsetY(idx);
this.dpi = nGetDpi(idx);
this.scale = nGetScale(idx);
}
@Override
public int getDepth() {
return this.depth;
}
@Override
public int getNativeFormat() {
return this.nativeFormat;
}
@Override
public int getWidth() {
return this.width;
}
@Override
public int getHeight() {
return this.height;
}
@Override
public int getOffsetX() {
return this.offsetX;
}
@Override
public int getOffsetY() {
return this.offsetY;
}
@Override
public int getDPI() {
return this.dpi;
}
@Override
public long getNativeHandle() {
return handle;
}
@Override
public void shutdown() {
}
@Override
public void uploadPixels(Buffer b, int x, int y, int width, int height, float alpha) {
}
@Override
public void swapBuffers() {
}
@Override
public ByteBuffer getScreenCapture() {
throw new UnsupportedOperationException("No screencapture on EGL platforms");
}
@Override
public float getScale() {
return this.scale;
}
private native long nGetHandle(int idx);
private native int nGetDepth(int idx);
private native int nGetWidth(int idx);
private native int nGetHeight(int idx);
private native int nGetOffsetX(int idx);
private native int nGetOffsetY(int idx);
private native int nGetDpi(int idx);
private native int nGetNativeFormat(int idx);
private native float nGetScale(int idx);
}
