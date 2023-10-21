package com.sun.glass.ui.monocle;
import com.sun.glass.ui.Size;
class EGLCursor extends NativeCursor {
private static final int CURSOR_WIDTH = 16;
private static final int CURSOR_HEIGHT = 16;
private native void _initEGLCursor(int cursorWidth, int cursorHeight);
private native void _setVisible(boolean visible);
private native void _setLocation(int x, int y);
private native void _setImage(byte[] cursorImage);
EGLCursor() {
_initEGLCursor(CURSOR_WIDTH, CURSOR_HEIGHT);
}
@Override
Size getBestSize() {
return new Size(CURSOR_WIDTH, CURSOR_HEIGHT);
}
@Override
void setVisibility(boolean visibility) {
isVisible = visibility;
_setVisible(visibility);
}
private void updateImage(boolean always) {
System.out.println("EGLCursor.updateImage: not implemented");
}
@Override
void setImage(byte[] cursorImage) {
_setImage(cursorImage);
}
@Override
void setLocation(int x, int y) {
_setLocation(x, y);
}
@Override
void setHotSpot(int hotspotX, int hotspotY) {
}
@Override
void shutdown() {
}
}
