package com.sun.glass.ui.monocle;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.Cursor;
import com.sun.glass.ui.Pixels;
import java.io.IOException;
import java.io.InputStream;
final class MonocleCursor extends Cursor {
private byte[] image;
private int hotspotX;
private int hotspotY;
MonocleCursor(int type) {
super(type);
image = getImage(type);
hotspotX = 0;
hotspotY = 0;
}
MonocleCursor(int x, int y, Pixels pixels) {
super(x, y, pixels);
}
void applyCursor() {
int type = getType();
if (type == CURSOR_NONE) {
((MonocleApplication) Application.GetApplication())
.staticCursor_setVisible(false);
} else {
NativeCursor cursor = NativePlatformFactory.getNativePlatform().getCursor();
cursor.setImage(image);
((MonocleApplication) Application.GetApplication())
.staticCursor_setVisible(true);
}
}
@Override
protected long _createCursor(int x, int y, Pixels pixels) {
hotspotX = x;
hotspotY = y;
image = pixels.asByteBuffer().array();
return 1l;
}
private static String cursorResourceName(int cursorType) {
switch (cursorType) {
case CURSOR_CLOSED_HAND: return "ClosedHand";
case CURSOR_CROSSHAIR: return "Crosshair";
case CURSOR_DISAPPEAR: return "Disappear";
case CURSOR_MOVE: return "Move";
case CURSOR_OPEN_HAND: return "OpenHand";
case CURSOR_POINTING_HAND: return "PointingHand";
case CURSOR_RESIZE_DOWN: return "ResizeDown";
case CURSOR_RESIZE_LEFT: return "ResizeLeft";
case CURSOR_RESIZE_LEFTRIGHT: return "ResizeLeftRight";
case CURSOR_RESIZE_NORTHEAST: return "ResizeNorthEast";
case CURSOR_RESIZE_NORTHWEST: return "ResizeNorthWest";
case CURSOR_RESIZE_RIGHT: return "ResizeRight";
case CURSOR_RESIZE_SOUTHEAST: return "ResizeSouthEast";
case CURSOR_RESIZE_SOUTHWEST: return "ResizeSouthWest";
case CURSOR_RESIZE_UP: return "ResizeUp";
case CURSOR_RESIZE_UPDOWN: return "ResizeUpDown";
case CURSOR_TEXT: return "Text";
case CURSOR_WAIT: return "Wait";
default: return "Default";
}
}
private static byte[] getImage(int cursorType) {
InputStream in = null;
try {
in = MonocleCursor.class.getResourceAsStream(
"Cursor"
+ cursorResourceName(cursorType)
+ "Translucent.raw");
byte[] b = new byte[1024];
int bytesRead = 0;
while (bytesRead < 1024) {
int read = in.read(b, bytesRead, 1024 - bytesRead);
if (read >= 0) {
bytesRead += read;
} else {
throw new IOException("Incomplete cursor resource");
}
}
return b;
} catch (IOException e) {
e.printStackTrace();
return null;
} finally {
if (in != null) {
try {
in.close();
} catch (IOException e) { }
}
}
}
}
