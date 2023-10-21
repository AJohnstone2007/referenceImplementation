package com.sun.glass.ui.monocle;
import com.sun.glass.ui.Pixels;
import java.nio.Buffer;
import java.nio.ByteBuffer;
public class AndroidScreen implements NativeScreen {
private float density = -1;
public int getDepth() {
return 24;
}
public int getNativeFormat() {
return Pixels.Format.BYTE_ARGB;
}
public int getWidth() {
int answer = (int)(_getWidth()/getScale());
return answer;
}
public int getHeight() {
return (int)(_getHeight()/getScale());
}
public int getDPI() {
return 100;
}
@Override
public float getScale () {
if (density < 0) {
density = _getDensity();
}
return density;
}
public long getNativeHandle() {
long answer = _getNativeHandle();
return answer;
}
public void shutdown() {
_shutdown();
}
public void uploadPixels(Buffer b,
int x, int y, int width, int height, float alpha) {
_uploadPixels (b, x, y, width, height, alpha);
}
public void swapBuffers() {
_swapBuffers();
}
public ByteBuffer getScreenCapture() {
return _getScreenCapture();
}
public static final Object framebufferSwapLock = new Object();
native int _getWidth();
native int _getHeight();
native float _getDensity();
native long _getNativeHandle();
native void _shutdown();
native void _uploadPixels(Buffer b,
int x, int y, int width, int height, float alpha);
native void _swapBuffers();
native ByteBuffer _getScreenCapture();
}
