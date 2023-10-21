package com.sun.glass.ui.win;
import com.sun.glass.ui.Cursor;
import com.sun.glass.ui.Pixels;
import com.sun.glass.ui.Size;
final class WinCursor extends Cursor {
private native static void _initIDs();
static {
_initIDs();
}
protected WinCursor(int type) {
super(type);
}
protected WinCursor(int x, int y, Pixels pixels) {
super(x, y, pixels);
}
@Override native protected long _createCursor(int x, int y, Pixels pixels);
native private static void _setVisible(boolean visible);
native private static Size _getBestSize(int width, int height);
static void setVisible_impl(boolean visible) {
_setVisible(visible);
}
static Size getBestSize_impl(int width, int height) {
return _getBestSize(width, height);
}
}
