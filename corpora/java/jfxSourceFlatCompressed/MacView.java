package com.sun.glass.ui.mac;
import com.sun.glass.ui.Pixels;
import com.sun.glass.ui.View;
import com.sun.glass.ui.Window;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.TreeSet;
final class MacView extends View {
private native static void _initIDs();
static {
_initIDs();
multiClickTime = _getMultiClickTime_impl();
multiClickMaxX = _getMultiClickMaxX_impl();
multiClickMaxY = _getMultiClickMaxY_impl();
}
private static final long multiClickTime;
private static final int multiClickMaxX, multiClickMaxY;
private native static long _getMultiClickTime_impl();
private native static int _getMultiClickMaxX_impl();
private native static int _getMultiClickMaxY_impl();
static long getMultiClickTime_impl() {
return multiClickTime;
}
static int getMultiClickMaxX_impl() {
return multiClickMaxX;
}
static int getMultiClickMaxY_impl() {
return multiClickMaxY;
}
@Override native protected int _getNativeFrameBuffer(long ptr);
@Override native protected long _create(Map caps);
@Override native protected int _getX(long ptr);
@Override native protected int _getY(long ptr);
@Override native protected void _setParent(long ptr, long parentPtr);
@Override native protected boolean _close(long ptr);
@Override native protected void _scheduleRepaint(long ptr);
@Override native protected void _begin(long ptr);
@Override native protected void _end(long ptr);
@Override native protected boolean _enterFullscreen(long ptr, boolean animate, boolean keepRatio, boolean hideCursor);
@Override native protected void _exitFullscreen(long ptr, boolean animate);
@Override native protected void _enableInputMethodEvents(long ptr, boolean enable);
@Override protected void _uploadPixels(long ptr, Pixels pixels) {
Buffer data = pixels.getPixels();
if (data.isDirect() == true) {
_uploadPixelsDirect(ptr, data, pixels.getWidth(), pixels.getHeight(), pixels.getScaleX(), pixels.getScaleY());
} else if (data.hasArray() == true) {
if (pixels.getBytesPerComponent() == 1) {
ByteBuffer bytes = (ByteBuffer)data;
_uploadPixelsByteArray(ptr, bytes.array(), bytes.arrayOffset(),
pixels.getWidth(), pixels.getHeight(), pixels.getScaleX(), pixels.getScaleY());
} else {
IntBuffer ints = (IntBuffer)data;
_uploadPixelsIntArray(ptr, ints.array(), ints.arrayOffset(),
pixels.getWidth(), pixels.getHeight(), pixels.getScaleX(), pixels.getScaleY());
}
} else {
_uploadPixelsDirect(ptr, pixels.asByteBuffer(),
pixels.getWidth(), pixels.getHeight(), pixels.getScaleX(), pixels.getScaleY());
}
}
native void _uploadPixelsDirect(long viewPtr, Buffer pixels, int width, int height, float scaleX, float scaleY);
native void _uploadPixelsByteArray(long viewPtr, byte[] pixels, int offset, int width, int height, float scaleX, float scaleY);
native void _uploadPixelsIntArray(long viewPtr, int[] pixels, int offset, int width, int height, float scaleX, float scaleY);
@Override
protected void notifyResize(int width, int height) {
Window w = getWindow();
float sx = (w == null) ? 1.0f : w.getPlatformScaleX();
float sy = (w == null) ? 1.0f : w.getPlatformScaleY();
width = Math.round(width * sx);
height = Math.round(height * sy);
super.notifyResize(width, height);
}
@Override
protected void notifyMouse(int type, int button, int x, int y, int xAbs,
int yAbs, int modifiers, boolean isPopupTrigger,
boolean isSynthesized) {
Window w = getWindow();
float sx = (w == null) ? 1.0f : w.getPlatformScaleX();
float sy = (w == null) ? 1.0f : w.getPlatformScaleY();
x = Math.round(x * sx);
y = Math.round(y * sy);
xAbs = Math.round(xAbs * sx);
yAbs = Math.round(yAbs * sy);
super.notifyMouse(type, button, x, y, xAbs, yAbs, modifiers,
isPopupTrigger, isSynthesized);
}
@Override protected long _getNativeView(long ptr) {
return ptr;
}
native protected long _getNativeLayer(long ptr);
public long getNativeLayer() {
return _getNativeLayer(getNativeView());
}
protected void notifyInputMethodMac(String str, int attrib, int length,
int cursor, int selStart, int selLength) {
byte atts[] = new byte[1];
atts[0] = (byte) attrib;
int attBounds[] = new int[2];
attBounds[0] = 0;
attBounds[1] = length;
if(attrib == 4) {
notifyInputMethod(str, null, attBounds, atts, length, cursor, 0);
} else {
if (selLength > 0
&& str != null && str.length() > 0
&& selStart >= 0
&& selLength + selStart <= str.length()) {
TreeSet<Integer> b = new TreeSet<>();
b.add(0);
b.add(selStart);
b.add(selStart + selLength);
b.add(str.length());
int[] boundary = new int[b.size()];
int i = 0;
for (int e : b) {
boundary[i] = e;
i++;
}
byte[] values = new byte[boundary.length - 1];
for (i = 0; i < boundary.length - 1; i++) {
values[i] = (boundary[i] == selStart)
? IME_ATTR_TARGET_CONVERTED
: IME_ATTR_CONVERTED;
}
notifyInputMethod(str, boundary, boundary, values, 0, cursor, 0);
} else {
notifyInputMethod(str, null, attBounds, atts, 0, cursor, 0);
}
}
}
}
