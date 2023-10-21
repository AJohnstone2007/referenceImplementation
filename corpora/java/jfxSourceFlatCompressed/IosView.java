package com.sun.glass.ui.ios;
import com.sun.glass.ui.Pixels;
import com.sun.glass.ui.View;
import java.util.Map;
final class IosView extends View {
private long nativePtr;
public IosView() {
super();
}
private static final long multiClickTime = 300;
private static final int multiClickMaxX = 2;
private static final int multiClickMaxY = 2;
static long _getMultiClickTime() {
return multiClickTime;
}
static int _getMultiClickMaxX() {
return multiClickMaxX;
}
static int _getMultiClickMaxY() {
return multiClickMaxY;
}
@Override protected void _enableInputMethodEvents(long ptr, boolean enable) { }
@Override native protected int _getNativeFrameBuffer(long ptr);
@Override native protected long _create(Map caps);
@Override native protected long _getNativeView(long ptr);
@Override native protected int _getX(long ptr);
@Override protected native int _getY(long ptr);
@Override native protected boolean _close(long ptr);
@Override native protected void _scheduleRepaint(long ptr);
@Override native protected void _begin(long ptr);
@Override native protected void _end(long ptr);
@Override native protected boolean _enterFullscreen(long ptr, boolean animate, boolean keepRatio, boolean hideCursor);
@Override native protected void _exitFullscreen(long ptr, boolean animate);
@Override native protected void _setParent(long ptr, long parentPtr);
@Override protected void _uploadPixels(long ptr, Pixels pixels) {
throw new RuntimeException("IosView._uploadPixels() UNIMPLEMENTED.");
}
private void notifyUnicode(int type, int keyCode, int unicode, int modifiers) {
notifyKey(type, keyCode, new char[] {(char) unicode}, modifiers);
}
}
