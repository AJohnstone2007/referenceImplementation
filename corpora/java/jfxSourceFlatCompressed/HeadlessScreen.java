package com.sun.glass.ui.monocle;
import com.sun.glass.ui.Pixels;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedAction;
class HeadlessScreen implements NativeScreen {
protected int depth;
protected int width;
protected int height;
protected Framebuffer fb;
HeadlessScreen() {
this(1280, 800, 32);
}
protected HeadlessScreen(int defaultWidth,
int defaultHeight,
int defaultDepth) {
this.width = defaultWidth;
this.height = defaultHeight;
this.depth = defaultDepth;
@SuppressWarnings("removal")
String geometry = AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty("headless.geometry"));
if (geometry != null && geometry.indexOf('x') > 0) {
try {
int i = geometry.indexOf("x");
width = Integer.parseInt(geometry.substring(0, i));
int j = geometry.indexOf("-", i + 1);
if (j > 0) {
depth = Integer.parseInt(geometry.substring(j + 1));
} else {
j = geometry.length();
}
height = Integer.parseInt(geometry.substring(i + 1, j));
} catch (NumberFormatException e) {
System.err.println("Cannot parse geometry string: '"
+ geometry + "'");
}
}
ByteBuffer bb = ByteBuffer.allocate(width * height * (depth >>> 3));
bb.order(ByteOrder.nativeOrder());
fb = new Framebuffer(bb, width, height, depth, true);
}
@Override
public int getDepth() {
return depth;
}
@Override
public int getNativeFormat() {
return Pixels.Format.BYTE_BGRA_PRE;
}
@Override
public int getWidth() {
return width;
}
@Override
public int getHeight() {
return height;
}
@Override
public float getScale() {
return 1.0f;
}
@Override
public long getNativeHandle() {
return 1l;
}
@Override
public int getDPI() {
return 96;
}
@Override
public void shutdown() {
}
@Override
public void uploadPixels(Buffer b,
int x, int y, int width, int height,
float alpha) {
fb.composePixels(b, x, y, width, height, alpha);
}
@Override
public void swapBuffers() {
fb.reset();
}
@Override
public ByteBuffer getScreenCapture() {
return fb.getBuffer();
}
}
