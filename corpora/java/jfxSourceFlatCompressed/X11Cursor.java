package com.sun.glass.ui.monocle;
import com.sun.glass.ui.Size;
import java.io.IOException;
import java.nio.ByteBuffer;
public class X11Cursor extends NativeCursor {
private static final int CURSOR_WIDTH = 16;
private static final int CURSOR_HEIGHT = 16;
protected long xdisplay;
protected long xwindow;
private ByteBuffer transparentCursorBuffer;
private long transparentCursor;
private long pixmap;
private static X xLib = X.getX();
X11Cursor() {
xdisplay =
NativePlatformFactory.getNativePlatform().accScreen.platformGetNativeDisplay();
xwindow = NativePlatformFactory.getNativePlatform().accScreen.platformGetNativeWindow();
transparentCursorBuffer = ByteBuffer.allocateDirect(4);
pixmap = xLib.XCreateBitmapFromData(xdisplay, xwindow,
transparentCursorBuffer, 1, 1);
X.XColor black = new X.XColor();
black.setRed(black.p, 0);
black.setGreen(black.p, 0);
black.setBlue(black.p, 0);
transparentCursor = xLib.XCreatePixmapCursor(xdisplay, pixmap,
pixmap, black.p, black.p, 0, 0);
xLib.XFreePixmap(xdisplay, pixmap);
}
@Override
Size getBestSize() {
return new Size(CURSOR_WIDTH, CURSOR_HEIGHT);
}
@Override
void setVisibility(boolean visibility) {
if (isVisible && !visibility) {
xLib.XDefineCursor(xdisplay, xwindow, transparentCursor);
MonocleWindowManager.getInstance().repaintAll();
} else if (!isVisible && visibility) {
xLib.XUndefineCursor(xdisplay, xwindow);
MonocleWindowManager.getInstance().repaintAll();
}
isVisible = visibility;
}
@Override
void setImage(byte[] cursorImage) {
}
@Override
void setLocation(int x, int y) {
}
@Override
void setHotSpot(int hotspotX, int hotspotY) {
}
@Override
void shutdown() {
}
}
